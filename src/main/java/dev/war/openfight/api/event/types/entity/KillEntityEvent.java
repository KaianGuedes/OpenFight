package dev.war.openfight.api.event.types.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import dev.war.openfight.api.event.LivingEntityPatchEvent;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public class KillEntityEvent extends LivingEntityPatchEvent {
	private final LivingEntity killedEntity;
	private final DamageSource damagesource;
	
	public KillEntityEvent(LivingEntityPatch<?> entityPatch, LivingEntity killedEntity, DamageSource damagesource) {
		super(entityPatch);
		this.killedEntity = killedEntity;
		this.damagesource = damagesource;
	}
	
	public LivingEntity getKilledEntity() {
		return this.killedEntity;
	}
	
	public DamageSource getDamageSource() {
		return this.damagesource;
	}
}
