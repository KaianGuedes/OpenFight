package dev.war.openfight.mixin.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.war.openfight.client.events.engine.RenderEngine;

@Mixin(BossHealthOverlay.class)
public abstract class MixinBossHealthOverlay {
	@Inject(method = "extractBar(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IILnet/minecraft/world/BossEvent;)V", at = @At("TAIL"))
	private void epicfight$stunShieldBar(GuiGraphicsExtractor graphics, int x, int y, BossEvent event, CallbackInfo ci) {
		if (event instanceof LerpingBossEvent lerpingEvent) {
			RenderEngine.getInstance().renderBossEventProgress(graphics, lerpingEvent, x, y);
		}
	}
}
