package dev.war.openfight.client.renderer.patched.entity;

import net.minecraft.client.model.monster.hoglin.HoglinModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.hoglin.HoglinBase;
import dev.war.openfight.api.animation.JointTransform;
import dev.war.openfight.api.animation.Pose;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.client.model.Meshes;
import dev.war.openfight.api.model.Armature;
import dev.war.openfight.api.utils.math.OpenMatrix4f;
import dev.war.openfight.api.utils.math.Vec3f;
import dev.war.openfight.client.mesh.HoglinMesh;
import dev.war.openfight.world.capabilities.entitypatch.MobPatch;

public class PHoglinRenderer<E extends Mob & HoglinBase, T extends MobPatch<E>> extends PatchedLivingEntityRenderer<E, T, HoglinModel, MobRenderer<E, ?, ?>, HoglinMesh> {
	public PHoglinRenderer(EntityRendererProvider.Context context, EntityType<?> entityType) {
		super(context, entityType);
	}
	
	@Override
	public void setJointTransforms(T entitypatch, Armature armature, Pose pose, float partialTicks) {
		if (entitypatch.getOriginal().isBaby()) {
			pose.orElseEmpty("Head").frontResult(JointTransform.scale(new Vec3f(1.25F, 1.25F, 1.25F)), OpenMatrix4f::mul);
		}
	}
	
	@Override
	public AssetAccessor<HoglinMesh> getDefaultMesh() {
		return Meshes.HOGLIN;
	}
}