package yesman.epicfight.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import yesman.epicfight.client.events.engine.RenderEngine;

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRenderDispatcher {
	/** The replacement of NeoForge's RenderLivingEvent.Pre: lets Epic Fight take over a living entity's model submission. */
	@WrapOperation(
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"),
		method = "submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/level/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V"
	)
	private void epicfight$submit(EntityRenderer<?, ?> renderer, EntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, Operation<Void> original) {
		if (!RenderEngine.getInstance().submitEntity(renderer, state, poseStack, collector, camera)) {
			original.call(renderer, state, poseStack, collector, camera);
		}
	}
}
