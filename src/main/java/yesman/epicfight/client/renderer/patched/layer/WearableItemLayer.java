package yesman.epicfight.client.renderer.patched.layer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.asset.JsonAssetLoader;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.api.client.event.types.render.AnimatedArmorTextureEvent;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.transformer.HumanoidModelBaker;
import yesman.epicfight.api.exception.AssetLoadingException;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.compat.MultiBufferSource;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

/**
 * Renders armor on the animated (skinned) humanoid mesh. 26.3 resolves armor through equipment assets
 * ({@link Equippable} + {@link EquipmentClientInfo}), so the layer textures come from there instead of ArmorMaterial.
 */
public class WearableItemLayer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends HumanoidModel<?>, AM extends HumanoidMesh> extends ModelRenderLayer<E, T, M, HumanoidArmorLayer<?, ?, ?>, AM> {
	private static final Map<Identifier, SkinnedMesh> ARMOR_MODELS = new HashMap<> ();
	private static final Map<String, Identifier> EPICFIGHT_OVERRIDING_TEXTURES = new HashMap<> ();

	public static void clearModels() {
		ARMOR_MODELS.values().stream().filter(v -> v != null).forEach(SkinnedMesh::destroy);
		ARMOR_MODELS.clear();
		EPICFIGHT_OVERRIDING_TEXTURES.clear();
	}

	public static void putModel(Identifier rl, SkinnedMesh skinnedMesh) {
		ARMOR_MODELS.computeIfPresent(rl, (key, mesh) -> {
			if (mesh != skinnedMesh) mesh.destroy();
			return mesh;
		});

		ARMOR_MODELS.put(rl, skinnedMesh);
	}

	public static SkinnedMesh getCachedModel(Item item) {
		Identifier key = BuiltInRegistries.ITEM.getKey(item);
		return ARMOR_MODELS.get(key);
	}

	private final boolean firstPersonModel;
	private final EquipmentAssetManager equipmentAssets;
    private static EquipmentAssetManager snapshotAssets;
    public static Identifier getArmorResource(LivingEntity entity,ItemStack stack,EquipmentSlot slot,String suffix) {
        var event=EpicFightClientEventHooks.Render.ANIMATED_ARMOR_TEXTURE.post(new AnimatedArmorTextureEvent(entity,stack,slot,null));
        if(event.getResultLocation()!=null)return event.getResultLocation();
        var equippable=stack.get(net.minecraft.core.component.DataComponents.EQUIPPABLE);
        if(equippable==null || equippable.assetId().isEmpty() || snapshotAssets==null)return null;
        var type=slot==EquipmentSlot.LEGS?EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS:EquipmentClientInfo.LayerType.HUMANOID;
        var layers=snapshotAssets.get(equippable.assetId().get()).getLayers(type);
        return layers.isEmpty()?null:layers.getFirst().getTextureLocation(type);
    }

	public WearableItemLayer(AssetAccessor<AM> meshProvider, boolean firstPersonModel, EntityRendererProvider.Context context) {
		super(meshProvider);

		this.firstPersonModel = firstPersonModel;
		this.equipmentAssets = context.getEquipmentAssets();
        snapshotAssets=this.equipmentAssets;
	}

	private static boolean innerModel(EquipmentSlot slot) {
		return slot == EquipmentSlot.LEGS;
	}

	@Override
	public void renderLayer(T entitypatch, E livingentity, @Nullable HumanoidArmorLayer<?, ?, ?> vanillaLayer, PoseStack poseStack, MultiBufferSource buffers, int packedLight, OpenMatrix4f[] poses, float bob, float yRot, float xRot, float partialTicks) {
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) {
				continue;
			}

			boolean firstPersonChest = false;

			if (entitypatch.isFirstPerson() && this.firstPersonModel) {
				if (slot != EquipmentSlot.CHEST) {
					continue;
				} else {
					firstPersonChest = true;
				}
			}

			if (slot == EquipmentSlot.HEAD && this.firstPersonModel) {
				continue;
			}

			ItemStack itemstack = livingentity.getItemBySlot(slot);
			Equippable equippable = itemstack.get(DataComponents.EQUIPPABLE);

			if (equippable == null || equippable.assetId().isEmpty() || equippable.slot() != slot) {
				continue;
			}

			SkinnedMesh armorMesh = this.getArmorModel(livingentity, itemstack, slot);

			if (armorMesh == null) {
				continue;
			}

			armorMesh.initialize();

			if (firstPersonChest) {
				armorMesh.getAllParts().forEach(part -> part.setHidden(true));

				if (armorMesh.hasPart("leftArm")) {
					armorMesh.getPart("leftArm").setHidden(false);
				}

				if (armorMesh.hasPart("rightArm")) {
					armorMesh.getPart("rightArm").setHidden(false);
				}
			}

			poseStack.pushPose();
			float head = 0.0F;

			if (slot == EquipmentSlot.HEAD) {
				poseStack.translate(0.0D, head * 0.055D, 0.0D);
			}

