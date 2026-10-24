package dev.war.openfight.world.capabilities.entitypatch.mob;

import java.util.Set;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.schedule.Activity;
import dev.war.openfight.platform.fabric.event.EntityAttributeModificationEvent;
import dev.war.openfight.api.animation.Animator;
import dev.war.openfight.api.animation.LivingMotions;
import dev.war.openfight.api.utils.math.OpenMatrix4f;
import dev.war.openfight.gameasset.Animations;
import dev.war.openfight.registry.entries.EpicFightAttributes;
import dev.war.openfight.world.capabilities.entitypatch.Factions;
import dev.war.openfight.world.capabilities.entitypatch.HumanoidMobPatch;
import dev.war.openfight.world.entity.ai.behavior.AnimatedCombatBehavior;
import dev.war.openfight.world.entity.ai.behavior.MoveToTargetSinkStopInaction;
import dev.war.openfight.world.entity.ai.brain.BrainRecomposer;
import dev.war.openfight.world.entity.ai.goal.CombatBehaviors;

public class PiglinBrutePatch extends HumanoidMobPatch<PiglinBrute> {
	public PiglinBrutePatch(PiglinBrute original) {
		super(original, Factions.PIGLINS);
	}
	
	public static void initAttributes(EntityAttributeModificationEvent event) {
		event.add(EntityTypes.PIGLIN_BRUTE, EpicFightAttributes.STUN_ARMOR, 8.0D);
		event.add(EntityTypes.PIGLIN_BRUTE, EpicFightAttributes.IMPACT, 3.0D);
	}
	
	@Override
	public void initAnimator(Animator animator) {
		super.initAnimator(animator);
		
		animator.addLivingAnimation(LivingMotions.IDLE, Animations.PIGLIN_IDLE);
		animator.addLivingAnimation(LivingMotions.WALK, Animations.PIGLIN_WALK);
		animator.addLivingAnimation(LivingMotions.CHASE, Animations.PIGLIN_WALK);
		animator.addLivingAnimation(LivingMotions.FALL, Animations.BIPED_FALL);
		animator.addLivingAnimation(LivingMotions.MOUNT, Animations.BIPED_MOUNT);
		animator.addLivingAnimation(LivingMotions.DEATH, Animations.PIGLIN_DEATH);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonMobUpdateMotion(considerInaction);
	}
	
	@Override
	protected void selectGoalToRemove(Set<Goal> toRemove) {
		BrainRecomposer.removeBehavior(this.original.getBrain(), Activity.FIGHT, 12, MeleeAttack.class);
	}
	
	@Override
	public void setAIAsInfantry(boolean holdingRanedWeapon) {
		CombatBehaviors.Builder<HumanoidMobPatch<?>> builder = this.getHoldingItemWeaponMotionBuilder();
		BrainRecomposer.recomposePiglinBruteBrain(this.original.getBrain(), (builder != null) ? new AnimatedCombatBehavior<>(this, builder.build(this)) : null, new MoveToTargetSinkStopInaction());
	}
	
	@Override
	public void setAIAsMounted(Entity ridingEntity) {
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		return super.getModelMatrix(partialTicks).scale(1.1F, 1.1F, 1.1F);
	}
}