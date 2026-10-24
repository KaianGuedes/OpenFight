package dev.war.openfight.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.war.openfight.client.events.engine.RenderEngine;

/**
 * Replacement for NeoForge RenderHandEvent: lets Epic Fight draw the animated first-person model.
 * Hooks the outer submitHandsWithItems so it runs before (and suppresses) other mods' per-arm
 * first-person overrides such as Hold My Items while battle mode is active.
 */
@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class MixinFirstPersonHandsAndItemsRenderer {
	@Inject(method = "submitHandsWithItems", at = @At("HEAD"), cancellable = true)
	private void epicfight$renderHands(
		float partialTicks, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
		PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state, CallbackInfo ci
	) {
		if (playerState.avatarRenderState == null || state.handRenderSelection == null) {
			return;
		}

		poseStack.pushPose();
		poseStack.rotateDegrees(Axis.XP, (state.viewXRot - state.xBob) * 0.1F);
		poseStack.rotateDegrees(Axis.YP, (state.viewYRot - state.yBob) * 0.1F);
		boolean handled = RenderEngine.getInstance().renderHand(InteractionHand.MAIN_HAND, poseStack, submitNodeCollector, playerState.avatarRenderState.lightCoords, partialTicks);
		poseStack.popPose();

		if (handled) {
			ci.cancel();
		}
	}
}
