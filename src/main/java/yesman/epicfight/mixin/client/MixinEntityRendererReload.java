package yesman.epicfight.mixin.client;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.server.packs.resources.ResourceManager;
import yesman.epicfight.client.events.engine.RenderEngine;
@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRendererReload {
 @Inject(method="onResourceManagerReload(Lnet/minecraft/server/packs/resources/ResourceManager;)V",at=@At("TAIL"))
 private void epicfight$reload(ResourceManager resources,CallbackInfo info,@Local EntityRendererProvider.Context context) {
  yesman.epicfight.client.platform.neoforge.event.ClientModBusEvent.reloadLayers();
  RenderEngine.getInstance().onEntityRenderersReloaded(context);
 }
}
