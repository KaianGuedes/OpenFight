package dev.war.openfight.api.event.types.player;

import dev.war.openfight.api.event.CancelableEvent;
import dev.war.openfight.api.event.LivingEntityPatchEvent;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;

public class TogglePlayerModeEvent extends LivingEntityPatchEvent implements CancelableEvent {
	private final PlayerPatch.PlayerMode playerMode;
	
	public TogglePlayerModeEvent(PlayerPatch<?> playerPatch, PlayerPatch.PlayerMode playerMode) {
		super(playerPatch);
		this.playerMode = playerMode;
	}
	
	public PlayerPatch<?> getPlayerPatch() {
		return (PlayerPatch<?>) this.getEntityPatch();
	}
	
	public PlayerPatch.PlayerMode getPlayerMode() {
		return this.playerMode;
	}
}
