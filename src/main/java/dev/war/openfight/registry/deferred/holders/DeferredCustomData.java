package dev.war.openfight.registry.deferred.holders;

import net.minecraft.resources.ResourceKey;
import dev.war.openfight.platform.fabric.registry.DeferredHolder;
import dev.war.openfight.world.capabilities.item.custom.CustomData;

public class DeferredCustomData<T extends CustomData<?>> extends DeferredHolder<CustomData<?>, T> {
    public DeferredCustomData(ResourceKey<CustomData<?>> key) {
        super(key);
    }
}
