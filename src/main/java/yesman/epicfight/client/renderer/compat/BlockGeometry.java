package yesman.epicfight.client.renderer.compat;
import java.util.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import yesman.epicfight.api.client.model.Mesh;
public final class BlockGeometry {
 public static List<BakedQuad> quads(BlockState state,long seed) {
  List<BlockStateModelPart> parts=new ArrayList<>();
  Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state).collectParts(net.minecraft.util.RandomSource.create(seed),parts);
  List<BakedQuad> quads=new ArrayList<>();
  for(var part:parts) { quads.addAll(part.getQuads(null)); for(var d:Direction.values())quads.addAll(part.getQuads(d)); }
  return List.copyOf(quads);
 }
 public static void write(List<BakedQuad> quads,PoseStack pose,VertexConsumer buffer,int light,float r,float g,float b,float a) {
  for(var quad:quads) Mesh.DrawingFunction.NEW_ENTITY.putBulkData(pose.last(),quad,buffer,r,g,b,a,light,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,false);
 }
}
