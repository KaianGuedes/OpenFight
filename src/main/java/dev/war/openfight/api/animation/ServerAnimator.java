package dev.war.openfight.api.animation;

import java.util.Optional;

import com.mojang.datafixers.util.Pair;

import dev.war.openfight.api.animation.types.DynamicAnimation;
import dev.war.openfight.api.animation.types.EntityState;
import dev.war.openfight.api.animation.types.LinkAnimation;
import dev.war.openfight.api.animation.types.StaticAnimation;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.gameasset.Animations;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ServerAnimator extends Animator {
	public static Animator getAnimator(LivingEntityPatch<?> entitypatch) {
		return new ServerAnimator(entitypatch);
	}
	
	private final LinkAnimation linkAnimation;
	public final AnimationPlayer animationPlayer;
	
	protected AssetAccessor<? extends DynamicAnimation> nextAnimation;
	public boolean hardPaused = false;
	public boolean softPaused = false;
	
	public ServerAnimator(LivingEntityPatch<?> entitypatch) {
		super(entitypatch);
		
		this.linkAnimation = new LinkAnimation();
		this.animationPlayer = new AnimationPlayer();
	}
	
	/** Play an animation by animation instance **/
	@Override
	public void playAnimation(AssetAccessor<? extends StaticAnimation> nextAnimation, float transitionTimeModifier) {
		this.softPaused = false;
		Pose lastPose = this.animationPlayer.getAnimation().get().getPoseByTime(this.entitypatch, 0.0F, 0.0F);
		
		if (!this.animationPlayer.isEnd()) {
			this.animationPlayer.getAnimation().get().end(this.entitypatch, nextAnimation, false);
		}
		
		nextAnimation.get().begin(this.entitypatch);
		
		if (!nextAnimation.get().isMetaAnimation()) {
			nextAnimation.get().setLinkAnimation(this.animationPlayer.getAnimation(), lastPose, true, transitionTimeModifier, this.entitypatch, this.linkAnimation);
			this.linkAnimation.getAnimationClip().setBaked();
			this.linkAnimation.putOnPlayer(this.animationPlayer, this.entitypatch);
			this.entitypatch.updateEntityState();
			this.nextAnimation = nextAnimation;
		}
	}
	
	@Override
	public void playAnimationInstantly(AssetAccessor<? extends StaticAnimation> nextAnimation) {
		this.softPaused = false;
		
		if (!this.animationPlayer.isEnd()) {
			this.animationPlayer.getAnimation().get().end(this.entitypatch, nextAnimation, false);
		}
		
		nextAnimation.get().begin(this.entitypatch);
		nextAnimation.get().putOnPlayer(this.animationPlayer, this.entitypatch);
		this.entitypatch.updateEntityState();
	}
	
	@Override
	public void reserveAnimation(AssetAccessor<? extends StaticAnimation> nextAnimation) {
		this.softPaused = false;
		this.nextAnimation = nextAnimation;
	}
	
	@Override
	public boolean stopPlaying(AssetAccessor<? extends StaticAnimation> targetAnimation) {
		if (this.animationPlayer.getRealAnimation() == targetAnimation) {
			this.animationPlayer.terminate(this.entitypatch);
			return true;
		}
		
		return false;
	}
	
	@Override
	public void playShootingAnimation() {
	}
	
	@Override
	public void tick() {
		if (this.hardPaused || this.softPaused) {
			this.entitypatch.updateEntityState();
			return;
		}
		
		this.animationPlayer.tick(this.entitypatch);
		this.entitypatch.updateEntityState();
		
		if (this.animationPlayer.isEnd()) {
			if (this.nextAnimation == null) {
				Animations.EMPTY_ANIMATION.putOnPlayer(this.animationPlayer, this.entitypatch);
				this.softPaused = true;
			} else {
				if (!this.animationPlayer.getAnimation().get().isLinkAnimation() && !this.nextAnimation.get().isLinkAnimation()) {
					this.nextAnimation.get().begin(this.entitypatch);
				}
				
				this.nextAnimation.get().putOnPlayer(this.animationPlayer, this.entitypatch);
				this.nextAnimation = null;
			}
		} else {
			this.animationPlayer.getAnimation().get().tick(this.entitypatch);
		}
	}
	
	@Override
	public Pose getPose(float partialTicks) {
		return this.animationPlayer.getCurrentPose(this.entitypatch, partialTicks);
	}
	
	@Override
	public AnimationPlayer getPlayerFor(AssetAccessor<? extends DynamicAnimation> playingAnimation) {
		return this.animationPlayer;
	}
	
	@Override
	public Optional<AnimationPlayer> getPlayer(AssetAccessor<? extends DynamicAnimation> playingAnimation) {
		if (this.animationPlayer.getRealAnimation() == playingAnimation.get().getRealAnimation()) {
			return Optional.of(this.animationPlayer);
		} else {
			return Optional.empty();
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> Pair<AnimationPlayer, T> findFor(Class<T> animationType) {
		return animationType.isAssignableFrom(this.animationPlayer.getAnimation().getClass()) ? Pair.of(this.animationPlayer, (T)this.animationPlayer.getAnimation()) : null;
	}
	
	@Override
	public EntityState getEntityState() {
		return this.animationPlayer.getAnimation().get().getState(this.entitypatch, this.animationPlayer.getElapsedTime());
	}
	
	@Override
	public void setSoftPause(boolean paused) {
		this.softPaused = paused;
	}

	@Override
	public void setHardPause(boolean paused) {
		this.hardPaused = paused;
	}

	@Override
	public boolean isPlaying(AssetAccessor<? extends DynamicAnimation> animation) {
		return this.animationPlayer.getRealAnimation().equals(animation);
	}
}