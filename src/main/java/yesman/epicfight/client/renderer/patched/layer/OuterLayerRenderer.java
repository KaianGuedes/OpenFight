package yesman.epicfight.client.renderer.patched.layer;

import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.monster.zombie.DrownedModel;
import yesman.epicfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.zombie.Drowned;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.world.capabilities.entitypatch.mob.DrownedPatch;

public class OuterLayerRenderer extends ModelRenderLayer<Drowned, DrownedPatch, DrownedModel, DrownedOuterLayer, HumanoidMesh> {
	public static final Identifier DROWNED_OUTER_LAYER = Identifier.withDefaultNamespace("textures/entity/zombie/drowned_outer_layer.png");
	
	public OuterLayerRenderer() {
		super(Meshes.BIPED_OUTLAYER);
	}
	
	@Override
	protected void renderLayer(DrownedPatch entitypatch, Drowned entityliving, DrownedOuterLayer vanillaLayer, PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			OpenMatrix4f[] poses, float bob, float yRot, float xRot, float partialTicks) {
		this.mesh.get().draw(poseStack, buffer, RenderTypes.entityCutout(DROWNED_OUTER_LAYER), packedLight, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.pack(0.0F, entityliving.hurtTime > 0 || entityliving.deathTime > 0), entitypatch.getArmature(), poses);
	}
	
	@Override
	protected void initMesh() {
		this.mesh.get().initialize();
	}
}