import re
from pathlib import Path
root=Path(__file__).resolve().parents[1]/'src/main/java/yesman/epicfight'
def edit(rel,f):
    p=root/rel
    raw=p.read_bytes().decode('utf-8');crlf='\r\n' in raw;s=raw.replace('\r\n','\n')
    n=f(s)
    if n!=s:
        if crlf:n=n.replace('\n','\r\n')
        p.write_bytes(n.encode('utf-8'))
def addimp(s,imp):
    if 'import '+imp+';' in s:return s
    return re.sub(r'(package [^;]+;\n)',lambda m:m.group(1)+'\nimport '+imp+';',s,count=1)

def block_end(s,start):
    i=s.index('{',start);d=0
    while True:
        c=s[i]
        if c=='{':d+=1
        elif c=='}':
            d-=1
            if d==0:return i+1
        i+=1

def in_button_classes(s):
    out=s;pos=0
    pat=re.compile(r'class\s+\w+(?:<[^>]*>)?\s+extends\s+(?:Button|AbstractButton|LegacyButton)\b[^{]*\{')
    while True:
        m=pat.search(out,pos)
        if not m:break
        e=block_end(out,m.start())
        blk=out[m.start():e]
        blk=blk.replace('void extractWidgetRenderState(','void extractContents(').replace('super.extractWidgetRenderState(','super.extractContents(')
        out=out[:m.start()]+blk+out[e:]
        pos=m.start()+10
    return out

# blits
edit('client/gui/screen/SkillEditScreen.java',lambda s:
    s.replace('guiGraphics.blit(RenderPipelines.GUI_TEXTURED, SKILL_EDIT_UI, this.width / 2 - 104, (float)((float)(0)), (float)((float)(0)), 208, 200, 256, 256)','guiGraphics.blit(RenderPipelines.GUI_TEXTURED, SKILL_EDIT_UI, this.width / 2 - 104, this.height / 2 - 100, 0.0F, 0.0F, 208, 200, 256, 256)')
     .replace('SKILL_EDIT_UI, this.getX(), (float)((float)(237)), (float)((float)(y)), this.width, this.height, 256, 256)','SKILL_EDIT_UI, this.getX(), this.getY(), 237.0F, (float)y, this.width, this.height, 256, 256)')
     .replace('SKILL_EDIT_UI, this.getX(), (float)((float)(237)), (float)((float)(yTex)), this.width, this.height, 256, 256)','SKILL_EDIT_UI, this.getX(), this.getY(), 237.0F, (float)yTex, this.width, this.height, 256, 256)')
     .replace('SKILL_EDIT_UI, this.getX(), (float)((float)(0)), (float)((float)(texY)), this.width, this.height, 256, 256)','SKILL_EDIT_UI, this.getX(), this.getY(), 0.0F, (float)texY, this.width, this.height, 256, 256)'))
edit('client/gui/datapack/widgets/PopupBox.java',lambda s:
    s.replace('POPUP_ICON, this._getX() + this.width - this.height, (float)((float)(0.0F)), (float)((float)(0.0F)), this.height, this.height, this.height, this.height)','POPUP_ICON, this._getX() + this.width - this.height, this._getY(), 0.0F, 0.0F, this.height, this.height, this.height, this.height)'))
edit('compat/kubejs/skill/CustomPassiveSkill.java',lambda s:
    re.sub(r'\.blit\(this\.getSkillTexture\(\), \(int\)x, \(int\)y, 24, 24, 0\.0F, 0\.0F, 1, 1, 1, 1\)',r'.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, this.getSkillTexture(), (int)x, (int)y, 0.0F, 0.0F, 24, 24, 1, 1, 1, 1)',s))

# buttons
btnfiles=['client/gui/screen/config/HUDLocationsScreen.java','client/gui/screen/SideBarScreen.java','client/gui/screen/SkillBookScreen.java','client/gui/screen/SkillEditScreen.java','client/gui/screen/SlotSelectScreen.java','client/gui/widgets/TextButton.java','client/gui/datapack/widgets/ResizableButton.java','client/gui/widgets/EmoteWheelTab.java','client/gui/screen/config/TPSSettingScreen.java','client/gui/widgets/AnchoredButton.java','client/gui/widgets/SettingTitle.java']
def btn(s):
    s=re.sub(r'(class\s+\w+(?:<[^>]*>)?\s+extends\s+)Button\b(?!\.)',r'\1LegacyButton',s)
    s=s.replace('Button.builder(','LegacyButton.legacyBuilder(') if 'build(LearnButton::new)' in s else s
    s=in_button_classes(s)
    if 'LegacyButton' in s: s=addimp(s,'yesman.epicfight.client.gui.compat.LegacyButton')
    return s
for f in btnfiles:
    edit(f,btn)
# TextButton / AnchoredButton must stay on vanilla Button but still get the rename
def rb(s):
    s=s.replace('public class ResizableButton extends LegacyButton','public class ResizableButton extends LegacyButton')
    s=s.replace('public static class Builder extends Button.Builder','public static class Builder extends LegacyButton.Builder')
    s=s.replace('super(builder);\n\t\t\n\t\tthis.x1 = builder.x1;','super(builder);\n\t\t\n\t\tthis.x1 = builder.x1;')
    return s
edit('client/gui/datapack/widgets/ResizableButton.java',rb)
edit('client/gui/widgets/TextButton.java',lambda s:s.replace('extends LegacyButton','extends Button').replace('import yesman.epicfight.client.gui.compat.LegacyButton;\n',''))
# no-arg onPress callers
for f in ['client/gui/screen/config/ItemsPreferenceScreen.java','client/gui/widgets/EmoteWheelTab.java','client/gui/screen/EmoteWheelScreen.java','client/gui/screen/SkillEditScreen.java']:
    edit(f,lambda s:re.sub(r'\.onPress\(\)',r'.onPress((net.minecraft.client.input.InputWithModifiers) null)',s).replace('this.onPress((net.minecraft.client.input.InputWithModifiers) null);','this.onPress((net.minecraft.client.input.InputWithModifiers) null);'))
print('ok')
