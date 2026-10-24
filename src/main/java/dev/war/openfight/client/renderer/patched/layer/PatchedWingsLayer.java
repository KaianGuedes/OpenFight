package dev.war.openfight.client.renderer.patched.layer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import dev.war.openfight.api.utils.math.MathUtils;
import dev.war.openfight.api.utils.math.OpenMatrix4f;
import dev.war.openfight.api.utils.math.Vec3f;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public class PatchedWingsLayer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<?>> extends PatchedLayer<E, T, M, WingsLayer<?, ?>> {
	@Override
	protected void renderLayer(T entitypatch, E livingentity, WingsLayer<?, ?> vanillaLayer, PoseStack poseStack, MultiBufferSource buffer, int packedLight, OpenMatrix4f[] poses, float bob, float yRot, float xRot, float partialTicks) {
		net.minecraft.world.item.equipment.Equippable equippable = livingentity.getItemBySlot(EquipmentSlot.CHEST).get(net.minecraft.core.component.DataComponents.EQUIPPABLE);

		if (equippable != null && equippable.assetId().isPresent()) {
			OpenMatrix4f modelMatrix = new OpenMatrix4f();
			modelMatrix.scale(new Vec3f(-0.9F, -0.9F, 0.9F)).translate(new Vec3f(0.0F, -0.5F, -0.1F)).mulFront(poses[8]);
			poseStack.pushPose();
			MathUtils.mulStack(poseStack, modelMatrix);
			submitVanillaLayer(vanillaLayer, poseStack, packedLight, yRot, xRot);
			poseStack.popPose();
		}
	}
}