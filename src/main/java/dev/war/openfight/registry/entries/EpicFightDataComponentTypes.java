package dev.war.openfight.registry.entries;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import dev.war.openfight.platform.fabric.registry.DeferredHolder;
import dev.war.openfight.platform.fabric.registry.DeferredRegister;
import dev.war.openfight.main.EpicFightMod;
import dev.war.openfight.skill.Skill;

public final class EpicFightDataComponentTypes {
	private EpicFightDataComponentTypes() {}
	
	public static final DeferredRegister<DataComponentType<?>> REGISTRY = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, EpicFightMod.NAMESPACE);
	
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<Skill>>> SKILL = REGISTRY.register("skill", () ->
		DataComponentType.<Holder<Skill>>builder().persistent(Skill.CODEC).networkSynchronized(Skill.STREAM_CODEC).cacheEncoding().build()
	);
}
