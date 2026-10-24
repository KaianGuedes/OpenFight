package dev.war.openfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import dev.war.openfight.api.animation.Animator;
import dev.war.openfight.api.animation.LivingMotions;
import dev.war.openfight.api.utils.AttackResult;
import dev.war.openfight.gameasset.Animations;
import dev.war.openfight.world.capabilities.entitypatch.Factions;
import dev.war.openfight.world.capabilities.entitypatch.HumanoidMobPatch;
import dev.war.openfight.world.entity.ai.goal.AnimatedAttackGoal;
import dev.war.openfight.world.entity.ai.goal.CombatBehaviors;
import dev.war.openfight.world.entity.ai.goal.TargetChasingGoal;

public class ZombifiedPiglinPatch extends HumanoidMobPatch<ZombifiedPiglin> {
	public ZombifiedPiglinPatch(ZombifiedPiglin original) {
		super(original, Factions.ZOMBIFIED_PIGLIN);
	}
	
	@Override
	public void initAnimator(Animator animator) {
		super.initAnimator(animator);
		animator.addLivingAnimation(LivingMotions.IDLE, Animations.PIGLIN_ZOMBIFIED_IDLE);
		animator.addLivingAnimation(LivingMotions.WALK, Animations.PIGLIN_ZOMBIFIED_WALK);
		animator.addLivingAnimation(LivingMotions.CHASE, Animations.PIGLIN_ZOMBIFIED_CHASE);
		animator.addLivingAnimation(LivingMotions.FALL, Animations.BIPED_FALL);
		animator.addLivingAnimation(LivingMotions.MOUNT, Animations.BIPED_MOUNT);
		animator.addLivingAnimation(LivingMotions.DEATH, Animations.PIGLIN_DEATH);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonAggressiveMobUpdateMotion(considerInaction);
	}
	
	@Override
	public void setAIAsInfantry(boolean holdingRanedWeapon) {
		CombatBehaviors.Builder<HumanoidMobPatch<?>> builder = this.getHoldingItemWeaponMotionBuilder();
		
		if (builder != null) {
			this.original.goalSelector.addGoal(1, new AnimatedAttackGoal<>(this, builder.build(this)));
			this.original.goalSelector.addGoal(1, new TargetChasingGoal(this, this.getOriginal(), 1.2D, true));
		}
	}
	
	@Override
	public AttackResult tryHurt(DamageSource damageSource, float amount) {
		if (damageSource.getEntity() instanceof ZombifiedPiglin) {
			return AttackResult.blocked(amount);
		}
		
		return super.tryHurt(damageSource, amount);
	}
}