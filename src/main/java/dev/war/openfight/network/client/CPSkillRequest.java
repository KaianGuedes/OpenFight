package dev.war.openfight.network.client;

import org.jetbrains.annotations.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import dev.war.openfight.api.utils.ByteBufCodecsExtends;
import dev.war.openfight.network.ManagedCustomPacketPayload;
import dev.war.openfight.skill.SkillSlot;

public class CPSkillRequest implements ManagedCustomPacketPayload {
	public static final StreamCodec<ByteBuf, CPSkillRequest> STREAM_CODEC =
		StreamCodec.composite(
			ByteBufCodecsExtends.extendableEnumCodec(SkillSlot.ENUM_MANAGER),
			CPSkillRequest::skillSlot,
			ByteBufCodecsExtends.enumCodec(WorkType.class),
			CPSkillRequest::workType,
			ByteBufCodecsExtends.ofNullable(ByteBufCodecs.COMPOUND_TAG),
			CPSkillRequest::arguments,
			CPSkillRequest::new
	    );
	
	private final SkillSlot skillSlot;
	private final WorkType workType;
	@Nullable
	private final CompoundTag arguments;
	
	public CPSkillRequest(SkillSlot skillSlot) {
		this(skillSlot, WorkType.CAST);
	}
	
	public CPSkillRequest(SkillSlot skillSlot, @Nullable CompoundTag arguments) {
		this(skillSlot, WorkType.CAST, arguments);
	}
	
	public CPSkillRequest(SkillSlot skillSlot, WorkType active) {
		this.skillSlot = skillSlot;
		this.workType = active;
		this.arguments = null;
	}
	
	public CPSkillRequest(SkillSlot skillSlot, WorkType active, @Nullable CompoundTag arguments) {
		this.skillSlot = skillSlot;
		this.workType = active;
		this.arguments = arguments;
		
	}
	
	public SkillSlot skillSlot() {
		return this.skillSlot;
	}
	
	public WorkType workType() {
		return this.workType;
	}
	
	@Nullable
	public CompoundTag arguments() {
		return this.arguments;
	}
	
	public enum WorkType {
		CAST, CANCEL, HOLD_START
	}
}