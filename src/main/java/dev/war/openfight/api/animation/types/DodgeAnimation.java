package dev.war.openfight.api.animation.types;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityDimensions;
import dev.war.openfight.api.animation.AnimationManager.AnimationAccessor;
import dev.war.openfight.api.animation.property.AnimationEvent;
import dev.war.openfight.api.animation.property.AnimationEvent.SimpleEvent;
import dev.war.openfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import dev.war.openfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.model.Armature;
import dev.war.openfight.api.utils.AttackResult;
import dev.war.openfight.gameasset.Animations;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;
import dev.war.openfight.world.damagesource.EpicFightDamageTypeTags;
import dev.war.openfight.world.entity.DodgeLocationIndicator;

import java.util.function.Function;

public class DodgeAnimation extends ActionAnimation {
	public static final Function<DamageSource, AttackResult.ResultType> DODGEABLE_SOURCE_VALIDATOR = (damagesource) -> {
		if (
            damagesource.getEntity() != null
			&& !damagesource.is(DamageTypeTags.IS_EXPLOSION)
			&& !damagesource.is(DamageTypes.MAGIC)
			&& !damagesource.is(DamageTypeTags.BYPASSES_ARMOR)
			&& !damagesource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
			&& !damagesource.is(EpicFightDamageTypeTags.BYPASS_DODGE)
		) {
			return AttackResult.ResultType.MISSED;
		}
		
		return AttackResult.ResultType.SUCCESS;
	};
	
	public static final EntityState.ProjectileHitPredicate IGNORE_ALL_PROJECTILES = (projectile, hitResult) -> false;
	
	public DodgeAnimation(float transitionTime, AnimationAccessor<? extends DodgeAnimation> accessor, float width, float height, AssetAccessor<? extends Armature> armature) {
		this(transitionTime, 10.0F, accessor, width, height, armature);
	}
	
	public DodgeAnimation(float transitionTime, float delayTime, AnimationAccessor<? extends DodgeAnimation> accessor, float width, float height, AssetAccessor<? extends Armature> armature) {
		super(transitionTime, delayTime, accessor, armature);
		
		this.stateSpectrumBlueprint.clear()
			.newTimePair(0.0F, delayTime)
			.addState(EntityState.TURNING_LOCKED, true)
			.addState(EntityState.MOVEMENT_LOCKED, true)
			.addState(EntityState.UPDATE_LIVING_MOTION, false)
			.addState(EntityState.COMBO_ATTACKS_DOABLE, false)
			.addState(EntityState.SKILL_EXECUTABLE, false)
			.addState(EntityState.INACTION, true)
			.newTimePair(0.0F, Float.MAX_VALUE)
			.addState(EntityState.ATTACK_RESULT, DODGEABLE_SOURCE_VALIDATOR)
			.addState(EntityState.PROJECTILE_IMPACT_RESULT, IGNORE_ALL_PROJECTILES);
		
		
		this.addProperty(ActionAnimationProperty.AFFECT_SPEED, true);
		this.addEvents(StaticAnimationProperty.ON_END_EVENTS, SimpleEvent.create(Animations.ReusableSources.RESTORE_BOUNDING_BOX, AnimationEvent.Side.BOTH));
		this.addEvents(StaticAnimationProperty.TICK_EVENTS, SimpleEvent.create(Animations.ReusableSources.RESIZE_BOUNDING_BOX, AnimationEvent.Side.BOTH).params(EntityDimensions.scalable(width, height)));
	}
	
	@Override
	public void begin(LivingEntityPatch<?> entitypatch) {
		super.begin(entitypatch);
		
		if (!entitypatch.isLogicalClient()) {
			entitypatch.getOriginal().level().addFreshEntity(new DodgeLocationIndicator(entitypatch));
		}
	}
}