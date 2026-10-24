package yesman.epicfight.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import yesman.epicfight.client.renderer.compat.StateEntityAccess;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRendererCapture<T extends Entity, S extends EntityRenderState> {
	@Inject(at = @At("RETURN"), method = "createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;")
	private void epicfight$captureEntity(T entity, float partialTicks, CallbackInfoReturnable<S> info) {
		((StateEntityAccess)info.getReturnValue()).epicfight$capture(entity, partialTicks);
	}
}
