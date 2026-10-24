package dev.war.openfight.client.renderer.patched.entity;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import dev.war.openfight.api.animation.AnimationPlayer;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.client.animation.Layer;
import dev.war.openfight.api.client.model.Meshes;
import dev.war.openfight.api.model.Armature;
import dev.war.openfight.client.mesh.WitherMesh;
import dev.war.openfight.client.renderer.entity.WitherGhostRenderer;
import dev.war.openfight.world.capabilities.entitypatch.boss.WitherGhostPatch;
import dev.war.openfight.world.entity.WitherGhostClone;

public class WitherGhostCloneRenderer extends PatchedEntityRenderer<WitherGhostClone, WitherGhostPatch, WitherGhostRenderer, WitherMesh> {
	@Override
	public void render(WitherGhostClone entity, WitherGhostPatch entitypatch, WitherGhostRenderer renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		RenderType renderType = RenderTypes.entityTranslucent(PWitherRenderer.WITHER_INVULNERABLE_LOCATION);
		WitherMesh mesh = this.getMeshProvider(entitypatch).get();
		Armature armature = entitypatch.getArmature();
		float tranparency = entity.isNoAi() ? 0.6F : Mth.sin((entity.tickCount + partialTicks) * 0.025F * Mth.PI) * 0.6F;
		
		poseStack.pushPose();
		this.mulPoseStack(poseStack, armature, entity, entitypatch, partialTicks);
		this.setArmaturePose(entitypatch, armature, partialTicks);
		mesh.draw(poseStack, buffer, renderType, packedLight, 1.0F, 1.0F, 1.0F, tranparency, OverlayTexture.NO_OVERLAY, entitypatch.getArmature(), armature.getPoseMatrices());
		
		if (Minecraft.getInstance().debugEntries.isCurrentlyEnabled(net.minecraft.client.gui.components.debug.DebugScreenEntries.ENTITY_HITBOXES)) {
			for (Layer.Priority priority : Layer.Priority.values()) {
				AnimationPlayer animPlayer = entitypatch.getClientAnimator().getCompositeLayer(priority).animationPlayer;
				float playTime = animPlayer.getPrevElapsedTime() + (animPlayer.getElapsedTime() - animPlayer.getPrevElapsedTime()) * partialTicks;
				animPlayer.getAnimation().get().renderDebugging(poseStack, buffer, entitypatch, playTime, partialTicks);
			}
		}
		
		poseStack.popPose();
	}
	
	@Override
	public AssetAccessor<WitherMesh> getDefaultMesh() {
		return Meshes.WITHER;
	}
}