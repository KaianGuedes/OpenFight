package dev.war.openfight.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import dev.war.openfight.api.utils.ByteBufCodecsExtends;
import dev.war.openfight.network.ManagedCustomPacketPayload;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;

public record CPChangePlayerMode(PlayerPatch.PlayerMode mode) implements ManagedCustomPacketPayload {
	public static final StreamCodec<ByteBuf, CPChangePlayerMode> STREAM_CODEC =
		StreamCodec.composite(
			ByteBufCodecsExtends.enumCodec(PlayerPatch.PlayerMode.class),
			CPChangePlayerMode::mode,
			CPChangePlayerMode::new
	    );
}