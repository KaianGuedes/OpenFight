package dev.war.openfight.world.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class DaggerItem extends TieredWeaponItem {
	public static ItemAttributeModifiers createDaggerAttributes(ToolMaterial iter) {
		return TieredWeaponItem.createAttributes(iter, 1.0F, -1.6F, 0.0F);
	}
	
	public DaggerItem(ToolMaterial tier, Item.Properties properties) {
		super(tier, properties);
	}
}