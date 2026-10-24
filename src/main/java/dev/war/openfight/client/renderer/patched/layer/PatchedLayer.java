package dev.war.openfight.client.renderer.patched.layer;

import dev.war.openfight.client.renderer.compat.EpicRenderContext;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import dev.war.openfight.api.utils.math.OpenMatrix4f;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public abstract class PatchedLayer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<?>, R extends RenderLayer<?, ?>> {
	public void renderLayer(E entityliving, T entitypatch, RenderLayer<?, ?> vanillaLayer, PoseStack poseStack, MultiBufferSource buffer, int packedLight, OpenMatrix4f[] poses, float bob, float yRot, float xRot, float partialTicks) {
		this.renderLayer(entitypatch, entityliving, this.castLayer(vanillaLayer), poseStack, buffer, packedLight, poses, bob, yRot, xRot, partialTicks);
	}
	
	protected abstract void renderLayer(T entitypatch, E entityliving, @Nullable R vanillaLayer, PoseStack poseStack, MultiBufferSource buffer, int packedLight, OpenMatrix4f[] poses, float bob, float yRot, float xRot, float partialTicks);
	
	@SuppressWarnings("unchecked")
	protected R castLayer(RenderLayer<?, ?> layer) {
		return (R)layer;
	}

	/** Lets a vanilla layer submit its own geometry at the current pose (26.3 layers work from the render state). */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected static void submitVanillaLayer(@Nullable RenderLayer vanillaLayer, PoseStack poseStack, int packedLight, float yRot, float xRot) {
		EntityRenderState state = EpicRenderContext.state();
		SubmitNodeCollector collector = EpicRenderContext.collector();

		if (vanillaLayer != null && state != null && collector != null) {
			vanillaLayer.submit(poseStack, collector, packedLight, state, yRot, xRot);
		}
	}
}