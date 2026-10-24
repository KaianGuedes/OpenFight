package dev.war.openfight.api.collider;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.joml.Vector3f;
import dev.war.openfight.api.animation.Joint;
import dev.war.openfight.api.animation.JointTransform;
import dev.war.openfight.api.animation.Pose;
import dev.war.openfight.api.animation.TransformSheet;
import dev.war.openfight.api.animation.types.AttackAnimation;
import dev.war.openfight.api.animation.types.EntityState;
import dev.war.openfight.api.model.Armature;
import dev.war.openfight.api.utils.math.Vec3f;
import dev.war.openfight.api.utils.side.ClientOnly;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;

public class MultiLineCollider extends MultiCollider<LineCollider> {
	public MultiLineCollider(int arrayLength, double posX, double posY, double posZ, double vecX, double vecY, double vecZ) {
		super(arrayLength, posX, posY, posZ, LineCollider.getInitialAABB(posX, posY, posZ, vecX, vecY, vecZ));
	}
	
	@Override @ClientOnly
	public void draw(PoseStack poseStack, MultiBufferSource buffer, LivingEntityPatch<?> entitypatch, AttackAnimation animation, Joint joint, float prevElapsedTime, float elapsedTime, float partialTicks, float attackSpeed) {
		int numberOf = Math.max(Math.round(this.numberOfColliders * attackSpeed), 1);
		float partialScale = 1.0F / numberOf;
		float interpolation = partialScale;
		Armature armature = entitypatch.getArmature();
		EntityState state = animation.getState(entitypatch, elapsedTime);
		EntityState prevState = animation.getState(entitypatch, prevElapsedTime);
		boolean attacking = prevState.attacking() || state.attacking() || (prevState.getLevel() < 2 && state.getLevel() > 2);
		List<LineCollider> colliders = Lists.newArrayList();
		
		for (int i = 0; i < numberOf; i++) {
			colliders.add(this.colliders.get(i).deepCopy());
		}
		
		for (LineCollider lineCollider : colliders) {
			float pt1 = prevElapsedTime + (elapsedTime - prevElapsedTime) * partialTicks;
			float pt2 = prevElapsedTime + (elapsedTime - prevElapsedTime) * interpolation;
			TransformSheet coordTransform = animation.getCoord();
			Vec3f p1 = coordTransform.getInterpolatedTranslation(pt1);
			Vec3f p2 = coordTransform.getInterpolatedTranslation(pt2);
			Vector3f gap = new Vector3f(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
			
			poseStack.pushPose();
			poseStack.translate(gap.x(), gap.y(), gap.z());
			
			Pose pose;
			
			if (armature.rootJoint.getName().equals(joint.getName())) {
				pose = new Pose();
				pose.putJointData("Root", JointTransform.empty());
				animation.modifyPose(animation, pose, entitypatch, elapsedTime, 1.0F);
			} else {
				pose = animation.getPoseByTime(entitypatch, pt2, 1.0F);
			}
			
			lineCollider.drawInternal(poseStack, buffer.getBuffer(this.getRenderType()), armature, joint, pose, pose, 1.0F, attacking ? 0xFFFF0000 : -1);
			poseStack.popPose();
			
			interpolation += partialScale;
		}
	}
	
	@Override @ClientOnly
	public RenderType getRenderType() {
		return null;
	}
}