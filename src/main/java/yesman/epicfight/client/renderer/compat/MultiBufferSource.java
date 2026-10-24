package yesman.epicfight.client.renderer.compat;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.rendertype.RenderType;

/**
 * Stand-in for the removed vanilla {@code MultiBufferSource}. Epic Fight's mesh code writes vertices straight into a
 * {@link VertexConsumer}; under 26.3's deferred (submit-node) renderer this is bridged by {@link SubmitBufferSource},
 * which records vertices and submits them as custom geometry.
 */
public interface MultiBufferSource {
	VertexConsumer getBuffer(RenderType renderType);

	/** Submits everything recorded so far. A no-op for sources that already render immediately. */
	default void endBatch() {
	}
}
