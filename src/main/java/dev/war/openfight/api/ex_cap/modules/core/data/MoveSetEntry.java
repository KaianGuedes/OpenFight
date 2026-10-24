package dev.war.openfight.api.ex_cap.modules.core.data;

import net.minecraft.resources.Identifier;
import dev.war.openfight.platform.fabric.registry.DeferredRegister;
import dev.war.openfight.api.ex_cap.data.Moveset;
import dev.war.openfight.registry.deferred.MovesetRegister;
import dev.war.openfight.registry.deferred.holders.DeferredMoveset;

/**
 * Represents an entry in the {@link Moveset} registry, containing the ID and the builder for the {@link Moveset}.
 * @param id The ID of the moveset.
 * @param builder The builder used to construct the moveset.
 * @deprecated For Removal. Replaced with {@link DeferredMoveset} and registered via {@link MovesetRegister} or by a {@link DeferredRegister}
 */
@Deprecated(forRemoval = true)
public record MoveSetEntry(Identifier id, Moveset.Builder builder) {}