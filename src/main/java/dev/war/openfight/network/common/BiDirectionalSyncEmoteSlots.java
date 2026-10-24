package dev.war.openfight.network.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import dev.war.openfight.network.ManagedCustomPacketPayload;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;

public record BiDirectionalSyncEmoteSlots(int playerId, CompoundTag compoundTag) implements ManagedCustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, BiDirectionalSyncEmoteSlots> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.INT,
            BiDirectionalSyncEmoteSlots::playerId,
            ByteBufCodecs.COMPOUND_TAG,
            BiDirectionalSyncEmoteSlots::compoundTag,
            BiDirectionalSyncEmoteSlots::new
        );

    public BiDirectionalSyncEmoteSlots(PlayerPatch<?> playerpatch) {
        this(playerpatch.getId(), playerpatch.getEmoteSlots().getSerialized());
    }
}
