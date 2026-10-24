package dev.war.openfight.api.client.event.types.entity;

import dev.war.openfight.api.event.CancelableEvent;
import dev.war.openfight.api.event.Event;
import dev.war.openfight.network.server.SPEntityPairingPacket;
import dev.war.openfight.world.capabilities.entitypatch.EntityPatch;

public class ProcessEntityPairingPacketEvent extends Event implements CancelableEvent {
	private final EntityPatch<?> entitypatch;
	private final SPEntityPairingPacket packet;
	
	public ProcessEntityPairingPacketEvent(EntityPatch<?> entitypatch, SPEntityPairingPacket packet) {
		this.entitypatch = entitypatch;
		this.packet = packet;
	}
	
	public EntityPatch<?> getEntityPatch() {
		return this.entitypatch;
	}
	
	public SPEntityPairingPacket getPacket() {
		return this.packet;
	}
}
