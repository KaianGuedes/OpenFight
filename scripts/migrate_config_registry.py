from pathlib import Path
import re
root=Path(__file__).resolve().parents[1]
for p in (root/'src/main/java').rglob('*.java'):
    old=s=p.read_text(encoding='utf-8')
    s=s.replace('net.neoforged.neoforge.common.ModConfigSpec','yesman.epicfight.platform.fabric.config.ModConfigSpec')
    s=s.replace('net.neoforged.neoforge.registries.RegistryBuilder','yesman.epicfight.platform.fabric.registry.RegistryBuilder')
    s=s.replace('net.neoforged.neoforge.registries.callback.','yesman.epicfight.platform.fabric.registry.callback.')
    if p.name=='EpicFightRegistries.java':
        s=re.sub(r'import net\.neoforged\.[^;]+;\n','',s)
        s=re.sub(r'@EventBusSubscriber\([^\n]*\)\n','',s)
        s=s.replace('        EpicFightAttachmentTypes.REGISTRY,\n','')
        s=re.sub(r'\s*@SubscribeEvent\s+public static void addNewRegistries\(NewRegistryEvent event\) \{.*?\n    \}', '',s,flags=re.S)
    if p.name=='ClientConfig.java':
        # The upstream default 3 is outside its declared [6,10] camera zoom interval.
        s=s.replace('"ingame.camera.zoom", 3, 6, 10','"ingame.camera.zoom", 6, 6, 10')
    if old!=s: p.write_text(s,encoding='utf-8')
print('Migrated config declarations and custom registry creation/callbacks')
