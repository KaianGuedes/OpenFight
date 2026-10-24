package dev.war.openfight.client.renderer.compat;

import java.util.LinkedHashMap;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

/**
 * Bridges immediate-mode style vertex writing onto the deferred 26.3 renderer: vertices are recorded per render
 * type and submitted as custom geometry (with an identity pose, because Epic Fight bakes its own matrices into the
 * vertex positions) when {@link #endBatch()} is called.
 */
public class SubmitBufferSource implements MultiBufferSource {
	private final SubmitNodeCollector collector;
	private final Map<RenderType, RecordingVertexConsumer> buffers = new LinkedHashMap<> ();
	private final PoseStack baseStack;
	
	public SubmitBufferSource(SubmitNodeCollector collector) {
		this(collector, new PoseStack());
	}
	
	/** @param baseStack the pose the recorded vertices are relative to (usually an identity stack or the entity-space stack) */
	public SubmitBufferSource(SubmitNodeCollector collector, PoseStack baseStack) {
		this.collector = collector;
		this.baseStack = baseStack;
	}
	
	public SubmitNodeCollector collector() { return this.collector; }

	@Override
	public VertexConsumer getBuffer(RenderType renderType) {
		return this.buffers.computeIfAbsent(renderType, rt -> new RecordingVertexConsumer());
	}
	
	@Override
	public void endBatch() {
		this.buffers.forEach((renderType, recorded) -> {
			if (recorded.vertexCount() > 0) {
				this.collector.submitCustomGeometry(this.baseStack, renderType, (pose, buffer) -> recorded.replay(buffer, renderType.format()));
			}
		});
		
		this.buffers.clear();
	}
}
