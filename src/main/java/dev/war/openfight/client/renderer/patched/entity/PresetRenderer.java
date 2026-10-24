package dev.war.openfight.client.renderer.patched.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.client.event.EpicFightClientEventHooks;
import dev.war.openfight.api.client.event.types.render.PrepareModelEvent;
import dev.war.openfight.api.client.model.SkinnedMesh;
import dev.war.openfight.api.model.Armature;
import dev.war.openfight.api.utils.math.MathUtils;
import dev.war.openfight.api.utils.math.OpenMatrix4f;
import dev.war.openfight.client.renderer.LayerRenderer;
import dev.war.openfight.client.renderer.patched.layer.LayerUtil;
import dev.war.openfight.client.renderer.patched.layer.PatchedLayer;
import dev.war.openfight.main.EpicFightMod;
import dev.war.openfight.mixin.client.MixinLivingEntityRenderer;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

/**
 * This class is for LivingEntity with renderer that doesn't extend LivingEntityRenderer (e.g. Geckolib renderer based entities)
 */
public class PresetRenderer extends PatchedEntityRenderer<LivingEntity, LivingEntityPatch<LivingEntity>, EntityRenderer<LivingEntity, ?>, SkinnedMesh> implements LayerRenderer<LivingEntity, LivingEntityPatch<LivingEntity>, EntityModel<?>> {
	private final LivingEntityRenderer<LivingEntity, ?, ?> presetRenderer;
	protected final Map<Class<?>, PatchedLayer<LivingEntity, LivingEntityPatch<LivingEntity>, EntityModel<?>, ? extends RenderLayer<?, ?>>> patchedLayers = Maps.newHashMap();
	protected final List<PatchedLayer<LivingEntity, LivingEntityPatch<LivingEntity>, EntityModel<?>, ? extends RenderLayer<?, ?>>> customLayers = Lists.newArrayList();
	protected final AssetAccessor<SkinnedMesh> mesh;
	
	public PresetRenderer(EntityRendererProvider.Context context, EntityType<?> entityType, LivingEntityRenderer<LivingEntity, ?, ?> renderer, AssetAccessor<SkinnedMesh> mesh) {
		this.presetRenderer = renderer;
		this.mesh = mesh;
		
		Identifier type = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
		FileToIdConverter filetoidconverter = FileToIdConverter.json("animated_layers/" + type.getPath());
		List<Pair<Identifier, JsonElement>> layers = Lists.newArrayList();
		
		for (Map.Entry<Identifier, Resource> entry : filetoidconverter.listMatchingResources(context.getResourceManager()).entrySet()) {
			Reader reader = null;
			
			try {
				reader = entry.getValue().openAsReader();
				JsonElement jsonelement = GsonHelper.fromJson(new GsonBuilder().create(), reader, JsonElement.class);
				layers.add(Pair.of(entry.getKey(), jsonelement));
			} catch (IllegalArgumentException | IOException | JsonParseException jsonparseexception) {
				EpicFightMod.LOGGER.error("Failed to parse layer file {} for {}", entry.getKey(), type);
				jsonparseexception.printStackTrace();
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
				}
			}
		}
		
