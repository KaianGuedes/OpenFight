package dev.war.openfight.world.capabilities.entitypatch;

import com.mojang.datafixers.util.Pair;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import dev.war.openfight.api.animation.Animator;
import dev.war.openfight.api.animation.LivingMotion;
import dev.war.openfight.api.animation.AnimationManager.AnimationAccessor;
import dev.war.openfight.api.animation.types.StaticAnimation;
import dev.war.openfight.api.data.reloader.MobPatchReloadListener;
import dev.war.openfight.api.utils.math.OpenMatrix4f;
import dev.war.openfight.particle.HitParticleType;
import dev.war.openfight.registry.entries.EpicFightAttributes;
import dev.war.openfight.world.capabilities.item.CapabilityItem;
import dev.war.openfight.world.damagesource.StunType;
import dev.war.openfight.world.entity.ai.behavior.AnimatedCombatBehavior;
import dev.war.openfight.world.entity.ai.behavior.MoveToTargetSinkStopInaction;
import dev.war.openfight.world.entity.ai.brain.BrainRecomposer;
import dev.war.openfight.world.entity.ai.goal.AnimatedAttackGoal;
import dev.war.openfight.world.entity.ai.goal.CombatBehaviors;
import dev.war.openfight.world.entity.ai.goal.TargetChasingGoal;

public class CustomMobPatch<T extends PathfinderMob> extends MobPatch<T> {
	private final MobPatchReloadListener.CustomMobPatchProvider provider;
	
	public CustomMobPatch(T original, Faction faction, MobPatchReloadListener.CustomMobPatchProvider provider) {
		super(original, faction);
		this.provider = provider;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void initAI() {
		super.initAI();
		
		boolean useBrain = !this.original.getBrain().availableBehaviorsByPriority.isEmpty();
		CombatBehaviors<CustomMobPatch<T>> combatBehaviors = ((CombatBehaviors.Builder<CustomMobPatch<T>>)this.provider.getCombatBehaviorsBuilder()).build(this);
		
		if (useBrain) {
			BrainRecomposer.recomposeBrainByType(this.original.getType(), this.original.getBrain(), new AnimatedCombatBehavior<>(this, combatBehaviors), new MoveToTargetSinkStopInaction());
		} else {
			this.original.goalSelector.addGoal(0, new AnimatedAttackGoal<>(this, combatBehaviors));
			this.original.goalSelector.addGoal(1, new TargetChasingGoal(this, this.getOriginal(), this.provider.getChasingSpeed(), true));
		}
	}
	
	public void initAttributes() {
		this.original.getAttribute(EpicFightAttributes.MAX_STRIKES).setBaseValue(this.provider.getAttributeValues().getDouble(EpicFightAttributes.MAX_STRIKES));
		this.original.getAttribute(EpicFightAttributes.ARMOR_NEGATION).setBaseValue(this.provider.getAttributeValues().getDouble(EpicFightAttributes.ARMOR_NEGATION));
		this.original.getAttribute(EpicFightAttributes.IMPACT).setBaseValue(this.provider.getAttributeValues().getDouble(EpicFightAttributes.IMPACT));
		this.original.getAttribute(EpicFightAttributes.STUN_ARMOR).setBaseValue(this.provider.getAttributeValues().getDouble(EpicFightAttributes.STUN_ARMOR));
		
		if (this.provider.getAttributeValues().containsKey(Attributes.ATTACK_DAMAGE)) {
			this.original.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.provider.getAttributeValues().getDouble(Attributes.ATTACK_DAMAGE));
		}
	}
	
	@Override
	public void readData(CompoundTag compound) {
		super.readData(compound);
		this.initAttributes();
	}
	
	@Override
	public void initAnimator(Animator animator) {
		super.initAnimator(animator);
		
		for (Pair<LivingMotion, AnimationAccessor<? extends StaticAnimation>> pair : this.provider.getDefaultAnimations()) {
			animator.addLivingAnimation(pair.getFirst(), pair.getSecond());
		}
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonAggressiveMobUpdateMotion(considerInaction);
	}
	
	@Override
	public AnimationAccessor<? extends StaticAnimation> getHitAnimation(StunType stunType) {
		return this.provider.getStunAnimations().get(stunType);
	}
	
	@Override
	public SoundEvent getWeaponHitSound(InteractionHand hand) {
		CapabilityItem itemCap = this.getAdvancedHoldingItemCapability(hand);
		
		if (itemCap.isEmpty()) {
			return this.provider.getHitSound();
		}
		
		return itemCap.getHitSound();
	}

	@Override
	public SoundEvent getSwingSound(InteractionHand hand) {
		CapabilityItem itemCap = this.getAdvancedHoldingItemCapability(hand);
		
		if (itemCap.isEmpty()) {
			return this.provider.getSwingSound();
		}
		
		return itemCap.getSmashingSound();
	}

	@Override
	public HitParticleType getWeaponHitParticle(InteractionHand hand) {
		CapabilityItem itemCap = this.getAdvancedHoldingItemCapability(hand);
		
		if (itemCap.isEmpty()) {
			return this.provider.getHitParticle();
		}
		
		return itemCap.getHitParticle();
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		float scale = this.provider.getScale();
		return super.getModelMatrix(partialTicks).scale(scale, scale, scale);
	}
}