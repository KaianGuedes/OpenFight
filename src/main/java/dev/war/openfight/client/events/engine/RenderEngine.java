package dev.war.openfight.client.events.engine;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import dev.war.openfight.EpicFight;
import dev.war.openfight.api.animation.JointTransform;
import dev.war.openfight.api.client.animation.AnimationSubFileReader;
import dev.war.openfight.api.client.camera.EpicFightCameraAPI;
import dev.war.openfight.api.client.event.EpicFightClientEventHooks;
import dev.war.openfight.api.client.event.types.registry.RegisterPatchedRenderersEvent;
import dev.war.openfight.api.client.event.types.render.RenderEnderDragonEvent;
import dev.war.openfight.api.client.input.InputManager;
import dev.war.openfight.api.client.input.action.EpicFightInputAction;
import dev.war.openfight.api.client.model.Meshes;
import dev.war.openfight.api.utils.math.MathUtils;
import dev.war.openfight.api.utils.math.OpenMatrix4f;
import dev.war.openfight.api.utils.math.Vec3f;
import dev.war.openfight.client.ClientEngine;
import dev.war.openfight.client.gui.BattleModeGui;
import dev.war.openfight.client.gui.EntityUI;
import dev.war.openfight.client.gui.screen.overlay.OverlayManager;
import dev.war.openfight.client.input.EpicFightKeyMappings;
import dev.war.openfight.client.mesh.HumanoidMesh;
import dev.war.openfight.client.renderer.EpicFightRenderTypes;
import dev.war.openfight.client.renderer.FakeBlockRenderer;
import dev.war.openfight.client.renderer.FirstPersonRenderer;
import dev.war.openfight.client.renderer.VanillaFakeBlockRenderer;
import dev.war.openfight.client.renderer.patched.entity.*;
import dev.war.openfight.client.renderer.patched.item.*;
import dev.war.openfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import dev.war.openfight.config.ClientConfig;
import dev.war.openfight.platform.ModPlatformProvider;
import dev.war.openfight.registry.entries.EpicFightEntityTypes;
import dev.war.openfight.skill.Skill;
import dev.war.openfight.world.capabilities.EpicFightCapabilities;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;
import dev.war.openfight.world.capabilities.entitypatch.boss.BossPatch;
import dev.war.openfight.world.capabilities.entitypatch.boss.enderdragon.EnderDragonPatch;
import dev.war.openfight.world.capabilities.item.*;
import dev.war.openfight.world.gamerule.EpicFightGameRules;

