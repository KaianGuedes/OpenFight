package dev.war.openfight.api.animation.types;

import dev.war.openfight.api.animation.AnimationManager.AnimationAccessor;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.model.Armature;
import dev.war.openfight.main.EpicFightSharedConstants;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public class MovementAnimation extends StaticAnimation {
	public MovementAnimation(boolean isRepeat, AnimationAccessor<? extends MovementAnimation> accessor, AssetAccessor<? extends Armature> armature) {
		super(EpicFightSharedConstants.GENERAL_ANIMATION_TRANSITION_TIME, isRepeat, accessor, armature);
	}
	
	public MovementAnimation(float transitionTime, boolean isRepeat, AnimationAccessor<? extends MovementAnimation> accessor, AssetAccessor<? extends Armature> armature) {
		super(transitionTime, isRepeat, accessor, armature);
	}
	
	/**
	 * For datapack animations
	 */
	public MovementAnimation(float transitionTime, boolean isRepeat, String path, AssetAccessor<? extends Armature> armature) {
		super(transitionTime, isRepeat, path, armature);
	}
	
	@Override
	public float getPlaySpeed(LivingEntityPatch<?> entitypatch, DynamicAnimation animation) {
		if (animation.isLinkAnimation()) {
			return 1.0F;
		}
		
		float movementSpeed = 1.0F;
		
		if (Math.abs(entitypatch.getOriginal().walkAnimation.speed() - entitypatch.getOriginal().walkAnimation.speed(1)) < 0.007F) {
			movementSpeed *= (entitypatch.getOriginal().walkAnimation.speed() * 1.16F);
		}
		
		return movementSpeed;
	}
	
	@Override
	public boolean canBePlayedReverse() {
		return true;
	}
}