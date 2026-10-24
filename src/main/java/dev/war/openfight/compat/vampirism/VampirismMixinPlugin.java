package dev.war.openfight.compat.vampirism;

import org.jetbrains.annotations.NotNull;
import dev.war.openfight.compat.ModMixinPlugin;

public final class VampirismMixinPlugin extends ModMixinPlugin {
    @Override
    public @NotNull String getModId() {
        return "vampirism";
    }
}
