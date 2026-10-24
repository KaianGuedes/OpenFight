package dev.war.openfight.client.renderer.patched.entity;

import dev.war.openfight.client.renderer.compat.EpicRenderContext;
import net.minecraft.client.renderer.entity.state.EnderDragonRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.client.model.Meshes;
import dev.war.openfight.api.model.Armature;
import dev.war.openfight.api.utils.math.MathUtils;
import dev.war.openfight.api.utils.math.OpenMatrix4f;
import dev.war.openfight.api.utils.math.Vec3f;
import dev.war.openfight.client.mesh.DragonMesh;
import dev.war.openfight.client.renderer.LightningRenderHelper;
import dev.war.openfight.world.capabilities.entitypatch.boss.enderdragon.DragonCrystalLinkPhase;
import dev.war.openfight.world.capabilities.entitypatch.boss.enderdragon.EnderDragonPatch;
import dev.war.openfight.world.capabilities.entitypatch.boss.enderdragon.PatchedPhases;

public class PEnderDragonRenderer extends PatchedEntityRenderer<EnderDragon, EnderDragonPatch, EnderDragonRenderer, DragonMesh> {
	private static final Identifier DRAGON_LOCATION = Identifier.withDefaultNamespace("textures/entity/enderdragon/dragon.png");
	private static final Identifier DRAGON_EXPLODING_LOCATION = Identifier.withDefaultNamespace("textures/entity/enderdragon/dragon_exploding.png");
	
	@Override
	public void render(EnderDragon entityIn, EnderDragonPatch entitypatch, EnderDragonRenderer renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		EnderDragonRenderState state = (EnderDragonRenderState)EpicRenderContext.state();
		DragonMesh mesh = this.getMeshProvider(entitypatch).get();
		Armature armature = entitypatch.getArmature();
		poseStack.pushPose();
        this.mulPoseStack(poseStack, armature, entityIn, entitypatch, partialTicks);
		this.setArmaturePose(entitypatch, armature, partialTicks);
		armature.getPoseMatrices()[0] = OpenMatrix4f.rotate(-90.0F, Vec3f.X_AXIS, armature.getPoseMatrices()[0], null);
		
		if (entityIn.dragonDeathTime > 0) {
			poseStack.translate(entityIn.getRandom().nextGaussian() * 0.08D, 0.0D, entityIn.getRandom().nextGaussian() * 0.08D);
			float deathTimeProgression = ((float) entityIn.dragonDeathTime + partialTicks) / 200.0F;
			mesh.draw(poseStack, buffer, RenderTypes.entityCutoutDissolve(DRAGON_LOCATION, DRAGON_EXPLODING_LOCATION), packedLight, 1.0F, 1.0F, 1.0F, 1.0F - deathTimeProgression, OverlayTexture.NO_OVERLAY, entitypatch.getArmature(), armature.getPoseMatrices());
		} else {
			mesh.draw(poseStack, buffer, RenderTypes.entityCutout(DRAGON_LOCATION), packedLight, 1.0F, 1.0F, 1.0F, 1.0F, this.getOverlayCoord(entityIn, entitypatch, partialTicks), entitypatch.getArmature(), armature.getPoseMatrices());
		}
		
		if (Minecraft.getInstance().debugEntries.isCurrentlyEnabled(net.minecraft.client.gui.components.debug.DebugScreenEntries.ENTITY_HITBOXES)) {
			entitypatch.getClientAnimator().renderDebuggingInfoForAllLayers(poseStack, buffer, partialTicks);
		}
		
		poseStack.popPose();
		buffer.endBatch();
		
		SubmitNodeCollector collector = EpicRenderContext.collector();
		
		if (state != null && collector != null && state.beamOffset != null) {
			EnderDragonRenderer.submitCrystalBeams((float)state.beamOffset.x, (float)state.beamOffset.y, (float)state.beamOffset.z, state.ageInTicks, poseStack, collector, state.lightCoords);
		}
		
		if (entityIn.dragonDeathTime > 0) {
			float deathTimeProgression = ((float) entityIn.dragonDeathTime + partialTicks) / 200.0F;
			VertexConsumer lightningBuffer = buffer.getBuffer(RenderTypes.dragonRays());
			int density = (int)((deathTimeProgression + deathTimeProgression * deathTimeProgression) / 2.0F * 60.0F);
			float f7 = Math.min(deathTimeProgression > 0.8F ? (deathTimeProgression - 0.8F) / 0.2F : 0.0F, 1.0F);
			
			poseStack.pushPose();
			LightningRenderHelper.renderCirclingLight(lightningBuffer, poseStack, 255, 0, 255, density, 1.0F, deathTimeProgression, f7);
			poseStack.popPose();
			buffer.endBatch();
		}
	}
	
	@Override
	public void mulPoseStack(PoseStack matStack, Armature armature, EnderDragon entityIn, EnderDragonPatch entitypatch, float partialTicks) {
		OpenMatrix4f modelMatrix;
		
		if (!entitypatch.isGroundPhase() || entitypatch.getOriginal().dragonDeathTime > 0) {
			float f = (float)entityIn.flightHistory.get(7, partialTicks).yRot();
		    float f1 = (float)(entityIn.flightHistory.get(5, partialTicks).y() - entityIn.flightHistory.get(10, partialTicks).y());
		    float f2 = entitypatch.getOriginal().dragonDeathTime > 0 ? 0.0F : MathUtils.rotWrap((entityIn.flightHistory.get(5, partialTicks).yRot() - entityIn.flightHistory.get(10, partialTicks).yRot()));
			modelMatrix = MathUtils.getModelMatrixIntegral(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, f1, f1, f, f, partialTicks, 1.0F, 1.0F, 1.0F).rotateDeg(-f2 * 1.5F, Vec3f.Z_AXIS);
		} else {
			modelMatrix = entitypatch.getModelMatrix(partialTicks).scale(-1.0F, 1.0F, -1.0F);
		}
		
		MathUtils.mulStack(matStack, modelMatrix);
	}
	
	protected int getOverlayCoord(EnderDragon entity, EnderDragonPatch entitypatch, float partialTicks) {
		DragonPhaseInstance currentPhase = entity.getPhaseManager().getCurrentPhase();
		float chargingTick = DragonCrystalLinkPhase.CHARGING_TICK;
		float progression = currentPhase.getPhase() == PatchedPhases.CRYSTAL_LINK ? (chargingTick - (float)((DragonCrystalLinkPhase)currentPhase).getChargingCount()) / chargingTick : 0.0F;
		
		return OverlayTexture.pack(OverlayTexture.u(progression), OverlayTexture.v(entity.hurtTime > 5 || entity.deathTime > 0));
	}
	
	@Override
	public AssetAccessor<DragonMesh> getDefaultMesh() {
		return Meshes.DRAGON;
	}
}