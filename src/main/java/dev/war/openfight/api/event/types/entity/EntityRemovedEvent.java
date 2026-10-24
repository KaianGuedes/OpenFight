package dev.war.openfight.api.event.types.entity;

import net.minecraft.world.entity.Entity;
import dev.war.openfight.api.event.LivingEntityPatchEvent;
import dev.war.openfight.mixin.common.MixinEntity;
import dev.war.openfight.mixin.common.MixinLivingEntity;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

/// Called not only when an entity is discarded from the leve by [Entity#remove], but also
/// when an entity is disappeared after short death animation. Note this event is only called
/// in the logical server side.
///
/// @see MixinEntity#epicfight$remove
/// @see MixinLivingEntity#epicfight$makePoofParticles
public class EntityRemovedEvent extends LivingEntityPatchEvent {
	private final Entity.RemovalReason removalReason;

	public EntityRemovedEvent(Entity.RemovalReason removalReason, LivingEntityPatch<?> entityPatch) {
        super(entityPatch);

		this.removalReason = removalReason;
	}
	
	public Entity.RemovalReason getRemovalReason() {
		return this.removalReason;
	}
}
