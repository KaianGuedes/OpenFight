package dev.war.openfight.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import dev.war.openfight.network.ManagedCustomPacketPayload;

public record SPUpdatePlayerInput(int entityId, float forward, float strafe) implements ManagedCustomPacketPayload {
	public static final StreamCodec<ByteBuf, SPUpdatePlayerInput> STREAM_CODEC =
		StreamCodec.composite(
			ByteBufCodecs.INT,
			SPUpdatePlayerInput::entityId,
			ByteBufCodecs.FLOAT,
			SPUpdatePlayerInput::forward,
			ByteBufCodecs.FLOAT,
			SPUpdatePlayerInput::strafe,
			SPUpdatePlayerInput::new
	    );
}