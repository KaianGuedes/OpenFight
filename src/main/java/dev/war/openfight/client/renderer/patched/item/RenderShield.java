package dev.war.openfight.client.renderer.patched.item;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import dev.war.openfight.api.utils.math.MathUtils;
import dev.war.openfight.api.utils.math.OpenMatrix4f;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public class RenderShield extends RenderItemBase {
	public RenderShield(JsonElement jsonElement) {
		super(jsonElement);
	}
	
	@Override
	public void renderItemInHand(ItemStack itemstack, LivingEntityPatch<?> entitypatch, InteractionHand hand, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {


        OpenMatrix4f modelMatrix = this.getCorrectionMatrix(entitypatch, hand, poses);
		poseStack.pushPose();
		MathUtils.mulStack(poseStack, modelMatrix);
		ItemDisplayContext transformType = (hand == InteractionHand.MAIN_HAND) ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND : ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
		
		// Used render method from item renderer directly for compatibility with modded shields
		itemInHandRenderer.renderItem(entitypatch.getOriginal(), itemstack, transformType, hand == InteractionHand.OFF_HAND, poseStack, buffer, packedLight);
		poseStack.popPose();
	}
}
