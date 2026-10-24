package dev.war.openfight.registry.entries;
import java.util.Map;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;

public final class EpicFightArmorMaterials {
    private EpicFightArmorMaterials() {}
    public static final ArmorMaterial STRAY_CLOTH = new ArmorMaterial(
        5,Map.of(ArmorType.BOOTS,1,ArmorType.LEGGINGS,2,ArmorType.CHESTPLATE,3,ArmorType.HELMET,1,ArmorType.BODY,2),
        15,SoundEvents.ARMOR_EQUIP_LEATHER,0.0F,0.0F,
        TagKey.create(Registries.ITEM,Identifier.fromNamespaceAndPath("epicfight","stray_cloth_repair_materials")),
        ResourceKey.create(EquipmentAssets.ROOT_ID,Identifier.fromNamespaceAndPath("epicfight","stray_cloth"))
    );
}
