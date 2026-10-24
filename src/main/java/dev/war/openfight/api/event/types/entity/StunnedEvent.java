package dev.war.openfight.api.event.types.entity;

import dev.war.openfight.api.event.CancelableEvent;
import dev.war.openfight.api.event.LivingEntityPatchEvent;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;
import dev.war.openfight.world.damagesource.EpicFightDamageSource;
import dev.war.openfight.world.damagesource.StunType;

import org.jetbrains.annotations.Nullable;

public class StunnedEvent extends LivingEntityPatchEvent implements CancelableEvent {
	@Nullable
	private final EpicFightDamageSource source;
	private final StunType stunType;
	
	public StunnedEvent(@Nullable EpicFightDamageSource source, LivingEntityPatch<?> entityPatch, StunType stunType) {
        super(entityPatch);

		this.source = source;
		this.stunType = stunType;
	}

    @Nullable
	public final EpicFightDamageSource getDamageSource() {
		return this.source;
	}
	
	public final StunType getStunType() {
		return this.stunType;
	}
}
