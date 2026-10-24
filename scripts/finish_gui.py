import re
from pathlib import Path
root=Path(__file__).resolve().parents[1]/'src/main/java/dev/war/openfight'
def edit(rel,fn):
 p=root/rel;s=p.read_text(encoding='utf-8');n=fn(s)
 if s!=n:p.write_text(n,encoding='utf-8')
def method(s,needle,new):
 a=s.index(needle);b=s.index('{',a);d=1;e=b+1
 while d:d+=(s[e]=='{')-(s[e]=='}');e+=1
 return s[:a]+new+s[e:]
for folder in ['client/gui','epicskins/client']:
 for p in (root/folder).rglob('*.java'):
  def fix(s):
   s=re.sub(r'\.mouseClicked\((x|mouseX), (y|mouseY), (action|pressType)\)', '.mouseClicked(event, doubleClick)',s)
   s=s.replace('.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)', '.mouseDragged(event, deltaX, deltaY)')
   s=s.replace('this.onClick(x, y)', 'this.onClick(event, doubleClick)').replace('this.onRelease(mouseX, mouseY)', 'this.onRelease(event)')
   s=s.replace('super.clicked(mouseX, mouseY)', 'this.isMouseOver(mouseX, mouseY)').replace('this.clicked(mouseX, mouseY)', 'this.isMouseOver(mouseX, mouseY)')
   s=s.replace('PoseStack poseStack = guiGraphics.pose();','org.joml.Matrix3x2fStack poseStack = guiGraphics.pose();')
   s=s.replace('.resize(this.minecraft, this.width, this.height)', '.resize(this.width, this.height)')
   s=s.replace('backgroundScreen.init(SideBarScreen.this.height)', 'backgroundScreen.init(SideBarScreen.this.width, SideBarScreen.this.height)').replace('this.parent.init(this.height)', 'this.parent.init(this.width, this.height)')
   s=s.replace('.keyPressed(keyCode, scanCode, modifiers)', '.keyPressed(event)')
   s=s.replace('registry.holders().map(Holder::value)', 'registry.stream()')
   s=s.replace('.getSkin().texture()', '.getSkin().body().texturePath()').replace('playerSkin.texture()', 'playerSkin.body().texturePath()')
   s=s.replace('net.minecraft.util.ARGB.lerp(t, startColor, endColor)', '(int)dev.war.openfight.api.utils.math.ColorUtil.mixPackedARGB(t, startColor, endColor)')
   s=s.replace('this.renderTransparentBackground(guiGraphics)', 'this.extractTransparentBackground(guiGraphics)')
   return s
  edit(str(p.relative_to(root)),fix)
edit('client/gui/compat/LegacyButton.java',lambda s:s.replace('public int getFGColor()', 'protected boolean clicked(double x, double y) { return this.isActive() && this.isMouseOver(x, y); }\n\n\tpublic int getFGColor()'))
edit('client/gui/compat/LegacySelectionList.java',lambda s:s.replace('protected final E getEntryAtPosition(', 'protected E getEntryAtPosition('))
edit('client/gui/compat/LegacyContainerObjectSelectionList.java',lambda s:s.replace('private GuiEventListener focused;', 'protected GuiEventListener focused;'))
edit('client/gui/widgets/WidgetTable.java',lambda s:s.replace('this.getFocused().children()', '((net.minecraft.client.gui.components.events.ContainerEventHandler)this.getFocused()).children()').replace('this.getFocused().setFocused(e)', '((net.minecraft.client.gui.components.events.ContainerEventHandler)this.getFocused()).setFocused(e)'))
for f in ['client/gui/widgets/ColorSlider.java','client/gui/widgets/ColorDeterminator.java']:
 edit(f,lambda s:s.replace('widget.getFGColor()', '(widget.active ? 0xFFFFFFFF : 0xFFA0A0A0)'))
edit('client/gui/widgets/SettingTitle.java',lambda s:method(s,'private static void fillGradientLeftToRight(','''private static void fillGradientLeftToRight(GuiGraphicsExtractor guiGraphics, int x1, int y1, int x2, int y2, int pColorFrom, int pColorTo) {
        for (int x = x1; x < x2; x++) guiGraphics.fill(x, y1, x+1, y2, (int)dev.war.openfight.api.utils.math.ColorUtil.mixPackedARGB((double)(x-x1)/Math.max(1,x2-x1-1), pColorFrom, pColorTo));
    }'''))
