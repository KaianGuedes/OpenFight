package yesman.epicfight.platform.fabric.compat;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;

/**
 * Replacement for the removed SwordItem / DiggerItem / ArmorItem / TridentItem class hierarchy:
 * 26.3 identifies these through item tags and data components.
 */
public final class ItemClassifier {
	private ItemClassifier() {}

	public static boolean isSword(Item item) {
		return item.builtInRegistryHolder().is(ItemTags.SWORDS);
	}

	/** Vanilla DiggerItem: pickaxe, axe, shovel, hoe. */
	public static boolean isDigger(Item item) {
		var holder = item.builtInRegistryHolder();
		return holder.is(ItemTags.PICKAXES) || holder.is(ItemTags.AXES) || holder.is(ItemTags.SHOVELS) || holder.is(ItemTags.HOES);
	}

	public static boolean isTrident(Item item) {
		return item == Items.TRIDENT;
	}

	public static boolean isArmor(ItemStack stack) {
		Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
		return equippable != null && equippable.slot().getType() == EquipmentSlot.Type.HUMANOID_ARMOR;
	}

	public static EquipmentSlot armorSlot(ItemStack stack) {
		Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
		return equippable == null ? null : equippable.slot();
	}
}
