package dev.war.openfight.api.event.types.player;

import net.minecraft.world.InteractionResult;
import dev.war.openfight.api.event.CancelableEvent;
import dev.war.openfight.api.event.LivingEntityPatchEvent;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;

public class StartUsingItemEvent extends LivingEntityPatchEvent implements CancelableEvent {
    private InteractionResult cancelationResult;

    public StartUsingItemEvent(PlayerPatch<?> entityPatch) {
        super(entityPatch);

        this.cancelationResult = InteractionResult.PASS;
    }

    public void setCancelationResult(InteractionResult cancelationResult) {
        this.cancelationResult = cancelationResult;
    }

    public InteractionResult getCancelationResult() {
        return this.cancelationResult;
    }

    public PlayerPatch<?> getPlayerPatch() {
        return (PlayerPatch<?>)this.getEntityPatch();
    }
}
