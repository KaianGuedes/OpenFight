package dev.war.openfight.client.renderer.patched.entity;

import net.minecraft.client.model.monster.witch.WitchModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WitchRenderer;
import net.minecraft.client.renderer.entity.layers.WitchItemLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Witch;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.client.model.Meshes;
import dev.war.openfight.client.mesh.VillagerMesh;
import dev.war.openfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import dev.war.openfight.world.capabilities.entitypatch.mob.WitchPatch;

public class PWitchRenderer extends PatchedLivingEntityRenderer<Witch, WitchPatch, WitchModel, WitchRenderer, VillagerMesh> {
	public PWitchRenderer(EntityRendererProvider.Context context, EntityType<?> entityType) {
		super(context, entityType);
		this.addPatchedLayer(WitchItemLayer.class, new PatchedItemInHandLayer<>());
	}
	
	@Override
	public AssetAccessor<VillagerMesh> getDefaultMesh() {
		return Meshes.WITCH;
	}
}