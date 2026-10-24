package dev.war.openfight.api.event.types.player;

import dev.war.openfight.api.event.CancelableEvent;
import dev.war.openfight.api.event.LivingEntityPatchEvent;
import dev.war.openfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class ComboAttackEvent extends LivingEntityPatchEvent implements CancelableEvent {
	public ComboAttackEvent(ServerPlayerPatch playerPatch) {
        super(playerPatch);
	}

    public ServerPlayerPatch getPlayerPatch() {
        return (ServerPlayerPatch)this.getEntityPatch();
    }
}