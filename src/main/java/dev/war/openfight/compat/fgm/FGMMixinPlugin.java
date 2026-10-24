package dev.war.openfight.compat.fgm;

import org.jetbrains.annotations.NotNull;
import dev.war.openfight.compat.ModMixinPlugin;

public class FGMMixinPlugin extends ModMixinPlugin {
    @Override
    public @NotNull String getModId() {
        return "wildfire_gender";
    }
}
