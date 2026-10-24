import re
from pathlib import Path
root=Path(__file__).resolve().parents[1]/'src/main/java/dev/war/openfight'
LEG={'LegacyObjectSelectionList':'dev.war.openfight.client.gui.compat.LegacyObjectSelectionList','LegacyContainerObjectSelectionList':'dev.war.openfight.client.gui.compat.LegacyContainerObjectSelectionList'}
def addimp(s,imp):
    if 'import '+imp+';' in s:return s
    return re.sub(r'(package [^;]+;\n)',r'\1\nimport '+imp+';',s,1)
n=0
for d in ['client/gui','epicskins/client']:
    for p in (root/d).rglob('*.java'):
        raw=p.read_bytes().decode('utf-8');crlf='\r\n' in raw;s=raw.replace('\r\n','\n');o=s
        s=re.sub(r'[ \t]*RenderSystem\.(enableBlend|disableBlend|enableCull|disableCull|enableDepthTest|disableDepthTest|defaultBlendFunc|setShaderColor|blendFunc|blendFuncSeparate|enableScissor|disableScissor)\([^;\n]*\);[ \t]*\n','',s)
        s=s.replace('RenderPipelines.GUI_TEXTURED, RenderPipelines.GUI_TEXTURED,','RenderPipelines.GUI_TEXTURED,')
        s=s.replace('isValidClickButton(button)','isValidClickButton(event.buttonInfo())')
        s=re.sub(r'[ \t]*@Override\n([ \t]*protected boolean clicked\()',r'\1',s)
        s=s.replace('public void onPress() {','public void onPress(net.minecraft.client.input.InputWithModifiers input) {')
        for k,v in LEG.items():
            if re.search(r'\b'+k+r'\b',s): s=addimp(s,v)
        if s!=o:
            n+=1
            if crlf:s=s.replace('\n','\r\n')
            p.write_bytes(s.encode('utf-8'))
# compat edits
p=root/'client/gui/compat/LegacySelectionList.java'
s=p.read_text(encoding='utf-8')
s=s.replace('renderListItems','extractListItems')
s=s.replace('	protected void extractHeader(','''	public void updateSizeAndPosition(int width, int height, int y) {
		this.setSize(width, height);
		this.setY(y);
		this.refreshScrollAmount();
	}

	public void updateSizeAndPosition(int width, int height, int x, int y) {
		this.setX(x);
		this.updateSizeAndPosition(width, height, y);
	}

	protected void extractHeader(''',1)
p.write_text(s,encoding='utf-8')
print('changed',n)
