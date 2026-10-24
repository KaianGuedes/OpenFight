package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.PlayerModelType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import yesman.epicfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Vector4f;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.api.client.event.types.registry.RegisterPatchedRenderersEvent;
import yesman.epicfight.api.client.event.types.render.PrepareModelEvent;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec2i;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.renderer.LayerRenderer;
import yesman.epicfight.client.renderer.patched.layer.LayerUtil;
import yesman.epicfight.client.renderer.patched.layer.PatchedLayer;
import yesman.epicfight.client.renderer.patched.layer.RenderOriginalModelLayer;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.mixin.client.MixinLivingEntityRenderer;
import yesman.epicfight.client.renderer.compat.EpicRenderContext;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.LightCoordsUtil;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

public abstract class PatchedLivingEntityRenderer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<?>, R extends LivingEntityRenderer<E, ?, ?>, AM extends SkinnedMesh> extends PatchedEntityRenderer<E, T, R, AM> implements LayerRenderer<E, T, M> {
	protected final Map<Class<?>, PatchedLayer<E, T, M, ? extends RenderLayer<?, ?>>> patchedLayers = Maps.newHashMap();
	protected final List<PatchedLayer<E, T, M, ? extends RenderLayer<?, ?>>> customLayers = Lists.newArrayList();
	
