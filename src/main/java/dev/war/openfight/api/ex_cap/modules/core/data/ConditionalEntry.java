package dev.war.openfight.api.ex_cap.modules.core.data;

import net.minecraft.resources.Identifier;
import dev.war.openfight.api.ex_cap.provider.ProviderConditional;

@Deprecated(forRemoval = true)
public record ConditionalEntry(Identifier id, ProviderConditional.Builder builder) { }