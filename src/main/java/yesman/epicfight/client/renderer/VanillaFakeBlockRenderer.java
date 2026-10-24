package yesman.epicfight.client.renderer;
import net.minecraft.client.Camera;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import yesman.epicfight.client.renderer.compat.*;
public class VanillaFakeBlockRenderer implements FakeBlockRenderer {
 public void render(Camera camera,PoseStack pose,MultiBufferSource buffers,Level level,BlockPos pos,float r,float g,float b,float a) {
  var state=level.getBlockState(pos); var cameraPos=camera.position();
  pose.pushPose();pose.translate(pos.getX()-cameraPos.x,pos.getY()-cameraPos.y,pos.getZ()-cameraPos.z);
  BlockGeometry.write(BlockGeometry.quads(state,state.getSeed(pos)),pose,buffers.getBuffer(EpicFightRenderTypes.blockHighlight()),15728880,r,g,b,a);
  pose.popPose();
 }
}
