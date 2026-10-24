from pathlib import Path
import re
root=Path(__file__).resolve().parents[1]
src=root/'src/main/java/dev/war/openfight'

p=src/'registry/entries/EpicFightArmorMaterials.java'
p.write_text('''package dev.war.openfight.registry.entries;
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
''',encoding='utf-8')
p=src/'registry/EpicFightRegistries.java'
s=p.read_text(encoding='utf-8').replace('        EpicFightArmorMaterials.REGISTRY,\n','')
p.write_text(s,encoding='utf-8')

p=src/'world/capabilities/item/WeaponCapabilityPresets.java'
s=p.read_text(encoding='utf-8').replace('import net.minecraft.world.item.TieredItem;','import dev.war.openfight.platform.fabric.item.ItemMaterial;')
a=s.index('\t\tif (tier instanceof ToolMaterial vanillaTier)')
b=s.index('\t\tdouble sqrt',a)
s=s[:a]+'''        if (tier == ToolMaterial.WOOD || tier == ToolMaterial.GOLD) return 0;
        if (tier == ToolMaterial.STONE || tier == ToolMaterial.COPPER) return 1;
        if (tier == ToolMaterial.IRON) return 2;
        if (tier == ToolMaterial.DIAMOND) return 3;
        if (tier == ToolMaterial.NETHERITE) return 4;
'''+s[b:]
s=s.replace('if (item instanceof TieredItem tieredItem) {','ToolMaterial material = ItemMaterial.of(item);\n        if (material != null) {').replace('tieredItem.getTier()','material')
p.write_text(s,encoding='utf-8')

p=src/'world/damagesource/ExtraDamageInstance.java'
s=p.read_text(encoding='utf-8').replace('import net.minecraft.world.item.TieredItem;','import dev.war.openfight.platform.fabric.item.ItemMaterial;')
s=s.replace('if (itemstack.getItem() instanceof TieredItem tieredItem) {','var material = ItemMaterial.of(itemstack.getItem());\n            if (material != null) {').replace('tieredItem.getTier()','material')
p.write_text(s,encoding='utf-8')

p=src/'world/capabilities/provider/CommonItemCapabilityProvider.java'
s=p.read_text(encoding='utf-8')
for name in ['ArmorItem','SwordItem','AxeItem','PickaxeItem','ShovelItem','HoeItem']:
    s=s.replace('import net.minecraft.world.item.'+name+';\n','')
    s=re.sub(r'\s*this\.typedCapabilities\.put\('+name+r'\.class,[^;]+;', '', s)
s=s.replace('import net.minecraft.world.item.Item;','import net.minecraft.world.item.Item;\nimport net.minecraft.core.component.DataComponents;\nimport net.minecraft.tags.ItemTags;')
s=s.replace('DeferredPreset<? extends CapabilityItem.Builder<?>> builderEntry = this.typedCapabilities.getOrDefault(item.getClass(), null);','DeferredPreset<? extends CapabilityItem.Builder<?>> builderEntry = this.typedCapabilities.getOrDefault(item.getClass(), null);\n        if (builderEntry == null) {\n            if (item.components().has(DataComponents.EQUIPPABLE) && item.components().has(DataComponents.ATTRIBUTE_MODIFIERS)) builderEntry = EpicFightItemCapabilityPresets.ARMOR;\n            else if (item.builtInRegistryHolder().is(ItemTags.SWORDS)) builderEntry = EpicFightItemCapabilityPresets.BOKKEN;\n            else if (item.builtInRegistryHolder().is(ItemTags.PICKAXES)) builderEntry = EpicFightItemCapabilityPresets.PICKAXE;\n            else if (item.builtInRegistryHolder().is(ItemTags.AXES)) builderEntry = EpicFightItemCapabilityPresets.AXE;\n            else if (item.builtInRegistryHolder().is(ItemTags.SHOVELS)) builderEntry = EpicFightItemCapabilityPresets.SHOVEL;\n            else if (item.builtInRegistryHolder().is(ItemTags.HOES)) builderEntry = EpicFightItemCapabilityPresets.HOE;\n        }')
# Ensure the tag/component route runs when a vanilla tool is now just Item rather than a tool subclass.
s=s.replace('CapabilityItem capability = null;', 'CapabilityItem capability = getDefault(item);')
# Copy armor builders before attaching item-specific defense/slot values, as weapon builders already do.
s=s.replace('result = builder.byItem(item);','result = builder.copy().byItem(item);')
p.write_text(s,encoding='utf-8')

p=src/'world/capabilities/item/ArmorCapability.java'
s=p.read_text(encoding='utf-8').replace('import net.minecraft.world.item.ArmorItem;','import net.minecraft.world.item.equipment.ArmorType;\nimport net.minecraft.core.component.DataComponents;')
s=s.replace('ArmorItem.Type','ArmorType')
a=s.index('\t\t\tif (item instanceof ArmorItem armorItem)')
b=s.index('\n\t\t\treturn this;',a)
s=s[:a]+'''            var equippable = item.components().get(DataComponents.EQUIPPABLE);
            if (equippable != null) {
                this.armorType = java.util.Arrays.stream(ArmorType.values()).filter(type -> type.getSlot() == equippable.slot()).findFirst().orElse(ArmorType.BODY);
                var attributes = item.components().getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
                double defense = attributes.modifiers().stream().filter(entry -> entry.attribute().is(Attributes.ARMOR)).mapToDouble(entry -> entry.modifier().amount()).sum();
                if (this.weight < 0.0D) this.weight = defense * 2.5D;
                if (this.stunArmor < 0.0D) this.stunArmor = defense * 0.375D;
            }
'''+s[b:]
s=s.replace('        private double weight;', '        private double weight;')
mark='\t\tpublic Builder byItem(Item item) {'
s=s.replace(mark,'''        public Builder copy() {
            Builder copy = new Builder();
            copy.armorType=this.armorType;
            copy.weight=this.weight;
            copy.stunArmor=this.stunArmor;
            copy.identifier=this.identifier;
            return copy;
        }
'''+mark)
p.write_text(s,encoding='utf-8')
print('Ported material/armor components and vanilla tool capability classification')
