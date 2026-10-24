package dev.war.openfight.api.animation.types;

import dev.war.openfight.api.animation.AnimationManager.AnimationAccessor;
import dev.war.openfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import dev.war.openfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.model.Armature;

public class LongHitAnimation extends ActionAnimation {
	public LongHitAnimation(float transitionTime, AnimationAccessor<? extends LongHitAnimation> accessor, AssetAccessor<? extends Armature> armature) {
		super(transitionTime, accessor, armature);
		
		this.addProperty(ActionAnimationProperty.STOP_MOVEMENT, true);
		this.addProperty(ActionAnimationProperty.REMOVE_DELTA_MOVEMENT, true);
		this.addProperty(StaticAnimationProperty.FIXED_HEAD_ROTATION, true);
		
		this.stateSpectrumBlueprint.clear()
			.newTimePair(0.0F, Float.MAX_VALUE)
			.addState(EntityState.TURNING_LOCKED, true)
			.addState(EntityState.MOVEMENT_LOCKED, true)
			.addState(EntityState.UPDATE_LIVING_MOTION, false)
			.addState(EntityState.COMBO_ATTACKS_DOABLE, false)
			.addState(EntityState.SKILL_EXECUTABLE, false)
			.addState(EntityState.INACTION, true)
			.addState(EntityState.HURT_LEVEL, 2);
	}
	
	/**
	 * For internal user
	 */
	public LongHitAnimation(float transitionTime, String path, AssetAccessor<? extends Armature> armature) {
		super(transitionTime, Float.MAX_VALUE, path, armature);
		
		this.addProperty(ActionAnimationProperty.STOP_MOVEMENT, true);
		this.addProperty(ActionAnimationProperty.REMOVE_DELTA_MOVEMENT, true);
		this.addProperty(StaticAnimationProperty.FIXED_HEAD_ROTATION, true);
		
		this.stateSpectrumBlueprint.clear()
			.newTimePair(0.0F, Float.MAX_VALUE)
			.addState(EntityState.TURNING_LOCKED, true)
			.addState(EntityState.MOVEMENT_LOCKED, true)
			.addState(EntityState.UPDATE_LIVING_MOTION, false)
			.addState(EntityState.COMBO_ATTACKS_DOABLE, false)
			.addState(EntityState.SKILL_EXECUTABLE, false)
			.addState(EntityState.INACTION, true)
			.addState(EntityState.HURT_LEVEL, 2);
	}
}