			this.renderEquipment(entitypatch, livingentity, itemstack, equippable.assetId().get(), slot, armorMesh, poseStack, buffers, packedLight, poses);
			poseStack.popPose();
		}
	}

	/** The equivalent of EquipmentLayerRenderer#renderLayers, drawing the skinned armor mesh instead of the vanilla model. */
	private void renderEquipment(T entitypatch, E livingentity, ItemStack itemstack, ResourceKey<EquipmentAsset> assetId, EquipmentSlot slot, SkinnedMesh armorMesh, PoseStack poseStack, MultiBufferSource buffers, int packedLight, OpenMatrix4f[] poses) {
		Armature armature = entitypatch.getArmature();
		EquipmentClientInfo.LayerType layerType = innerModel(slot) ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS : EquipmentClientInfo.LayerType.HUMANOID;
		EquipmentClientInfo equipmentInfo = this.equipmentAssets.get(assetId);
		List<EquipmentClientInfo.Layer> layers = equipmentInfo.getLayers(layerType);

		if (layers.isEmpty()) {
			return;
		}

		AnimatedArmorTextureEvent textureEvent = EpicFightClientEventHooks.Render.ANIMATED_ARMOR_TEXTURE.post(new AnimatedArmorTextureEvent(livingentity, itemstack, slot, null));
		Identifier overriddenTexture = textureEvent.getResultLocation();
		int dyeColor = DyedItemColor.getOrDefault(itemstack, 0);
		boolean hasFoil = itemstack.hasFoil();
		net.minecraft.world.item.equipment.trim.ArmorTrim trim = itemstack.get(DataComponents.TRIM);
		boolean renderShaderGlint = hasFoil && trim == null;

		for (EquipmentClientInfo.Layer layer : layers) {
			int color = getColorForLayer(layer, dyeColor);

			if (color == 0) {
				continue;
			}

			Identifier texture = overriddenTexture != null
				? overriddenTexture
				: ParseUtil.tryGetOr(() -> armorMesh.getRenderProperties().customTexturePath(), () -> layer.getTextureLocation(layerType));
			RenderType renderType = renderShaderGlint ? RenderTypes.armorCutoutNoCullGlint(texture) : RenderTypes.armorCutoutNoCull(texture);
			armorMesh.draw(poseStack, buffers, renderType, packedLight, ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color), 1.0F, OverlayTexture.NO_OVERLAY, armature, poses);
			renderShaderGlint = false;
		}

		// Armor trims are drawn from the trim pattern texture; palette remapping of trim colors is not replicated.
		if (trim != null) {
			Identifier patternTexture = trim.pattern().value().assetId().withPath(path -> "textures/trims/entity/" + layerType.trimAssetPrefix().replace("trims/entity/", "") + "/" + path + ".png");
			Minecraft minecraft = Minecraft.getInstance();

			if (minecraft.getResourceManager().getResource(patternTexture).isPresent()) {
				RenderType trimType = RenderTypes.armorTrim(patternTexture, trim.pattern().value().decal());
				armorMesh.draw(poseStack, buffers, trimType, packedLight, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY, armature, poses);
			}

			if (hasFoil) {
				armorMesh.draw(poseStack, buffers, RenderTypes.trimmedArmorGlint(), packedLight, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY, armature, poses);
			}
		}
	}

	private static int getColorForLayer(EquipmentClientInfo.Layer layer, int dyeColor) {
		Optional<EquipmentClientInfo.Dyeable> dyeable = layer.dyeable();

		if (dyeable.isPresent()) {
			int colorWhenUndyed = dyeable.get().colorWhenUndyed().map(ARGB::opaque).orElse(0);
			return dyeColor != 0 ? dyeColor : colorWhenUndyed;
		}

		return -1;
	}

	@Nullable
	private SkinnedMesh getArmorModel(E entityliving, ItemStack itemstack, EquipmentSlot slot) {
		Item item = itemstack.getItem();
		Identifier registryName = BuiltInRegistries.ITEM.getKey(item);

		if (ARMOR_MODELS.containsKey(registryName) && !yesman.epicfight.client.events.engine.RenderEngine.getInstance().shouldRenderVanillaModel()) {
			return ARMOR_MODELS.get(registryName);
		}

		ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
		Identifier rl = Identifier.fromNamespaceAndPath(registryName.getNamespace(), "animmodels/armor/" + registryName.getPath() + ".json");
		SkinnedMesh skinnedMesh = null;

		if (resourceManager.getResource(rl).isPresent()) {
			try {
				JsonAssetLoader modelLoader = new JsonAssetLoader(resourceManager, rl);
				skinnedMesh = modelLoader.loadSkinnedMesh(SkinnedMesh::new);
			} catch (AssetLoadingException e) {
				e.printStackTrace();
				skinnedMesh = null;
			}
		} else {
			// Fabric has no replaced client armor models, so the vanilla-model transformation path is never needed.
			skinnedMesh = HumanoidModelBaker.bakeArmor(entityliving, itemstack, slot, this.mesh.get());
		}

		putModel(registryName, skinnedMesh);

		return skinnedMesh;
	}
}
