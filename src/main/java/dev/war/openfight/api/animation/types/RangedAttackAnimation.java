package dev.war.openfight.api.animation.types;

import net.minecraft.world.entity.monster.RangedAttackMob;
import dev.war.openfight.api.animation.AnimationManager.AnimationAccessor;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.animation.Joint;
import dev.war.openfight.api.collider.Collider;
import dev.war.openfight.api.model.Armature;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public class RangedAttackAnimation extends AttackAnimation {
	public RangedAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, Collider collider, Joint colliderJoint, AnimationAccessor<? extends RangedAttackAnimation> accessor, AssetAccessor<? extends Armature> armature) {
		super(convertTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature);
	}
	
	@Override
	public void hurtCollidingEntities(LivingEntityPatch<?> entitypatch, float prevElapsedTime, float elapsedTime, EntityState prevState, EntityState state, Phase phase) {
		if (entitypatch.getTarget() != null && (entitypatch.getOriginal() instanceof RangedAttackMob rangedAttackMob)) {
			rangedAttackMob.performRangedAttack(entitypatch.getTarget(), elapsedTime);
		}
	}
}