package dev.war.openfight.compat.geckolib;

import org.jetbrains.annotations.NotNull;
import dev.war.openfight.compat.ModMixinPlugin;

public final class GeckolibMixinPlugin extends ModMixinPlugin {
    @Override
    public @NotNull String getModId() {
        return "geckolib";
    }
}
