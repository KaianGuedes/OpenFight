package dev.war.openfight.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import dev.war.openfight.api.utils.ByteBufCodecsExtends;
import dev.war.openfight.network.ManagedCustomPacketPayload;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;

public record SPChangePlayerMode(int entityId, PlayerPatch.PlayerMode mode) implements ManagedCustomPacketPayload {
	public static final StreamCodec<ByteBuf, SPChangePlayerMode> STREAM_CODEC =
		StreamCodec.composite(
			ByteBufCodecs.INT,
			SPChangePlayerMode::entityId,
			ByteBufCodecsExtends.enumCodec(PlayerPatch.PlayerMode.class),
			SPChangePlayerMode::mode,
			SPChangePlayerMode::new
	    );
}