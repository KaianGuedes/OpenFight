import re
from pathlib import Path
root=Path(__file__).resolve().parents[1]/'src/main/java'
files=[l.strip() for l in '''yesman/epicfight/client/gui/datapack/screen/SelectAnimationScreen.java
yesman/epicfight/client/gui/datapack/screen/SelectFromRegistryScreen.java
yesman/epicfight/client/gui/datapack/screen/SelectModelScreen.java
yesman/epicfight/client/gui/datapack/widgets/ComboBox.java
yesman/epicfight/client/gui/datapack/widgets/Grid.java
yesman/epicfight/client/gui/datapack/widgets/InputComponentList.java
yesman/epicfight/client/gui/screen/config/ItemsPreferenceScreen.java
yesman/epicfight/client/gui/screen/EmoteEditScreen.java
yesman/epicfight/client/gui/screen/SideBarScreen.java
yesman/epicfight/client/gui/screen/SkillBookScreen.java
yesman/epicfight/client/gui/widgets/ComboBox.java
yesman/epicfight/client/gui/widgets/WidgetTable.java
yesman/epicfight/epicskins/client/screen/AvatarEditScreen.java'''.split('\n')]
for f in files:
    p=root/f
    raw=p.read_bytes().decode('utf-8');crlf='\r\n' in raw;s=raw.replace('\r\n','\n')
    s=s.replace('import net.minecraft.client.gui.components.ObjectSelectionList;','import yesman.epicfight.client.gui.compat.LegacyObjectSelectionList;')
    s=s.replace('import net.minecraft.client.gui.components.ContainerObjectSelectionList;','import yesman.epicfight.client.gui.compat.LegacyContainerObjectSelectionList;')
    s=re.sub(r'(?<![A-Za-z])ObjectSelectionList','LegacyObjectSelectionList',s)
    s=re.sub(r'(?<![A-Za-z])ContainerObjectSelectionList','LegacyContainerObjectSelectionList',s)
    s=s.replace('this.updateScrollingState(x, y, button);','')
    if crlf:s=s.replace('\n','\r\n')
    p.write_bytes(s.encode('utf-8'))
print('ok')
