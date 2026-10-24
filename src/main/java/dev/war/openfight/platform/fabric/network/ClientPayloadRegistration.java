package dev.war.openfight.platform.fabric.network;

import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@Environment(EnvType.CLIENT)
final class ClientPayloadRegistration {
    static <T extends CustomPacketPayload> void register(CustomPacketPayload.Type<T> type, BiConsumer<T, IPayloadContext> handler) {
        if (!ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> handler.accept(payload, context::player))) {
            throw new IllegalStateException("Duplicate client receiver: " + type.id());
        }
    }
}
