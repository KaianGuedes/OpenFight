package dev.war.openfight;

import org.jetbrains.annotations.NotNull;
import dev.war.openfight.platform.client.ClientModPlatform;
import dev.war.openfight.platform.client.ClientModPlatformProvider;

/// Common functionalities shared between the platform client entrypoints.
public final class EpicFightClient {
    private EpicFightClient() {

    }

    /// Initializes common client-only functionalities.
    ///
    /// Provides the global [ClientModPlatform] instance given by the mod platform (e.g., NeoForge, Fabric)
    /// to common vanilla code for sharing.
    public static void initialize(@NotNull final ClientModPlatform platform) {
        ClientModPlatformProvider.initialize(platform);
    }
}
