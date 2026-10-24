from pathlib import Path
import re
root=Path(__file__).resolve().parents[1]
for p in (root/'src/main/java').rglob('*.java'):
    old=s=p.read_text(encoding='utf-8')
    s=re.sub(r'\bEnderMan\b','Enderman',s)
    if 'extends SimpleJsonResourceReloadListener' in s:
        s=s.replace('extends SimpleJsonResourceReloadListener', 'extends SimpleJsonResourceReloadListener<JsonElement>')
        s=s.replace('import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;','import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;\nimport net.minecraft.resources.FileToIdConverter;\nimport yesman.epicfight.platform.fabric.resource.JsonElementCodec;')
        s=re.sub(r'super\(GSON, ([^;]+)\);',r'super(JsonElementCodec.CODEC, FileToIdConverter.json(\1));',s)
        s=s.replace('super(new GsonBuilder().create(), "item_skins");','super(JsonElementCodec.CODEC, FileToIdConverter.json("item_skins"));')
        s=s.replace('super((new GsonBuilder()).create(), "animmodels/joint_mask");','super(JsonElementCodec.CODEC, FileToIdConverter.json("animmodels/joint_mask"));')
    if p.name=='ClientConfig.java':
        s=s.replace('event.getConfig().getFullPath().getParent().toString()', 'net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().toString()')
    if old!=s: p.write_text(s,encoding='utf-8')
print('Ported JSON reloaders and Enderman rename')
