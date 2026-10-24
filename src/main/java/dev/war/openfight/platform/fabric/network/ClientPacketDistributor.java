package dev.war.openfight.platform.fabric.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@Environment(EnvType.CLIENT)
final class ClientPacketDistributor {
    static void sendToServer(CustomPacketPayload first, CustomPacketPayload... others) {
        ClientPlayNetworking.send(first);
        for (CustomPacketPayload payload : others) ClientPlayNetworking.send(payload);
    }
}
