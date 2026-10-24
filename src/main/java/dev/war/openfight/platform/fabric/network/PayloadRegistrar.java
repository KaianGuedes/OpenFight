package dev.war.openfight.platform.fabric.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Registers codecs on both physical sides, while deferring client handlers to client initialization. */
public final class PayloadRegistrar {
    private static final List<Runnable> CLIENT_RECEIVERS = new ArrayList<>();
    private static boolean clientInitialized;

    public <T extends CustomPacketPayload> PayloadRegistrar playToClient(CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, IPayloadContext> handler) {
        PayloadTypeRegistry.clientboundPlay().register(type, codec);
        if (clientInitialized) throw new IllegalStateException("Payload registration after client initialization: " + type.id());
        CLIENT_RECEIVERS.add(() -> ClientPayloadRegistration.register(type, handler));
        return this;
    }

    public <T extends CustomPacketPayload> PayloadRegistrar playToServer(CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, IPayloadContext> handler) {
        PayloadTypeRegistry.serverboundPlay().register(type, codec);
        if (!ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> handler.accept(payload, context::player))) {
            throw new IllegalStateException("Duplicate server receiver: " + type.id());
        }
        return this;
    }

    public <T extends CustomPacketPayload> PayloadRegistrar playBidirectional(CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec, DirectionalPayloadHandler<T> handler) {
        return playToClient(type, codec, handler.client()).playToServer(type, codec, handler.server());
    }

    public static void initializeClientReceivers() {
        if (clientInitialized) throw new IllegalStateException("Client payload receivers already initialized");
        CLIENT_RECEIVERS.forEach(Runnable::run);
        CLIENT_RECEIVERS.clear();
        clientInitialized = true;
    }
}
