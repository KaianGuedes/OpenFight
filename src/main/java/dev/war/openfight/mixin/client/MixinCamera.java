package dev.war.openfight.mixin.client;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.war.openfight.api.client.camera.EpicFightCameraAPI;
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
