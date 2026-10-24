package dev.war.openfight.client.renderer.patched.layer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.client.model.SkinnedMesh;
import dev.war.openfight.api.utils.math.OpenMatrix4f;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public abstract class ModelRenderLayer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<?>, R extends RenderLayer<?, ?>, AM extends SkinnedMesh> extends PatchedLayer<E, T, M, R> {
	protected final AssetAccessor<AM> mesh;
	
	public ModelRenderLayer(AssetAccessor<AM> mesh) {
		this.mesh = mesh;
	}
	
	@Override
	public void renderLayer(E entityliving, T entitypatch, RenderLayer<?, ?> vanillaLayer, PoseStack poseStack, MultiBufferSource buffer, int packedLight, OpenMatrix4f[] poses, float bob, float yRot, float xRot, float partialTicks) {
		this.initMesh();
		this.renderLayer(entitypatch, entityliving, this.castLayer(vanillaLayer), poseStack, buffer, packedLight, poses, bob, yRot, xRot, partialTicks);
	}
	
	protected void initMesh() {
		if (this.mesh != null) {
			this.mesh.get().initialize();
		}
	}
}