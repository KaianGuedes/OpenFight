package yesman.epicfight.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.events.engine.RenderEngine;

/** Replacement for NeoForge RenderHandEvent: lets Epic Fight draw the animated first-person model. */
@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class MixinFirstPersonHandsAndItemsRenderer {
	@Inject(method = "submitArmWithItem", at = @At("HEAD"), cancellable = true)
	private void epicfight$renderHand(
		net.minecraft.client.renderer.state.level.PlayerRenderState playerState,
		net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState state,
		float partialTicks, float xRot, InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight,
		PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci
	) {
		if (RenderEngine.getInstance().renderHand(hand, poseStack, submitNodeCollector, lightCoords, partialTicks)) {
			ci.cancel();
		}
	}
}
