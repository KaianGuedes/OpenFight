package yesman.epicfight.client.renderer.compat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
public class ItemRenderer {
 public void renderStatic(ItemStack stack,ItemDisplayContext context,int light,int overlay,PoseStack pose,MultiBufferSource buffers,Level level,int seed) {
  var state=new ItemStackRenderState();
  Minecraft.getInstance().getItemModelResolver().updateForTopItem(state,stack,context,level,null,seed);
  state.submit(pose,((SubmitBufferSource)buffers).collector(),light,overlay,0);
 }
 public void renderItem(LivingEntity entity,ItemStack stack,ItemDisplayContext context,boolean left,PoseStack pose,MultiBufferSource buffers,int light) {
  var state=new ItemStackRenderState();
  Minecraft.getInstance().getItemModelResolver().updateForLiving(state,stack,context,entity);
  state.submit(pose,((SubmitBufferSource)buffers).collector(),light,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,0);
 }
 public void renderMap(PoseStack pose,MultiBufferSource buffers,int light,ItemStack stack) {
  renderStatic(stack,ItemDisplayContext.FIXED,light,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,pose,buffers,Minecraft.getInstance().level,0);
 }
}
