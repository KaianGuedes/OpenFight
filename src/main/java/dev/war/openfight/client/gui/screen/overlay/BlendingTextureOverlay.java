package dev.war.openfight.client.gui.screen.overlay;
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
