package yesman.epicfight.world.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class LongswordItem extends TieredWeaponItem {
	public static ItemAttributeModifiers createLongswordAttributes(ToolMaterial iter) {
		return TieredWeaponItem.createAttributes(iter, 4.0F, -2.8F, 0.0F);
	}
	
	public LongswordItem(ToolMaterial tier, Item.Properties properties) {
		super(tier, properties);
	}
}