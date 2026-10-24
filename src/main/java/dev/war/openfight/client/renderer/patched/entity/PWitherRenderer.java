package dev.war.openfight.client.renderer.patched.entity;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.monster.wither.WitherBossModel;
import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WitherBossRenderer;
import net.minecraft.client.renderer.entity.layers.WitherArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.client.model.Meshes;
import dev.war.openfight.api.model.Armature;
import dev.war.openfight.client.mesh.WitherMesh;
import dev.war.openfight.client.renderer.patched.layer.PatchedWitherArmorLayer;
import dev.war.openfight.mixin.client.MixinLivingEntityRenderer;
import dev.war.openfight.world.capabilities.entitypatch.boss.WitherPatch;

public class PWitherRenderer extends PatchedLivingEntityRenderer<WitherBoss, WitherPatch, WitherBossModel, WitherBossRenderer, WitherMesh> {
	public static final Identifier WITHER_INVULNERABLE_LOCATION = Identifier.withDefaultNamespace("textures/entity/wither/wither_invulnerable.png");
	private static final Identifier WITHER_LOCATION = Identifier.withDefaultNamespace("textures/entity/wither/wither.png");
	
	public PWitherRenderer(EntityRendererProvider.Context context, EntityType<?> entityType) {
		super(context, entityType);
		this.addPatchedLayer(WitherArmorLayer.class, new PatchedWitherArmorLayer());
	}
	
	@Override
	public void render(WitherBoss entity, WitherPatch entitypatch, WitherBossRenderer renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		Minecraft mc = Minecraft.getInstance();
		MixinLivingEntityRenderer livingEntityRendererAccessor = (MixinLivingEntityRenderer)renderer;
		net.minecraft.client.renderer.entity.state.LivingEntityRenderState state = (net.minecraft.client.renderer.entity.state.LivingEntityRenderState)dev.war.openfight.client.renderer.compat.EpicRenderContext.state();
		boolean isVisible = this.isVisible(entity, entitypatch);
		boolean isVisibleToPlayer = !isVisible && !state.isInvisibleToPlayer;
		boolean isGlowing = state.appearsGlowing();
		RenderType renderType = livingEntityRendererAccessor.invokeGetRenderType(state, isVisible, isVisibleToPlayer, isGlowing);
		WitherMesh mesh = this.getMeshProvider(entitypatch).get();
		Armature armature = entitypatch.getArmature();
		
		poseStack.pushPose();
		this.mulPoseStack(poseStack, armature, entity, entitypatch, partialTicks);
		this.setArmaturePose(entitypatch, armature, partialTicks);
		
		if (renderType != null) {
			int transparencyCount = entitypatch.getTransparency();
			
			if (transparencyCount == 0) {
				if (!entitypatch.isGhost()) {
					mesh.draw(poseStack, buffer, renderType, packedLight, 1.0F, 1.0F, 1.0F, 1.0F, this.getOverlayCoord(entity, entitypatch, partialTicks), entitypatch.getArmature(), armature.getPoseMatrices());
				}
			} else {
				float transparency = (Math.abs(transparencyCount) + partialTicks) / 41.0F;
				
				if (transparencyCount < 0) {
					transparency = 1.0F - transparency;
				}
				
				mesh.draw(poseStack, buffer, RenderTypes.entityTranslucent(WITHER_LOCATION), packedLight, 1.0F, 1.0F, 1.0F, transparency, OverlayTexture.NO_OVERLAY, entitypatch.getArmature(), armature.getPoseMatrices());
				mesh.draw(poseStack, buffer, RenderTypes.entityTranslucent(WITHER_INVULNERABLE_LOCATION), packedLight, 1.0F, 1.0F, 1.0F, Mth.sin(transparency * 3.1415F), OverlayTexture.NO_OVERLAY, entitypatch.getArmature(), armature.getPoseMatrices());
			}
			
			this.renderLayer(renderer, entitypatch, entity, state, armature.getPoseMatrices(), buffer, poseStack, packedLight, partialTicks);
			
			if (Minecraft.getInstance().debugEntries.isCurrentlyEnabled(net.minecraft.client.gui.components.debug.DebugScreenEntries.ENTITY_HITBOXES)) {
				entitypatch.getClientAnimator().renderDebuggingInfoForAllLayers(poseStack, buffer, partialTicks);
			}
		}
		
		poseStack.popPose();
	}
	
	protected boolean isVisible(WitherBoss witherboss, WitherPatch witherpatch) {
		return !witherpatch.isGhost() || witherpatch.getTransparency() != 0;
	}
	
	@Override
	public void mulPoseStack(PoseStack poseStack, Armature armature, WitherBoss witherboss, WitherPatch entitypatch, float partialTicks) {
		super.mulPoseStack(poseStack, armature, witherboss, entitypatch, partialTicks);
        
		float f = 1.0F;
		int i = witherboss.getInvulnerableTicks();
		
		if (i > 0) {
			f -= ((float) i - partialTicks) / 440.0F;
		}

		poseStack.scale(f, f, f);
	}
	
	@Override
	public AssetAccessor<WitherMesh> getDefaultMesh() {
		return Meshes.WITHER;
	}
}