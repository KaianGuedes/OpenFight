package dev.war.openfight.network.client;

import io.netty.buffer.Unpooled;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import dev.war.openfight.api.utils.ByteBufCodecsExtends;
import dev.war.openfight.network.ManagedCustomPacketPayload;
import dev.war.openfight.skill.SkillDataKey;
import dev.war.openfight.skill.SkillSlot;

public record CPHandleSkillData(SkillSlot skillSlot, Holder<SkillDataKey<?>> skillDataKey, FriendlyByteBuf buffer) implements ManagedCustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, CPHandleSkillData> STREAM_CODEC =
		StreamCodec.composite(
			ByteBufCodecsExtends.extendableEnumCodec(SkillSlot.ENUM_MANAGER),
			CPHandleSkillData::skillSlot,
			SkillDataKey.STREAM_CODEC,
			CPHandleSkillData::skillDataKey,
			ByteBufCodecs.BYTE_ARRAY,
			payload -> payload.buffer().array(),
			CPHandleSkillData::new
	    );
	
	// From codec
	public CPHandleSkillData(SkillSlot skillSlot, Holder<SkillDataKey<?>> skillDataKey, byte[] bytes) {
		this(skillSlot, skillDataKey, new FriendlyByteBuf(Unpooled.copiedBuffer(bytes)));
	}
	
	public CPHandleSkillData(SkillSlot skillSlot, Holder<SkillDataKey<?>> skillDataKey) {
		this(skillSlot, skillDataKey, new FriendlyByteBuf(Unpooled.buffer()));
	}
}