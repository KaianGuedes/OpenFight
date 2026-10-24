from pathlib import Path
import re,json
root=Path(__file__).resolve().parents[1];j=root/'src/main/java/yesman/epicfight'
def edit(f,fn):
 p=j/f;s=p.read_text(encoding='utf-8');p.write_text(fn(s),encoding='utf-8')
def method(s,needle,new):
 a=s.index(needle);b=s.index('{',a);d=1;e=b+1
 while d:d+=(s[e]=='{')-(s[e]=='}');e+=1
 return s[:a]+new+s[e:]
edit('api/client/camera/EpicFightCameraAPI.java',lambda s:method(s,'private Matrix4f getCompactProjectionMatrix(','''private Matrix4f getCompactProjectionMatrix() {
        var camera=this.minecraft.gameRenderer.mainCamera();
        float aspect=(float)this.minecraft.getWindow().getWidth()/this.minecraft.getWindow().getHeight();
        return new Matrix4f().perspective((float)Math.toRadians(camera.getFov()),aspect,0.05F,this.minecraft.options.getEffectiveRenderDistance()*64F);
    }''').replace('import net.neoforged.neoforge.client.event.ViewportEvent;', '').replace('this.minecraft.levelRenderer.getFrustum()', 'this.minecraft.gameRenderer.mainCamera().getCullFrustum()').replace('this.minecraft.player.onGround()\n', 'this.minecraft.player.onGround(), this.minecraft.player.horizontalCollision\n').replace('combatItem.getItem().canAttackBlock(bs,', 'combatItem.canDestroyBlock(bs,'))
