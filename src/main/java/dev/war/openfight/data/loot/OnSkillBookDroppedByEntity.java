package dev.war.openfight.data.loot;

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

		Entity entity = context.getOptional(LootContextParams.THIS_ENTITY);

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
