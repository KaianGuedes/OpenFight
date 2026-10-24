package dev.war.openfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.illager.Pillager;
import dev.war.openfight.api.animation.Animator;
import dev.war.openfight.api.animation.LivingMotions;
import dev.war.openfight.gameasset.Animations;
import dev.war.openfight.world.capabilities.entitypatch.Factions;

public class PillagerPatch extends AbstractIllagerPatch<Pillager> {
	public PillagerPatch(Pillager original) {
		super(original, Factions.ILLAGER);
	}
	
	@Override
	public void initAnimator(Animator animator) {
		super.initAnimator(animator);
		
		animator.addLivingAnimation(LivingMotions.IDLE, Animations.BIPED_IDLE);
		animator.addLivingAnimation(LivingMotions.WALK, Animations.BIPED_WALK);
		animator.addLivingAnimation(LivingMotions.CHASE, Animations.BIPED_WALK);
		animator.addLivingAnimation(LivingMotions.FALL, Animations.BIPED_FALL);
		animator.addLivingAnimation(LivingMotions.MOUNT, Animations.BIPED_MOUNT);
		animator.addLivingAnimation(LivingMotions.DEATH, Animations.BIPED_DEATH);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonAggressiveRangedMobUpdateMotion(considerInaction);
	}
	
	@Override
	public void setAIAsInfantry(boolean holdingRanedWeapon) {
		if (!holdingRanedWeapon) {
			super.setAIAsInfantry(holdingRanedWeapon);
		}
	}
	
	@Override
	public void setAIAsMounted(Entity ridingEntity) {

	}
}