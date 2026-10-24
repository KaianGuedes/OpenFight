package dev.war.openfight.network.client;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import dev.war.openfight.api.utils.ByteBufCodecsExtends;
import dev.war.openfight.network.ManagedCustomPacketPayload;
import dev.war.openfight.skill.Skill;
import dev.war.openfight.skill.SkillSlot;

public record CPChangeSkill(SkillSlot skillSlot, @Nullable Holder<Skill> skill, int skillBookSlotIndex) implements ManagedCustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, CPChangeSkill> STREAM_CODEC =
		StreamCodec.composite(
			ByteBufCodecsExtends.extendableEnumCodec(SkillSlot.ENUM_MANAGER),
			CPChangeSkill::skillSlot,
			ByteBufCodecsExtends.ofNullable(Skill.STREAM_CODEC),
			CPChangeSkill::skill,
			ByteBufCodecs.INT,
			CPChangeSkill::skillBookSlotIndex,
			CPChangeSkill::new
	    );
}
