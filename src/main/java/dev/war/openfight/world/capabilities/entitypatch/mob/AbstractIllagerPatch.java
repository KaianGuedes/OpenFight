package dev.war.openfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.entity.PathfinderMob;
import dev.war.openfight.api.animation.Animator;
import dev.war.openfight.api.animation.LivingMotions;
import dev.war.openfight.gameasset.Animations;
import dev.war.openfight.world.capabilities.entitypatch.Faction;
import dev.war.openfight.world.capabilities.entitypatch.HumanoidMobPatch;

public abstract class AbstractIllagerPatch<T extends PathfinderMob> extends HumanoidMobPatch<T> {
	public AbstractIllagerPatch(T original, Faction faction) {
		super(original, faction);	
	}
	
	@Override
	public void initAnimator(Animator animator) {
		super.initAnimator(animator);
		
		animator.addLivingAnimation(LivingMotions.IDLE, Animations.ILLAGER_IDLE);
		animator.addLivingAnimation(LivingMotions.WALK, Animations.ILLAGER_WALK);
		animator.addLivingAnimation(LivingMotions.DEATH, Animations.BIPED_DEATH);
	}
}