	public PatchedLivingEntityRenderer(EntityRendererProvider.Context context, EntityType<?> entityType) {
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
	
	@SuppressWarnings("unchecked")
	public PatchedLivingEntityRenderer<E, T, M, R, AM> initLayerLast(EntityRendererProvider.Context context, EntityType<?> entityType) {
		List<RenderLayer<?, ?>> vanillaLayers = null;
		
		if (entityType == EntityTypes.PLAYER) {
			if (context.getEntityRenderDispatcher().playerRenderers.get(PlayerModelType.WIDE) instanceof LivingEntityRenderer livingentityrenderer) {
				vanillaLayers = livingentityrenderer.layers;
			}
		} else {
			if (context.getEntityRenderDispatcher().renderers.get(entityType) instanceof LivingEntityRenderer livingentityrenderer) {
				vanillaLayers = livingentityrenderer.layers;
			}
		}
		
		if (vanillaLayers != null) {
			for (RenderLayer<?, ?> layer : vanillaLayers) {
				Class<?> layerClass = layer.getClass();
				
				if (layerClass.isAnonymousClass()) {
					layerClass = layer.getClass().getSuperclass();
				}
				
				if (this.patchedLayers.containsKey(layerClass)) {
					continue;
				}
				
				this.addPatchedLayer(layerClass, new RenderOriginalModelLayer<> ("Root", new Vec3f(0.0F, this.getDefaultLayerHeightCorrection(), 0.0F), new Vec3f(0.0F, 0.0F, 0.0F)));
			}
		}
		
		return this;
	}
	
	@Override
	public void render(E entity, T entitypatch, R renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		super.render(entity, entitypatch, renderer, buffer, poseStack, packedLight, partialTicks);
		
		if (!(EpicRenderContext.state() instanceof LivingEntityRenderState state)) {
			return;
		}
		
		MixinLivingEntityRenderer livingEntityRendererAccessor = (MixinLivingEntityRenderer)renderer;
		
		boolean isVisible = livingEntityRendererAccessor.invokeIsBodyVisible(state);
		boolean isVisibleToPlayer = !isVisible && !state.isInvisibleToPlayer;
		boolean isGlowing = state.appearsGlowing();
		RenderType renderType = livingEntityRendererAccessor.invokeGetRenderType(state, isVisible, isVisibleToPlayer, isGlowing);
		Armature armature = entitypatch.getArmature();
		poseStack.pushPose();
		this.mulPoseStack(poseStack, armature, entity, entitypatch, partialTicks);
		this.prepareVanillaModel(entity, state, renderer, partialTicks);
		this.setArmaturePose(entitypatch, armature, partialTicks);
		
		if (renderType != null) {
			AM mesh = this.getMeshProvider(entitypatch).get();
			this.prepareModel(mesh, entity, entitypatch, renderer);
			PrepareModelEvent prepareModelEvent = new PrepareModelEvent(this, mesh, entitypatch, buffer, poseStack, packedLight, partialTicks);

			if (!EpicFightClientEventHooks.Render.PREPARE_MODEL_TO_RENDER.post(prepareModelEvent).isCanceled()) {
				Vector4f color = new Vector4f(1.0F, 1.0F, 1.0F, isVisibleToPlayer ? 0.15F : 1.0F);
				entitypatch.getEntityDecorations().modifyColor(color, partialTicks);
				
				int blockLight = LightCoordsUtil.block(packedLight);
				int skyLight = LightCoordsUtil.sky(packedLight);
				Vec2i lightUv = new Vec2i(blockLight, skyLight);
				entitypatch.getEntityDecorations().modifyLight(lightUv, partialTicks);
				int modifiedLight = LightCoordsUtil.pack(lightUv.x, lightUv.y);
				mesh.draw(poseStack, buffer, renderType, modifiedLight, color.x(), color.y(), color.z(), color.w(), this.getOverlayCoord(entity, entitypatch, partialTicks), armature, armature.getPoseMatrices());
				
				entitypatch.getEntityDecorations().listDecorationOverlays().forEach(decorationOverlay -> {
					if (decorationOverlay.shouldRender()) {
						Vector4f overlayColor = decorationOverlay.color(partialTicks);
						mesh.draw(poseStack, buffer, decorationOverlay.getRenderType(), modifiedLight, overlayColor.x(), overlayColor.y(), overlayColor.z(), overlayColor.w(), OverlayTexture.NO_OVERLAY, armature, armature.getPoseMatrices());
					}
				});
			}
		}
		
		if (!entity.isSpectator()) {
			this.renderLayer(renderer, entitypatch, entity, state, armature.getPoseMatrices(), buffer, poseStack, packedLight, partialTicks);
		}
		
		if (renderType != null) {
			if (Minecraft.getInstance().debugEntries.isCurrentlyEnabled(net.minecraft.client.gui.components.debug.DebugScreenEntries.ENTITY_HITBOXES)) {
				entitypatch.getClientAnimator().renderDebuggingInfoForAllLayers(poseStack, buffer, partialTicks);
			}
		}
		
		poseStack.popPose();
		
		// Everything written into the buffer source is submitted as one custom geometry batch per render type
		buffer.endBatch();
	}
	
	/** Poses the vanilla model from the render state so that vanilla layers that rely on it keep working. */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void prepareVanillaModel(E entity, LivingEntityRenderState state, LivingEntityRenderer<E, ?, ?> renderer, float partialTicks) {
		((EntityModel)renderer.getModel()).setupAnim(state);
	}
	
	protected void prepareModel(AM mesh, E entity, T entitypatch, R renderer) {
		mesh.initialize();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void renderLayer(LivingEntityRenderer<E, ?, ?> renderer, T entitypatch, E entity, LivingEntityRenderState state, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		float bob = state.ageInTicks;
		float yRot = state.yRot;
		float xRot = state.xRot;
		
		for (RenderLayer<?, ?> layer : renderer.layers) {
			Class<?> layerClass = layer.getClass();
			
			if (layerClass.isAnonymousClass()) {
				layerClass = layerClass.getSuperclass();
			}
			
			if (this.patchedLayers.containsKey(layerClass)) {
				this.patchedLayers.get(layerClass).renderLayer(entity, entitypatch, (RenderLayer)layer, poseStack, buffer, packedLight, poses, bob, yRot, xRot, partialTicks);
			}
		}
		
		for (PatchedLayer<E, T, M, ? extends RenderLayer<?, ?>> patchedLayer : this.customLayers) {
			patchedLayer.renderLayer(entity, entitypatch, null, poseStack, buffer, packedLight, poses, bob, yRot, xRot, partialTicks);
		}
	}
	
	protected int getOverlayCoord(E entity, T entitypatch, float partialTicks) {
		int initU = 0;
		int initV = OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0);
		
		Vec2i coord = new Vec2i(initU, initV);
		entitypatch.getEntityDecorations().modifyOverlay(coord, partialTicks);
		
		return OverlayTexture.pack(coord.x, coord.y);
	}
	
	@Override
	public void mulPoseStack(PoseStack poseStack, Armature armature, E entity, T entitypatch, float partialTicks) {
		super.mulPoseStack(poseStack, armature, entity, entitypatch, partialTicks);
        
        if (entity.isCrouching()) {
			poseStack.translate(0.0D, 0.15D, 0.0D);
		}
	}
	
	@Override
	public void addPatchedLayer(Class<?> originalLayerClass, PatchedLayer<E, T, M, ? extends RenderLayer<?, ?>> patchedLayer) {
		this.patchedLayers.putIfAbsent(originalLayerClass, patchedLayer);
	}
	
	/**
	 * Use this method in {@link RegisterPatchedRenderersEvent.ModifyEntity}}
	 */
	public void addPatchedLayerAlways(Class<?> originalLayerClass, PatchedLayer<E, T, M, ? extends RenderLayer<?, ?>> patchedLayer) {
		this.patchedLayers.put(originalLayerClass, patchedLayer);
	}
	
	@Override
	public void addCustomLayer(PatchedLayer<E, T, M, ? extends RenderLayer<?, ?>> patchedLayer) {
		this.customLayers.add(patchedLayer);
	}
	
	protected float getDefaultLayerHeightCorrection() {
		return 1.15F;
	}
}
