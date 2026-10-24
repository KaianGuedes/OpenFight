package yesman.epicfight.world.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Blocks;
import yesman.epicfight.generated.LangKeys;

import java.util.List;

public class UchigatanaItem extends WeaponItem {
	public static ItemAttributeModifiers createUchigatanaAttributes() {
		return TieredWeaponItem.createAttributes(6.0F, -2.0F);
	}
	
	public UchigatanaItem(Item.Properties properties) {
		super(properties.enchantable(22).repairable(Items.IRON_BARS));
	}
    
	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		tooltipComponents.accept(Component.literal(""));
		tooltipComponents.accept(Component.translatable(LangKeys.ITEM_UCHIGATANA_TOOLTIP));
	}
}