		LayerUtil.addLayer(this, entityType, layers);
	}
	
	@Override
	public void render(LivingEntity entity, LivingEntityPatch<LivingEntity> entitypatch, EntityRenderer<LivingEntity, ?> renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		super.render(entity, entitypatch, renderer, buffer, poseStack, packedLight, partialTicks);
		
		Minecraft mc = Minecraft.getInstance();
		MixinLivingEntityRenderer livingEntityRendererAccessor = (MixinLivingEntityRenderer)this.presetRenderer;
		
		if (!(dev.war.openfight.client.renderer.compat.EpicRenderContext.state() instanceof net.minecraft.client.renderer.entity.state.LivingEntityRenderState state)) {
			return;
		}
		
		boolean isVisible = livingEntityRendererAccessor.invokeIsBodyVisible(state);
		boolean isVisibleToPlayer = !isVisible && !state.isInvisibleToPlayer;
		boolean isGlowing = state.appearsGlowing();
		RenderType renderType = livingEntityRendererAccessor.invokeGetRenderType(state, isVisible, isVisibleToPlayer, isGlowing);
		Armature armature = entitypatch.getArmature();
		poseStack.pushPose();
		this.mulPoseStack(poseStack, armature, entity, entitypatch, partialTicks);
		this.setArmaturePose(entitypatch, armature, partialTicks);
		
		if (renderType != null) {
		    this.prepareVanillaModel(entity, state, this.presetRenderer, partialTicks);
			SkinnedMesh mesh = this.getMeshProvider(entitypatch).get();
			this.prepareModel(mesh, entity, entitypatch, this.presetRenderer);
			PrepareModelEvent prepareModelEvent = new PrepareModelEvent(this, mesh, entitypatch, buffer, poseStack, packedLight, partialTicks);

            if (!EpicFightClientEventHooks.Render.PREPARE_MODEL_TO_RENDER.post(prepareModelEvent).isCanceled()) {
                mesh.draw(poseStack, buffer, renderType, packedLight, 1.0F, 1.0F, 1.0F, isVisibleToPlayer ? 0.15F : 1.0F, this.getOverlayCoord(entity, entitypatch, partialTicks), armature, armature.getPoseMatrices());
            }
		}
		
		if (!entity.isSpectator()) {
			this.renderLayer(this.presetRenderer, entitypatch, entity, state, armature.getPoseMatrices(), buffer, poseStack, packedLight, partialTicks);
		}
		
		if (renderType != null) {
			if (Minecraft.getInstance().debugEntries.isCurrentlyEnabled(net.minecraft.client.gui.components.debug.DebugScreenEntries.ENTITY_HITBOXES)) {
				entitypatch.getClientAnimator().renderDebuggingInfoForAllLayers(poseStack, buffer, partialTicks);
			}
		}
		
		poseStack.popPose();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void prepareVanillaModel(LivingEntity entityIn, net.minecraft.client.renderer.entity.state.LivingEntityRenderState state, LivingEntityRenderer<LivingEntity, ?, ?> renderer, float partialTicks) {
		((EntityModel)renderer.getModel()).setupAnim(state);
	}
	
	protected void prepareModel(SkinnedMesh mesh, LivingEntity entity, LivingEntityPatch<LivingEntity> entitypatch, LivingEntityRenderer<LivingEntity, ?, ?> renderer) {
		mesh.initialize();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void renderLayer(LivingEntityRenderer<LivingEntity, ?, ?> renderer, LivingEntityPatch<LivingEntity> entitypatch, LivingEntity entity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState state, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		float bob = state.ageInTicks;
		float f2 = state.yRot;
		float f7 = state.xRot;
		
		for (RenderLayer<?, ?> layer : renderer.layers) {
			Class<?> layerClass = layer.getClass();
			
			if (layerClass.isAnonymousClass()) {
				layerClass = layerClass.getSuperclass();
			}
			
			if (this.patchedLayers.containsKey(layerClass)) {
				this.patchedLayers.get(layerClass).renderLayer(entity, entitypatch, (RenderLayer)layer, poseStack, buffer, packedLight, poses, bob, f2, f7, partialTicks);
			}
		}
		
		for (PatchedLayer<LivingEntity, LivingEntityPatch<LivingEntity>, EntityModel<?>, ? extends RenderLayer<?, ?>> patchedLayer : this.customLayers) {
			patchedLayer.renderLayer(entity, entitypatch, null, poseStack, buffer, packedLight, poses, bob, f2, f7, partialTicks);
		}
	}
	
	protected int getOverlayCoord(LivingEntity entity, LivingEntityPatch<LivingEntity> entitypatch, float partialTicks) {
		return OverlayTexture.pack(0, OverlayTexture.v(entity.hurtTime > 5));
	}
	
	@Override
	public void addPatchedLayer(Class<?> originalLayerClass, PatchedLayer<LivingEntity, LivingEntityPatch<LivingEntity>, EntityModel<?>, ? extends RenderLayer<?, ?>> patchedLayer) {
		this.patchedLayers.putIfAbsent(originalLayerClass, patchedLayer);
	}
	
	@Override
	public void addCustomLayer(PatchedLayer<LivingEntity, LivingEntityPatch<LivingEntity>, EntityModel<?>, ? extends RenderLayer<?, ?>> patchedLayer) {
		this.customLayers.add(patchedLayer);
	}
	
	@Override
	public AssetAccessor<SkinnedMesh> getDefaultMesh() {
		return this.mesh;
	}
}
