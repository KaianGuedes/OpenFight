package dev.war.openfight.client.renderer.patched.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.EntityModel;
import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import dev.war.openfight.api.utils.math.MathUtils;
import dev.war.openfight.api.utils.math.OpenMatrix4f;
import dev.war.openfight.api.utils.math.Vec3f;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public class RenderOriginalModelLayer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<?>> extends PatchedLayer<E, T, M, RenderLayer<?, ?>> {
	private final String parentJoint;
	private final Vec3f vec;
	private final Vec3f rot;
	
	public RenderOriginalModelLayer(String parentJoint, Vec3f vec, Vec3f rot) {
		this.parentJoint = parentJoint;
		this.vec = vec;
		this.rot = rot;
	}
	
	@Override
	protected void renderLayer(T entitypatch, E entityliving, RenderLayer<?, ?> vanillaLayer, PoseStack poseStack, MultiBufferSource buffer, int packedLight, OpenMatrix4f[] poses, float bob, float yRot, float xRot, float partialTicks) {
		OpenMatrix4f modelMatrix = poses[entitypatch.getArmature().searchJointByName(this.parentJoint).getId()];
		
		poseStack.pushPose();
		MathUtils.mulStack(poseStack, modelMatrix);
		poseStack.translate(this.vec.x, this.vec.y, this.vec.z);
		poseStack.rotate(Axis.YP.rotationDegrees(this.rot.y));
		poseStack.rotate(Axis.XP.rotationDegrees(this.rot.x));
		poseStack.rotate(Axis.ZP.rotationDegrees(this.rot.z));
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		
		submitVanillaLayer(vanillaLayer, poseStack, packedLight, yRot, xRot);
		
		poseStack.popPose();
	}
}
