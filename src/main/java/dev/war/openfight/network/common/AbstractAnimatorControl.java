package dev.war.openfight.network.common;

import dev.war.openfight.api.animation.types.StaticAnimation;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.network.ManagedCustomPacketPayload;
import dev.war.openfight.network.server.SPAnimatorControl;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAnimatorControl implements ManagedCustomPacketPayload {
	protected final Action action;
	protected final AssetAccessor<? extends StaticAnimation> animation;
	protected final float transitionTimeModifier;
	protected final boolean pause;
	protected final List<BiDirectionalAnimationVariable> animationVariables;
	
	public AbstractAnimatorControl(Action action, AssetAccessor<? extends StaticAnimation> animation, float transitionTimeModifier, boolean pause) {
		this(action, animation, transitionTimeModifier, pause, new ArrayList<> ());
	}
	
	public AbstractAnimatorControl(Action action, AssetAccessor<? extends StaticAnimation> animation, float transitionTimeModifier, boolean pause, List<BiDirectionalAnimationVariable> animationVariables) {
		this.action = action;
		this.animation = animation;
		this.transitionTimeModifier = transitionTimeModifier;
		this.pause = pause;
		this.animationVariables = animationVariables;
	}
	
	public Action action() {
		return this.action;
	}
	
	public AssetAccessor<? extends StaticAnimation> animation() {
		return this.animation;
	}
	
	public float transitionTimeModifier() {
		return this.transitionTimeModifier;
	}
	
	public boolean pause() {
		return this.pause;
	}
	
	public List<BiDirectionalAnimationVariable> animationVariables() {
		return this.animationVariables;
	}
	
	public <T extends SPAnimatorControl> void commonProcess(LivingEntityPatch<?> entitypatch) {
		try {
			switch (this.action) {
			case PLAY -> {
				entitypatch.getAnimator().playAnimation(this.animation, this.transitionTimeModifier);
			}
			case PLAY_CLIENT -> {
				/** Processed in  * */
			}
			case PLAY_INSTANTLY -> {
				entitypatch.getAnimator().playAnimationInstantly(this.animation);
			}
			case RESERVE -> {
				entitypatch.getAnimator().reserveAnimation(this.animation);
			}
			case STOP -> {
				entitypatch.getAnimator().stopPlaying(this.animation);
			}
			case SHOT -> {
				entitypatch.getAnimator().playShootingAnimation();
			}
			case SOFT_PAUSE -> {
				entitypatch.getAnimator().setSoftPause(this.pause);
			}
			case HARD_PAUSE -> {
				entitypatch.getAnimator().setHardPause(this.pause);
			}
			}
		} catch (Exception e) {
			// print out exceptions since any exceptions that occurred in the packet queue won't be printed out
			e.printStackTrace();
		}
	}
	
	public enum Action {
		PLAY(true), PLAY_CLIENT(true), PLAY_INSTANTLY(true), RESERVE(true), STOP(false), SHOT(true), SOFT_PAUSE(false), HARD_PAUSE(false);
		
		boolean syncVariables;
		
		Action(boolean syncVariables) {
			this.syncVariables = syncVariables;
		}
		
		public boolean syncVariables() {
			return this.syncVariables;
		}
	}
	
	public enum Layer {
		ANIMATION, BASE_LAYER, COMPOSITE_LAYER;
	}
	
	public enum Priority {
		ANIMATION, LOWEST, LOW, MIDDLE, HIGH, HIGHEST;
	}
	
	public static dev.war.openfight.api.client.animation.Layer.Priority getPriority(Priority priority) {
		switch (priority) {
		case LOWEST -> {
			return dev.war.openfight.api.client.animation.Layer.Priority.LOWEST;
		}
		case LOW -> {
			return dev.war.openfight.api.client.animation.Layer.Priority.LOW;
		}
		case MIDDLE -> {
			return dev.war.openfight.api.client.animation.Layer.Priority.MIDDLE;
		}
		case HIGH -> {
			return dev.war.openfight.api.client.animation.Layer.Priority.HIGH;
		}
		case HIGHEST-> {
			return dev.war.openfight.api.client.animation.Layer.Priority.HIGHEST;
		}
		default -> {
			return null;
		}
		}
	}
}
