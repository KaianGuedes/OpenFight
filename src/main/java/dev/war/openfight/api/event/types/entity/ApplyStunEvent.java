package dev.war.openfight.api.event.types.entity;

import dev.war.openfight.api.animation.types.StaticAnimation;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.event.LivingEntityPatchEvent;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;
import dev.war.openfight.world.damagesource.StunType;

/// Use [StunnedEvent] to completely disable stun
public class ApplyStunEvent extends LivingEntityPatchEvent {
	private final StunType stunType;
    private AssetAccessor<? extends StaticAnimation> stunAnimation;
    private float stunTime;

	public ApplyStunEvent(LivingEntityPatch<?> entityPatch, StunType stunType, AssetAccessor<? extends StaticAnimation> stunAnimation, float stunTime) {
        super(entityPatch);

		this.stunType = stunType;
        this.stunAnimation = stunAnimation;
	}

	public final StunType getStunType() {
		return this.stunType;
	}

    public final AssetAccessor<? extends StaticAnimation> getStunAnimation() {
        return stunAnimation;
    }

    public void setSTunAnimation(AssetAccessor<? extends StaticAnimation> stunAnimation) {
        this.stunAnimation = stunAnimation;
    }

    public float getStunTime() {
        return stunTime;
    }

    public void setStunTime(final float stunTime) {
        this.stunTime = stunTime;
    }
}
