import re
from pathlib import Path
root=Path(__file__).resolve().parents[1]/'src/main/java/dev/war/openfight'
CALLS={
 'RenderType.entityCutoutNoCull(':'RenderTypes.entityCutout(',
 'RenderType.entityTranslucent(':'RenderTypes.entityTranslucent(',
 'RenderType.entityCutout(':'RenderTypes.entityCutout(',
 'RenderType.itemEntityTranslucentCull(':'RenderTypes.itemTranslucent(',
 'RenderType.lightning()':'RenderTypes.lightning()',
 'RenderType.lines()':'RenderTypes.lines()',
 'RenderType.eyes(':'RenderTypes.eyes(',
 'RenderType.armorCutoutNoCull(':'RenderTypes.armorCutoutNoCull(',
 'RenderType.armorEntityGlint()':'RenderTypes.armorCutoutNoCullGlint(net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer.class == null ? null : dev.war.openfight.EpicFight.identifier("textures/misc/enchanted_glint_armor.png"))',
 'RenderType.outline(':'RenderTypes.outline(',
 'RenderType.energySwirl(':'RenderTypes.energySwirl(',
 'RenderType.entityDecal(':'RenderTypes.entityCutoutZOffset(',
 'RenderType.dragonExplosionAlpha(':'RenderTypes.entityTranslucent(',
 'RenderType.text(':'RenderTypes.text(',
 'RenderType.cutout()':'RenderTypes.cutoutMovingBlock()',
}
changed=0
for p in root.rglob('*.java'):
    s=str(p).replace('\\','/')
    if '/compat/' in s and 'client/renderer/compat' not in s: continue
    if 'EpicFightRenderTypes.java' in s: continue
    raw=p.read_bytes().decode('utf-8');crlf='\r\n' in raw;t=raw.replace('\r\n','\n');o=t
    for a,b in CALLS.items(): t=t.replace(a,b)
    t=t.replace('Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()','Minecraft.getInstance().debugEntries.isCurrentlyEnabled(net.minecraft.client.gui.components.debug.DebugScreenEntries.ENTITY_HITBOXES)')
    if 'RenderTypes.' in t and 'import net.minecraft.client.renderer.rendertype.RenderTypes;' not in t and 'package net.minecraft.client.renderer.rendertype' not in t and 'EpicFightRenderTypes.' not in t.replace('RenderTypes.','',0) or ('RenderTypes.' in re.sub(r'EpicFightRenderTypes\.','',t) and 'import net.minecraft.client.renderer.rendertype.RenderTypes;' not in t):
        t=re.sub(r'(\nimport )',r'\nimport net.minecraft.client.renderer.rendertype.RenderTypes;\1',t,count=1)
    if t!=o:
        if crlf:t=t.replace('\n','\r\n')
        p.write_bytes(t.encode('utf-8'));changed+=1
print('changed',changed)
