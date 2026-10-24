package dev.war.openfight.api.animation.types;

import dev.war.openfight.api.animation.AnimationManager.AnimationAccessor;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.animation.Joint;
import dev.war.openfight.api.collider.Collider;
import dev.war.openfight.api.model.Armature;
import dev.war.openfight.api.utils.math.Vec3f;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public class MountAttackAnimation extends AttackAnimation {
	public MountAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, Collider collider, Joint colliderJoint, AnimationAccessor<? extends MountAttackAnimation> accessor, AssetAccessor<? extends Armature> armature) {
		super(convertTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature);
	}
	
	protected Vec3f getCoordVector(LivingEntityPatch<?> entitypatch) {
		return new Vec3f(0, 0, 0);
	}
}
