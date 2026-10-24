package dev.war.openfight.platform.fabric.network;

import java.util.LinkedHashSet;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

public final class PacketDistributor {
    private static volatile MinecraftServer server;
    private PacketDistributor() {}
    public static void initialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(value -> server = value);
        ServerLifecycleEvents.SERVER_STOPPED.register(value -> { if (server == value) server = null; });
    }
    public static void sendToAllPlayers(CustomPacketPayload first, CustomPacketPayload... others) {
        MinecraftServer active = server;
        if (active == null) throw new IllegalStateException("No running server");
        PlayerLookup.all(active).forEach(player -> sendToPlayer(player, first, others));
    }
    public static void sendToServer(CustomPacketPayload first, CustomPacketPayload... others) {
        ClientPacketDistributor.sendToServer(first, others);
    }
    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload first, CustomPacketPayload... others) {
        // Entity join/load callbacks can run before ServerPlayer's connection is installed.
        if (player.connection == null) return;
        ServerPlayNetworking.send(player, first);
        for (CustomPacketPayload payload : others) ServerPlayNetworking.send(player, payload);
    }
    public static void sendToPlayersTrackingEntity(Entity entity, CustomPacketPayload first, CustomPacketPayload... others) {
        PlayerLookup.tracking(entity).forEach(player -> sendToPlayer(player, first, others));
    }
    public static void sendToPlayersTrackingEntityAndSelf(Entity entity, CustomPacketPayload first, CustomPacketPayload... others) {
        var players = new LinkedHashSet<>(PlayerLookup.tracking(entity));
        if (entity instanceof ServerPlayer player) players.add(player);
        players.forEach(player -> sendToPlayer(player, first, others));
    }
    public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos chunk, CustomPacketPayload first, CustomPacketPayload... others) {
        PlayerLookup.tracking(level, chunk).forEach(player -> sendToPlayer(player, first, others));
    }
}
