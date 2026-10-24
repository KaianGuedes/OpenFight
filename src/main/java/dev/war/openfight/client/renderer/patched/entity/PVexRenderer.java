package dev.war.openfight.client.renderer.patched.entity;

import net.minecraft.client.model.monster.vex.VexModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.VexRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Vex;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.client.model.Meshes;
import dev.war.openfight.client.mesh.VexMesh;
import dev.war.openfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import dev.war.openfight.world.capabilities.entitypatch.mob.VexPatch;

public class PVexRenderer extends PatchedLivingEntityRenderer<Vex, VexPatch, VexModel, VexRenderer, VexMesh> {
	public PVexRenderer(EntityRendererProvider.Context context, EntityType<?> entityType) {
		super(context, entityType);
		this.addPatchedLayer(ItemInHandLayer.class, new PatchedItemInHandLayer<>());
	}
	
	@Override
	public AssetAccessor<VexMesh> getDefaultMesh() {
		return Meshes.VEX;
	}
}