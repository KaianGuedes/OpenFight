package dev.war.openfight.registry.entries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import dev.war.openfight.platform.fabric.registry.DeferredHolder;
import dev.war.openfight.platform.fabric.registry.DeferredRegister;
import dev.war.openfight.main.EpicFightMod;
import dev.war.openfight.world.item.DaggerItem;
import dev.war.openfight.world.item.GloveItem;
import dev.war.openfight.world.item.GreatswordItem;
import dev.war.openfight.world.item.LongswordItem;
import dev.war.openfight.world.item.SkillBookItem;
import dev.war.openfight.world.item.SpearItem;
import dev.war.openfight.world.item.TachiItem;
import dev.war.openfight.world.item.TieredWeaponItem;
import dev.war.openfight.world.item.UchigatanaItem;

public final class EpicFightItems {
	private EpicFightItems() {}
	
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(Registries.ITEM, EpicFightMod.NAMESPACE);
	
	// Uchigatana & sheath
	public static final DeferredHolder<Item, UchigatanaItem> UCHIGATANA = REGISTRY.register("uchigatana",
		id -> new UchigatanaItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).rarity(Rarity.RARE).durability(1625).attributes(UchigatanaItem.createUchigatanaAttributes()))
	);
	public static final DeferredHolder<Item, Item> UCHIGATANA_SHEATH = REGISTRY.register("uchigatana_sheath",
		id -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).rarity(Rarity.EPIC))
	);
	
	// Greatsword variants
	public static final DeferredHolder<Item, GreatswordItem> WOODEN_GREATSWORD = REGISTRY.register("wooden_greatsword", id ->
		new GreatswordItem(ToolMaterial.WOOD, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(GreatswordItem.createGreatswordAttributes(ToolMaterial.WOOD)))
	);
	public static final DeferredHolder<Item, GreatswordItem> STONE_GREATSWORD = REGISTRY.register("stone_greatsword", id ->
		new GreatswordItem(ToolMaterial.STONE, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(GreatswordItem.createGreatswordAttributes(ToolMaterial.STONE)))
	);
	public static final DeferredHolder<Item, GreatswordItem> IRON_GREATSWORD = REGISTRY.register("iron_greatsword", id ->
		new GreatswordItem(ToolMaterial.IRON, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(GreatswordItem.createGreatswordAttributes(ToolMaterial.IRON)))
	);
	public static final DeferredHolder<Item, GreatswordItem> GOLDEN_GREATSWORD = REGISTRY.register("golden_greatsword", id ->
		new GreatswordItem(ToolMaterial.GOLD, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(GreatswordItem.createGreatswordAttributes(ToolMaterial.GOLD)))
	);
	public static final DeferredHolder<Item, GreatswordItem> DIAMOND_GREATSWORD = REGISTRY.register("diamond_greatsword", id ->
		new GreatswordItem(ToolMaterial.DIAMOND, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(GreatswordItem.createGreatswordAttributes(ToolMaterial.DIAMOND)))
	);
	public static final DeferredHolder<Item, GreatswordItem> NETHERITE_GREATSWORD = REGISTRY.register("netherite_greatsword", id ->
		new GreatswordItem(ToolMaterial.NETHERITE, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).fireResistant().attributes(GreatswordItem.createGreatswordAttributes(ToolMaterial.NETHERITE)))
	);
	
	// Spear variants
	public static final DeferredHolder<Item, SpearItem> WOODEN_SPEAR = REGISTRY.register("wooden_spear", id ->
		new SpearItem(ToolMaterial.WOOD, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(SpearItem.createSpearAttributes(ToolMaterial.WOOD)))
	);
	public static final DeferredHolder<Item, SpearItem> STONE_SPEAR = REGISTRY.register("stone_spear", id ->
		new SpearItem(ToolMaterial.STONE, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(SpearItem.createSpearAttributes(ToolMaterial.STONE)))
	);
	public static final DeferredHolder<Item, SpearItem> IRON_SPEAR = REGISTRY.register("iron_spear", id ->
		new SpearItem(ToolMaterial.IRON, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(SpearItem.createSpearAttributes(ToolMaterial.IRON)))
	);
	public static final DeferredHolder<Item, SpearItem> GOLDEN_SPEAR = REGISTRY.register("golden_spear", id ->
		new SpearItem(ToolMaterial.GOLD, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(SpearItem.createSpearAttributes(ToolMaterial.GOLD)))
	);
	public static final DeferredHolder<Item, SpearItem> DIAMOND_SPEAR = REGISTRY.register("diamond_spear", id ->
		new SpearItem(ToolMaterial.DIAMOND, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(SpearItem.createSpearAttributes(ToolMaterial.DIAMOND)))
	);
	public static final DeferredHolder<Item, SpearItem> NETHERITE_SPEAR = REGISTRY.register("netherite_spear", id ->
		new SpearItem(ToolMaterial.NETHERITE, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).fireResistant().attributes(SpearItem.createSpearAttributes(ToolMaterial.NETHERITE)))
	);
	
	// Tachi variants
	public static final DeferredHolder<Item, TachiItem> WOODEN_TACHI = REGISTRY.register("wooden_tachi", id ->
		new TachiItem(ToolMaterial.WOOD, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(TachiItem.createTachiAttributes(ToolMaterial.WOOD)))
	);
	public static final DeferredHolder<Item, TachiItem> STONE_TACHI = REGISTRY.register("stone_tachi", id ->
		new TachiItem(ToolMaterial.STONE, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(TachiItem.createTachiAttributes(ToolMaterial.STONE)))
	);
	public static final DeferredHolder<Item, TachiItem> IRON_TACHI = REGISTRY.register("iron_tachi", id ->
		new TachiItem(ToolMaterial.IRON, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(TachiItem.createTachiAttributes(ToolMaterial.IRON)))
	);
	public static final DeferredHolder<Item, TachiItem> GOLDEN_TACHI = REGISTRY.register("golden_tachi", id ->
		new TachiItem(ToolMaterial.GOLD, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(TachiItem.createTachiAttributes(ToolMaterial.GOLD)))
	);
	public static final DeferredHolder<Item, TachiItem> DIAMOND_TACHI = REGISTRY.register("diamond_tachi", id ->
		new TachiItem(ToolMaterial.DIAMOND, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(TachiItem.createTachiAttributes(ToolMaterial.DIAMOND)))
	);
	public static final DeferredHolder<Item, TachiItem> NETHERITE_TACHI = REGISTRY.register("netherite_tachi", id ->
		new TachiItem(ToolMaterial.NETHERITE, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).fireResistant().attributes(TachiItem.createTachiAttributes(ToolMaterial.NETHERITE)))
	);
	
	// Longsword variants
	public static final DeferredHolder<Item, LongswordItem> WOODEN_LONGSWORD = REGISTRY.register("wooden_longsword", id ->
		new LongswordItem(ToolMaterial.WOOD, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(LongswordItem.createLongswordAttributes(ToolMaterial.WOOD)))
	);
	public static final DeferredHolder<Item, LongswordItem> STONE_LONGSWORD = REGISTRY.register("stone_longsword", id ->
		new LongswordItem(ToolMaterial.STONE, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(LongswordItem.createLongswordAttributes(ToolMaterial.STONE)))
	);
	public static final DeferredHolder<Item, LongswordItem> IRON_LONGSWORD = REGISTRY.register("iron_longsword", id ->
		new LongswordItem(ToolMaterial.IRON, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(LongswordItem.createLongswordAttributes(ToolMaterial.IRON)))
	);
	public static final DeferredHolder<Item, LongswordItem> GOLDEN_LONGSWORD = REGISTRY.register("golden_longsword", id ->
		new LongswordItem(ToolMaterial.GOLD, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(LongswordItem.createLongswordAttributes(ToolMaterial.GOLD)))
	);
	public static final DeferredHolder<Item, LongswordItem> DIAMOND_LONGSWORD = REGISTRY.register("diamond_longsword", id ->
		new LongswordItem(ToolMaterial.DIAMOND, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(LongswordItem.createLongswordAttributes(ToolMaterial.DIAMOND)))
	);
	public static final DeferredHolder<Item, LongswordItem> NETHERITE_LONGSWORD = REGISTRY.register("netherite_longsword", id ->
		new LongswordItem(ToolMaterial.NETHERITE, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).fireResistant().attributes(LongswordItem.createLongswordAttributes(ToolMaterial.NETHERITE)))
	);
	
	// Dagger variants
	public static final DeferredHolder<Item, DaggerItem> WOODEN_DAGGER = REGISTRY.register("wooden_dagger", id ->
		new DaggerItem(ToolMaterial.WOOD, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(DaggerItem.createDaggerAttributes(ToolMaterial.WOOD)))
	);
	public static final DeferredHolder<Item, DaggerItem> STONE_DAGGER = REGISTRY.register("stone_dagger", id ->
		new DaggerItem(ToolMaterial.STONE, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(DaggerItem.createDaggerAttributes(ToolMaterial.STONE)))
	);
	public static final DeferredHolder<Item, DaggerItem> IRON_DAGGER = REGISTRY.register("iron_dagger", id ->
		new DaggerItem(ToolMaterial.IRON, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(DaggerItem.createDaggerAttributes(ToolMaterial.IRON)))
	);
	public static final DeferredHolder<Item, DaggerItem> GOLDEN_DAGGER = REGISTRY.register("golden_dagger", id ->
		new DaggerItem(ToolMaterial.GOLD, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(DaggerItem.createDaggerAttributes(ToolMaterial.GOLD)))
	);
	public static final DeferredHolder<Item, DaggerItem> DIAMOND_DAGGER = REGISTRY.register("diamond_dagger", id ->
		new DaggerItem(ToolMaterial.DIAMOND, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).attributes(DaggerItem.createDaggerAttributes(ToolMaterial.DIAMOND)))
	);
	public static final DeferredHolder<Item, DaggerItem> NETHERITE_DAGGER = REGISTRY.register("netherite_dagger", id ->
		new DaggerItem(ToolMaterial.NETHERITE, new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).fireResistant().attributes(DaggerItem.createDaggerAttributes(ToolMaterial.NETHERITE)))
	);
	
	// Weapons etc
	public static final DeferredHolder<Item, GloveItem> GLOVE = REGISTRY.register("glove", id ->
		new GloveItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).durability(1625).attributes(TieredWeaponItem.createAttributes(2.0F, 0.0F)))
	);
	public static final DeferredHolder<Item, Item> BOKKEN = REGISTRY.register("bokken", id ->
		new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).sword(ToolMaterial.WOOD, 3.0F, -2.4F))
	);
	
	// Stray armor
	public static final DeferredHolder<Item, Item> STRAY_HAT = REGISTRY.register("stray_hat", id -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).humanoidArmor(EpicFightArmorMaterials.STRAY_CLOTH, ArmorType.HELMET)));
	public static final DeferredHolder<Item, Item> STRAY_ROBE = REGISTRY.register("stray_robe", id -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).humanoidArmor(EpicFightArmorMaterials.STRAY_CLOTH, ArmorType.CHESTPLATE)));
	public static final DeferredHolder<Item, Item> STRAY_PANTS = REGISTRY.register("stray_pants", id -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).humanoidArmor(EpicFightArmorMaterials.STRAY_CLOTH, ArmorType.LEGGINGS)));
	
	public static final DeferredHolder<Item, SkillBookItem> SKILLBOOK = REGISTRY.register("skillbook", id -> new SkillBookItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).rarity(Rarity.RARE).stacksTo(1)));
}