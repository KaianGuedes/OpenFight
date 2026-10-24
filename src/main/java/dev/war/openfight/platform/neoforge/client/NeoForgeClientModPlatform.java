package dev.war.openfight.platform.neoforge.client;

import net.neoforged.bus.api.IEventBus;
import org.jetbrains.annotations.NotNull;
import dev.war.openfight.platform.client.ClientModPlatform;
import dev.war.openfight.platform.client.KeyMappingRegistrar;

public final class NeoForgeClientModPlatform implements ClientModPlatform {

    public NeoForgeClientModPlatform(IEventBus modEventBus) {
        modEventBus.addListener(keyMappingRegistrar::onRegisterKeys);
    }

    private final NeoForgeKeyMappingRegistrar keyMappingRegistrar = new NeoForgeKeyMappingRegistrar();

    @Override
    public @NotNull KeyMappingRegistrar keyMappingRegistrar() {
        return keyMappingRegistrar;
    }
}
