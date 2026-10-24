"""One-time mechanical migration; only edits the copied proj sources."""
from pathlib import Path
import re
from collections import defaultdict

root = Path(__file__).resolve().parents[1]
mc = root.parent / 'mcsrc'
by_name = defaultdict(list)
for p in mc.rglob('*.java'):
    by_name[p.stem].append('.'.join(p.relative_to(mc).with_suffix('').parts))

rename = {'ResourceLocation': 'Identifier', 'Tiers': 'ToolMaterial', 'Tier': 'ToolMaterial'}
explicit = {
    'javax.annotation.Nullable': 'org.jetbrains.annotations.Nullable',
    'javax.annotation.Nonnull': 'org.jetbrains.annotations.NotNull',
    'org.checkerframework.checker.nullness.qual.NonNull': 'org.jetbrains.annotations.NotNull',
    'com.mojang.blaze3d.vertex.VertexFormat': 'com.mojang.renderpearl.api.vertex.VertexFormat',
    'com.mojang.blaze3d.vertex.VertexFormatElement': 'com.mojang.renderpearl.api.vertex.VertexFormatElement',
}
changes = []
for p in (root / 'src/main/java').rglob('*.java'):
    old = s = p.read_text(encoding='utf-8')
    for a,b in explicit.items():
        s = s.replace(a,b)
    for a,b in rename.items():
        s = re.sub(r'\b'+a+r'\b',b,s)
    # Only relocate a Minecraft/Mojang import when the source reference uniquely identifies it.
    for match in list(re.finditer(r'import (?:static )?((?:net\.minecraft|com\.mojang)\.[\w.]+);',s)):
        fqn = match.group(1)
        if (mc / (fqn.replace('.','/')+'.java')).exists():
            continue
        candidates = by_name.get(fqn.rsplit('.',1)[-1],[])
        if len(candidates)==1:
            s=s.replace(fqn,candidates[0])
    s = s.replace('EntityType.', 'EntityTypes.')
    # EntityType generic declarations and static factories stay on EntityType.
    for member in ['Builder','EntityFactory','STREAM_CODEC','CODEC','byString','create','loadEntityRecursive','loadEntitiesRecursive']:
        s=s.replace('EntityTypes.'+member,'EntityType.'+member)
    if 'EntityTypes.' in s and 'import net.minecraft.world.entity.EntityTypes;' not in s:
        s=s.replace('import net.minecraft.world.entity.EntityType;', 'import net.minecraft.world.entity.EntityType;\nimport net.minecraft.world.entity.EntityTypes;')
    s = s.replace('.getAttackDamageBonus()', '.attackDamageBonus()')
    s = s.replace('.getUses()', '.durability()')
    s = s.replace('.getEnchantmentValue()', '.enchantmentValue()')
    s = s.replace('.getMainCamera()', '.mainCamera()')
    s = s.replace('InputConstants.Type.KEYSYM', 'InputConstants.Type.KEYBOARD')
    if old!=s:
        p.write_text(s,encoding='utf-8')
        changes.append(str(p.relative_to(root)))
(root/'name-migration.txt').write_text('\n'.join(changes),encoding='utf-8')
print(f'Migrated names in {len(changes)} Java files')
