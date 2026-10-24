package dev.war.openfight.api.event.types.player;

import net.minecraft.world.entity.LivingEntity;
import dev.war.openfight.api.event.CancelableEvent;
import dev.war.openfight.api.event.LivingEntityPatchEvent;
import dev.war.openfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class SetTargetEvent extends LivingEntityPatchEvent implements CancelableEvent {
	private final LivingEntity target;
	
	public SetTargetEvent(ServerPlayerPatch playerPatch, LivingEntity target) {
		super(playerPatch);

		this.target = target;
	}

    public final ServerPlayerPatch getPlayerPatch() {
        return (ServerPlayerPatch)this.getEntityPatch();
    }

	public final LivingEntity getTarget() {
		return this.target;
	}
}