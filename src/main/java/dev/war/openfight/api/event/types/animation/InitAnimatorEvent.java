package dev.war.openfight.api.event.types.animation;

import dev.war.openfight.api.animation.Animator;
import dev.war.openfight.api.event.LivingEntityPatchEvent;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public class InitAnimatorEvent extends LivingEntityPatchEvent {
	private final Animator animator;
	
	public InitAnimatorEvent(LivingEntityPatch<?> entityPatch, Animator animator) {
		super(entityPatch);
		this.animator = animator;
	}
	
	public Animator getAnimator() {
		return this.animator;
	}
}