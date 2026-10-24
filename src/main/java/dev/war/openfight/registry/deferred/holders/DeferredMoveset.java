package dev.war.openfight.registry.deferred.holders;

import net.minecraft.resources.ResourceKey;
import dev.war.openfight.platform.fabric.registry.DeferredHolder;
import org.jetbrains.annotations.ApiStatus;
import dev.war.openfight.api.ex_cap.data.Moveset;

public final class DeferredMoveset extends DeferredHolder<Moveset.Builder, Moveset.Builder> {
    @ApiStatus.Internal
    public DeferredMoveset(ResourceKey<Moveset.Builder> key) {
        super(key);
    }
}
