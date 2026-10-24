package dev.war.openfight.client.renderer.patched.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import dev.war.openfight.api.animation.JointTransform;
import dev.war.openfight.api.animation.Pose;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.model.Armature;
import dev.war.openfight.api.utils.math.OpenMatrix4f;
import dev.war.openfight.api.utils.math.Vec3f;
import dev.war.openfight.client.mesh.HumanoidMesh;
import dev.war.openfight.client.renderer.patched.layer.PatchedWingsLayer;
import dev.war.openfight.client.renderer.patched.layer.PatchedHeadLayer;
import dev.war.openfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import dev.war.openfight.client.renderer.patched.layer.WearableItemLayer;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public class PCustomHumanoidEntityRenderer<AM extends HumanoidMesh> extends PatchedLivingEntityRenderer<LivingEntity, LivingEntityPatch<LivingEntity>, HumanoidModel<?>, LivingEntityRenderer<LivingEntity, ?, ?>, AM> {
	private final AssetAccessor<AM> mesh;
	
	public PCustomHumanoidEntityRenderer(AssetAccessor<AM> mesh, EntityRendererProvider.Context context, EntityType<?> entityType) {
		super(context, entityType);
		
		this.mesh = mesh;
		this.addPatchedLayer(WingsLayer.class, new PatchedWingsLayer<>());
		this.addPatchedLayer(ItemInHandLayer.class, new PatchedItemInHandLayer<>());
		this.addPatchedLayer(HumanoidArmorLayer.class, new WearableItemLayer<>(mesh, false, context));
		this.addPatchedLayer(CustomHeadLayer.class, new PatchedHeadLayer<>());
	}
	
	@Override
	public void setJointTransforms(LivingEntityPatch<LivingEntity> entitypatch, Armature armature, Pose pose, float partialTicks) {
		if (entitypatch.getOriginal().isBaby()) {
			pose.orElseEmpty("Head").frontResult(JointTransform.scale(new Vec3f(1.25F, 1.25F, 1.25F)), OpenMatrix4f::mul);
		}
	}
	
	@Override
	protected float getDefaultLayerHeightCorrection() {
		return 0.75F;
	}
	
	@Override
	public AssetAccessor<AM> getDefaultMesh() {
		return this.mesh;
	}
}