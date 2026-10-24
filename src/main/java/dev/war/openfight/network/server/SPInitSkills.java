package dev.war.openfight.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import dev.war.openfight.network.ManagedCustomPacketPayload;
import dev.war.openfight.world.capabilities.skill.PlayerSkills;

public record SPInitSkills(CompoundTag serializedSkill) implements ManagedCustomPacketPayload {
	public static final StreamCodec<ByteBuf, SPInitSkills> STREAM_CODEC =
		StreamCodec.composite(
			ByteBufCodecs.COMPOUND_TAG,
			SPInitSkills::serializedSkill,
			SPInitSkills::new
	    );
	
	public SPInitSkills(PlayerSkills skillCapability) {
		this(skillCapability.write(new CompoundTag()));
	}
}