from pathlib import Path
import re
root=Path(__file__).resolve().parents[1]
src=root/'src/main/java/dev/war/openfight'

p=src/'client/input/EpicFightKeyMappings.java'
s=p.read_text(encoding='utf-8').replace('import net.neoforged.neoforge.client.settings.KeyConflictContext;','import dev.war.openfight.platform.fabric.client.ContextKeyMapping;')
s=s.replace('new KeyMapping(','new ContextKeyMapping(').replace('KeyConflictContext.','ContextKeyMapping.Context.')
p.write_text(s,encoding='utf-8')

p=src/'client/input/EpicFightInputCategories.java'
s=p.read_text(encoding='utf-8').replace('import org.jetbrains.annotations.ApiStatus;','import org.jetbrains.annotations.ApiStatus;\nimport net.minecraft.client.KeyMapping;\nimport net.minecraft.resources.Identifier;')
s=re.sub(r'public static final String (\w+) = LangKeys.KEY_\w+;',lambda m:'public static final KeyMapping.Category '+m.group(1)+' = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("epicfight", "'+m.group(1).lower()+'"));',s)
p.write_text(s,encoding='utf-8')

p=src/'client/input/CombatKeyMapping.java'
s=p.read_text(encoding='utf-8').replace('import net.neoforged.neoforge.client.settings.KeyConflictContext;','import dev.war.openfight.platform.fabric.client.ContextKeyMapping;')
s=s.replace('extends KeyMapping','extends ContextKeyMapping').replace('String category','KeyMapping.Category category')
s=s.replace('KeyConflictContext.IN_GAME','ContextKeyMapping.Context.IN_GAME')
s=s.replace('isActiveAndMatches','matches')
s=s.replace('    @Override\n    public boolean matches', '    @Override\n    protected boolean active() { return super.active() && ClientEngine.getInstance().isEpicFightMode(); }\n\n    @Override\n    public boolean matches')
p.write_text(s,encoding='utf-8')

p=src/'world/item/WeaponItem.java'
p.write_text('''package dev.war.openfight.world.item;

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
''',encoding='utf-8')

p=src/'world/item/TieredWeaponItem.java'
s=p.read_text(encoding='utf-8').replace('import net.minecraft.world.item.SwordItem;\n','').replace('extends SwordItem','extends WeaponItem')
s=s.replace('public abstract class TieredWeaponItem extends WeaponItem {','public abstract class TieredWeaponItem extends WeaponItem {\n    private final ToolMaterial material;\n    public ToolMaterial getMaterial() { return material; }')
s=s.replace('super(tier, properties);','super(properties.durability(tier.durability()).repairable(tier.repairItems()).enchantable(tier.enchantmentValue()));\n        this.material=tier;')
p.write_text(s,encoding='utf-8')

for name,repair in [('GloveItem','IRON_NUGGET'),('UchigatanaItem','IRON_BARS')]:
    p=src/f'world/item/{name}.java'
    s=p.read_text(encoding='utf-8')
    # Preserve the upstream's toRepair-item check exactly as a repair component.
    s=s.replace('super(properties);',f'super(properties.enchantable(22).repairable(Items.{repair}));')
    s=s.replace('super(properties.component(DataComponents.TOOL, createToolProperties()));',f'super(properties.enchantable(22).repairable(Items.{repair}));')
    s=re.sub(r'\s*@Override\s+public int getEnchantmentValue\(\) \{.*?\n\s*\}', '',s,flags=re.S)
    s=re.sub(r'\s*@Override\s+public boolean isValidRepairItem\([^)]*\) \{.*?\n\s*\}', '',s,flags=re.S)
    if name=='UchigatanaItem':
        s=re.sub(r'\s*public static Tool createToolProperties\(\) \{.*?\n\s*\}', '',s,flags=re.S)
    p.write_text(s,encoding='utf-8')

# 26.3 tooltips use a Consumer and TooltipDisplay.
for p in (src/'world/item').glob('*.java'):
    s=p.read_text(encoding='utf-8')
    if 'List<Component> tooltipComponents' in s:
        s=s.replace('List<Component> tooltipComponents','net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltipComponents')
        s=s.replace('tooltipComponents.add(', 'tooltipComponents.accept(')
    if p.name=='SkillBookItem.java':
        s=s.replace('import net.minecraft.world.InteractionResultHolder;','import net.minecraft.world.InteractionResult;')
        s=s.replace('InteractionResultHolder<ItemStack> use','InteractionResult use').replace('InteractionResultHolder.pass(itemstack)','InteractionResult.PASS')
        s=s.replace('skill.getKey().identifier()','skill.unwrapKey().orElseThrow().identifier()')
    p.write_text(s,encoding='utf-8')

p=src/'registry/entries/EpicFightItems.java'
s=p.read_text(encoding='utf-8')
s=s.replace('import net.minecraft.world.item.ArmorItem;','import net.minecraft.world.item.equipment.ArmorType;\nimport net.minecraft.resources.ResourceKey;')
s=s.replace('import net.minecraft.world.item.SwordItem;\n','')
s=re.sub(r'DeferredHolder<Item, (?:SwordItem|ArmorItem)>', 'DeferredHolder<Item, Item>',s)
s=s.replace('new SwordItem(ToolMaterial.WOOD, new Item.Properties().attributes(SwordItem.createAttributes(ToolMaterial.WOOD, 3, -2.4F)))','new Item(new Item.Properties().sword(ToolMaterial.WOOD, 3.0F, -2.4F))')
s=re.sub(r'new ArmorItem\(EpicFightArmorMaterials.STRAY_CLOTH, ArmorItem.Type.(\w+), new Item.Properties\(\)\)',r'new Item(new Item.Properties().humanoidArmor(EpicFightArmorMaterials.STRAY_CLOTH, ArmorType.\1))',s)
s=s.replace('() ->','id ->')
s=s.replace('new Item.Properties()', 'new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id))')
p.write_text(s,encoding='utf-8')
print('Ported SDL key contexts/categories and custom weapon, armor and skill-book declarations')
