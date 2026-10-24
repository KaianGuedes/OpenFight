package dev.war.openfight.client.renderer.patched.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import dev.war.openfight.api.animation.Pose;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.client.model.SkinnedMesh;
import dev.war.openfight.api.model.Armature;
import dev.war.openfight.api.utils.math.MathUtils;
import dev.war.openfight.api.utils.math.OpenMatrix4f;
import dev.war.openfight.api.utils.math.QuaternionUtils;
import dev.war.openfight.client.renderer.compat.EpicRenderContext;
import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import dev.war.openfight.mixin.client.MixinEntityRenderer;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public abstract class PatchedEntityRenderer<E extends LivingEntity, T extends LivingEntityPatch<E>, R extends EntityRenderer<E, ?>, AM extends SkinnedMesh> {
	/**
	 * Called from the entity submit hook. The vertex data written into {@code buffer} is submitted as custom geometry
	 * once the render call finishes; {@link EpicRenderContext} provides the vanilla render state and submit collector.
	 */
	public void render(E entity, T entitypatch, R renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTick) {
		EntityRenderState state = EpicRenderContext.state();
		SubmitNodeCollector collector = EpicRenderContext.collector();
		CameraRenderState camera = EpicRenderContext.camera();
		
		if (state == null || collector == null) {
			return;
		}
		
		// Leashes and name tags are part of EntityRenderer#submit in 26.3, which Epic Fight replaces for patched entities.
		if (state.leashStates != null) {
			for (EntityRenderState.LeashState leashState : state.leashStates) {
				collector.submitLeash(poseStack, leashState);
			}
		}
		
		((MixinEntityRenderer)renderer).invokeSubmitNameDisplay(state, poseStack, collector, camera);
	}
	
	protected static boolean isUpsideDown(LivingEntity entity) {
		Component name = entity.getCustomName();
		return name != null && ("Dinnerbone".equals(name.getString()) || "Grumm".equals(name.getString()));
	}
	
	public void mulPoseStack(PoseStack poseStack, Armature armature, E entity, T entitypatch, float partialTicks) {
		OpenMatrix4f modelMatrix = entitypatch.getModelMatrix(partialTicks);
        poseStack.rotate(QuaternionUtils.YP.rotationDegrees(180.0F));
        MathUtils.mulStack(poseStack, modelMatrix);
        
        if (isUpsideDown(entity)) {
        	poseStack.translate(0.0D, entity.getBbHeight() + 0.1F, 0.0D);
        	poseStack.rotate(QuaternionUtils.ZP.rotationDegrees(180.0F));
		}
	}
	
	public void setArmaturePose(T entitypatch, Armature armature, float partialTicks) {
		Pose pose = entitypatch.getAnimator().getPose(partialTicks);
        this.setJointTransforms(entitypatch, armature, pose, partialTicks);
        armature.setPose(pose);
	}
	
	public AssetAccessor<AM> getMeshProvider(T entitypatch) {
		return this.getDefaultMesh();
	}
	
	public abstract AssetAccessor<AM> getDefaultMesh();
	
	/**
	 * Developers shouldn't implement any interpolations in this method
	 * Use {@link LivingEntityPatch#poseTick} instead
	 */
	public void setJointTransforms(T entitypatch, Armature armature, Pose pose, float partialTicks) {
	}
}
