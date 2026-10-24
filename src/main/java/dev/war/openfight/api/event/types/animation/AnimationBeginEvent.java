package dev.war.openfight.api.event.types.animation;

import dev.war.openfight.api.animation.AnimationManager;
import dev.war.openfight.api.animation.types.StaticAnimation;
import dev.war.openfight.api.event.LivingEntityPatchEvent;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

/// Fired when [StaticAnimation] starts to play
/// Consider using [StartActionEvent] instead to save type-checking costs called everytime when an animation is played
public class AnimationBeginEvent extends LivingEntityPatchEvent {
	private final AnimationManager.AnimationAccessor<? extends StaticAnimation> animation;
	
	public AnimationBeginEvent(LivingEntityPatch<?> playerPatch, AnimationManager.AnimationAccessor<? extends StaticAnimation> animation) {
		super(playerPatch);
		
		this.animation = animation;
	}

	public AnimationManager.AnimationAccessor<? extends StaticAnimation> getAnimation() {
		return this.animation;
	}
}
