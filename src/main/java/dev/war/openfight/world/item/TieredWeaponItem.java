package dev.war.openfight.world.item;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import dev.war.openfight.world.capabilities.item.WeaponCapabilityPresets;

public abstract class TieredWeaponItem extends WeaponItem {
    private final ToolMaterial material;
    public ToolMaterial getMaterial() { return material; }
    public static ItemAttributeModifiers createAttributes(ToolMaterial tier, float attackDamage, float attackSpeed, float attackSpeedModifier) {
        return createAttributes(attackDamage + tier.attackDamageBonus(), attackSpeed + WeaponCapabilityPresets.vanillaTierToLevel(tier) * attackSpeedModifier);
    }
	
    public static ItemAttributeModifiers createAttributes(float attackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
            .add(
                Attributes.ATTACK_DAMAGE
                , new AttributeModifier(BASE_ATTACK_DAMAGE_ID, (double)attackDamage, AttributeModifier.Operation.ADD_VALUE)
                , EquipmentSlotGroup.MAINHAND
            )
            .add(
                  Attributes.ATTACK_SPEED
                , new AttributeModifier(BASE_ATTACK_SPEED_ID, (double)attackSpeed, AttributeModifier.Operation.ADD_VALUE)
                , EquipmentSlotGroup.MAINHAND
            )
            .build();
    }
    
	public TieredWeaponItem(ToolMaterial tier, Item.Properties properties) {
		super(properties.durability(tier.durability()).repairable(tier.repairItems()).enchantable(tier.enchantmentValue()));
        this.material=tier;
	}
}