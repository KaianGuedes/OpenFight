from pathlib import Path
import re
root=Path(__file__).resolve().parents[1]
log=(root/'platform-build.log').read_text(encoding='utf-8').split('> Task :compileJava FAILED')[0]
rotations={}
for path,line in re.findall(r'([^\r\n]+\.java):(\d+): error: no suitable method found for mulPose\(Quaternionf\)',log):
    rotations.setdefault(Path(path),set()).add(int(line))
for p in (root/'src/main/java').rglob('*.java'):
    old=s=p.read_text(encoding='utf-8')
    s=s.replace('net.minecraft.world.item.UseAnim','net.minecraft.world.item.ItemUseAnimation')
    s=re.sub(r'\bUseAnim\b','ItemUseAnimation',s)
    s=s.replace('net.minecraft.util.FastColor','net.minecraft.util.ARGB')
    s=s.replace('FastColor.ARGB32.','ARGB.')
    s=s.replace('net.neoforged.api.distmarker.Dist','net.fabricmc.api.EnvType')
    s=s.replace('net.neoforged.api.distmarker.OnlyIn','net.fabricmc.api.Environment')
    s=re.sub(r'\bDist\b','EnvType',s)
    s=s.replace('EnvType.DEDICATED_SERVER','EnvType.SERVER')
    s=s.replace('@OnlyIn(', '@Environment(')
    s=s.replace('.getHolder(','.get(')
    s=s.replace('.registryOrThrow(','.lookupOrThrow(')
    s=s.replace('.registry(','.lookup(') if 'RegistryAccess' in s else s
    # Only compiler-identified Quaternionf overload calls become rotate; Matrix4f calls remain mulPose.
    if p in rotations:
        lines=s.splitlines(keepends=True)
        for line in rotations[p]: lines[line-1]=lines[line-1].replace('.mulPose(','.rotate(')
        s=''.join(lines)
    # ResourceKey.location was renamed, including keys used in registry callbacks.
    s=s.replace('.location()', '.identifier()')
    # Registered item lookups now return a holder; getValue retains the old value semantics.
    s=re.sub(r'(BuiltInRegistries\.\w+)\.get\(',r'\1.getValue(',s)
    if old!=s: p.write_text(s,encoding='utf-8')
print('Migrated vanilla registry accessors, use animations, color, side annotations and quaternion poses')