edit('client/renderer/blockentity/FractureBlockRenderer.java',lambda s:s.replace('import org.joml.*;', 'import org.joml.Matrix4f;\nimport org.joml.Vector3f;\nimport org.joml.Quaternionf;'))
edit('api/utils/EntitySnapshot.java',lambda s:s.replace('vanillarenderer.getTextureLocation(vanillarenderer.createRenderState(entitypatch.getOriginal(),1.0F))', 'vanillarenderer.getTextureLocation((net.minecraft.client.renderer.entity.state.LivingEntityRenderState)vanillarenderer.createRenderState(entitypatch.getOriginal(),1.0F))'))
# Native data-driven range property replaces the original baked-model override mutation.
(j/'client/renderer/patched/item/EpicFightItemProperties.java').write_text('''package yesman.epicfight.client.renderer.patched.item;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.properties.numeric.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.world.item.SkillBookItem;
public final class EpicFightItemProperties {
 public static void registerItemProperties(){ RangeSelectItemModelProperties.ID_MAPPER.put(yesman.epicfight.main.EpicFightMod.identifier("skill"),SkillCategoryProperty.CODEC); }
 public record SkillCategoryProperty() implements RangeSelectItemModelProperty {
  public static final MapCodec<SkillCategoryProperty> CODEC=MapCodec.unit(new SkillCategoryProperty());
  public float get(ItemStack stack,ClientLevel level,ItemOwner owner,int seed){return SkillBookItem.getContainSkill(stack).map(s->(float)s.value().getCategory().universalOrdinal()).orElse(-1F);}
  public MapCodec<SkillCategoryProperty> type(){return CODEC;}
 }
}
''',encoding='utf-8')
edit('client/EpicFightFabricClient.java',lambda s:s.replace('        EpicFightKeyMappings.registerKeys();', '        EpicFightKeyMappings.registerKeys();\n        yesman.epicfight.client.renderer.patched.item.EpicFightItemProperties.registerItemProperties();'))
icons=['skillbook','skillbook_dodge','skillbook_passive','skillbook','skillbook','skillbook_guard','skillbook','skillbook_mover','skillbook_identity','skillbook']
model=lambda name:{'type':'minecraft:model','model':'epicfight:item/'+name}
(root/'src/main/resources/assets/epicfight/items/skillbook.json').write_text(json.dumps({'model':{'type':'minecraft:range_dispatch','property':'epicfight:skill','entries':[{'threshold':i,'model':model(icon)} for i,icon in enumerate(icons)],'fallback':model('skillbook')}},indent=2)+'\n',encoding='utf-8')
edit('mixin/common/MixinThrownTrident.java',lambda s:s.replace('epicfight_playerTouch(ThrownTrident entity,', 'epicfight_playerTouch(AbstractArrow entity,').replace('super.playerTouch(player);', 'original.call(entity,player);').replace('entity.setPosRaw(x, y, z);', 'operation.call(entity,x,y,z);').replace('entity.setDeltaMovement(vec3);', 'operation.call(entity,vec3);'))
p=j/'mixin/client/MixinThrownTridentRenderer.java';s=p.read_text(encoding='utf-8')
start=s.index('\t\tThrownTridentPatch tridentPatch');end=s.index('\n\t\t\t\ttridentPatch.renderXRot',start)
body=s[start:end]
header='''package yesman.epicfight.mixin.client;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.entity.state.ThrownTridentRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import yesman.epicfight.api.utils.math.*;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.projectile.ThrownTridentPatch;
import yesman.epicfight.client.renderer.compat.StateEntityAccess;
@Mixin(ThrownTridentRenderer.class)
public abstract class MixinThrownTridentRenderer {
 @Unique private float epicfight$oldX,epicfight$oldY;
 @Inject(method="submit(Lnet/minecraft/client/renderer/entity/state/ThrownTridentRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",at=@At(value="INVOKE",target="Lcom/mojang/blaze3d/vertex/PoseStack;rotateDegrees(Lcom/mojang/math/Axis;F)V",ordinal=0))
 private void epicfight$render(ThrownTridentRenderState state,PoseStack poseStack,SubmitNodeCollector collector,CameraRenderState camera,CallbackInfo info) {
  this.epicfight$oldX=state.xRot;this.epicfight$oldY=state.yRot;
  ThrownTrident tridentEntity=(ThrownTrident)((StateEntityAccess)state).epicfight$getEntity();
  float partialTicks=((StateEntityAccess)state).epicfight$getPartialTick();
'''
p.write_text(header+body+'''
                state.xRot=-90F;state.yRot=90F;
            }
        }
 }
 @Inject(method="submit(Lnet/minecraft/client/renderer/entity/state/ThrownTridentRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",at=@At("TAIL"))
 private void epicfight$restore(ThrownTridentRenderState state,PoseStack pose,SubmitNodeCollector collector,CameraRenderState camera,CallbackInfo info){state.xRot=this.epicfight$oldX;state.yRot=this.epicfight$oldY;}
}
''',encoding='utf-8')
# Dispatcher already replaces the dragon model; this preserves external pre-submit event listeners.
p=j/'mixin/client/MixinEnderDragonRenderer.java';s=p.read_text(encoding='utf-8');s=method(s,'\t@Inject(','''\t@Inject(at=@At("HEAD"),method="submit(Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",cancellable=true)
    private void epicfight$render(net.minecraft.client.renderer.entity.state.EnderDragonRenderState state,PoseStack pose,net.minecraft.client.renderer.SubmitNodeCollector collector,net.minecraft.client.renderer.state.level.CameraRenderState camera,CallbackInfo info){
        var access=(yesman.epicfight.client.renderer.compat.StateEntityAccess)state;
        var buffer=new yesman.epicfight.client.renderer.compat.SubmitBufferSource(collector);
        var event=new RenderEnderDragonEvent((EnderDragon)access.epicfight$getEntity(),(EnderDragonRenderer)(Object)this,access.epicfight$getPartialTick(),pose,buffer,state.lightCoords);
        if(EpicFightClientEventHooks.Render.RENDER_ENDER_DRAGON.post(event).isCanceled()) info.cancel();
        buffer.endBatch();
    }''');p.write_text(s,encoding='utf-8')
print('Final client compile APIs and render mixins adapted')
