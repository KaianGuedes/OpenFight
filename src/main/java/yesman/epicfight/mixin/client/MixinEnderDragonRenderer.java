package yesman.epicfight.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import yesman.epicfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.api.client.event.types.render.RenderEnderDragonEvent;

@Mixin(value = EnderDragonRenderer.class)
public abstract class MixinEnderDragonRenderer {
	@Inject(at=@At("HEAD"),method="submit(Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",cancellable=true)
    private void epicfight$render(net.minecraft.client.renderer.entity.state.EnderDragonRenderState state,PoseStack pose,net.minecraft.client.renderer.SubmitNodeCollector collector,net.minecraft.client.renderer.state.level.CameraRenderState camera,CallbackInfo info){
        var access=(yesman.epicfight.client.renderer.compat.StateEntityAccess)state;
        var buffer=new yesman.epicfight.client.renderer.compat.SubmitBufferSource(collector);
        var event=new RenderEnderDragonEvent((EnderDragon)access.epicfight$getEntity(),(EnderDragonRenderer)(Object)this,access.epicfight$getPartialTick(),pose,buffer,state.lightCoords);
        if(EpicFightClientEventHooks.Render.RENDER_ENDER_DRAGON.post(event).isCanceled()) info.cancel();
        buffer.endBatch();
    }
}