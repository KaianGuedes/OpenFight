package dev.war.openfight.client.renderer.patched.entity;

import net.minecraft.client.model.animal.golem.IronGolemModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.client.renderer.entity.layers.IronGolemCrackinessLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.golem.IronGolem;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.client.model.Meshes;
import dev.war.openfight.client.mesh.IronGolemMesh;
import dev.war.openfight.client.renderer.patched.layer.PatchedGolemCrackLayer;
import dev.war.openfight.world.capabilities.entitypatch.mob.IronGolemPatch;

public class PIronGolemRenderer extends PatchedLivingEntityRenderer<IronGolem, IronGolemPatch, IronGolemModel, IronGolemRenderer, IronGolemMesh> {
	public PIronGolemRenderer(EntityRendererProvider.Context context, EntityType<?> entityType) {
		super(context, entityType);
		this.addPatchedLayer(IronGolemCrackinessLayer.class, new PatchedGolemCrackLayer(Meshes.IRON_GOLEM));
	}
	
	@Override
	public AssetAccessor<IronGolemMesh> getDefaultMesh() {
		return Meshes.IRON_GOLEM;
	}
}