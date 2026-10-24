import re
from pathlib import Path
root=Path(__file__).resolve().parents[1]/'src/main/java/dev/war/openfight'
p=root/'data/loot/EpicFightLootTables.java'
raw=p.read_bytes().decode('utf-8');crlf='\r\n' in raw
t=raw.replace('\r\n','\n')
t=t.replace('''import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;
''','''import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.UniformGenerator;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
''')
t=t.replace('@EventBusSubscriber(modid = EpicFightMod.MODID)\n','')
t=t.replace('''	@SubscribeEvent
	public static void onLootTableRegistry(final LootTableLoadEvent event) {''','''	private static Holder<ContextIntProvider> uniformRolls(int min, int max) {
		return Holder.direct(new UniformGenerator(ContextIntProviders.exactly(min), ContextIntProviders.exactly(max)));
	}

	/** Fabric LootTableEvents.MODIFY handler; replaces the NeoForge LootTableLoadEvent subscriber. */
	public static void onLootTableRegistry(ResourceKey<LootTable> lootTableKey, LootTable.Builder tableBuilder, LootTableSource source, HolderLookup.Provider registries) {''')
t=re.sub(r'event\.getName\(\)\.equals\((BuiltInLootTables\.\w+)\.identifier\(\)\)',r'lootTableKey.equals(\1)',t)
t=t.replace('event.getTable().addPool(','tableBuilder.pool(')
t=re.sub(r'UniformGenerator\.between\((\d+)\.0F, (\d+)\.0F\)',r'uniformRolls(\1, \2)',t)
t=t.replace('ConstantValue.exactly(1.0F)','ContextIntProviders.exactly(1)')
t=t.replace('    	OnSkillBookDroppedByEntity.registerEntitySkillLootTable();\n','')
if crlf:t=t.replace('\n','\r\n')
p.write_bytes(t.encode('utf-8'))

# OnSkillBookDroppedByEntity -> plain helper using Fabric MODIFY_DROPS
p=root/'data/loot/OnSkillBookDroppedByEntity.java'
p.write_text('''package dev.war.openfight.data.loot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import dev.war.openfight.api.event.EpicFightEventHooks;
import dev.war.openfight.api.event.types.registry.RegisterMobSkillBookLootTableEvent;

/**
 * Port of the NeoForge global loot modifier: appends skill book drops by entity type to any loot table
 * generated with a THIS_ENTITY parameter. Hooked through Fabric's LootTableEvents.MODIFY_DROPS.
 */
public final class OnSkillBookDroppedByEntity {
	private static final Map<EntityType<?>, LootTable> SKILL_LOOT_TABLE = Maps.newHashMap();
	private static boolean applying;

	private OnSkillBookDroppedByEntity() {}

	public static void registerEntitySkillLootTable() {
		Map<EntityType<?>, LootTable.Builder> builders = new HashMap<> ();
		RegisterMobSkillBookLootTableEvent skillBookLootTableRegistryEvent = new RegisterMobSkillBookLootTableEvent(builders);
		EpicFightEventHooks.Registry.SKILLBOOK_LOOT_TABLE.post(skillBookLootTableRegistryEvent);
		SKILL_LOOT_TABLE.clear();
		builders.forEach((k, v) -> SKILL_LOOT_TABLE.put(k, v.build()));
	}

	public static void modifyDrops(Holder<LootTable> lootTable, LootContext context, List<ItemStack> generatedLoot) {
		if (applying) {
			// Avoid recursive modification while rolling the skill book table.
			return;
		}

		Entity entity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);

		if (entity != null && SKILL_LOOT_TABLE.containsKey(entity.getType())) {
			applying = true;

			try {
				SKILL_LOOT_TABLE.get(entity.getType()).getRandomItemsRaw(context, generatedLoot::add);
			} finally {
				applying = false;
			}
		}
	}
}
''',encoding='utf-8')
# remove global loot modifier registry
p=root/'registry/entries/EpicFightGlobalLootModifiers.java'
p.write_text('''package dev.war.openfight.registry.entries;

/** NeoForge global loot modifiers have no registry on Fabric; see data.loot.OnSkillBookDroppedByEntity. */
public final class EpicFightGlobalLootModifiers {
	private EpicFightGlobalLootModifiers() {}
}
''',encoding='utf-8')
p=root/'registry/EpicFightRegistries.java'
raw=p.read_bytes().decode('utf-8')
raw=re.sub(r'\s*EpicFightGlobalLootModifiers\.REGISTRY,','',raw)
p.write_bytes(raw.encode('utf-8'))
print('ok')