edit('client/gui/screen/config/HUDLocationsScreen.java',lambda s:method(s,'public void drawOutline(','''public void drawOutline(GuiGraphicsExtractor guiGraphics) {
            int x=this.getX()-1, y=this.getY()-1, r=this.getRight()+1, b=this.getBottom()+1, color=0xFF45A6F4;
            guiGraphics.fill(x,y,r,y+2,color); guiGraphics.fill(x,b-2,r,b,color);
            guiGraphics.fill(x,y,x+2,b,color); guiGraphics.fill(r-2,y,r,b,color);
            int ax=this.horizontalBasis.getValue()==HorizontalBasis.CENTER?this.parentScreen.width/2:this.horizontalBasis.getValue()==HorizontalBasis.LEFT?0:this.parentScreen.width;
            int ay=this.verticalBasis.getValue()==VerticalBasis.CENTER?this.parentScreen.height/2:this.verticalBasis.getValue()==VerticalBasis.TOP?0:this.parentScreen.height;
            guiGraphics.fill(Math.min(x,ax),y,Math.max(x,ax)+1,y+1,color);
            guiGraphics.fill(x,Math.min(y,ay),x+1,Math.max(y,ay)+1,color);
        }''').replace('protected void extractWidgetRenderState(', 'protected void extractContents('))
# The HUD popup is a Screen, so restore its Screen extraction method.
edit('client/gui/screen/config/HUDLocationsScreen.java',lambda s:s.replace('protected void extractContents(GuiGraphicsExtractor guiGraphics, int x, int y, float partialTicks) {\n            super.extractWidgetRenderState', 'protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int x, int y, float partialTicks) {\n            super.extractWidgetRenderState'))
edit('client/gui/screen/config/TPSSettingScreen.java',lambda s:s.replace('    @Override\n    protected void renderBlurredBackground(', '    protected void renderBlurredBackground(').replace('protected void extractWidgetRenderState(', 'protected void extractContents('))
edit('client/gui/screen/SkillEditScreen.java',lambda s:s.replace('protected void extractWidgetRenderState(@NotNull', 'protected void extractContents(@NotNull'))
edit('client/gui/widgets/EmoteWheelTab.java',lambda s:s.replace('PoseStack poseStack', 'org.joml.Matrix3x2fStack poseStack').replace('poseStack.translate(', 'poseStack.translate(').replace('public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {\n            this.theme', 'public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {\n            this.theme').replace('protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {\n            super.extractWidgetRenderState', 'protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {\n            super.extractContents'))
edit('client/gui/screen/config/EpicFightSettingScreen.java',lambda s:s.replace('abstract class SettingTabPage implements Tab {','abstract class SettingTabPage implements Tab {\n        public Component getTabExtraNarration() { return Component.empty(); }'))
for f in ['client/gui/screen/config/EpicFightSettingScreen.java','client/gui/datapack/screen/DatapackEditScreen.java']:
 edit(f,lambda s:s.replace('TabNavigationBar.builder(this.tabManager, 0, 0, this.width, 24).addTabs(', 'dev.war.openfight.client.gui.compat.GuiPort.tabs(this.tabManager, this.width, '))
edit('client/gui/BattleModeGui.java',lambda s:re.sub(r'import com.mojang.blaze3d.platform.GlStateManager[^;]*;\n','',s))
edit('client/gui/HealthBar.java',lambda s:s.replace('import net.neoforged.neoforge.common.Tags.EntityTypes;', '').replace('EntityTypes.BOSSES','net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "bosses"))'))
edit('client/gui/screen/overlay/OverlayManager.java',lambda s:s.replace('Minecraft.renderNames()', '!Minecraft.getInstance().options.hideGui'))
# GUI projection now belongs to the renderer; scale only the extracted GUI pose.
edit('client/gui/screen/SkillBookScreen.java',lambda s:re.sub(r'(?m)^.*(?:import net.neoforged.neoforge.client.ClientHooks;|window.setGuiScale\(|Matrix4f matrix4f = \(new Matrix4f\(\)\).setOrtho|RenderSystem.setProjectionMatrix|RenderSystem.setShader\().*\n','',s).replace('//Fix: expand extra far plane distance','guiGraphics.pose().scale((float)(this.customScale / originalScale));').replace('return super.mouseClicked((int)(x * window.getGuiScale() / this.customScale), (int)(y * window.getGuiScale() / this.customScale), button);','return super.mouseClicked(new MouseButtonEvent(x * window.getGuiScale() / this.customScale, y * window.getGuiScale() / this.customScale, event.buttonInfo()), doubleClick);').replace('return super.mouseReleased((int)(x * window.getGuiScale() / this.customScale), (int)(y * window.getGuiScale() / this.customScale), button);','return super.mouseReleased(new MouseButtonEvent(x * window.getGuiScale() / this.customScale, y * window.getGuiScale() / this.customScale, event.buttonInfo()));').replace('return super.mouseDragged((int)(mouseX * window.getGuiScale() / this.customScale), (int)(mouseY * window.getGuiScale() / this.customScale), button, dx, dy);','return super.mouseDragged(new MouseButtonEvent(mouseX * window.getGuiScale() / this.customScale, mouseY * window.getGuiScale() / this.customScale, event.buttonInfo()), dx * window.getGuiScale() / this.customScale, dy * window.getGuiScale() / this.customScale);'))
print('GUI follow-up applied')
