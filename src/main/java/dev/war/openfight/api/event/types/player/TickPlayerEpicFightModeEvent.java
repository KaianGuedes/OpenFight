package dev.war.openfight.api.event.types.player;

import dev.war.openfight.api.event.CancelableEvent;
import dev.war.openfight.api.event.LivingEntityPatchEvent;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;

public class TickPlayerEpicFightModeEvent extends LivingEntityPatchEvent implements CancelableEvent {
	public TickPlayerEpicFightModeEvent(PlayerPatch<?> playerPatch) {
        super(playerPatch);
	}
	
	public PlayerPatch<?> getPlayerPatch() {
		return (PlayerPatch<?>)this.getEntityPatch();
	}
}