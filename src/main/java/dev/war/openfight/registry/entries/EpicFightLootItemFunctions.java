package dev.war.openfight.registry.entries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import com.mojang.serialization.MapCodec;
import dev.war.openfight.platform.fabric.registry.DeferredHolder;
import dev.war.openfight.platform.fabric.registry.DeferredRegister;
import dev.war.openfight.data.loot.function.SetSkillFunction;
import dev.war.openfight.main.EpicFightMod;

public final class EpicFightLootItemFunctions {
	private EpicFightLootItemFunctions() {}
	
	public static final DeferredRegister<MapCodec<? extends LootItemFunction>> REGISTRY = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, EpicFightMod.NAMESPACE);
	
	public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<SetSkillFunction>> SKILLS =
        REGISTRY.register("skillbook_loot_table_modifier", () -> SetSkillFunction.CODEC);
}
