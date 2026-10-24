package dev.war.openfight.client.renderer.patched.entity;

import net.minecraft.client.model.monster.illager.IllagerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.client.model.Meshes;
import dev.war.openfight.client.mesh.HumanoidMesh;
import dev.war.openfight.client.renderer.patched.layer.PatchedHeadLayer;
import dev.war.openfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import dev.war.openfight.world.capabilities.entitypatch.MobPatch;

public class PIllagerRenderer<E extends AbstractIllager, T extends MobPatch<E>> extends PatchedLivingEntityRenderer<E, T, IllagerModel<?>, IllagerRenderer<E, ?>, HumanoidMesh> {
	public PIllagerRenderer(EntityRendererProvider.Context context, EntityType<?> entityType) {
		super(context, entityType);
		this.addPatchedLayer(ItemInHandLayer.class, new PatchedItemInHandLayer<>());
		this.addPatchedLayer(CustomHeadLayer.class, new PatchedHeadLayer<>());
	}
	
	@Override
	public AssetAccessor<HumanoidMesh> getDefaultMesh() {
		return Meshes.ILLAGER;
	}
}