package dev.war.openfight.registry.deferred.holders;

import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;
import dev.war.openfight.world.capabilities.item.CapabilityItem;
import dev.war.openfight.world.capabilities.item.WeaponCapability;


public final class DeferredWeapon extends DeferredPreset<WeaponCapability.Builder> {
    @ApiStatus.Internal
    public DeferredWeapon(ResourceKey<CapabilityItem.Builder<?>> key) {
        super(key);
    }
}
