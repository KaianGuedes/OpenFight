package dev.war.openfight.world.item;

import java.util.List;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.level.block.Blocks;

public class WeaponItem extends Item {
    public WeaponItem(Item.Properties properties) {
        super(properties.component(DataComponents.WEAPON,new Weapon(1)).component(DataComponents.TOOL,createSwordTool()));
    }
    public static Tool createSwordTool() {
        var blocks=BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        return new Tool(List.of(
            Tool.Rule.minesAndDrops(HolderSet.direct(Blocks.COBWEB.builtInRegistryHolder()),15.0F),
            Tool.Rule.overrideSpeed(blocks.getOrThrow(BlockTags.SWORD_EFFICIENT),1.5F)
        ),1.0F,2,false);
    }
    @Override public void hurtEnemy(ItemStack stack,LivingEntity target,LivingEntity attacker) {}
    @Override public void postHurtEnemy(ItemStack stack,LivingEntity target,LivingEntity attacker) {
        stack.hurtAndBreak(1,attacker,EquipmentSlot.MAINHAND);
    }
}
