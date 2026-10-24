package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import yesman.epicfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.api.client.event.types.render.PrepareModelEvent;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class PCustomEntityRenderer extends PatchedEntityRenderer<LivingEntity, LivingEntityPatch<LivingEntity>, EntityRenderer<LivingEntity, ?>, SkinnedMesh> {
	private final AssetAccessor<SkinnedMesh> mesh;
	
	public PCustomEntityRenderer(AssetAccessor<SkinnedMesh> mesh, EntityRendererProvider.Context context) {
		this.mesh = mesh;
	}
	
	@Override
	public void render(LivingEntity entity, LivingEntityPatch<LivingEntity> entitypatch, EntityRenderer<LivingEntity, ?> renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		super.render(entity, entitypatch, renderer, buffer, poseStack, packedLight, partialTicks);
		
		Minecraft mc = Minecraft.getInstance();
		boolean isGlowing = mc.shouldEntityAppearGlowing(entity);
		Identifier textureLocation = ((net.minecraft.client.renderer.entity.LivingEntityRenderer<LivingEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState, ?>)renderer).getTextureLocation((net.minecraft.client.renderer.entity.state.LivingEntityRenderState)yesman.epicfight.client.renderer.compat.EpicRenderContext.state());
		RenderType renderType = isGlowing ? RenderTypes.outline(textureLocation) : RenderTypes.entityCutout(textureLocation);
		Armature armature = entitypatch.getArmature();
		poseStack.pushPose();
		this.mulPoseStack(poseStack, armature, entity, entitypatch, partialTicks);
		this.setArmaturePose(entitypatch, armature, partialTicks);
		
		if (renderType != null) {
            SkinnedMesh mesh = this.mesh.get();
            PrepareModelEvent prepareModelEvent = new PrepareModelEvent(this, mesh, entitypatch, buffer, poseStack, packedLight, partialTicks);

            if (!EpicFightClientEventHooks.Render.PREPARE_MODEL_TO_RENDER.post(prepareModelEvent).isCanceled()) {
                mesh.draw(poseStack, buffer, renderType, packedLight, 1.0F, 1.0F, 1.0F, entity.isInvisibleTo(mc.player) ? 0.15F : 1.0F, this.getOverlayCoord(entity, entitypatch, partialTicks), armature, armature.getPoseMatrices());
            }
        }
		
		if (Minecraft.getInstance().debugEntries.isCurrentlyEnabled(net.minecraft.client.gui.components.debug.DebugScreenEntries.ENTITY_HITBOXES)) {
			entitypatch.getClientAnimator().renderDebuggingInfoForAllLayers(poseStack, buffer, partialTicks);
		}
		
		poseStack.popPose();
		buffer.endBatch();
	}
	
	protected int getOverlayCoord(LivingEntity entity, LivingEntityPatch<LivingEntity> entitypatch, float partialTicks) {
		return OverlayTexture.pack(0, OverlayTexture.v(entity.hurtTime > 5));
	}
	
	@Override
	public AssetAccessor<SkinnedMesh> getDefaultMesh() {
		return this.mesh;
	}
}
