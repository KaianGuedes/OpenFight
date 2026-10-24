package dev.war.openfight.client.renderer.patched.layer;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import dev.war.openfight.platform.fabric.compat.SkinCompat;
import java.util.function.Function;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import dev.war.openfight.api.client.model.Mesh;
import dev.war.openfight.api.client.physics.cloth.ClothSimulator;
import dev.war.openfight.api.client.physics.cloth.ClothSimulator.ClothObject;
import dev.war.openfight.api.utils.math.MathUtils;
import dev.war.openfight.api.utils.math.OpenMatrix4f;
import dev.war.openfight.api.utils.math.QuaternionUtils;
import dev.war.openfight.api.utils.math.Vec3f;
import dev.war.openfight.client.events.engine.RenderEngine;
import dev.war.openfight.client.renderer.EpicFightRenderTypes;
import dev.war.openfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;
import dev.war.openfight.gameasset.Armatures;

public class PatchedCapeLayer extends PatchedLayer<AbstractClientPlayer, AbstractClientPlayerPatch<AbstractClientPlayer>, PlayerModel, CapeLayer> {
	@SuppressWarnings("unchecked")
	@Override
	protected void renderLayer(AbstractClientPlayerPatch<AbstractClientPlayer> entitypatch, AbstractClientPlayer entityliving, CapeLayer vanillaLayer, PoseStack poseStack, MultiBufferSource buffer, int packedLight, OpenMatrix4f[] poses, float bob, float yRot, float xRot, float partialTick) {
		if (SkinCompat.capeTexture(entityliving.getSkin()) != null && !entityliving.isInvisible() && entityliving.isModelPartShown(PlayerModelPart.CAPE)) {
			ItemStack itemstack = entityliving.getItemBySlot(EquipmentSlot.CHEST);
			if (itemstack.getItem() != Items.ELYTRA) {
				OpenMatrix4f modelMatrix = new OpenMatrix4f();
				modelMatrix.scale(new Vec3f(-1.0F, -1.0F, 1.0F)).mulFront(poses[8]);
				poseStack.pushPose();
				MathUtils.mulStack(poseStack, modelMatrix);
				poseStack.translate(0.0D, -0.4D, -0.025D);
				submitVanillaLayer(vanillaLayer, poseStack, packedLight, yRot, xRot);
				poseStack.popPose();
			}
		}
	}
	
	public static void renderSimulatingCape(
		PoseStack poseStack,
		MultiBufferSource buffers,
		RenderType rendertype,
		Mesh.DrawingFunction drawFunction,
		ClothObject clothObj,
		double x,
		double y,
		double z,
		float r,
		float g,
		float b,
		float a,
		AbstractClientPlayerPatch<?> entitypatch,
		OpenMatrix4f[] poses,
		int packedLight,
		Matrix4f renderLocalMatrix,
		float yBodyRot
	) {
		poseStack.pushPose();
		float scaler = entitypatch.getScale();
		poseStack.scale(scaler, scaler, scaler);
		
		if (entitypatch.getOriginal().hasItemInSlot(EquipmentSlot.CHEST)) {
			OpenMatrix4f poseMat = poses[Armatures.BIPED.get().chest.getId()];
			poseStack.translate(poseMat.m30, poseMat.m31, poseMat.m32);
			poseStack.scale(1.17F, 1.17F, 1.17F);
			poseStack.translate(-poseMat.m30, -poseMat.m31, -poseMat.m32);
		}
		
		clothObj.scaleFromPose(poseStack, poses);
		poseStack.pushPose();
		renderLocalMatrix = renderLocalMatrix.invert(new Matrix4f());
		
		poseStack.last().pose().mul(renderLocalMatrix);
		poseStack.translate(-renderLocalMatrix.m30() - x, -renderLocalMatrix.m31() - y, -renderLocalMatrix.m32() - z);
		poseStack.last().normal().rotate(QuaternionUtils.YP.rotationDegrees(yBodyRot));
		
		clothObj.draw(poseStack, buffers, rendertype, drawFunction, packedLight, r, g, b, a, OverlayTexture.NO_OVERLAY, entitypatch.getArmature(), poses);
		
		poseStack.popPose();
		poseStack.popPose();
	}
}
