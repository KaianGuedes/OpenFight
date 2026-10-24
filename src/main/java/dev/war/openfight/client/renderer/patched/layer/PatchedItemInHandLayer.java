package dev.war.openfight.client.renderer.patched.layer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import dev.war.openfight.api.utils.math.OpenMatrix4f;
import dev.war.openfight.client.events.engine.RenderEngine;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public class PatchedItemInHandLayer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<?>> extends PatchedLayer<E, T, M, RenderLayer<?, ?>> {
	@Override
	protected void renderLayer(T entitypatch, E entityliving, RenderLayer<?, ?> vanillaLayer, PoseStack postStack, MultiBufferSource buffer, int packedLight, OpenMatrix4f[] poses, float bob, float yRot, float xRot, float partialTicks) {
		ItemStack mainHandStack = entitypatch.getOriginal().getMainHandItem();

		if (mainHandStack.getItem() != Items.AIR && entitypatch.isMainhandItemValid()) {
			RenderEngine.getInstance().getItemRenderer(mainHandStack).renderItemInHand(mainHandStack, entitypatch, InteractionHand.MAIN_HAND, poses, buffer, postStack, packedLight, partialTicks);
		}
		
		ItemStack offHandStack = entitypatch.getOriginal().getOffhandItem();
		
		if (entitypatch.isOffhandItemValid()) {
			RenderEngine.getInstance().getItemRenderer(offHandStack).renderItemInHand(offHandStack, entitypatch, InteractionHand.OFF_HAND, poses, buffer, postStack, packedLight, partialTicks);
		}
	}
}