package dev.war.openfight.platform.fabric.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import dev.war.openfight.world.item.TieredWeaponItem;

/** Vanilla tools lost their item subclasses; repair component tags retain their material identity. */
public final class ItemMaterial {
    private ItemMaterial() {}
    public static ToolMaterial of(Item item) {
        if (item instanceof TieredWeaponItem weapon) return weapon.getMaterial();
        var repairable=item.components().get(DataComponents.REPAIRABLE);
        if (repairable==null) return null;
        var tag=repairable.items().unwrapKey();
        for (ToolMaterial material : new ToolMaterial[]{ToolMaterial.WOOD,ToolMaterial.STONE,ToolMaterial.COPPER,ToolMaterial.IRON,ToolMaterial.GOLD,ToolMaterial.DIAMOND,ToolMaterial.NETHERITE}) {
            if (tag.isPresent() && tag.get().equals(material.repairItems())) return material;
        }
        return null;
    }
}
