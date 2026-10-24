package dev.war.openfight.client.renderer.patched.entity;

import net.minecraft.client.model.monster.ravager.RavagerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RavagerRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Ravager;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.client.model.Meshes;
import dev.war.openfight.client.mesh.RavagerMesh;
import dev.war.openfight.world.capabilities.entitypatch.mob.RavagerPatch;

public class PRavagerRenderer extends PatchedLivingEntityRenderer<Ravager, RavagerPatch, RavagerModel, RavagerRenderer, RavagerMesh> {
	public PRavagerRenderer(EntityRendererProvider.Context context, EntityType<?> entityType) {
		super(context, entityType);
	}
	
	@Override
	public AssetAccessor<RavagerMesh> getDefaultMesh() {
		return Meshes.RAVAGER;
	}
}