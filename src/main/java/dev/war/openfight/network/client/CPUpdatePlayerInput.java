package dev.war.openfight.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import dev.war.openfight.network.ManagedCustomPacketPayload;

public record CPUpdatePlayerInput(int entityId, float forward, float strafe) implements ManagedCustomPacketPayload {
	public static final StreamCodec<ByteBuf, CPUpdatePlayerInput> STREAM_CODEC =
		StreamCodec.composite(
			ByteBufCodecs.INT,
			CPUpdatePlayerInput::entityId,
			ByteBufCodecs.FLOAT,
			CPUpdatePlayerInput::forward,
			ByteBufCodecs.FLOAT,
			CPUpdatePlayerInput::strafe,
			CPUpdatePlayerInput::new
	    );
}