package dev.war.openfight.compat.werewolves;

import org.jetbrains.annotations.NotNull;
import dev.war.openfight.compat.ModMixinPlugin;

public final class WerewolvesMixinPlugin extends ModMixinPlugin {
    @Override
    public @NotNull String getModId() {
        return "werewolves";
    }
}
