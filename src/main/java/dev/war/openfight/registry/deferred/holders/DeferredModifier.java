package dev.war.openfight.registry.deferred.holders;

import net.minecraft.resources.ResourceKey;
import dev.war.openfight.platform.fabric.registry.DeferredHolder;
import dev.war.openfight.api.ex_cap.data.modifier.WeaponModifier;

public final class DeferredModifier extends DeferredHolder<WeaponModifier.Builder, WeaponModifier.Builder>
{
    public DeferredModifier(ResourceKey<WeaponModifier.Builder> key) {
        super(key);
    }
}
