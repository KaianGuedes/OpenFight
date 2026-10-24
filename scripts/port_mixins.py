from pathlib import Path
import re,json
root=Path(__file__).resolve().parents[1];j=root/'src/main/java/yesman/epicfight'
def edit(f,fn):
 p=j/f;s=p.read_text(encoding='utf-8');p.write_text(fn(s),encoding='utf-8')
def method(s,needle,new):
 a=s.index(needle);b=s.index('{',a);d=1;e=b+1
 while d:d+=(s[e]=='{')-(s[e]=='}');e+=1
 return s[:a]+new+s[e:]
edit('mixin/client/MixinEntityRenderDispatcher.java',lambda s:s.replace('renderer/state/CameraRenderState', 'renderer/state/level/CameraRenderState'))
edit('mixin/client/MixinGui.java',lambda s:s.replace('import net.minecraft.client.gui.Gui;', 'import net.minecraft.client.gui.Hud;\nimport com.mojang.renderpearl.api.pipeline.RenderPipeline;').replace('Gui.class', 'Hud.class').replace('renderCrosshair(L', 'extractCrosshair(L').replace('guiGraphics.blitSprite(Gui.CROSSHAIR_SPRITE,', 'guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, Identifier.withDefaultNamespace("hud/crosshair"),').replace('blitSprite(Lnet/minecraft/resources/Identifier;IIII)V', 'blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V').replace('GuiGraphicsExtractor guiGraphics, Identifier sprite,', 'GuiGraphicsExtractor guiGraphics, RenderPipeline pipeline, Identifier sprite,').replace('original.call(guiGraphics, sprite,', 'original.call(guiGraphics, pipeline, sprite,').replace('RenderPipelines.GUI_TEXTURED, RenderPipelines.GUI_TEXTURED, EntityUI.BATTLE_ICON, (guiGraphics.guiWidth() - 15) / 2, (float)((float)(0)), (float)((float)(240)),', 'RenderPipelines.GUI_TEXTURED, EntityUI.BATTLE_ICON, (guiGraphics.guiWidth() - 15) / 2, (guiGraphics.guiHeight() - 15) / 2, 0F, 240F,').replace('if (setupBlend) {\n\t\t\tRenderSystem.enableBlend();\n\t\t\tRenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);\n\t\t}', '').replace('if (setupBlend) RenderSystem.defaultBlendFunc();',''))
(j/'mixin/client/MixinCamera.java').write_text('''package yesman.epicfight.mixin.client;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;
@Mixin(Camera.class)
public abstract class MixinCamera {
 @Shadow private boolean detached;
 @Unique private boolean epicfight$postTransform;
 @Inject(method="alignWithEntity(F)V",at=@At("HEAD"),cancellable=true)
 private void epicfight$align(float partialTick,CallbackInfo info) {
  this.detached=!Minecraft.getInstance().options.getCameraType().isFirstPerson();
  var event=EpicFightCameraAPI.getInstance().setupCamera((Camera)(Object)this,partialTick);
  this.epicfight$postTransform=!event.isCanceled()&&!event.isVanillaCameraSetupCanceled();
  if(!event.isCanceled()&&event.isVanillaCameraSetupCanceled()) info.cancel();
 }
 @Inject(method="alignWithEntity(F)V",at=@At("TAIL"))
 private void epicfight$post(float partialTick,CallbackInfo info) {
  if(this.epicfight$postTransform) EpicFightCameraAPI.getInstance().fireCameraBuildPost((Camera)(Object)this,partialTick);
 }
}
''',encoding='utf-8')
edit('mixin/client/MixinMinecraft.java',lambda s:method(s,'@WrapOperation(\n        at = @At(\n            value = "INVOKE",\n            target = "Lnet/minecraft/client/renderer/GameRenderer;pick(F)V"', '''@Inject(method="pick(F)V",at=@At("HEAD"),cancellable=true)
    private void epicfight$pick(float partialTick,CallbackInfo info) {
        if(EpicFightCameraAPI.getInstance().isTPSMode()) {
            this.pickInTPSPerspectiveMode();
            ((Minecraft)(Object)this).crosshairPickEntity=EpicFightCameraAPI.getInstance().getFocusingEntity();
            info.cancel();
        }
    }''').replace('LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V','LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/component/SwingAnimation;Z)Z'))
