package dev.war.openfight.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import dev.war.openfight.client.events.engine.RenderEngine;
import dev.war.openfight.client.gui.screen.overlay.OverlayManager;

@Mixin(value = LightmapRenderStateExtractor.class)
public abstract class MixinLightTexture {
	@Inject(at = @At(value = "HEAD"), method = "extract(Lnet/minecraft/client/renderer/state/LightmapRenderState;F)V", cancellable = true)
	private void epicfight$head_updateLightTexture(CallbackInfo info) {
		OverlayManager overlayManager = RenderEngine.getInstance().getOverlayManager();
		
		if (overlayManager.isGammaChanged()) {
			Minecraft minecraft = Minecraft.getInstance();
			minecraft.options.gamma().set(overlayManager.getModifiedGamma(minecraft.options.gamma().get()));
		}
	}
	
	@Inject(at = @At(value = "TAIL"), method = "extract(Lnet/minecraft/client/renderer/state/LightmapRenderState;F)V", cancellable = true)
	private void epicfight$tail_updateLightTexture(CallbackInfo info) {
		OverlayManager overlayManager = RenderEngine.getInstance().getOverlayManager();
		
		if (overlayManager.isGammaChanged()) {
			Minecraft minecraft = Minecraft.getInstance();
			minecraft.options.gamma().set(overlayManager.getOriginalGamma());
		}
	}
}