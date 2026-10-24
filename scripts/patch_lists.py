import re
from pathlib import Path
root=Path(__file__).resolve().parents[1]/'src/main/java'
files=[l.strip() for l in '''dev/war/openfight/client/gui/datapack/screen/SelectAnimationScreen.java
dev/war/openfight/client/gui/datapack/screen/SelectFromRegistryScreen.java
dev/war/openfight/client/gui/datapack/screen/SelectModelScreen.java
dev/war/openfight/client/gui/datapack/widgets/ComboBox.java
dev/war/openfight/client/gui/datapack/widgets/Grid.java
dev/war/openfight/client/gui/datapack/widgets/InputComponentList.java
dev/war/openfight/client/gui/screen/config/ItemsPreferenceScreen.java
dev/war/openfight/client/gui/screen/EmoteEditScreen.java
dev/war/openfight/client/gui/screen/SideBarScreen.java
dev/war/openfight/client/gui/screen/SkillBookScreen.java
dev/war/openfight/client/gui/widgets/ComboBox.java
dev/war/openfight/client/gui/widgets/WidgetTable.java
dev/war/openfight/epicskins/client/screen/AvatarEditScreen.java'''.split('\n')]
for f in files:
    p=root/f
    raw=p.read_bytes().decode('utf-8');crlf='\r\n' in raw;s=raw.replace('\r\n','\n')
    s=s.replace('import net.minecraft.client.gui.components.ObjectSelectionList;','import dev.war.openfight.client.gui.compat.LegacyObjectSelectionList;')
    s=s.replace('import net.minecraft.client.gui.components.ContainerObjectSelectionList;','import dev.war.openfight.client.gui.compat.LegacyContainerObjectSelectionList;')
    s=re.sub(r'(?<![A-Za-z])ObjectSelectionList','LegacyObjectSelectionList',s)
    s=re.sub(r'(?<![A-Za-z])ContainerObjectSelectionList','LegacyContainerObjectSelectionList',s)
    s=s.replace('this.updateScrollingState(x, y, button);','')
    if crlf:s=s.replace('\n','\r\n')
    p.write_bytes(s.encode('utf-8'))
print('ok')