import net.minecraft.client.Camera;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.TooltipFlag;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import dev.war.openfight.client.renderer.compat.EpicRenderContext;
import dev.war.openfight.client.renderer.compat.StateEntityAccess;
import dev.war.openfight.client.renderer.compat.SubmitBufferSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public class RenderEngine {
    private static final RenderEngine INSTANCE = new RenderEngine();

    public static RenderEngine getInstance() {
        return INSTANCE;
    }

    public final BattleModeGui battleModeHUD;
    public final Minecraft minecraft;

    private final BiMap<EntityType<?>, Function<EntityType<?>, PatchedEntityRenderer>> entityRendererProvider;
    private final Map<EntityType<?>, PatchedEntityRenderer> entityRendererCache;
    private final Map<Item, RenderItemBase> itemRendererMapByInstance;
    private final Map<Class<?>, RenderItemBase> itemRendererMapByClass;
    private final Map<UUID, BossPatch> bossEventOwners = new ConcurrentHashMap<> ();
    private final OverlayManager overlayManager;
    private FakeBlockRenderer fakeBlockRenderer;
    private EntityRendererProvider.Context rendererContext;

    private FirstPersonRenderer firstPersonRenderer;
    private PHumanoidRenderer<?, ?, ?, ?, ?> basicHumanoidRenderer;
    private int modelInitTimer;

    private RenderEngine() {
        this.minecraft = Minecraft.getInstance();
        this.battleModeHUD = new BattleModeGui(this.minecraft);
        this.entityRendererProvider = HashBiMap.create();
        this.entityRendererCache = new HashMap<> ();
        this.itemRendererMapByInstance = new HashMap<> ();
        this.itemRendererMapByClass = new HashMap<> ();
        this.overlayManager = new OverlayManager();
        this.fakeBlockRenderer = new VanillaFakeBlockRenderer();
    }

    public void reloadFakeBlockRenderer(FakeBlockRenderer fakeBlockRenderer) {
        this.fakeBlockRenderer = fakeBlockRenderer;
    }

    public void reloadItemRenderers(Map<Identifier, JsonElement> objects) {
        //Clear item renderers
        this.itemRendererMapByInstance.clear();
        this.itemRendererMapByClass.clear();

        Map<Identifier, Function<JsonElement, RenderItemBase>> itemRenderers = new HashMap<> ();
        itemRenderers.put(Identifier.withDefaultNamespace("base"), RenderItemBase::new);
        itemRenderers.put(Identifier.withDefaultNamespace("ranged"), RenderTwoHandedRangedWeapon::new);
        itemRenderers.put(Identifier.withDefaultNamespace("map"), RenderFilledMap::new);
        itemRenderers.put(Identifier.withDefaultNamespace("shield"), RenderShield::new);
        itemRenderers.put(Identifier.withDefaultNamespace("trident"), RenderTrident::new);
        itemRenderers.put(EpicFight.identifier("uchigatana"), RenderKatana::new);

        EpicFightClientEventHooks.Registry.PATCHED_ITEM.post(new RegisterPatchedRenderersEvent.Item(itemRenderers));

        for (Map.Entry<Identifier, JsonElement> entry : objects.entrySet()) {
            Identifier rl = entry.getKey();
            String pathString = rl.getPath();
            Identifier registryName = Identifier.fromNamespaceAndPath(rl.getNamespace(), pathString);

            if (!BuiltInRegistries.ITEM.containsKey(registryName)) {
                EpicFight.LOGGER.warn("Failed to load item skin: no item named " + registryName);
                continue;
            }

            Item item = BuiltInRegistries.ITEM.getValue(registryName);
            Function<JsonElement, RenderItemBase> rendererProvider;

            if (entry.getValue().getAsJsonObject().has("renderer")) {
                Identifier rendererName = Identifier.parse(entry.getValue().getAsJsonObject().get("renderer").getAsString());

                if (itemRenderers.containsKey(rendererName)) {
                    rendererProvider = itemRenderers.get(rendererName);
                } else {
                    EpicFight.LOGGER.warn("No renderer named " + rendererName);
                    rendererProvider = RenderItemBase::new;
                }
            } else {
                rendererProvider = RenderItemBase::new;
            }

            RenderItemBase itemRenderer = rendererProvider.apply(entry.getValue());
            this.itemRendererMapByInstance.put(item, itemRenderer);
        }

        RenderItemBase baseRenderer = new RenderItemBase(new JsonObject());
        RenderTwoHandedRangedWeapon bowRenderer = new RenderTwoHandedRangedWeapon(objects.get(BuiltInRegistries.ITEM.getKey(Items.BOW)).getAsJsonObject());
        RenderTwoHandedRangedWeapon crossbowRenderer = new RenderTwoHandedRangedWeapon(objects.get(BuiltInRegistries.ITEM.getKey(Items.CROSSBOW)).getAsJsonObject());
        RenderTrident tridentRenderer = new RenderTrident(objects.get(BuiltInRegistries.ITEM.getKey(Items.TRIDENT)).getAsJsonObject());
        RenderFilledMap mapRenderer = new RenderFilledMap(objects.get(BuiltInRegistries.ITEM.getKey(Items.FILLED_MAP)).getAsJsonObject());
        RenderShield shieldRenderer = new RenderShield(objects.get(BuiltInRegistries.ITEM.getKey(Items.SHIELD)).getAsJsonObject());

        // Render by item classes
        this.itemRendererMapByClass.put(BowItem.class, bowRenderer);
        this.itemRendererMapByClass.put(CrossbowItem.class, crossbowRenderer);
        this.itemRendererMapByClass.put(ShieldItem.class, baseRenderer);
        this.itemRendererMapByClass.put(TridentItem.class, tridentRenderer);
        this.itemRendererMapByClass.put(ShieldItem.class, shieldRenderer);

        // Render by capability classes
        this.itemRendererMapByClass.put(BowCapability.class, bowRenderer);
        this.itemRendererMapByClass.put(CrossbowCapability.class, crossbowRenderer);
        this.itemRendererMapByClass.put(TridentCapability.class, tridentRenderer);
        this.itemRendererMapByClass.put(MapCapability.class, mapRenderer);
        this.itemRendererMapByClass.put(ShieldCapability.class, shieldRenderer);
    }

    public void resetRenderers() {
        this.entityRendererCache.clear();

        for (Map.Entry<EntityType<?>, Function<EntityType<?>, PatchedEntityRenderer>> entry : this.entityRendererProvider.entrySet()) {
            this.entityRendererCache.put(entry.getKey(), entry.getValue().apply(entry.getKey()));
        }

        EpicFightClientEventHooks.Registry.MODIFY_PATCHED_ENTITY.post(new RegisterPatchedRenderersEvent.ModifyEntity(this.entityRendererCache));
    }

    @SuppressWarnings("unchecked")
    public void registerCustomEntityRenderer(EntityType<?> entityType, String rendererName, CompoundTag compound) {
        if (StringUtil.isNullOrEmpty(rendererName)) {
            return;
        }

        EntityRendererProvider.Context context = this.rendererContext;

        if ("player".equals(rendererName)) {
            this.entityRendererCache.put(entityType, this.basicHumanoidRenderer);
        } else if ("epicfight:custom".equals(rendererName)) {
            if (compound.getBooleanOr("humanoid", false)) {
                this.entityRendererCache.put(entityType, new PCustomHumanoidEntityRenderer<> (Meshes.getOrCreate(Identifier.parse(compound.getStringOr("model", "")), (jsonAssetLoader) -> jsonAssetLoader.loadSkinnedMesh(HumanoidMesh::new)), context, entityType));
            } else {
                this.entityRendererCache.put(entityType, new PCustomEntityRenderer(Meshes.getOrCreate(Identifier.parse(compound.getStringOr("model", "")), (jsonAssetLoader) -> jsonAssetLoader.loadSkinnedMesh(HumanoidMesh::new)), context));
            }
        } else {
            EntityType<?> presetEntityType = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(rendererName));

            if (this.entityRendererProvider.containsKey(presetEntityType)) {
                PatchedEntityRenderer renderer = this.entityRendererProvider.get(presetEntityType).apply(entityType);

                if (!(this.minecraft.getEntityRenderDispatcher().renderers.get(entityType) instanceof LivingEntityRenderer) && (renderer instanceof PatchedLivingEntityRenderer patchedLivingEntityRenderer)) {
                    this.entityRendererCache.put(entityType, new PresetRenderer(context, entityType, (LivingEntityRenderer<LivingEntity, ?, ?>)context.getEntityRenderDispatcher().renderers.get(presetEntityType), patchedLivingEntityRenderer.getDefaultMesh()));
                } else {
                    this.entityRendererCache.put(entityType, this.entityRendererProvider.get(presetEntityType).apply(entityType));
                }
            } else {
                throw new IllegalArgumentException("Datapack Mob Patch Crash: Invalid Renderer type " + rendererName);
            }
        }
    }

    public RenderItemBase getItemRenderer(ItemStack itemstack) {
        RenderItemBase renderItem = this.itemRendererMapByInstance.get(itemstack.getItem());

        if (renderItem == null) {
            renderItem = this.findMatchingRendererByClass(itemstack.getItem().getClass());

            if (renderItem == null) {
                CapabilityItem itemCap = EpicFightCapabilities.getItemStackCapability(itemstack);
                renderItem = this.findMatchingRendererByClass(itemCap.getClass());
            }

            if (renderItem == null) {
                // Get generic renderer
                renderItem = this.itemRendererMapByInstance.get(Items.AIR);
            }

            this.itemRendererMapByInstance.put(itemstack.getItem(), renderItem);
        }

        return renderItem;
    }

    private RenderItemBase findMatchingRendererByClass(Class<?> clazz) {
        RenderItemBase renderer = null;

        for (; clazz != null && renderer == null; clazz = clazz.getSuperclass()) {
            renderer = this.itemRendererMapByClass.get(clazz);
        }

        return renderer;
    }

    @SuppressWarnings("unchecked")
    public void renderEntityArmatureModel(LivingEntity livingEntity, LivingEntityPatch<?> entitypatch, EntityRenderer<?, ?> renderer, MultiBufferSource buffer, PoseStack matStack, int packedLight, float partialTicks) {
        this.getEntityRenderer(livingEntity).render(livingEntity, entitypatch, renderer, buffer, matStack, packedLight, partialTicks);
    }

    public PatchedEntityRenderer getEntityRenderer(Entity entity) {
        return this.getEntityRenderer(entity.getType());
    }

    public PatchedEntityRenderer getEntityRenderer(EntityType entityType) {
        return this.entityRendererCache.get(entityType);
    }

    public boolean hasRendererFor(Entity entity) {
        return this.entityRendererCache.computeIfAbsent(entity.getType(), (key) -> this.entityRendererProvider.containsKey(key) ? this.entityRendererProvider.get(entity.getType()).apply(entity.getType()) : null) != null;
    }

    public Set<Identifier> getRendererEntries() {
        Set<Identifier> availableRendererEntities = this.entityRendererProvider.keySet().stream().map((entityType) -> BuiltInRegistries.ENTITY_TYPE.getKey(entityType)).collect(Collectors.toSet());
        availableRendererEntities.add(EpicFight.identifier("custom"));

        return availableRendererEntities;
    }

    public void setModelInitializerTimer(int tick) {
        this.modelInitTimer = tick;
    }

    public OverlayManager getOverlayManager() {
        return this.overlayManager;
    }

    public FirstPersonRenderer getFirstPersonRenderer() {
        return firstPersonRenderer;
    }

    public boolean shouldRenderVanillaModel() {
        return ClientEngine.getInstance().isVanillaModelDebuggingMode() || this.modelInitTimer > 0;
    }

    public void addBossEventOwner(UUID uuid, BossPatch bosspatch) {
        this.bossEventOwners.put(uuid, bosspatch);
    }

    public void removeBossEventOwner(UUID uuid, BossPatch bosspatch) {
        this.bossEventOwners.remove(uuid);
    }

    public void initHUD(LocalPlayerPatch playerpatch) {
        this.battleModeHUD.init(playerpatch);
    }

    private void freeUnusedSources() {
        this.bossEventOwners.entrySet().removeIf((entry) -> {
            Entity entity = entry.getValue().cast().getOriginal();
            return !entity.isAlive() || entity.isRemoved();
        });

        EpicFightRenderTypes.freeUnusedWorldRenderTypes();
    }

    public void clear() {
        EpicFightCameraAPI.getInstance().zoomOut(0);
        this.bossEventOwners.clear();

        this.resetRenderers();
        EpicFightRenderTypes.clearWorldRenderTypes();
    }

    public static boolean hitResultEquals(@Nullable HitResult hitResult, HitResult.Type hitType) {
        return hitResult == null ? false : hitType.equals(hitResult.getType());
    }

    public static boolean hitResultNotEquals(@Nullable HitResult hitResult, HitResult.Type hitType) {
        return hitResult == null ? true : !hitType.equals(hitResult.getType());
    }

    /// More strict type sensitive hit result getter by instanceof
    public static BlockHitResult asBlockHitResult(@Nullable HitResult hitResult) {
        if (hitResult == null) {
            return null;
        }

        if (hitResult.getType() == HitResult.Type.BLOCK && hitResult instanceof BlockHitResult blockHitResult) {
            return blockHitResult;
        }

        return null;
    }

    /// More strict type sensitive hit result getter by instanceof
    public static EntityHitResult asEntityHitResult(@Nullable HitResult hitResult) {
        if (hitResult == null) {
            return null;
        }

        if (hitResult.getType() == HitResult.Type.ENTITY && hitResult instanceof EntityHitResult entityHitResult) {
            return entityHitResult;
        }

        return null;
    }

    /******************
     * Client event hooks (Fabric API / mixins)
     ******************/

    /**
     * Takes over the submission of a living entity (and the ender dragon) model; this replaces NeoForge's
     * RenderLivingEvent.Pre / RenderEnderDragonEvent. Called around {@code EntityRenderer#submit}.
     *
     * @return true when the vanilla renderer submission must be skipped
     */
    @SuppressWarnings("unchecked")
    public boolean submitEntity(EntityRenderer<?, ?> renderer, EntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        Entity entity = ((StateEntityAccess)state).epicfight$getEntity();

        if (!(entity instanceof LivingEntity livingentity) || livingentity.level() == null) {
            return false;
        }

        float partialTick = ((StateEntityAccess)state).epicfight$getPartialTick();
        int packedLight = state.lightCoords;
        boolean cancelled = false;

        EpicRenderContext.begin(state, collector, camera);
        SubmitBufferSource buffer = new SubmitBufferSource(collector);

        try {
            if (this.hasRendererFor(livingentity)) {
                LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(livingentity, LivingEntityPatch.class);

                //Draw the player in inventory
                if ((partialTick == 0.0F || partialTick == 1.0F) && entitypatch instanceof LocalPlayerPatch localPlayerPatch) {
                    if (entitypatch.overrideRender()) {
                        float originalYRot = localPlayerPatch.getModelYRot();
                        localPlayerPatch.setModelYRotInGui(livingentity.getYRot());
                        poseStack.translate(0, 0.1D, 0);
                        boolean compusteShaderSetting = ClientConfig.activateComputeShader;

                        // Disable compute shader
                        ClientConfig.activateComputeShader = false;
                        this.renderEntityArmatureModel(livingentity, entitypatch, renderer, buffer, poseStack, packedLight, partialTick);
                        ClientConfig.activateComputeShader = compusteShaderSetting;

                        cancelled = true;
                        localPlayerPatch.disableModelYRotInGui(originalYRot);
                    }

                    return cancelled;
                }

                if (entitypatch != null && entitypatch.overrideRender()) {
                    this.renderEntityArmatureModel(livingentity, entitypatch, renderer, buffer, poseStack, packedLight, partialTick);

                    if (this.shouldRenderVanillaModel()) {
                        poseStack.translate(this.modelInitTimer > 0 ? 10000.0F : 1.5F, 0.0F, 0.0F);
                        --this.modelInitTimer;
                    } else {
                        cancelled = true;
                    }
                }
            }

            if (!this.minecraft.gui.hud.isHidden() && !EpicFightGameRules.DISABLE_ENTITY_UI.getRuleValue(livingentity.level())) {
                EpicFightCapabilities.getUnparameterizedEntityPatch(this.minecraft.player, LocalPlayerPatch.class).ifPresent(playerpatch -> {
                    LivingEntityPatch<?> entityPatch = EpicFightCapabilities.getEntityPatch(livingentity, LivingEntityPatch.class);

                    for (EntityUI entityIndicator : EntityUI.ENTITY_UI_LIST) {
                        if (entityIndicator.shouldDraw(livingentity, entityPatch, playerpatch, partialTick)) {
                            entityIndicator.draw(livingentity, entityPatch, playerpatch, poseStack, buffer, partialTick);
                        }
                    }
                });
            }
        } finally {
            buffer.endBatch();
            EpicRenderContext.end();
        }

        return cancelled;
    }

    public void onItemTooltip(ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipFlag tooltipFlag, List<Component> tooltipLines) {
        if (ClientConfig.showEpicFightAttributesInTooltip && this.minecraft.player != null && this.minecraft.player.level().isClientSide()) {
            EpicFightCapabilities.getUnparameterizedEntityPatch(this.minecraft.player, LocalPlayerPatch.class).ifPresent(playerpatch -> {
                EpicFightCapabilities.getItemCapability(itemStack).ifPresent(itemCapability -> {
                    if (InputManager.isActionPhysicallyActive(EpicFightInputAction.WEAPON_INNATE_SKILL_TOOLTIP)) {
                        Skill weaponInnateSkill = itemCapability.getInnateSkill(playerpatch, itemStack);
                        if (weaponInnateSkill != null) {
                            tooltipLines.clear();
                            List<Component> skilltooltip = weaponInnateSkill.getTooltipOnItem(itemStack, itemCapability, playerpatch);

                            for (Component s : skilltooltip) {
                                tooltipLines.add(s);
                            }
                        }
                    } else {
                        List<Component> tooltip = tooltipLines;
                        if (true) {
                            itemCapability.modifyItemTooltip(itemStack, tooltipLines, playerpatch);
                        }
                        else
                        {
                            itemCapability.modifySimplyTooltip(itemStack, tooltipLines, playerpatch);
                        }

                        for (int i = 0; i < tooltip.size(); i++) {
                            Component textComp = tooltip.get(i);

                            if (!textComp.getSiblings().isEmpty()) {
                                Component sibling = textComp.getSiblings().getFirst();

                                if (sibling instanceof MutableComponent mutableComponent && mutableComponent.getContents() instanceof TranslatableContents translatableContent) {
                                    if (translatableContent.getArgs().length > 1 && translatableContent.getArgs()[1] instanceof MutableComponent mutableComponent$2) {
                                        if (mutableComponent$2.getContents() instanceof TranslatableContents translatableContent$2) {
                                            if (translatableContent$2.getKey().equals(Attributes.ATTACK_SPEED.value().getDescriptionId())) {
                                                float weaponSpeed = (float)playerpatch.getWeaponAttribute(Attributes.ATTACK_SPEED, itemStack);
                                                tooltip.remove(i);
                                                tooltip.add(i, Component.literal(String.format(" %.2f ", playerpatch.getModifiedAttackSpeedOfItem(itemCapability, weaponSpeed)))
                                                        .append(Component.translatable(Attributes.ATTACK_SPEED.value().getDescriptionId())));

                                            } else if (translatableContent$2.getKey().equals(Attributes.ATTACK_DAMAGE.value().getDescriptionId())) {
                                                float weaponDamage = (float)playerpatch.getWeaponAttribute(Attributes.ATTACK_DAMAGE, itemStack);
                                                String damageFormat = ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(playerpatch.getModifiedBaseDamage(weaponDamage));

                                                tooltip.remove(i);
                                                tooltip.add(i, Component.literal(String.format(" %s ", damageFormat))
                                                                        .append(Component.translatable(Attributes.ATTACK_DAMAGE.value().getDescriptionId()))
                                                                        .withStyle(ChatFormatting.DARK_GREEN));
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Skill weaponInnateSkill = itemCapability.getInnateSkill(playerpatch, itemStack);
                        if (weaponInnateSkill != null) {
                            tooltipLines.add(Component.translatable("inventory.epicfight.guide_innate_tooltip", EpicFightKeyMappings.WEAPON_INNATE_SKILL_TOOLTIP.getTranslatedKeyMessage()).withStyle(ChatFormatting.DARK_GRAY));
                        }
                    }
                });
            });
        }
    }

    private static final Vector3f CAMERA_ROTATION_EULER = new Vector3f();
    private static final OpenMatrix4f PLAYER_ROTATION = new OpenMatrix4f();

    /** Mutable view/pitch/yaw/roll, the equivalent of ViewportEvent.ComputeCameraAngles. */
    public static final class CameraAngles {
        public float pitch;
        public float yaw;
        public float roll;

        public CameraAngles(float pitch, float yaw, float roll) {
            this.pitch = pitch;
            this.yaw = yaw;
            this.roll = roll;
        }
    }

    public void computeCameraAngles(Camera camera, float partialTick, CameraAngles angles) {
        EpicFightCapabilities.getUnparameterizedEntityPatch(this.minecraft.player, LocalPlayerPatch.class).ifPresent(playerpatch -> {
            // First person camera correction
            if (ClientConfig.enableFirstPersonCameraMove && this.minecraft.options.getCameraType().isFirstPerson() && playerpatch.isEpicFightMode() && !playerpatch.getFirstPersonLayer().isOff()) {
                EpicFightCameraAPI cameraApi = EpicFightCameraAPI.getInstance();

                if (cameraApi.isLerpingFpv()) {
                    float xRot = cameraApi.getLerpedFpvXRot(partialTick);
                    float yRot = cameraApi.getLerpedFpvYRot(partialTick);
                    this.minecraft.getCameraEntity().setXRot(xRot);
                    this.minecraft.getCameraEntity().setYRot(yRot);
                } else {
                    AnimationSubFileReader.PovSettings.ViewLimit viewLimit = playerpatch.getPovSettings().viewLimit();

                    if (viewLimit != null) {
                        float clampedXRot = Mth.clamp(angles.pitch, viewLimit.xRotMin(), viewLimit.xRotMax());
                        float bodyY = MathUtils.findNearestRotation(angles.yaw, playerpatch.getYRot());
                        float clampedYRot = Mth.clamp(angles.yaw, bodyY + viewLimit.yRotMin(), bodyY + viewLimit.yRotMax());

                        if (Float.compare(clampedXRot, angles.pitch) != 0 || Float.compare(clampedYRot, angles.yaw) != 0) {
                            cameraApi.fixFpvRotation(clampedXRot, playerpatch.getYRot(), 5);
                        }
                    }
                }

                if (playerpatch.hasCameraAnimation()) {
                    float time = Mth.lerp(partialTick, playerpatch.getFirstPersonLayer().animationPlayer.getPrevElapsedTime(), playerpatch.getFirstPersonLayer().animationPlayer.getElapsedTime());
                    JointTransform cameraTransform;

                    if (playerpatch.getFirstPersonLayer().animationPlayer.getAnimation().get().isLinkAnimation() || playerpatch.getPovSettings() == null) {
                        cameraTransform = playerpatch.getFirstPersonLayer().getLinkCameraTransform().getInterpolatedTransform(time);
                    } else {
                        cameraTransform = playerpatch.getPovSettings().cameraTransform().getInterpolatedTransform(time);
                    }

                    float xRot = playerpatch.getOriginal().getXRot();
                    float yRot = playerpatch.getOriginal().getYRot();

                    Vec3f translation = OpenMatrix4f.transform3v(OpenMatrix4f.ofRotationDegree(yRot, Vec3f.Y_AXIS, PLAYER_ROTATION).rotate(xRot, Vec3f.X_AXIS), cameraTransform.translation(), null);
                    Quaternionf rot = cameraTransform.rotation();
                    rot.getEulerAnglesXYZ(CAMERA_ROTATION_EULER);

                    CAMERA_ROTATION_EULER.x = (float)Math.toDegrees(CAMERA_ROTATION_EULER.x);
                    CAMERA_ROTATION_EULER.y = (float)Math.toDegrees(CAMERA_ROTATION_EULER.y);
                    CAMERA_ROTATION_EULER.z = (float)Math.toDegrees(CAMERA_ROTATION_EULER.z);

                    camera.move(translation.x, translation.y, translation.z);
                    angles.pitch += CAMERA_ROTATION_EULER.x;
                    angles.yaw += CAMERA_ROTATION_EULER.y;
                    angles.roll += CAMERA_ROTATION_EULER.z;
                }
            }
        });
    }

    public void renderGuiPre(GuiGraphicsExtractor graphics) {
        Window window = Minecraft.getInstance().getWindow();
        LocalPlayerPatch playerpatch = EpicFightCapabilities.getCachedLocalPlayerPatch();

        if (playerpatch != null) {
            playerpatch.getPlayerSkills().listSkillContainers().filter(skillContainer -> skillContainer.getSkill() != null).forEach(skillContainer -> {
                skillContainer.getSkill().onScreen(playerpatch, graphics.guiWidth(), graphics.guiHeight());
            });

            this.overlayManager.renderTick(graphics, graphics.guiWidth(), graphics.guiHeight());

            //Shows the epic fight version in beta
        }
    }

    private static final Identifier YELLOWBAR_BACKGROUND = Identifier.withDefaultNamespace("boss_bar/yellow_background");
    private static final Identifier YELLOWBAR_PROGRESS = Identifier.withDefaultNamespace("boss_bar/yellow_progress");

    /** Draws the stun shield bar of Epic Fight bosses over the vanilla boss bar (was CustomizeGuiOverlayEvent.BossEventProgress). */
    public void renderBossEventProgress(GuiGraphicsExtractor graphics, net.minecraft.client.gui.components.LerpingBossEvent bossEvent, int x, int y) {
        if (bossEvent.getName().getString().equals("Ender Dragon")) {
            if (this.bossEventOwners.containsKey(bossEvent.getId())) {
                LivingEntityPatch<?> entitypatch = this.bossEventOwners.get(bossEvent.getId()).cast();
                float stunShield = entitypatch.getStunShield();

                if (stunShield > 0) {
                    float progression = stunShield / entitypatch.getMaxStunShield();

                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, YELLOWBAR_BACKGROUND, x, y + 6, 182, 5);
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, YELLOWBAR_PROGRESS, 182, 5, 0, 0, x, y + 6, (int)(182 * progression), 5);
                }
            }
        }
    }

    /**
     * Replacement for RenderHandEvent: renders the animated first person model.
     *
     * @return true if the vanilla hand rendering must be cancelled
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean renderHand(InteractionHand hand, PoseStack poseStack, SubmitNodeCollector collector, int packedLight, float partialTick) {
        LocalPlayerPatch playerpatch = EpicFightCapabilities.getCachedLocalPlayerPatch();

        if (playerpatch != null) {
            if (playerpatch.isEpicFightMode() && ClientConfig.enableAnimatedFirstPersonModel) {
                RenderItemBase mainhandItemSkin = this.getItemRenderer(playerpatch.getOriginal().getMainHandItem());
                RenderItemBase offhandItemSkin = this.getItemRenderer(playerpatch.getOriginal().getOffhandItem());
                boolean useEpicFightModel = (mainhandItemSkin == null || !mainhandItemSkin.forceVanillaFirstPerson()) && (offhandItemSkin == null || !offhandItemSkin.forceVanillaFirstPerson());

                if (useEpicFightModel) {
                    if (hand == InteractionHand.MAIN_HAND) {
                        SubmitBufferSource buffer = new SubmitBufferSource(collector);
                        EpicRenderContext.begin(null, collector, null);

                        try {
                            this.firstPersonRenderer.render(
                                  playerpatch.getOriginal()
                                , playerpatch
                                , (LivingEntityRenderer)this.minecraft.getEntityRenderDispatcher().getRenderer(playerpatch.getOriginal())
                                , buffer
                                , poseStack
                                , packedLight
                                , partialTick
                            );
                        } finally {
                            buffer.endBatch();
                            EpicRenderContext.end();
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }

    public void renderAfterLevel(LevelRenderContext context) {
        dev.war.openfight.client.particle.ParticleBridge.submit(context.submitNodeCollector(), this.minecraft.gameRenderer.mainCamera(), this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false));
        BlockHitResult blockHitResult = RenderEngine.asBlockHitResult(this.minecraft.hitResult);

        if (ClientConfig.mineBlockGuideOption.showBlockHighlight() && blockHitResult != null) {
            EpicFightCapabilities.getUnparameterizedEntityPatch(this.minecraft.player, LocalPlayerPatch.class).ifPresent(playerpatch -> {
                if (!playerpatch.canPlayAttackAnimation() && playerpatch.isEpicFightMode()) {
                    SubmitBufferSource buffer = new SubmitBufferSource(context.submitNodeCollector(), context.poseStack());
                    this.fakeBlockRenderer.render(this.minecraft.gameRenderer.mainCamera(), context.poseStack(), buffer, this.minecraft.level, blockHitResult.getBlockPos(), 1.0F, 1.0F, 1.0F, 0.4F);
                    buffer.endBatch();
                }
            });
        }
    }

    public void renderTickPre() {
        EntityUI.HEALTH_BAR.reset();
    }

    public void renderTickPost() {
        EntityUI.HEALTH_BAR.remove();
    }

    public void clientTickPre() {
        EpicFightCameraAPI.getInstance().preClientTick();
        EpicFightCapabilities.getUnparameterizedEntityPatch(this.minecraft.player, LocalPlayerPatch.class).ifPresent(this.battleModeHUD::tick);
        this.freeUnusedSources();
    }

    public void clientTickPost() {
        EpicFightCameraAPI.getInstance().postClientTick();
    }

    public void levelTickPost(ClientLevel level) {
        EntityUI.HEALTH_BAR.tick();
    }

    /** @return true if the vanilla block outline must be suppressed */
    public boolean shouldCancelBlockHighlight() {
        LocalPlayerPatch playerpatch = EpicFightCapabilities.getCachedLocalPlayerPatch();
        return playerpatch != null && playerpatch.canPlayAttackAnimation();
    }

    /** Called after the entity renderers were (re)created; replaces EntityRenderersEvent.AddLayers. */
    public void onEntityRenderersReloaded(EntityRendererProvider.Context context) {
        this.rendererContext = context;
        this.epicfight$addLayers(context);
    }

    private void epicfight$addLayers(EntityRendererProvider.Context context) {
        this.entityRendererProvider.clear();
        this.entityRendererProvider.put(EntityTypes.CREEPER, (entityType) -> new PCreeperRenderer(context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.ENDERMAN, (entityType) -> new PEndermanRenderer(context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.ZOMBIE, (entityType) -> new PHumanoidRenderer<>(Meshes.BIPED_OLD_TEX, context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.ZOMBIE_VILLAGER, (entityType) -> new PZombieVillagerRenderer(context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.ZOMBIFIED_PIGLIN, (entityType) -> new PHumanoidRenderer<>(Meshes.PIGLIN, context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.HUSK, (entityType) -> new PHumanoidRenderer<>(Meshes.BIPED_OLD_TEX, context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.SKELETON, (entityType) -> new PHumanoidRenderer<>(Meshes.SKELETON, context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.WITHER_SKELETON, (entityType) -> new PHumanoidRenderer<>(Meshes.SKELETON, context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.STRAY, (entityType) -> new PStrayRenderer(context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.PLAYER, (entityType) -> new PPlayerRenderer(context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.SPIDER, (entityType) -> new PSpiderRenderer(context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.CAVE_SPIDER, (entityType) -> new PSpiderRenderer(context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.IRON_GOLEM, (entityType) -> new PIronGolemRenderer(context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.VINDICATOR, (entityType) -> new PVindicatorRenderer(context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.EVOKER, (entityType) -> new PIllagerRenderer<> (context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.WITCH, (entityType) -> new PWitchRenderer(context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.DROWNED, (entityType) -> new PDrownedRenderer(context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.PILLAGER, (entityType) -> new PIllagerRenderer<> (context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.RAVAGER, (entityType) -> new PRavagerRenderer(context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.VEX, (entityType) -> new PVexRenderer(context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.PIGLIN, (entityType) -> new PHumanoidRenderer<>(Meshes.PIGLIN, context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.PIGLIN_BRUTE, (entityType) -> new PHumanoidRenderer<>(Meshes.PIGLIN, context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.HOGLIN, (entityType) -> new PHoglinRenderer<> (context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.ZOGLIN, (entityType) -> new PHoglinRenderer<> (context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EntityTypes.ENDER_DRAGON, (entityType) -> new PEnderDragonRenderer());
        this.entityRendererProvider.put(EntityTypes.WITHER, (entityType) -> new PWitherRenderer(context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EpicFightEntityTypes.WITHER_SKELETON_MINION.get(), (entityType) -> new PWitherSkeletonMinionRenderer(context, entityType).initLayerLast(context, entityType));
        this.entityRendererProvider.put(EpicFightEntityTypes.WITHER_GHOST_CLONE.get(), (entityType) -> new WitherGhostCloneRenderer());

        this.firstPersonRenderer = new FirstPersonRenderer(context, EntityTypes.PLAYER);
        this.basicHumanoidRenderer = new PHumanoidRenderer<>(Meshes.BIPED, context, EntityTypes.PLAYER);

        EpicFightClientEventHooks.Registry.ADD_PATCHED_ENTITY.post(new RegisterPatchedRenderersEvent.AddEntity(this.entityRendererProvider, context));

        this.resetRenderers();
    }

    /** Registers every hook of this engine to Fabric API callbacks. */
    public void registerFabricCallbacks() {
        net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT.register(this::onItemTooltip);
        net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry.addFirst(EpicFight.identifier("render_gui_pre"), (graphics, deltaTracker) -> this.renderGuiPre(graphics));
        net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents.AFTER_TRANSLUCENT_FEATURES.register(this::renderAfterLevel);
        net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents.START_MAIN.register(context -> this.renderTickPre());
        net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents.END_MAIN.register(context -> this.renderTickPost());
        net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents.BEFORE_BLOCK_OUTLINE.register((context, outline) -> !this.shouldCancelBlockHighlight());
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.START_CLIENT_TICK.register(client -> this.clientTickPre());
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> this.clientTickPost());
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_LEVEL_TICK.register(this::levelTickPost);
    }

    /**
     * @deprecated Use {@link EpicFightCameraAPI#zoomIn()} instead
     */
    @Deprecated(forRemoval = true)
    public void zoomIn() {
        EpicFightCameraAPI.getInstance().zoomIn();
    }

    /**
     * @deprecated Use {@link EpicFightCameraAPI#zoomOut(int)} instead
     */
    @Deprecated(forRemoval = true)
    public void zoomOut(int zoomOutTicks) {
        EpicFightCameraAPI.getInstance().zoomOut(zoomOutTicks);
    }
}
