from pathlib import Path
import re
root=Path(__file__).resolve().parents[1]/'src/main/java/yesman/epicfight'
def edit(f,fn):
 p=root/f;s=p.read_text(encoding='utf-8');p.write_text(fn(s),encoding='utf-8')
def method(s,needle,new):
 a=s.index(needle);b=s.index('{',a);d=1;e=b+1
 while d:d+=(s[e]=='{')-(s[e]=='}');e+=1
 return s[:a]+new+s[e:]
# Item model layers retain their transforms, material tint indices and snapshot alpha.
edit('api/utils/EntitySnapshot.java',lambda s:method(method(method(s,'public void renderItems(','''public void renderItems(PoseStack poseStack, MultiBufferSource buffers, RenderType rendertype, Mesh.DrawingFunction drawingFunction, int packedLight, float alpha) {
        for(var item:this.handItems) {
            ItemStack stack=item.getSecond();
            if(!RenderEngine.getInstance().getItemRenderer(stack).appearedInAfterimage()) continue;
            poseStack.pushPose();
            MathUtils.mulStack(poseStack,RenderEngine.getInstance().getItemRenderer(stack).getCorrectionMatrix(this.entitypatch,item.getFirst(),this.poseMatrices));
            var state=new net.minecraft.client.renderer.item.ItemStackRenderState();
            Minecraft.getInstance().getItemModelResolver().updateForLiving(state,stack,item.getFirst()==InteractionHand.MAIN_HAND?ItemDisplayContext.THIRD_PERSON_RIGHT_HAND:ItemDisplayContext.THIRD_PERSON_LEFT_HAND,this.entitypatch.getOriginal());
            for(int i=0;i<state.activeLayerCount;i++) {
                var layer=state.layers[i];
                poseStack.pushPose(); layer.applyTransform(poseStack.last());
                for(var quad:layer.quads.all()) {
                    int tint=quad.materialInfo().tintIndex();
                    int color=layer.tintLayers!=null && tint>=0 && tint<layer.tintLayers.size()?layer.tintLayers.getInt(tint):0xFFFFFFFF;
                    drawingFunction.putBulkData(poseStack.last(),quad,buffers.getBuffer(rendertype),(color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,alpha,packedLight,OverlayTexture.NO_OVERLAY,false);
                }
                poseStack.popPose();
            }
            poseStack.popPose();
        }
    }'''),'public static void renderModelLists(','''public static void renderModelLists(net.minecraft.client.resources.model.geometry.ItemQuads model, ItemStack stack, int light, int overlay, float alpha, PoseStack pose, VertexConsumer buffer, Mesh.DrawingFunction function) {
        renderQuadList(pose,buffer,model.all(),stack,light,overlay,alpha,function);
    }'''),'public static void renderQuadList(','''public static void renderQuadList(PoseStack pose, VertexConsumer buffer, List<BakedQuad> quads, ItemStack stack, int light, int overlay, float alpha, Mesh.DrawingFunction function) {
        for(var quad:quads) function.putBulkData(pose.last(),quad,buffer,1F,1F,1F,alpha,light,overlay,false);
    }''').replace('import net.minecraft.client.renderer.entity.ItemRenderer;', '').replace('import net.minecraft.client.resources.model.BakedModel;', '').replace('import net.minecraft.world.item.ArmorItem;', '').replace('import net.neoforged.neoforge.client.ClientHooks;', '').replace('LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>', 'LivingEntityRenderer').replace('vanillarenderer.getTextureLocation(entitypatch.getOriginal())', 'vanillarenderer.getTextureLocation(vanillarenderer.createRenderState(entitypatch.getOriginal(),1.0F))').replace('entitypatch.getOriginal().getArmorSlots().forEach(itemstack -> {', 'java.util.Arrays.stream(new EquipmentSlot[]{EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}).map(slot->entitypatch.getOriginal().getItemBySlot(slot)).forEach(itemstack -> {').replace('itemstack.getItem() instanceof ArmorItem', 'itemstack.has(net.minecraft.core.component.DataComponents.EQUIPPABLE)').replace('EquipmentSlot armorSlot = itemstack.getEquipmentSlot()', 'EquipmentSlot armorSlot = itemstack.get(net.minecraft.core.component.DataComponents.EQUIPPABLE).slot()'))
