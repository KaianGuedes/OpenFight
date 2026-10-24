package dev.war.openfight.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import dev.war.openfight.api.utils.ByteBufCodecsExtends;
import dev.war.openfight.network.ManagedCustomPacketPayload;
import dev.war.openfight.world.gamerule.EpicFightGameRules;

public record SPChangeGamerule(EpicFightGameRules.KeyValuePair keyValuePair) implements ManagedCustomPacketPayload {
	public static final StreamCodec<ByteBuf, SPChangeGamerule> STREAM_CODEC =
		StreamCodec.composite(
			ByteBufCodecsExtends.GAMERULE,
			SPChangeGamerule::keyValuePair,
			SPChangeGamerule::new
	    );
}