package dev.war.openfight.client.renderer.patched.layer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import dev.war.openfight.api.utils.math.OpenMatrix4f;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public class EmptyLayer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<?>> extends UniqueLayer<E, T, M> {
	// Do nothing
	@Override
	protected void renderLayer(T entitypatch, E entityliving, PoseStack poseStack, MultiBufferSource buffer, int packedLight, OpenMatrix4f[] poses, float bob, float yRot, float xRot, float partialTicks) {
	}
}