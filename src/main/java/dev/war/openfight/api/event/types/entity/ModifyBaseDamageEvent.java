package dev.war.openfight.api.event.types.entity;

import dev.war.openfight.api.event.LivingEntityPatchEvent;
import dev.war.openfight.api.utils.math.ValueModifier;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

/// Fired when compute the final attack damage of an entity.
/// This event is called on both side, to modify the delivering
/// damage in server side while it's for modifying tooltip
/// in the client side
public class ModifyBaseDamageEvent extends LivingEntityPatchEvent {
	private final ValueModifier.ResultCalculator modifiedDamageCalculator;
	private final float baseDamage;
	
	public ModifyBaseDamageEvent(LivingEntityPatch<?> entityPatch, float damage, ValueModifier.ResultCalculator modifiedDamageCalculator) {
		super(entityPatch);
		
		this.baseDamage = damage;
		this.modifiedDamageCalculator = modifiedDamageCalculator;
	}
	
	public float getBaseDamage() {
		return this.baseDamage;
	}
	
	public void attachValueModifier(ValueModifier modifier) {
		this.modifiedDamageCalculator.attach(modifier);
	}
	
	public float calculateModifiedDamage() {
		return this.modifiedDamageCalculator.getResult(this.baseDamage);
	}
}