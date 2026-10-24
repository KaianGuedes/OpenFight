package dev.war.openfight.world.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class TachiItem extends TieredWeaponItem {
	public static ItemAttributeModifiers createTachiAttributes(ToolMaterial iter) {
		return TieredWeaponItem.createAttributes(iter, 4.0F, -2.8F, 0.0F);
	}
	
	public TachiItem(ToolMaterial tier, Item.Properties properties) {
		super(tier, properties);
	}
}