package dev.war.openfight.mixin.client;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import dev.war.openfight.api.client.camera.EpicFightCameraAPI;
import dev.war.openfight.config.ClientConfig;
@Mixin(EntityRenderer.class)
public abstract class MixinLevelRenderer {
 @Inject(method="extractRenderState(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;F)V",at=@At("TAIL"))
 private void epicfight$outline(Entity entity,EntityRenderState state,float partialTick,CallbackInfo info) {
  if(EpicFightCameraAPI.getInstance().shouldHighlightTarget(entity)) state.outlineColor=ClientConfig.packedTargetOutlineColor|0xFF000000;
 }
}
