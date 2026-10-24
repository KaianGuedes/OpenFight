package dev.war.openfight.network.common;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import dev.war.openfight.api.utils.ByteBufCodecsExtends;
import dev.war.openfight.network.ManagedCustomPacketPayload;

public record BiDirectionalSyncAnimationPositionPacket(int entityId, float elapsedTime, Vec3 position, int lerpSteps) implements ManagedCustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, BiDirectionalSyncAnimationPositionPacket> STREAM_CODEC =
		StreamCodec.composite(
	        ByteBufCodecs.INT,
	        BiDirectionalSyncAnimationPositionPacket::entityId,
	        ByteBufCodecs.FLOAT,
	        BiDirectionalSyncAnimationPositionPacket::elapsedTime,
	        ByteBufCodecsExtends.VEC3,
	        BiDirectionalSyncAnimationPositionPacket::position,
	        ByteBufCodecs.INT,
	        BiDirectionalSyncAnimationPositionPacket::lerpSteps,
	        BiDirectionalSyncAnimationPositionPacket::new
	    );
}
