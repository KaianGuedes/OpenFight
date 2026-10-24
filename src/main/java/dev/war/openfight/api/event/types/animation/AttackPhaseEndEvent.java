package dev.war.openfight.api.event.types.animation;

import dev.war.openfight.api.animation.AnimationManager.AnimationAccessor;
import dev.war.openfight.api.animation.types.AttackAnimation;
import dev.war.openfight.api.event.LivingEntityPatchEvent;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

/// Fired when each attack phase is over
///
///
/// @see AttackAnimation.Phase
public class AttackPhaseEndEvent extends LivingEntityPatchEvent {
	private final AnimationAccessor<? extends AttackAnimation> animation;
	private final AttackAnimation.Phase phase;
	private final int phaseOrder;
    private final boolean isAnimationTerminated;

	public AttackPhaseEndEvent(LivingEntityPatch<?> entitypatch, AnimationAccessor<? extends AttackAnimation> animation, AttackAnimation.Phase phase, int phaseOrder, boolean isLastPhase) {
		super(entitypatch);
		
		this.animation = animation;
		this.phase = phase;
		this.phaseOrder = phaseOrder;
        this.isAnimationTerminated = isLastPhase;
	}

	public final AnimationAccessor<? extends AttackAnimation> getAnimation() {
		return this.animation;
	}

	public final AttackAnimation.Phase getPhase() {
		return this.phase;
	}

	public final int getPhaseOrder() {
		return this.phaseOrder;
	}

    public final boolean isAnimationTerminated() {
        return this.isAnimationTerminated;
    }
}
