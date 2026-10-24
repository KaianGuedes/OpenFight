package dev.war.openfight.platform.neoforge;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;
import dev.war.openfight.platform.ModPlatform;

public final class NeoForgeModPlatform implements ModPlatform {
    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public boolean isModLoaded(@NotNull final String id) {
        return ModList.get().isLoaded(id);
    }
}
