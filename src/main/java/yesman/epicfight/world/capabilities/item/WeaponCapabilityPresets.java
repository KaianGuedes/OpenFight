package yesman.epicfight.world.capabilities.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import yesman.epicfight.platform.fabric.item.ItemMaterial;
import net.minecraft.world.item.ToolMaterial;
import yesman.epicfight.api.ex_cap.managers.ExCapManager;
import yesman.epicfight.registry.entries.EpicFightSounds;

public abstract class WeaponCapabilityPresets {
	public static int vanillaTierToLevel(ToolMaterial tier) {
        if (tier == ToolMaterial.WOOD || tier == ToolMaterial.GOLD) return 0;
        if (tier == ToolMaterial.STONE || tier == ToolMaterial.COPPER) return 1;
        if (tier == ToolMaterial.IRON) return 2;
        if (tier == ToolMaterial.DIAMOND) return 3;
        if (tier == ToolMaterial.NETHERITE) return 4;
		double sqrt = Math.sqrt(tier.durability());

		// Custom tier mapping
		return sqrt < 10.0D ? 0 : (int)Math.round(sqrt / 10.0D);
	}

    public static CapabilityItem.Builder<?> registerPreset(CapabilityItem.Builder<?> builder, Item item)
    {
        if (builder instanceof WeaponCapability.Builder weaponBuilder)
        {
            WeaponCapability.Builder copy = weaponBuilder.copy();
            handleItemOverrides(copy, item);
            ExCapManager.retrieveExCapData(builder.identifier).forEach(
                    data -> data.build().apply(copy)
            );
            return copy;
        }

        return CapabilityItem.builder();
    }

    private static void handleItemOverrides(WeaponCapability.Builder builder, Item item)
    {
        ToolMaterial material = ItemMaterial.of(item);
        if (material != null) {
            int tierLevel = vanillaTierToLevel(material);
            builder.modifyTierAttributes(tierLevel);
            if (material == ToolMaterial.WOOD)
            {
                builder.hitSound(EpicFightSounds.BLUNT_HIT);
            }
            if (material == ToolMaterial.STONE)
            {
                builder.hitSound(EpicFightSounds.BLUNT_HIT_HARD);
            }
        }
        builder.handleOverrides(item);
    }
}
