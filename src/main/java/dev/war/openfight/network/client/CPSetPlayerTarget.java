package dev.war.openfight.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import dev.war.openfight.network.ManagedCustomPacketPayload;

public record CPSetPlayerTarget(int targetEntityId) implements ManagedCustomPacketPayload {
	public static final StreamCodec<ByteBuf, CPSetPlayerTarget> STREAM_CODEC =
		StreamCodec.composite(
			ByteBufCodecs.INT,
			CPSetPlayerTarget::targetEntityId,
			CPSetPlayerTarget::new
	    );
}