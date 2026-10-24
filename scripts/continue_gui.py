"""One-time continuation of the 26.3 GUI migration; source contracts checked in mcsrc."""
import re
from pathlib import Path
root=Path(__file__).resolve().parents[1]/'src/main/java/dev/war/openfight'
def edit(rel,fn):
 p=root/rel;s=p.read_text(encoding='utf-8');n=fn(s)
 if s!=n:p.write_text(n,encoding='utf-8')
def method(s,needle,new):
 a=s.index(needle);b=s.index('{',a);d=1;e=b+1
 while d:
  d+=(s[e]=='{')-(s[e]=='}');e+=1
 return s[:a]+new+s[e:]
for folder in ['client/gui','epicskins/client']:
 for p in (root/folder).rglob('*.java'):
  def fix(s):
   s=re.sub(r'\.mouseClicked\((?:x|mouseX), (?:y|mouseY), button\)', '.mouseClicked(event, doubleClick)',s)
   s=re.sub(r'\.mouseReleased\((?:x|mouseX), (?:y|mouseY), button\)', '.mouseReleased(event)',s)
   s=re.sub(r'\.mouseDragged\((?:x|mouseX), (?:y|mouseY), button, dx, dy\)', '.mouseDragged(event, dx, dy)',s)
   s=s.replace('.keyPressed(keycode, p_100876_, p_100877_)','.keyPressed(event)').replace('.charTyped(c, i)','.charTyped(event)')
   s=s.replace('.renderListItems(','.extractListItems(')
   s=s.replace('protected int getRowTop(', 'public int getRowTop(').replace('protected int getRowBottom(', 'public int getRowBottom(')
   s=s.replace('DatapackEditScreen.Minecraft','Minecraft')
   s=re.sub(r'TabNavigationBar.builder\(this.tabManager, this.width\)', 'TabNavigationBar.builder(this.tabManager, 0, 0, this.width, 24)',s)
   s=s.replace('this.tabNavigationBar.arrangeElements();','this.tabNavigationBar.arrangeElements(this.width);').replace('this.tabNavigationBar.keyPressed(keycode)','this.tabNavigationBar.keyPressed(event)')
   s=s.replace('this.parentScreen.init(this.height)','this.parentScreen.init(this.width, this.height)')
   s=s.replace('Blaze3D.openPath(resourcePackDirectory)','Blaze3D.openPath(resourcePackDirectory.toPath())')
   s=s.replace('registry.containsValue(item)','registry.getKey(item) != null')
   s=s.replace('this.registry.get(', 'this.registry.getValue(')
   s=s.replace('SharedConstants.getCurrentVersion().getId()','SharedConstants.getCurrentVersion().id()')
   s=s.replace('EntityType.byString(', 'dev.war.openfight.client.gui.compat.GuiPort.entityType(')
   s=re.sub(r'(?m)^.*Minecraft.getInstance\(\).getMainRenderTarget\(\).enableStencil\(\);\n','',s)
   s=re.sub(r'(?m)^.*RenderSystem\.(stencilOp|stencilFunc|stencilMask|clear)\([^\n]*\);\n','',s)
   return s
  edit(str(p.relative_to(root)),fix)
edit('epicskins/client/widget/CapePopupBox.java',lambda s:s.replace('clickedPopupButton(x, y)','clickedPopupButton(event.x(), event.y())'))
# NBT accessors return Optional now, retaining the upstream empty defaults.
for f in ['client/gui/datapack/screen/DatapackEditScreen.java','client/gui/datapack/screen/CombatBehaviorScreen.java','client/gui/datapack/screen/StylesScreen.java']:
 edit(f,lambda s:re.sub(r'\.getString\(([^()]+)\)',r'.getStringOr(\1, "")',s).replace('.getCompound("trail")','.getCompoundOrEmpty("trail")'))
