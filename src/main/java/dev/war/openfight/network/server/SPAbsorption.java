package dev.war.openfight.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import dev.war.openfight.network.ManagedCustomPacketPayload;

public record SPAbsorption(int entityId, float amount) implements ManagedCustomPacketPayload {
	public static final StreamCodec<ByteBuf, SPAbsorption> STREAM_CODEC =
		StreamCodec.composite(
	        ByteBufCodecs.INT,
	        SPAbsorption::entityId,
	        ByteBufCodecs.FLOAT,
	        SPAbsorption::amount,
	        SPAbsorption::new
	    );
}