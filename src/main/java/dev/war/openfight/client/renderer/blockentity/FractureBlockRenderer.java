package dev.war.openfight.client.renderer.blockentity;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Quaternionf;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import dev.war.openfight.api.utils.math.MathUtils;
import dev.war.openfight.world.level.block.entity.FractureBlockEntity;
import dev.war.openfight.client.renderer.compat.BlockGeometry;
public class FractureBlockRenderer implements BlockEntityRenderer<FractureBlockEntity,FractureBlockRenderer.State> {
 public static class State extends BlockEntityRenderState { Matrix4f transform; java.util.List<net.minecraft.client.resources.model.geometry.BakedQuad> quads; }
 public FractureBlockRenderer(BlockEntityRendererProvider.Context context) {}
 public State createRenderState(){return new State();}
 public void extractRenderState(FractureBlockEntity blockEntity,State state,float partialTicks,Vec3 cameraPosition,ModelFeatureRenderer.CrumblingOverlay crumbling) {
  BlockEntityRenderer.super.extractRenderState(blockEntity,state,partialTicks,cameraPosition,crumbling);
  PoseStack poseStack=new PoseStack();
		float turnBackTime = 5.0F;
		float lerpAmount = Mth.clamp(partialTicks * (1.0F / turnBackTime) + (turnBackTime - (blockEntity.getMaxLifeTime() - blockEntity.getLifeTime())) * (1.0F / turnBackTime), 0.0F, 1.0F);
		
		Vector3f translate = blockEntity.getMaxLifeTime() > blockEntity.getLifeTime() + turnBackTime ? blockEntity.getTranslate() : MathUtils.lerpMojangVector(blockEntity.getTranslate(), new Vector3f(), lerpAmount);
		Quaternionf rotate = blockEntity.getMaxLifeTime() > blockEntity.getLifeTime() + turnBackTime ? blockEntity.getRotation() : MathUtils.lerpQuaternion(blockEntity.getRotation(), new Quaternionf(), lerpAmount);
		
		double BOUNCE_MAX_HEIGHT = blockEntity.getBouncing();
		double TIME = Math.max(BOUNCE_MAX_HEIGHT * 8.0D, 8.0D);
		double EXTENDER = 1 / Math.pow(TIME * 0.5, 2);
		double MOVE_GRAPH = Math.sqrt(BOUNCE_MAX_HEIGHT / EXTENDER);
		double bouncingAnimation = Math.max(-EXTENDER * Math.pow(blockEntity.getLifeTime() + partialTicks - MOVE_GRAPH, 2.0D) + BOUNCE_MAX_HEIGHT, 0.0D);
		
		poseStack.pushPose();
		poseStack.translate(0.5D, 0.5D, 0.5D);
		poseStack.rotate(rotate);
		poseStack.translate(translate.x(), translate.y() + bouncingAnimation, translate.z());
		poseStack.translate(-0.5D, -0.5D, -0.5D);	
		
  state.transform=new Matrix4f(poseStack.last().pose());
  var block=blockEntity.getOriginalBlockState();state.quads=BlockGeometry.quads(block,block.getSeed(blockEntity.getBlockPos()));
 }
 public void submit(State state,PoseStack pose,SubmitNodeCollector collector,CameraRenderState camera) {
  if(state.quads==null)return;
  pose.pushPose();pose.mulPose(state.transform);
  collector.submitCustomGeometry(pose,RenderTypes.cutoutMovingBlock(),(p,b)->{
   PoseStack transformed=new PoseStack();transformed.last().set(p);
   BlockGeometry.write(state.quads,transformed,b,state.lightCoords,1F,1F,1F,1F);
  });pose.popPose();
 }
}