edit('client/gui/datapack/screen/DatapackEditScreen.java',lambda s:s.replace('PackResources packResources = pack.open();','PackResources packResources = pack.open().findFirst().orElseThrow();'))
for f in ['client/gui/widgets/ColorSlider.java','client/gui/widgets/ColorDeterminator.java']:
 edit(f,lambda s:method(s,'private static void fillGradient(','''private static void fillGradient(GuiGraphicsExtractor guiGraphics, int x1, int y1, int x2, int y2, int startColor, int endColor) {
        for (int x = x1; x < x2; x++) {
            float t = (float)(x - x1) / Math.max(1, x2 - x1 - 1);
            guiGraphics.fill(x, y1, x + 1, y2, net.minecraft.util.ARGB.lerp(t, startColor, endColor));
        }
    }''').replace('y2, widget.colors[i]);','y2, widget.colors[i], widget.colors[i + 1]);'))
# Loader metadata is available from Fabric, with no NeoForge UI dependency.
for f in ['client/gui/VersionNotifier.java','client/gui/screen/config/EpicFightSettingScreen.java']:
 edit(f,lambda s:s.replace('import net.neoforged.fml.ModList;','import net.fabricmc.loader.api.FabricLoader;').replace('import net.neoforged.fml.ModContainer;','import net.fabricmc.loader.api.ModContainer;').replace('ModList.get().getModFileById(EpicFightMod.MODID).versionString()','FabricLoader.getInstance().getModContainer(EpicFightMod.MODID).orElseThrow().getMetadata().getVersion().getFriendlyString()'))
edit('client/gui/screen/config/EpicFightSettingScreen.java',lambda s:s.replace('abstract class SettingTabPage implements Tab {','abstract class SettingTabPage implements Tab {\n        public net.minecraft.client.gui.layouts.Layout getLayout() {\n            net.minecraft.client.gui.layouts.FrameLayout layout = new net.minecraft.client.gui.layouts.FrameLayout();\n            layout.addChild(this.widgetTable);\n            layout.addChild(this.textBox);\n            return layout;\n        }'))
edit('client/gui/screen/config/ItemsPreferenceScreen.java',lambda s:re.sub(r'(?m)^.*\.add\((SwordItem|AxeItem|HoeItem|PickaxeItem|ShovelItem).class\);\n','',s).replace('boolean isweapon = false;','if (item.getDefaultInstance().is(net.minecraft.tags.ItemTags.SWORDS)) return true;\n        if (item.getDefaultInstance().is(net.minecraft.tags.ItemTags.AXES) || item.getDefaultInstance().is(net.minecraft.tags.ItemTags.HOES) || item.getDefaultInstance().is(net.minecraft.tags.ItemTags.PICKAXES) || item.getDefaultInstance().is(net.minecraft.tags.ItemTags.SHOVELS)) return false;\n        boolean isweapon = false;').replace('ItemsPreferenceScreen.this.setTooltipForNextRenderPass(this.tooltip)','guiGraphics.setTooltipForNextFrame(this.tooltip, mouseX, mouseY)'))
edit('client/gui/screen/config/HUDLocationsScreen.java',lambda s:re.sub(r'(?m)^.*RenderSystem.setShader(?:Texture)?\([^\n]*\);\n','',method(s,'public void blitRotate(','''public void blitRotate(GuiGraphicsExtractor guiGraphics, Vec2[] texCoords) {
                float u = Math.min(Math.min(texCoords[0].x, texCoords[1].x), Math.min(texCoords[2].x, texCoords[3].x));
                float v = Math.min(Math.min(texCoords[0].y, texCoords[1].y), Math.min(texCoords[2].y, texCoords[3].y));
                int turns = texCoords[0].x == u ? (texCoords[0].y == v ? 0 : 3) : (texCoords[0].y == v ? 1 : 2);
                float size = (this.isHovered ? 36 : 31);
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(this.getX() + this.width / 2.0F, this.getY() + this.height / 2.0F);
                guiGraphics.pose().rotate((float)(turns * Math.PI / 2));
                guiGraphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, BATTLE_ICONS, -this.width/2, -this.height/2, u * 255, v * 255, this.width, this.height, (int)size, (int)size, 255, 255);
                guiGraphics.pose().popMatrix();
            }''')))
print('GUI continuation applied')
