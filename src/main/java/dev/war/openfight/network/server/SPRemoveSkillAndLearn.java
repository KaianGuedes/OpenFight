package dev.war.openfight.network.server;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import dev.war.openfight.api.utils.ByteBufCodecsExtends;
import dev.war.openfight.network.ManagedCustomPacketPayload;
import dev.war.openfight.skill.Skill;
import dev.war.openfight.skill.SkillSlot;

public record SPRemoveSkillAndLearn(Holder<Skill> skill, SkillSlot skillSlot) implements ManagedCustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, SPRemoveSkillAndLearn> STREAM_CODEC =
		StreamCodec.composite(
			Skill.STREAM_CODEC,
			SPRemoveSkillAndLearn::skill,
			ByteBufCodecsExtends.extendableEnumCodec(SkillSlot.ENUM_MANAGER),
			SPRemoveSkillAndLearn::skillSlot,
			SPRemoveSkillAndLearn::new
	    );
	
	public SPRemoveSkillAndLearn(Skill skill, SkillSlot skillSlot) {
		this(skill.holder(), skillSlot);
	}
}