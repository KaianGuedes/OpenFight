from pathlib import Path
root=Path(__file__).resolve().parents[1]/'src/main/java/yesman/epicfight'
for f in ['client/gui/screen/overlay/OverlayManager.java','client/gui/screen/overlay/FlickeringOverlay.java']:
 p=root/f;s=p.read_text(encoding='utf-8').replace('renderTick(int xResolution, int yResolution)', 'renderTick(net.minecraft.client.gui.GuiGraphicsExtractor graphics, int xResolution, int yResolution)').replace('render(xResolution, yResolution)', 'render(graphics, xResolution, yResolution)').replace('render(int xResolution, int yResolution)', 'render(net.minecraft.client.gui.GuiGraphicsExtractor graphics, int xResolution, int yResolution)').replace('Minecraft.getInstance().options.hideGui', 'Minecraft.getInstance().gameRenderer.gameRenderState().guiRenderState.isHudHidden');p.write_text(s,encoding='utf-8')
p=root/'client/events/engine/RenderEngine.java';s=p.read_text(encoding='utf-8').replace('this.overlayManager.renderTick(graphics.guiWidth(), graphics.guiHeight())', 'this.overlayManager.renderTick(graphics, graphics.guiWidth(), graphics.guiHeight())');p.write_text(s,encoding='utf-8')
(root/'client/gui/screen/overlay/BlendingTextureOverlay.java').write_text('''package yesman.epicfight.client.gui.screen.overlay;
import net.minecraft.resources.Identifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
public class BlendingTextureOverlay extends OverlayManager.Overlay {
 public Identifier texture;
 private boolean isAlive=true;
 public BlendingTextureOverlay(Identifier texture){this.texture=texture;}
 public void remove(){this.isAlive=false;}
 @Override public boolean render(GuiGraphicsExtractor graphics,int width,int height){
  graphics.blit(RenderPipelines.GUI_TEXTURED,this.texture,0,0,0F,0F,width,height,1,1,1,1);
  return !this.isAlive;
 }
}
''',encoding='utf-8')
