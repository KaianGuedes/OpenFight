package dev.war.openfight.client.renderer.entity;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import dev.war.openfight.client.renderer.LightningRenderHelper;

public class DroppedNetherStarRenderer extends ItemEntityRenderer {
	public DroppedNetherStarRenderer(Context context) {
		super(context);
	}
	
	@Override
	public void submit(net.minecraft.client.renderer.entity.state.ItemEntityRenderState state, PoseStack poseStack, net.minecraft.client.renderer.SubmitNodeCollector collector, net.minecraft.client.renderer.state.level.CameraRenderState camera) {
		super.submit(state, poseStack, collector, camera);
		
		poseStack.pushPose();
		poseStack.translate(0.0D, state.boundingBoxHeight + Mth.sin((state.ageInTicks) / 10.0F + state.bobOffset) * 0.1F + 0.1F, 0.0D);
		var multiBufferSource = new dev.war.openfight.client.renderer.compat.SubmitBufferSource(collector);
        VertexConsumer vertexBuilder = multiBufferSource.getBuffer(RenderTypes.lightning());
		float progression = (state.ageInTicks) * 0.01F;
		float repeater = ((float)Math.sin(progression * 5.0F) + 1.0F) * 0.5F;
		LightningRenderHelper.renderCirclingLight(vertexBuilder, poseStack, 32, 0, 255, 9, 0.05F, progression, repeater);
		poseStack.popPose();
        multiBufferSource.endBatch();
	}
}