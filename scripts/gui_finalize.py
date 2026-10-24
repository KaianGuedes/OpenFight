from pathlib import Path
import re,subprocess
root=Path(__file__).resolve().parents[1]
j=root/'src/main/java/yesman/epicfight'
p=j/'client/gui/widgets/FilteredEditBox.java'
p.write_bytes(subprocess.check_output(['git','show','HEAD:src/main/java/yesman/epicfight/client/gui/widgets/FilteredEditBox.java'],cwd=root))
(j/'client/gui/compat/FilteredEditBox.java').write_text('''package yesman.epicfight.client.gui.compat;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
public class FilteredEditBox extends yesman.epicfight.client.gui.widgets.FilteredEditBox {
 public FilteredEditBox(Font f,Component m){super(f,150,20,m);}
 public FilteredEditBox(Font f,int w,int h,Component m){super(f,w,h,m);}
 public FilteredEditBox(Font f,int x,int y,int w,int h,Component m){super(f,x,y,w,h,m);}
 public FilteredEditBox(Font f,int x,int y,int w,int h,EditBox old,Component m){super(f,x,y,w,h,m);if(old!=null)this.setValue(old.getValue());}
}
''',encoding='utf-8')
for f in ['client/gui/BattleModeGui.java','client/gui/screen/config/HUDLocationsScreen.java']:
 p=j/f;s=p.read_text(encoding='utf-8').replace('poseStack.pushPose()', 'poseStack.pushMatrix()').replace('poseStack.popPose()', 'poseStack.popMatrix()').replace('poseStack.translate(0, sliding, 0)', 'poseStack.translate(0, sliding)').replace('poseStack.translate(0, 0, 200)', 'guiGraphics.nextStratum()');p.write_text(s,encoding='utf-8')
p=j/'client/gui/widgets/EmoteWheelTab.java';s=p.read_text(encoding='utf-8');a=s.index('class EmoteWheelButton ');s=s[:a]+s[a:].replace('void extractWidgetRenderState(', 'void extractContents(');p.write_text(s,encoding='utf-8')
p=j/'client/gui/screen/config/TPSSettingScreen.java';s=p.read_text(encoding='utf-8');a=s.index('class ZoomScroll ');s=s[:a]+s[a:].replace('void extractContents(', 'void extractWidgetRenderState(');p.write_text(s,encoding='utf-8')
p=j/'client/gui/widgets/ModelPreviewer.java';s=p.read_text(encoding='utf-8').replace('        @Override protected int contentHeight() { return this.getHeight(); }\n','');p.write_text(s,encoding='utf-8')
# Keep the newline convention of each existing tracked file to keep reviews focused.
for name in subprocess.check_output(['git','diff','--name-only'],cwd=root,text=True).splitlines():
 p=root/name
 if p.suffix not in ['.java','.md']:continue
 old=subprocess.check_output(['git','show','HEAD:'+name],cwd=root)
 data=p.read_bytes().replace(b'\r\n',b'\n')
 if b'\r\n' in old:data=data.replace(b'\n',b'\r\n')
 p.write_bytes(data)
