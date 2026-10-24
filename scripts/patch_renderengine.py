import re
from pathlib import Path
p=Path(__file__).resolve().parents[1]/'src/main/java/yesman/epicfight/client/events/engine/RenderEngine.java'
raw=p.read_bytes().decode('utf-8');crlf='\r\n' in raw;s=raw.replace('\r\n','\n')

# imports
s=re.sub(r'import net\.neoforged[^\n]*\n','',s)
s=re.sub(r'import net\.sweenus[^\n]*\n','',s)
s=s.replace('import com.mojang.blaze3d.systems.RenderSystem;\n','')
s=s.replace('public class RenderEngine implements IEventBasedEngine {','public class RenderEngine {')

# renderEntityArmatureModel signature
s=s.replace('public void renderEntityArmatureModel(LivingEntity livingEntity, LivingEntityPatch<?> entitypatch, EntityRenderer<? extends Entity> renderer,','public void renderEntityArmatureModel(LivingEntity livingEntity, LivingEntityPatch<?> entitypatch, EntityRenderer<?, ?> renderer,')

# freeUnusedSources / clear: no recordRenderCall
s=s.replace('''        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                EpicFightRenderTypes.freeUnusedWorldRenderTypes();
            });
        } else {
            EpicFightRenderTypes.freeUnusedWorldRenderTypes();
        }''','''        EpicFightRenderTypes.freeUnusedWorldRenderTypes();''')
s=s.replace('''        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                this.resetRenderers();
                EpicFightRenderTypes.clearWorldRenderTypes();
            });
        } else {
            this.resetRenderers();
            EpicFightRenderTypes.clearWorldRenderTypes();
        }''','''        this.resetRenderers();
        EpicFightRenderTypes.clearWorldRenderTypes();''')

# registerCustomEntityRenderer: Context construction
a=s.index('        EntityRenderDispatcher erd = this.minecraft.getEntityRenderDispatcher();')
b=s.index('        if ("player".equals(rendererName)) {')
s=s[:a]+'        EntityRendererProvider.Context context = this.rendererContext;\n\n'+s[b:]
s=s.replace('BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(rendererName))','BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(rendererName))')
s=s.replace('new PresetRenderer(context, entityType, (LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>)context.getEntityRenderDispatcher().renderers.get(presetEntityType), patchedLivingEntityRenderer.getDefaultMesh())','new PresetRenderer(context, entityType, (LivingEntityRenderer<LivingEntity, ?, ?>)context.getEntityRenderDispatcher().renderers.get(presetEntityType), patchedLivingEntityRenderer.getDefaultMesh())')
s=s.replace('    private FakeBlockRenderer fakeBlockRenderer;','    private FakeBlockRenderer fakeBlockRenderer;\n    private EntityRendererProvider.Context rendererContext;')

# replace everything from the Forge listeners to end
a=s.index('    /******************\n     * Forge EventHook listeners\n     ******************/')
head=s[:a]
tail_old=s[a:]

# extract the tooltip body for conversion
ta=tail_old.index('    private boolean noSimplyTooltipsSupport')
tb=tail_old.index('    private static final Vector3f CAMERA_ROTATION_EULER')
tooltip=tail_old[ta:tb]
tooltip=re.sub(r'    private boolean noSimplyTooltipsSupport\(ItemTooltipEvent event\) \{.*?\n    \}\n\n','',tooltip,flags=re.S)
tooltip=tooltip.replace('private void epicfight$itemTooltip(ItemTooltipEvent event) {','public void onItemTooltip(ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipFlag tooltipFlag, List<Component> tooltipLines) {')
tooltip=tooltip.replace('event.getEntity() != null && event.getEntity().level().isClientSide','this.minecraft.player != null && this.minecraft.player.level().isClientSide()')
tooltip=tooltip.replace('getUnparameterizedEntityPatch(event.getEntity(), LocalPlayerPatch.class)','getUnparameterizedEntityPatch(this.minecraft.player, LocalPlayerPatch.class)')
tooltip=tooltip.replace('event.getItemStack()','itemStack').replace('event.getToolTip()','tooltipLines')
tooltip=tooltip.replace(' && noSimplyTooltipsSupport(event)','').replace('noSimplyTooltipsSupport(event)','true')
tooltip=tooltip.replace('EpicFightKeyMappings.WEAPON_INNATE_SKILL_TOOLTIP.getKey().getDisplayName()','EpicFightKeyMappings.WEAPON_INNATE_SKILL_TOOLTIP.getTranslatedKeyMessage()')

new_tail='''    /******************
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

            if (!this.minecraft.options.hideGui && !EpicFightGameRules.DISABLE_ENTITY_UI.getRuleValue(livingentity.level())) {
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

''' + tooltip + '''    private static final Vector3f CAMERA_ROTATION_EULER = new Vector3f();
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

            this.overlayManager.renderTick(graphics.guiWidth(), graphics.guiHeight());

            //Shows the epic fight version in beta
            this.versionNotifier.render(graphics, true);
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
'''

# convert the addLayers body from old tail
ba=tail_old.index('        this.entityRendererProvider.clear();')
bb=tail_old.index('        this.resetRenderers();\n    }\n    /**********************\n     * Mod EventHook listeners end')
body=tail_old[ba:bb]+'        this.resetRenderers();\n    }\n'
rest='''
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
'''
s=head+new_tail+body+rest
imports='''import net.minecraft.client.Camera;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.TooltipFlag;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import yesman.epicfight.client.renderer.compat.EpicRenderContext;
import yesman.epicfight.client.renderer.compat.StateEntityAccess;
import yesman.epicfight.client.renderer.compat.SubmitBufferSource;
'''
s=s.replace('import java.util.*;',imports+'import java.util.*;',1)
if crlf:s=s.replace('\n','\r\n')
p.write_bytes(s.encode('utf-8'))
print('done')
