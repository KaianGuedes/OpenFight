package dev.war.openfight.platform.fabric;

import net.fabricmc.loader.api.FabricLoader;
import dev.war.openfight.platform.ModPlatform;

public final class FabricModPlatform implements ModPlatform {
    @Override public boolean isDevelopmentEnvironment() { return FabricLoader.getInstance().isDevelopmentEnvironment(); }
    @Override public boolean isModLoaded(String id) { return FabricLoader.getInstance().isModLoaded(id); }
}
