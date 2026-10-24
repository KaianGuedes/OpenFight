package dev.war.openfight.network.server;

import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import dev.war.openfight.api.utils.ByteBufCodecsExtends;
import dev.war.openfight.network.ManagedCustomPacketPayload;
import dev.war.openfight.skill.Skill;

public record SPAddLearnedSkill(List<Holder<Skill>> skills) implements ManagedCustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, SPAddLearnedSkill> STREAM_CODEC =
		StreamCodec.composite(
	        ByteBufCodecsExtends.listOfHolder(Skill.STREAM_CODEC),
	        SPAddLearnedSkill::skills,
	        SPAddLearnedSkill::new
	    );
}