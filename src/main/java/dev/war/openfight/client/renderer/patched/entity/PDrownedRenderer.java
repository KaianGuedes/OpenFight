package dev.war.openfight.client.renderer.patched.entity;

import net.minecraft.client.model.monster.zombie.DrownedModel;
import net.minecraft.client.renderer.entity.DrownedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.zombie.Drowned;
import dev.war.openfight.api.client.model.Meshes;
import dev.war.openfight.client.mesh.HumanoidMesh;
import dev.war.openfight.client.renderer.patched.layer.OuterLayerRenderer;
import dev.war.openfight.world.capabilities.entitypatch.mob.DrownedPatch;

public class PDrownedRenderer extends PHumanoidRenderer<Drowned, DrownedPatch, DrownedModel, DrownedRenderer, HumanoidMesh> {
	public PDrownedRenderer(EntityRendererProvider.Context context, EntityType<?> entityType) {
		super(Meshes.BIPED, context, entityType);
		
		this.addPatchedLayer(DrownedOuterLayer.class, new OuterLayerRenderer());
	}
}