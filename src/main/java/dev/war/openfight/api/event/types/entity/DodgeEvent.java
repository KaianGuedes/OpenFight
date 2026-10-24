package dev.war.openfight.api.event.types.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import dev.war.openfight.api.event.LivingEntityPatchEvent;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public class DodgeEvent extends LivingEntityPatchEvent {
	private final DamageSource damageSource;
	private final Vec3 location;
	
	public DodgeEvent(LivingEntityPatch<?> entityPatch, DamageSource damageSource, Vec3 location) {
		super(entityPatch);
		
		this.damageSource = damageSource;
		this.location = location;
	}
	
	public DamageSource getDamageSource() {
		return this.damageSource;
	}
	
	public Vec3 getLocation() {
		return this.location;
	}
}