package dev.war.openfight.api.event;

import dev.war.openfight.world.capabilities.EpicFightCapabilities;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

/// Events that are called during an entity's lifecycle
/// 
/// Target entities must hold [LivingEntityPatch] returned by [EpicFightCapabilities#getEntityPatch]
///
/// Developers can add or remove these events throguth [LivingEntityPatch#getEventListener()] in runtime
public abstract class LivingEntityPatchEvent extends Event {
    private final LivingEntityPatch<?> entityPatch;

    public LivingEntityPatchEvent(LivingEntityPatch<?> entityPatch) {
        this.entityPatch = entityPatch;
    }

    public final LivingEntityPatch<?> getEntityPatch() {
        return this.entityPatch;
    }
}