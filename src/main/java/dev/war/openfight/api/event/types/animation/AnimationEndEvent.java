package dev.war.openfight.api.event.types.animation;

import dev.war.openfight.api.animation.types.StaticAnimation;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.event.LivingEntityPatchEvent;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

/// Fired when [StaticAnimation] is stopped by time expiration, or by another animation is played
public class AnimationEndEvent extends LivingEntityPatchEvent {
	private final AssetAccessor<? extends StaticAnimation> animation;
	private final boolean isEnd;
	
	public AnimationEndEvent(LivingEntityPatch<?> playerPatch, AssetAccessor<? extends StaticAnimation> animation, boolean isEnd) {
		super(playerPatch);
		
		this.animation = animation;
		this.isEnd = isEnd;
	}

	public AssetAccessor<? extends StaticAnimation> getAnimation() {
		return this.animation;
	}

    /// Returns whether the animation is stopped by time expiration
	public boolean isEnd() {
		return this.isEnd;
	}
}
