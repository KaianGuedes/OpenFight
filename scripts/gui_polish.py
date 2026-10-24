import re
from pathlib import Path
root=Path(__file__).resolve().parents[1]/'src/main/java/yesman/epicfight'
for folder in ['client/gui','epicskins/client']:
 for p in (root/folder).rglob('*.java'):
  if 'compat' in p.parts:continue
  s=p.read_text(encoding='utf-8');o=s
  s=s.replace('yesman.epicfight.api.utils.math.ColorUtil','yesman.epicfight.api.utils.ColorUtil')
  s=s.replace('import net.minecraft.client.gui.components.EditBox;', 'import yesman.epicfight.client.gui.compat.FilteredEditBox;')
  s=re.sub(r'\bEditBox\b','FilteredEditBox',s)
  if 'extends AbstractContainerWidget' in s:
   s=re.sub(r'(class \w+[^\n]*extends AbstractContainerWidget[^\n]*\{)',r'\1\n        @Override protected int contentHeight() { return this.getHeight(); }',s)
  s=s.replace('poseStack.translate(centerX, centerY, 0)', 'poseStack.translate(centerX, centerY)').replace('poseStack.translate(-centerX, -centerY, 0)', 'poseStack.translate(-centerX, -centerY)').replace('poseStack.scale(interpolation, interpolation, interpolation)', 'poseStack.scale(interpolation, interpolation)')
  if s!=o:p.write_text(s,encoding='utf-8')
print('GUI compatibility polish applied')
