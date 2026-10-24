package dev.war.openfight.network.server;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import dev.war.openfight.api.utils.ByteBufCodecsExtends;
import dev.war.openfight.network.ManagedCustomPacketPayload;
import dev.war.openfight.skill.Skill;
import dev.war.openfight.skill.SkillSlot;

public record SPChangeSkill(SkillSlot skillSlot, int entityId, @Nullable Holder<Skill> skill) implements ManagedCustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, SPChangeSkill> STREAM_CODEC =
		StreamCodec.composite(
			ByteBufCodecsExtends.extendableEnumCodec(SkillSlot.ENUM_MANAGER),
			SPChangeSkill::skillSlot,
			ByteBufCodecs.INT,
			SPChangeSkill::entityId,
			ByteBufCodecsExtends.ofNullable(Skill.STREAM_CODEC),
			SPChangeSkill::skill,
			SPChangeSkill::new
	    );
}