(root/'client/renderer/VanillaFakeBlockRenderer.java').write_text('''package yesman.epicfight.client.renderer;
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
''',encoding='utf-8')
p=root/'client/renderer/blockentity/FractureBlockRenderer.java';s=p.read_text(encoding='utf-8');a=s.index('\t\tfloat turnBackTime');b=s.index('\n\t\tthis.blockRenderDispatcher.renderBreakingTexture',a);calc=s[a:b]
p.write_text('''package yesman.epicfight.client.renderer.blockentity;
import org.joml.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.world.level.block.entity.FractureBlockEntity;
import yesman.epicfight.client.renderer.compat.BlockGeometry;
public class FractureBlockRenderer implements BlockEntityRenderer<FractureBlockEntity,FractureBlockRenderer.State> {
 public static class State extends BlockEntityRenderState { Matrix4f transform; java.util.List<net.minecraft.client.resources.model.geometry.BakedQuad> quads; }
 public FractureBlockRenderer(BlockEntityRendererProvider.Context context) {}
 public State createRenderState(){return new State();}
 public void extractRenderState(FractureBlockEntity blockEntity,State state,float partialTicks,Vec3 cameraPosition,ModelFeatureRenderer.CrumblingOverlay crumbling) {
  BlockEntityRenderer.super.extractRenderState(blockEntity,state,partialTicks,cameraPosition,crumbling);
  PoseStack poseStack=new PoseStack();
'''+calc+'''
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
''',encoding='utf-8')
(root/'client/renderer/entity/NoopLivingEntityRenderer.java').write_text('''package yesman.epicfight.client.renderer.entity;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
public class NoopLivingEntityRenderer<T extends LivingEntity> extends LivingEntityRenderer<T,LivingEntityRenderState,EntityModel<LivingEntityRenderState>> {
 public NoopLivingEntityRenderer(EntityRendererProvider.Context context,float shadow) {super(context,new EntityModel<LivingEntityRenderState>(new ModelPart(java.util.List.of(),java.util.Map.of())){},shadow);}
 public LivingEntityRenderState createRenderState(){return new LivingEntityRenderState();}
 public Identifier getTextureLocation(LivingEntityRenderState state){return yesman.epicfight.client.renderer.patched.entity.PWitherRenderer.WITHER_INVULNERABLE_LOCATION;}
 // RenderEngine's dispatcher hook replaces this model with the original Epic Fight mesh.
}
''',encoding='utf-8')
edit('client/renderer/entity/WitherGhostRenderer.java',lambda s:s.replace('public Identifier getTextureLocation(WitherGhostClone entity)', 'public Identifier getTextureLocation(net.minecraft.client.renderer.entity.state.LivingEntityRenderState entity)'))
edit('client/renderer/entity/WitherSkeletonMinionRenderer.java',lambda s:s.replace('public Identifier getTextureLocation(WitherSkeleton entity)', 'public Identifier getTextureLocation(net.minecraft.client.renderer.entity.state.SkeletonRenderState entity)'))
edit('client/renderer/entity/DroppedNetherStarRenderer.java',lambda s:s.replace('public void render(ItemEntity entityIn, float yRot, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight)', 'public void submit(net.minecraft.client.renderer.entity.state.ItemEntityRenderState state, PoseStack poseStack, net.minecraft.client.renderer.SubmitNodeCollector collector, net.minecraft.client.renderer.state.level.CameraRenderState camera)').replace('super.render(entityIn, yRot, partialTicks, poseStack, multiBufferSource, packedLight)', 'super.submit(state, poseStack, collector, camera)').replace('entityIn.getBbHeight()', 'state.boundingBoxHeight').replace('(float) entityIn.getAge() + partialTicks', 'state.ageInTicks').replace('entityIn.bobOffs', 'state.bobOffset').replace('entityIn.tickCount + partialTicks', 'state.ageInTicks').replace('VertexConsumer vertexBuilder = multiBufferSource.getBuffer(RenderTypes.lightning());', 'var multiBufferSource = new yesman.epicfight.client.renderer.compat.SubmitBufferSource(collector);\n        VertexConsumer vertexBuilder = multiBufferSource.getBuffer(RenderTypes.lightning());').replace('\t\tposeStack.popPose();', '\t\tposeStack.popPose();\n        multiBufferSource.endBatch();'))
print('Native block, item snapshot and special entity rendering applied')
