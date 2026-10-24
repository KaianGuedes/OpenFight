package dev.war.openfight.world.capabilities.entitypatch.mob;

import java.util.Set;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import dev.war.openfight.platform.fabric.event.EntityAttributeModificationEvent;
import dev.war.openfight.api.animation.AnimationManager.AnimationAccessor;
import dev.war.openfight.api.animation.Animator;
import dev.war.openfight.api.animation.LivingMotions;
import dev.war.openfight.api.animation.types.StaticAnimation;
import dev.war.openfight.gameasset.Animations;
import dev.war.openfight.gameasset.MobCombatBehaviors;
import dev.war.openfight.registry.entries.EpicFightAttributes;
import dev.war.openfight.registry.entries.EpicFightSounds;
import dev.war.openfight.world.capabilities.entitypatch.Factions;
import dev.war.openfight.world.capabilities.entitypatch.MobPatch;
import dev.war.openfight.world.damagesource.StunType;
import dev.war.openfight.world.entity.ai.goal.AnimatedAttackGoal;
import dev.war.openfight.world.entity.ai.goal.TargetChasingGoal;

public class IronGolemPatch extends MobPatch<IronGolem> {
	public IronGolemPatch(IronGolem original) {
		super(original, Factions.VILLAGER);
	}
	
	@Override
	protected void initAI() {
		super.initAI();
		this.original.goalSelector.addGoal(0, new AnimatedAttackGoal<>(this, MobCombatBehaviors.IRON_GOLEM.build(this)));
		this.original.goalSelector.addGoal(1, new TargetChasingGoal(this, this.original, 1.0D, false));
	}
	
	@Override
	protected void selectGoalToRemove(Set<Goal> toRemove) {
		super.selectGoalToRemove(toRemove);
		
		for (WrappedGoal wrappedGoal : this.original.goalSelector.getAvailableGoals()) {
			Goal goal = wrappedGoal.getGoal();
			
			if (goal instanceof MoveTowardsTargetGoal) {
				toRemove.add(goal);
			}
		}
	}
	
	public static void initAttributes(EntityAttributeModificationEvent event) {
		event.add(EntityTypes.IRON_GOLEM, EpicFightAttributes.MAX_STRIKES, 4.0D);
		event.add(EntityTypes.IRON_GOLEM, EpicFightAttributes.IMPACT, 6.0D);
	}
	
	@Override
	public void initAnimator(Animator animator) {
		super.initAnimator(animator);
		animator.addLivingAnimation(LivingMotions.IDLE, Animations.GOLEM_IDLE);
		animator.addLivingAnimation(LivingMotions.WALK, Animations.GOLEM_WALK);
		animator.addLivingAnimation(LivingMotions.DEATH, Animations.GOLEM_DEATH);
	}

	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonMobUpdateMotion(considerInaction);
	}
	
	@Override
	public SoundEvent getWeaponHitSound(InteractionHand hand) {
		return EpicFightSounds.BLUNT_HIT_HARD.get();
	}
	
	@Override
	public SoundEvent getSwingSound(InteractionHand hand) {
		return EpicFightSounds.WHOOSH_BIG.get();
	}
	
	@Override
	public AnimationAccessor<? extends StaticAnimation> getHitAnimation(StunType stunType) {
		return null;
	}
}