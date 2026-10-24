package dev.war.openfight.api.ex_cap.modules.core.data;

import net.minecraft.resources.Identifier;
import dev.war.openfight.platform.fabric.registry.DeferredHolder;
import dev.war.openfight.registry.deferred.holders.DeferredPreset;
import dev.war.openfight.world.capabilities.item.WeaponCapability;

/**
 * Represents a single entry in the builder data file.
 * @param id The ID of the entry.
 * @param template The template associated with the entry.
 * @deprecated For Removal. DeferredHolder does exactly the same thing. So this has been removed and transferred to {@link DeferredPreset} which extends {@link DeferredHolder}
 */
@Deprecated(forRemoval = true)
public record BuilderEntry(Identifier id, WeaponCapability.Builder template) { }