package dev.war.openfight.world.capabilities.entitypatch.mob;

import java.util.Optional;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Zoglin;
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
import dev.war.openfight.world.entity.ai.behavior.AnimatedCombatBehavior;
import dev.war.openfight.world.entity.ai.behavior.MoveToTargetSinkStopInaction;
import dev.war.openfight.world.entity.ai.brain.BrainRecomposer;

public class ZoglinPatch extends MobPatch<Zoglin> {
	public ZoglinPatch(Zoglin original) {
		super(original, Factions.ZOMBIFIED_PIGLIN);
	}
	
	@Override
	public void initAI() {
		super.initAI();
		BrainRecomposer.recomposeZoglinBrain(this.original.getBrain(), new AnimatedCombatBehavior<>(this, MobCombatBehaviors.HOGLIN.build(this)), new MoveToTargetSinkStopInaction());
	}
	
	@Override
	public void initAnimator(Animator animator) {
		super.initAnimator(animator);
		animator.addLivingAnimation(LivingMotions.IDLE, Animations.HOGLIN_IDLE);
		animator.addLivingAnimation(LivingMotions.WALK, Animations.HOGLIN_WALK);
		animator.addLivingAnimation(LivingMotions.DEATH, Animations.HOGLIN_DEATH);
	}
	
	public static void initAttributes(EntityAttributeModificationEvent event) {
		event.add(EntityTypes.ZOGLIN, EpicFightAttributes.MAX_STRIKES, 4.0D);
		event.add(EntityTypes.ZOGLIN, EpicFightAttributes.IMPACT, 5.0D);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonMobUpdateMotion(considerInaction);
	}
	
	@Override
	public AnimationAccessor<? extends StaticAnimation> getHitAnimation(StunType stunType) {
		return null;
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
	public LivingEntity getTarget() {
		Optional<LivingEntity> opt = this.original.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
		return opt.orElse(null);
	}
}