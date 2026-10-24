package dev.war.openfight.registry.deferred.holders;

import net.minecraft.resources.ResourceKey;
import dev.war.openfight.platform.fabric.registry.DeferredHolder;
import org.jetbrains.annotations.ApiStatus;
import dev.war.openfight.api.ex_cap.provider.ProviderConditional;

public final class DeferredConditional extends DeferredHolder<ProviderConditional.Builder, ProviderConditional.Builder> {
    @ApiStatus.Internal
    public DeferredConditional(ResourceKey<ProviderConditional.Builder> key) {
        super(key);
    }
}
