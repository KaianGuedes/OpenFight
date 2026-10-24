package dev.war.openfight.compat.skinlayer3d;

import org.jetbrains.annotations.NotNull;
import dev.war.openfight.compat.ModMixinPlugin;

public final class SkinLayer3DMixinPlugin extends ModMixinPlugin {
    @Override
    public @NotNull String getModId() {
        return "skinlayers3d";
    }
}
