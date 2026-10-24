package yesman.epicfight.world.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class SpearItem extends TieredWeaponItem {
	public static ItemAttributeModifiers createSpearAttributes(ToolMaterial iter) {
		return TieredWeaponItem.createAttributes(iter, 3.0F, -2.8F, 0.0F);
	}
	
	public SpearItem(ToolMaterial tier, Item.Properties properties) {
		super(tier, properties);
	}
}