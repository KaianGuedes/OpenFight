package dev.war.openfight.world.capabilities.entitypatch.mob;

import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;

import net.minecraft.world.entity.PathfinderMob;
import dev.war.openfight.api.animation.Animator;
import dev.war.openfight.api.animation.LivingMotions;
import dev.war.openfight.gameasset.Animations;
import dev.war.openfight.gameasset.MobCombatBehaviors;
import dev.war.openfight.world.capabilities.entitypatch.Faction;
import dev.war.openfight.world.capabilities.entitypatch.Factions;
import dev.war.openfight.world.capabilities.entitypatch.HumanoidMobPatch;
import dev.war.openfight.world.capabilities.item.CapabilityItem;
import dev.war.openfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import dev.war.openfight.world.entity.ai.goal.AnimatedAttackGoal;
import dev.war.openfight.world.entity.ai.goal.CombatBehaviors;
import dev.war.openfight.world.entity.ai.goal.TargetChasingGoal;

public class SkeletonPatch<T extends PathfinderMob> extends HumanoidMobPatch<T> {
	public SkeletonPatch(T original) {
		super(original, Factions.UNDEAD);
	}
	
	public SkeletonPatch(T original, Faction faction) {
		super(original, faction);
	}
	
	@Override
	public void initAnimator(Animator animator) {
		super.initAnimator(animator);
		super.commonAggresiveMobAnimatorInit(animator);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonAggressiveRangedMobUpdateMotion(considerInaction);
	}
	
	@Override
	protected void setWeaponMotions() {
		super.setWeaponMotions();
		
		this.weaponLivingMotions.put(WeaponCategories.SWORD, ImmutableMap.of(
			CapabilityItem.Styles.ONE_HAND, Set.of(
				Pair.of(LivingMotions.CHASE, Animations.WITHER_SKELETON_CHASE)
			)
		));
		
		this.weaponAttackMotions.put(WeaponCategories.SWORD, ImmutableMap.of(CapabilityItem.Styles.COMMON, MobCombatBehaviors.SKELETON_SWORD));
	}
	
	@Override
	public void setAIAsInfantry(boolean holdingRanedWeapon) {
		if (!holdingRanedWeapon) {
			CombatBehaviors.Builder<HumanoidMobPatch<?>> builder = this.getHoldingItemWeaponMotionBuilder();
			
			if (builder != null) {
				this.original.goalSelector.addGoal(0, new AnimatedAttackGoal<>(this, builder.build(this)));
				this.original.goalSelector.addGoal(1, new TargetChasingGoal(this, this.original, 1.2D, true));
			}
		}
	}
}