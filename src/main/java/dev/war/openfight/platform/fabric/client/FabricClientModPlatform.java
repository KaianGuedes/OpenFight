package dev.war.openfight.platform.fabric.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import dev.war.openfight.platform.client.ClientModPlatform;
import dev.war.openfight.platform.client.KeyMappingRegistrar;

@Environment(EnvType.CLIENT)
public final class FabricClientModPlatform implements ClientModPlatform {
    private final KeyMappingRegistrar registrar = KeyMappingHelper::registerKeyMapping;
    @Override public KeyMappingRegistrar keyMappingRegistrar() { return registrar; }
}
