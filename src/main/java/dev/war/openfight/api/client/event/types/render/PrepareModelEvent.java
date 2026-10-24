package dev.war.openfight.api.client.event.types.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import dev.war.openfight.api.client.model.SkinnedMesh;
import dev.war.openfight.api.event.CancelableEvent;
import dev.war.openfight.api.event.Event;
import dev.war.openfight.client.renderer.patched.entity.PatchedEntityRenderer;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public class PrepareModelEvent extends Event implements CancelableEvent {
	private final SkinnedMesh mesh;
	private final LivingEntityPatch<?> entitypatch;
	private final MultiBufferSource buffer;
	private final PoseStack poseStack;
	private final int packedLight;
	private final float partialTicks;
	private final PatchedEntityRenderer<?, ?, ?, ?> renderer;
	
	public PrepareModelEvent(PatchedEntityRenderer<?, ?, ?, ?> renderer, SkinnedMesh mesh, LivingEntityPatch<?> entitypatch, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		this.renderer = renderer;
		this.mesh = mesh;
		this.entitypatch = entitypatch;
		this.buffer = buffer;
		this.poseStack = poseStack;
		this.packedLight = packedLight;
		this.partialTicks = partialTicks;
	}

	public SkinnedMesh getMesh() {
		return this.mesh;
	}
	
	public LivingEntityPatch<?> getEntityPatch() {
		return this.entitypatch;
	}

	public MultiBufferSource getBuffer() {
		return this.buffer;
	}

	public PoseStack getPoseStack() {
		return this.poseStack;
	}

	public int getPackedLight() {
		return this.packedLight;
	}

	public float getPartialTicks() {
		return this.partialTicks;
	}

	public PatchedEntityRenderer<?, ?, ?, ?> getRenderer() {
		return this.renderer;
	}
}