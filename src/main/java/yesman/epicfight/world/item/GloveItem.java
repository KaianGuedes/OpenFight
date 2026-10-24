package yesman.epicfight.world.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class GloveItem extends WeaponItem {
	public GloveItem(Item.Properties properties) {
		super(properties.enchantable(22).repairable(Items.IRON_NUGGET));
	}
}