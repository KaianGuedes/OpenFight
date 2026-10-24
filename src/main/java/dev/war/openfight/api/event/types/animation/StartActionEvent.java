package dev.war.openfight.api.event.types.animation;

import dev.war.openfight.api.animation.AnimationManager.AnimationAccessor;
import dev.war.openfight.api.animation.types.ActionAnimation;
import dev.war.openfight.api.animation.types.MainFrameAnimation;
import dev.war.openfight.api.event.LivingEntityPatchEvent;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

/// Fired when [ActionAnimation] starts to play
public class StartActionEvent extends LivingEntityPatchEvent {
	private final AnimationAccessor<? extends MainFrameAnimation> animation;
	private boolean resetActionTick;
	
	public StartActionEvent(LivingEntityPatch<?> entityPatch, AnimationAccessor<? extends MainFrameAnimation> actionAnimation) {
		super(entityPatch);
		
		this.animation = actionAnimation;
		this.resetActionTick = true;
	}
	
	public AnimationAccessor<? extends MainFrameAnimation> getAnimation() {
		return this.animation;
	}

	public void resetActionTick(boolean flag) {
		this.resetActionTick = flag;
	}
	
	public boolean shouldResetActionTick() {
		return this.resetActionTick;
	}
}