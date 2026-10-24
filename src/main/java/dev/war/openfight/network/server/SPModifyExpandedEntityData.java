package dev.war.openfight.network.server;

import io.netty.buffer.Unpooled;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import dev.war.openfight.network.ManagedCustomPacketPayload;
import dev.war.openfight.world.entity.data.ExpandedEntityDataAccessor;

public record SPModifyExpandedEntityData(int entityId, Holder<ExpandedEntityDataAccessor<?>> expandedEntityDataAccessor, FriendlyByteBuf buffer) implements ManagedCustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, SPModifyExpandedEntityData> STREAM_CODEC =
		StreamCodec.composite(
			ByteBufCodecs.INT,
			SPModifyExpandedEntityData::entityId,
			ExpandedEntityDataAccessor.STREAM_CODEC,
			SPModifyExpandedEntityData::expandedEntityDataAccessor,
			ByteBufCodecs.BYTE_ARRAY,
			payload -> payload.buffer().array(),
			SPModifyExpandedEntityData::new
	    );
	
	public SPModifyExpandedEntityData(int entityId, Holder<ExpandedEntityDataAccessor<?>> expandedEntityDataAccessor, byte[] bytes) {
		this(entityId, expandedEntityDataAccessor, new FriendlyByteBuf(Unpooled.copiedBuffer(bytes)));
	}
}