from pathlib import Path
import json
import re
root=Path(__file__).resolve().parents[1]
rows=json.loads((root/'build/reports/compile-errors.json').read_text(encoding='utf-8'))
files={}
for row in rows:
    if row['error'] in ['cannot assign a value to final variable minecraft','cannot assign a value to final variable font']:
        files.setdefault(root/row['file'],set()).add(row['line'])
fixed=0
for p,lines in files.items():
    text=p.read_text(encoding='utf-8').splitlines(keepends=True)
    for line in lines:
        # Screen now initializes these final singleton references in its constructor.
        if re.fullmatch(r'\s*this\.(?:minecraft|font)\s*=\s*(?:Minecraft.getInstance\(\)|(?:this\.)?\w+\.font)\s*;\s*',text[line-1]):
            text[line-1]=''; fixed+=1
    p.write_text(''.join(text),encoding='utf-8')
for p in (root/'src/main/java').rglob('*.java'):
    old=s=p.read_text(encoding='utf-8')
    if p.name=='Meshes.java':
        s=s.replace('public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor)', 'public CompletableFuture<Void> reload(PreparableReloadListener.SharedState state, Executor backgroundExecutor, PreparableReloadListener.PreparationBarrier stage, Executor gameExecutor)')
        s=s.replace('return CompletableFuture.runAsync(() -> {\n\t\t\tMeshes.reload(resourceManager);\n\t\t}, gameExecutor).thenCompose(stage::wait);', 'return stage.wait(net.minecraft.util.Unit.INSTANCE).thenRunAsync(() -> Meshes.reload(state.resourceManager()), gameExecutor);')
    if p.name=='ColliderPreset.java':
        s=s.replace('public @NotNull CompletableFuture<Void> reload(PreparationBarrier stage, @NotNull ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor)', 'public @NotNull CompletableFuture<Void> reload(PreparableReloadListener.SharedState state, Executor backgroundExecutor, PreparationBarrier stage, Executor gameExecutor)')
        # The upstream listener's body only contained commented-out debug code.
        s=re.sub(r'return CompletableFuture.runAsync\(\(\) -> \{\s*//Collider.*?\}, gameExecutor\).thenCompose\(stage::wait\);','return stage.wait(net.minecraft.util.Unit.INSTANCE).thenRunAsync(() -> {}, gameExecutor);',s,flags=re.S)
    if 'new SimpleParticleType(' in s:
        s=s.replace('new SimpleParticleType(', 'net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple(')
    if old!=s: p.write_text(s,encoding='utf-8')
print(f'Removed {fixed} obsolete Screen singleton assignments; updated reload barriers and particle factories')