# Picking is now one hook in Minecraft for both frame and tick updates.
(j/'mixin/client/MixinGameRenderer.java').write_text('''package yesman.epicfight.mixin.client;
/** The original frame-pick override was merged into MixinMinecraft.pick for 26.3. */
public final class MixinGameRenderer { private MixinGameRenderer() {} }
''',encoding='utf-8')
(j/'mixin/client/MixinLevelRenderer.java').write_text('''package yesman.epicfight.mixin.client;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;
import yesman.epicfight.config.ClientConfig;
@Mixin(EntityRenderer.class)
public abstract class MixinLevelRenderer {
 @Inject(method="extractRenderState(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;F)V",at=@At("TAIL"))
 private void epicfight$outline(Entity entity,EntityRenderState state,float partialTick,CallbackInfo info) {
  if(EpicFightCameraAPI.getInstance().shouldHighlightTarget(entity)) state.outlineColor=ClientConfig.packedTargetOutlineColor|0xFF000000;
 }
}
''',encoding='utf-8')
edit('mixin/client/MixinLightTexture.java',lambda s:s.replace('import net.minecraft.client.renderer.LightTexture;', 'import net.minecraft.client.renderer.LightmapRenderStateExtractor;').replace('LightTexture.class', 'LightmapRenderStateExtractor.class').replace('updateLightTexture(F)V', 'extract(Lnet/minecraft/client/renderer/state/LightmapRenderState;F)V'))
# Native billboards share SINGLE_QUADS; custom geometry order is now handled by ParticleBridge.
(j/'mixin/client/MixinParticleEngine.java').write_text('''package yesman.epicfight.mixin.client;
/** Obsolete queue-order hook: 26.3 groups billboards and ParticleBridge submits custom geometry. */
public final class MixinParticleEngine { private MixinParticleEngine() {} }
''',encoding='utf-8')
(j/'mixin/client/MixinAgeableListModel.java').write_text('''package yesman.epicfight.mixin.client;
/** The deleted AgeableListModel head/body invoker had no remaining consumers. */
public interface MixinAgeableListModel {}
''',encoding='utf-8')
edit('mixin/client/MixinKeyboardHandler.java',lambda s:s.replace('handleDebugKeys(I)Z', 'handleDebugKeys(Lnet/minecraft/client/input/KeyEvent;)Z').replace('epicfight$handleDebugKeys(int key,', 'epicfight$handleDebugKeys(net.minecraft.client.input.KeyEvent event,').replace('if (key == InputConstants.KEY_Y)', 'if (event.key() == InputConstants.KEY_Y)').replace('public void debugFeedbackTranslated(String p_90914_, Object... p_90915_) {}', 'private void debugFeedbackTranslated(String message) {}'))
edit('mixin/client/MixinLocalPlayer.java',lambda s:s.replace('method = "tick()V"', 'method = "sendChanges()V"').replace('method = "drop"', 'method = "canDropItems()Z"').replace('epicfight$onDrop(boolean fullStack, CallbackInfoReturnable<Boolean> cir)', 'epicfight$onDrop(CallbackInfoReturnable<Boolean> cir)').replace('            cir.cancel();', '            cir.setReturnValue(false);'))
edit('mixin/client/MixinInventory.java',lambda s:s.replace('method = "swapPaint"', 'method = "setSelectedSlot(I)V"').replace('onCycleHotbarSlot(double direction,', 'onCycleHotbarSlot(int slot,'))
edit('mixin/common/MixinPlayer.java',lambda s:s.replace('actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V', 'actuallyHurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)V').replace('method = "serverAiStep()V"', 'method = "aiStep()V"').replace('Player player, Operation<Void> originalOperation', 'Player player, Operation<Float> originalOperation').replace('self.recordDamage(damagesource, damage);', 'operation.call(self, damagesource, damage);'))
edit('mixin/common/MixinTridentAttack.java',lambda s:s.replace('import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;', 'import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;').replace('Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z', 'Lnet/minecraft/world/entity/projectile/Projectile;spawnProjectileFromRotation(Lnet/minecraft/world/entity/projectile/Projectile$ProjectileFactory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;FFF)Lnet/minecraft/world/entity/projectile/Projectile;').replace('CallbackInfo ci', 'CallbackInfoReturnable<Boolean> ci'))
edit('mixin/client/MixinMeshData.java',lambda s:s.replace('import com.mojang.renderpearl.api.vertex.VertexFormatElement;', 'import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;\nimport com.mojang.renderpearl.api.pipeline.IndexType;\nimport com.mojang.blaze3d.vertex.CompactVectorArray;\nimport org.spongepowered.asm.mixin.Unique;').replace('this.drawState.mode() == VertexFormat.Mode.TRIANGLES', 'this.drawState.primitiveTopology() == PrimitiveTopology.TRIANGLES').replace('Vector3f[] centroids =', 'CompactVectorArray centroids =').replace('this.drawState.mode().primitiveStride', '3').replace('aint.length * 6', 'aint.length * 3').replace('VertexFormat.IndexType', 'IndexType').replace('private static Vector3f[] unpackTriangleCentroids', 'private static CompactVectorArray unpackTriangleCentroids').replace('int i = format.getOffset(VertexFormatElement.POSITION);', 'int i = format.getElement("Position") == null ? -1 : format.getElement("Position").offset() / 4;').replace('Vector3f[] avector3f = new Vector3f[l];', 'CompactVectorArray avector3f = new CompactVectorArray(l);').replace('avector3f[i1] = triangleCentroid(x1, y1, z1, x2, y2, z2, x3, y3, z3);', 'Vector3f center=triangleCentroid(x1, y1, z1, x2, y2, z2, x3, y3, z3);\n                avector3f.set(i1,center.x,center.y,center.z);').replace('private static ', '@Unique\n    private static '))
edit('mixin/client/MixinThrownTridentRenderer.java',lambda s:re.sub(r'extends EntityRenderer<ThrownTrident>', 'extends EntityRenderer<ThrownTrident, net.minecraft.client.renderer.entity.state.ThrownTridentRenderState>',s))
config=root/'src/main/resources/epicfight.mixins.json';data=json.loads(config.read_text(encoding='utf-8'));data['client']=[v for v in data['client'] if v not in ['client.MixinGameRenderer','client.MixinParticleEngine','client.MixinAgeableListModel']];config.write_text(json.dumps(data,indent=2)+'\n',encoding='utf-8')
print('Mixin target migration applied')
