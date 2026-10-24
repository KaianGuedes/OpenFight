package dev.war.openfight.api.client.event.types.hud;

import dev.war.openfight.api.event.LivingEntityPatchEvent;
import dev.war.openfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public class TickTargetIndicatorEvent extends LivingEntityPatchEvent {
	private final LivingEntityPatch<?> target;
	private Type type;
	
	public TickTargetIndicatorEvent(LocalPlayerPatch playerpatch, LivingEntityPatch<?> target) {
		super(playerpatch);
		
		this.target = target;
		this.type = Type.NORMAL;
	}
	
	public LivingEntityPatch<?> getTarget() {
		return this.target;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public Type getIndicatorType() {
		return this.type;
	}
	
	public enum Type {
		NORMAL, FLASH
	}
}