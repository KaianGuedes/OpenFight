package dev.war.openfight.client.renderer.patched.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.SkeletonClothingLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import dev.war.openfight.api.client.model.Meshes;
import dev.war.openfight.client.mesh.HumanoidMesh;
import dev.war.openfight.client.renderer.patched.layer.EmptyLayer;
import dev.war.openfight.world.capabilities.entitypatch.mob.SkeletonPatch;

public class PStrayRenderer extends PHumanoidRenderer<PathfinderMob, SkeletonPatch<PathfinderMob>, HumanoidModel<?>, HumanoidMobRenderer<PathfinderMob, ?, ?>, HumanoidMesh> {
	public PStrayRenderer(EntityRendererProvider.Context context, EntityType<?> entityType) {
		super(Meshes.SKELETON, context, entityType);
		this.addPatchedLayer(SkeletonClothingLayer.class, new EmptyLayer<>());
	}
}