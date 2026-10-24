package yesman.epicfight.data.loot;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.UniformGenerator;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import yesman.epicfight.api.event.types.registry.RegisterMobSkillBookLootTableEvent;
import yesman.epicfight.config.CommonConfig;
import yesman.epicfight.data.loot.function.SetSkillFunction;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.registry.entries.EpicFightItems;
import yesman.epicfight.registry.entries.EpicFightSkills;

public class EpicFightLootTables {
	private static Holder<ContextIntProvider> uniformRolls(int min, int max) {
		return Holder.direct(new UniformGenerator(ContextIntProviders.exactly(min), ContextIntProviders.exactly(max)));
	}

	/** Fabric LootTableEvents.MODIFY handler; replaces the NeoForge LootTableLoadEvent subscriber. */
	public static void onLootTableRegistry(ResourceKey<LootTable> lootTableKey, LootTable.Builder tableBuilder, LootTableSource source, HolderLookup.Provider registries) {
		int modifier = CommonConfig.skillBookChestLootModifier;
		int dropChance = 100 + modifier;
		int antiDropChance = 100 - modifier;
		float dropChanceModifier = dropChance / (float)(antiDropChance + dropChance);
		
    	if (lootTableKey.equals(BuiltInLootTables.DESERT_PYRAMID)) {
    		tableBuilder.pool(LootPool.lootPool().setRolls(uniformRolls(1, 2))
    			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
    				EpicFightSkills.BERSERKER,
    				EpicFightSkills.STAMINA_PILLAGER,
    				EpicFightSkills.TECHNICIAN,
    				EpicFightSkills.SWORD_MASTER,
    				EpicFightSkills.GUARD,
    				EpicFightSkills.STEP,
    				EpicFightSkills.ROLL,
    				EpicFightSkills.PHANTOM_ASCENT
    			)).when(LootItemRandomChanceCondition.randomChance(dropChanceModifier)))
    		.build());
    		
    		tableBuilder.pool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.25F))
    			.add(LootItem.lootTableItem(EpicFightItems.UCHIGATANA.get()))
    		.build());
    	}
    	
    	if (lootTableKey.equals(BuiltInLootTables.JUNGLE_TEMPLE)) {
    		tableBuilder.pool(LootPool.lootPool().setRolls(uniformRolls(1, 2))
        		.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
    				EpicFightSkills.BERSERKER,
    				EpicFightSkills.STAMINA_PILLAGER,
    				EpicFightSkills.TECHNICIAN,
    				EpicFightSkills.SWORD_MASTER,
    				EpicFightSkills.GUARD,
    				EpicFightSkills.STEP,
    				EpicFightSkills.ROLL,
    				EpicFightSkills.PHANTOM_ASCENT
        		))).when(LootItemRandomChanceCondition.randomChance(dropChanceModifier))
        	.build());
    		
    		tableBuilder.pool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.25F))
    			.add(LootItem.lootTableItem(EpicFightItems.UCHIGATANA.get()))
    		.build());
    	}
    	
    	if (lootTableKey.equals(BuiltInLootTables.SIMPLE_DUNGEON)) {
    		tableBuilder.pool(LootPool.lootPool().setRolls(uniformRolls(1, 3))
        		.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
    				EpicFightSkills.BERSERKER,
    				EpicFightSkills.STAMINA_PILLAGER,
    				EpicFightSkills.TECHNICIAN,
    				EpicFightSkills.SWORD_MASTER,
    				EpicFightSkills.GUARD,
    				EpicFightSkills.STEP,
    				EpicFightSkills.ROLL
        		))).when(LootItemRandomChanceCondition.randomChance(dropChanceModifier * 0.3F))
        	.build());
    	}
    	
    	if (lootTableKey.equals(BuiltInLootTables.ABANDONED_MINESHAFT)) {
    		tableBuilder.pool(LootPool.lootPool().setRolls(uniformRolls(1, 3))
        		.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
    				EpicFightSkills.BERSERKER,
    				EpicFightSkills.STAMINA_PILLAGER,
    				EpicFightSkills.TECHNICIAN,
    				EpicFightSkills.SWORD_MASTER,
    				EpicFightSkills.GUARD,
    				EpicFightSkills.STEP,
    				EpicFightSkills.ROLL
        		))).when(LootItemRandomChanceCondition.randomChance(dropChanceModifier * 0.3F))
        	.build());
    	}
    	
    	if (lootTableKey.equals(BuiltInLootTables.PILLAGER_OUTPOST)) {
    		tableBuilder.pool(LootPool.lootPool().setRolls(uniformRolls(1, 3))
        		.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
    				EpicFightSkills.BERSERKER,
    				EpicFightSkills.STAMINA_PILLAGER,
    				EpicFightSkills.TECHNICIAN,
    				EpicFightSkills.SWORD_MASTER,
    				EpicFightSkills.GUARD,
    				EpicFightSkills.STEP,
    				EpicFightSkills.ROLL
        		))).when(LootItemRandomChanceCondition.randomChance(dropChanceModifier * 0.3F))
        	.build());
    	}
    	
    	if (lootTableKey.equals(BuiltInLootTables.UNDERWATER_RUIN_BIG)) {
    		tableBuilder.pool(LootPool.lootPool().setRolls(uniformRolls(1, 3))
        		.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
    				EpicFightSkills.BERSERKER,
    				EpicFightSkills.STAMINA_PILLAGER,
    				EpicFightSkills.TECHNICIAN,
    				EpicFightSkills.SWORD_MASTER,
    				EpicFightSkills.GUARD,
    				EpicFightSkills.STEP,
    				EpicFightSkills.ROLL,
    				EpicFightSkills.PHANTOM_ASCENT
        		))).when(LootItemRandomChanceCondition.randomChance(dropChanceModifier * 0.3F))
        	.build());
    	}
    	
    	if (lootTableKey.equals(BuiltInLootTables.SHIPWRECK_MAP)) {
    		tableBuilder.pool(LootPool.lootPool().setRolls(uniformRolls(1, 2))
        		.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
    				EpicFightSkills.BERSERKER,
    				EpicFightSkills.STAMINA_PILLAGER,
    				EpicFightSkills.TECHNICIAN,
    				EpicFightSkills.SWORD_MASTER,
    				EpicFightSkills.GUARD,
    				EpicFightSkills.STEP,
    				EpicFightSkills.ROLL
        		))).when(LootItemRandomChanceCondition.randomChance(dropChanceModifier * 0.3F))
        	.build());
    	}
    	
    	if (lootTableKey.equals(BuiltInLootTables.STRONGHOLD_LIBRARY)) {
    		tableBuilder.pool(LootPool.lootPool().setRolls(uniformRolls(1, 5))
    			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
    				EpicFightSkills.BERSERKER,
    				EpicFightSkills.STAMINA_PILLAGER,
    				EpicFightSkills.TECHNICIAN,
    				EpicFightSkills.SWORD_MASTER,
    				EpicFightSkills.HYPERVITALITY,
    				EpicFightSkills.FORBIDDEN_STRENGTH,
    				EpicFightSkills.GUARD,
    				EpicFightSkills.STEP,
    				EpicFightSkills.ROLL,
    				EpicFightSkills.PHANTOM_ASCENT
    			))).when(LootItemRandomChanceCondition.randomChance(dropChanceModifier * 0.3F))
    		.build());
    	}
    	
    	if (lootTableKey.equals(BuiltInLootTables.WOODLAND_MANSION)) {
    		tableBuilder.pool(LootPool.lootPool().setRolls(uniformRolls(1, 5))
    			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
    				EpicFightSkills.BERSERKER,
    				EpicFightSkills.STAMINA_PILLAGER,
    				EpicFightSkills.TECHNICIAN,
    				EpicFightSkills.SWORD_MASTER,
    				EpicFightSkills.HYPERVITALITY,
    				EpicFightSkills.FORBIDDEN_STRENGTH,
    				EpicFightSkills.GUARD,
    				EpicFightSkills.STEP,
    				EpicFightSkills.ROLL,
    				EpicFightSkills.PHANTOM_ASCENT
    			))).when(LootItemRandomChanceCondition.randomChance(dropChanceModifier * 0.3F))
    		.build());
    	}
    	
    	if (lootTableKey.equals(BuiltInLootTables.BASTION_OTHER)) {
    		tableBuilder.pool(LootPool.lootPool().setRolls(uniformRolls(1, 4))
    			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
    				EpicFightSkills.BERSERKER,
    				EpicFightSkills.STAMINA_PILLAGER,
    				EpicFightSkills.TECHNICIAN,
    				EpicFightSkills.SWORD_MASTER,
    				EpicFightSkills.HYPERVITALITY,
    				EpicFightSkills.FORBIDDEN_STRENGTH,
    				EpicFightSkills.GUARD,
    				EpicFightSkills.STEP,
    				EpicFightSkills.ROLL,
    				EpicFightSkills.PHANTOM_ASCENT
    			))).when(LootItemRandomChanceCondition.randomChance(dropChanceModifier * 0.3F))
    		.build());
    	}
    	
    }

	public static void createSkillLootTable(RegisterMobSkillBookLootTableEvent skillLootTableRegistryEvent) {
        int modifier = CommonConfig.skillBookMobDropChanceModifier;
        int dropChance = 100 + modifier;
        int antiDropChance = 100 - modifier;
        float dropChanceModifier = antiDropChance == 0 ? Float.MAX_VALUE : dropChance / (float) antiDropChance;

        skillLootTableRegistryEvent.put(
            EntityTypes.ZOMBIE,
            LootTable.lootTable().withPool(
                LootPool.lootPool()
                    .setRolls(ContextIntProviders.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
                    .add(
                        LootItem
                            .lootTableItem(EpicFightItems.SKILLBOOK.get())
                            .apply(
                                SetSkillFunction.builder(
                                    1.0F, EpicFightSkills.BERSERKER,
                                    1.0F, EpicFightSkills.STAMINA_PILLAGER,
                                    1.0F, EpicFightSkills.ROLL,
                                    1.0F, EpicFightSkills.STEP,
                                    1.0F, EpicFightSkills.GUARD,
                                    0.5F, EpicFightSkills.ENDURANCE
                                )
                            )
                    )
            )
        );

        skillLootTableRegistryEvent.put(EntityTypes.HUSK, LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
                .add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
                    1.0F, EpicFightSkills.BERSERKER,
                    1.0F, EpicFightSkills.STAMINA_PILLAGER,
                    1.0F, EpicFightSkills.ROLL,
                    1.0F, EpicFightSkills.STEP,
                    1.0F, EpicFightSkills.GUARD,
                    0.5F, EpicFightSkills.ENDURANCE
                )))
        ));
        skillLootTableRegistryEvent.put(EntityTypes.DROWNED, LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
                .add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
                    1.0F, EpicFightSkills.BERSERKER,
                    1.0F, EpicFightSkills.STAMINA_PILLAGER,
                    1.0F, EpicFightSkills.ROLL,
                    1.0F, EpicFightSkills.STEP,
                    1.0F, EpicFightSkills.GUARD,
                    0.5F, EpicFightSkills.ENDURANCE
                )))
        ));
        skillLootTableRegistryEvent.put(EntityTypes.SKELETON, LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
                .add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
                    1.0F, EpicFightSkills.SWORD_MASTER,
                    1.0F, EpicFightSkills.TECHNICIAN,
                    1.0F, EpicFightSkills.ROLL,
                    1.0F, EpicFightSkills.STEP,
                    1.0F, EpicFightSkills.GUARD,
                    0.5F, EpicFightSkills.EMERGENCY_ESCAPE
                )))));
        skillLootTableRegistryEvent.put(EntityTypes.STRAY, LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
                .add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
                    1.0F, EpicFightSkills.SWORD_MASTER,
                    1.0F, EpicFightSkills.TECHNICIAN,
                    1.0F, EpicFightSkills.ROLL,
                    1.0F, EpicFightSkills.STEP,
                    1.0F, EpicFightSkills.GUARD,
                    0.5F, EpicFightSkills.EMERGENCY_ESCAPE
                )))));
        skillLootTableRegistryEvent.put(EntityTypes.SPIDER, LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).when(LootItemRandomChanceCondition.randomChance(.025F * dropChanceModifier))
                .add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
                    EpicFightSkills.ROLL,
                    EpicFightSkills.STEP,
                    EpicFightSkills.GUARD
                )))));
        skillLootTableRegistryEvent.put(EntityTypes.CAVE_SPIDER, LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
                .add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
                    EpicFightSkills.ROLL,
                    EpicFightSkills.STEP,
                    EpicFightSkills.GUARD
                )))
        ));
        skillLootTableRegistryEvent.put(EntityTypes.CREEPER, LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
                .add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
                    EpicFightSkills.HYPERVITALITY,
                    EpicFightSkills.IMPACT_GUARD
                )))
        ));
        skillLootTableRegistryEvent.put(EntityTypes.ENDERMAN, LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
                .add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
                    EpicFightSkills.HYPERVITALITY,
                    EpicFightSkills.FORBIDDEN_STRENGTH,
                    EpicFightSkills.ENDURANCE,
                    EpicFightSkills.EMERGENCY_ESCAPE,
                    EpicFightSkills.PARRYING,
                    EpicFightSkills.IMPACT_GUARD
                )))
        ));
        skillLootTableRegistryEvent.put(EntityTypes.VINDICATOR, LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
                .add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
                    EpicFightSkills.HYPERVITALITY,
                    EpicFightSkills.BERSERKER,
                    EpicFightSkills.GUARD,
                    EpicFightSkills.STEP,
                    EpicFightSkills.ROLL
                )))
        ));
        skillLootTableRegistryEvent.put(EntityTypes.PILLAGER, LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
                .add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
                    EpicFightSkills.HYPERVITALITY,
                    EpicFightSkills.STAMINA_PILLAGER,
                    EpicFightSkills.GUARD,
                    EpicFightSkills.STEP,
                    EpicFightSkills.ROLL
                )))
        ));
        skillLootTableRegistryEvent.put(EntityTypes.WITCH, LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
                .add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
                    EpicFightSkills.FORBIDDEN_STRENGTH,
                    EpicFightSkills.BERSERKER
                )))
        ));
        skillLootTableRegistryEvent.put(EntityTypes.EVOKER, LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
                .add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
                    EpicFightSkills.PARRYING,
                    EpicFightSkills.IMPACT_GUARD
                )))).withPool(
            LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).when(LootItemRandomChanceCondition.randomChance(0.1F * dropChanceModifier))
                .add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
                    EpicFightSkills.DEATH_HARVEST,
                    EpicFightSkills.EMERGENCY_ESCAPE
                )))
        ));
        skillLootTableRegistryEvent.put(EntityTypes.PIGLIN, LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
                .add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
                    EpicFightSkills.SWORD_MASTER,
                    EpicFightSkills.STAMINA_PILLAGER,
                    EpicFightSkills.GUARD,
                    EpicFightSkills.STEP,
                    EpicFightSkills.ROLL
                )))
        ));
        skillLootTableRegistryEvent.put(EntityTypes.PIGLIN_BRUTE, LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
                .add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
                    EpicFightSkills.HYPERVITALITY,
                    EpicFightSkills.PARRYING,
                    EpicFightSkills.ENDURANCE,
                    EpicFightSkills.IMPACT_GUARD
                )))
        ));
        skillLootTableRegistryEvent.put(EntityTypes.ZOMBIFIED_PIGLIN, LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
                .add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
                    EpicFightSkills.BERSERKER,
                    EpicFightSkills.STAMINA_PILLAGER,
                    EpicFightSkills.GUARD,
                    EpicFightSkills.STEP,
                    EpicFightSkills.ROLL
                )))
        ));
        skillLootTableRegistryEvent.put(EntityTypes.WITHER_SKELETON, LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
                .add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
                    1.0F, EpicFightSkills.SWORD_MASTER,
                    1.0F, EpicFightSkills.STAMINA_PILLAGER,
                    1.0F, EpicFightSkills.GUARD,
                    1.0F, EpicFightSkills.STEP,
                    1.0F, EpicFightSkills.ROLL,
                    0.75F, EpicFightSkills.DEATH_HARVEST
                )))
        ));
        skillLootTableRegistryEvent.put(EntityTypes.WITHER, LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(ContextIntProviders.exactly(1))
                .add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
                    EpicFightSkills.DEATH_HARVEST
                )))
        ));
	}
}