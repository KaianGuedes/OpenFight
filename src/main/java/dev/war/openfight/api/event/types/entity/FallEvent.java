package dev.war.openfight.api.event.types.entity;

import dev.war.openfight.api.event.LivingEntityPatchEvent;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public class FallEvent extends LivingEntityPatchEvent {
    private final float damageMultiplier;
    private final float distance;

    private boolean playFallAnimation = true;

    public FallEvent(LivingEntityPatch<?> entityPatch, float damageMultiplier, float distance) {
        super(entityPatch);

        this.damageMultiplier = damageMultiplier;
        this.distance = distance;
    }

    public final float getDamageMultiplier() {
        return this.damageMultiplier;
    }

    public final float getDistance() {
        return this.distance;
    }

    public void setPlayFallAnimation(boolean flag) {
        this.playFallAnimation = flag;
    }

    public final boolean doesPlayFallAnimation() {
        return this.playFallAnimation;
    }
}
