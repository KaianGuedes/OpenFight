package dev.war.openfight.client.renderer.patched.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import dev.war.openfight.api.animation.JointTransform;
import dev.war.openfight.api.animation.Pose;
import dev.war.openfight.api.client.model.Meshes;
import dev.war.openfight.api.model.Armature;
import dev.war.openfight.api.utils.math.OpenMatrix4f;
import dev.war.openfight.api.utils.math.Vec3f;
import dev.war.openfight.client.mesh.HumanoidMesh;
import dev.war.openfight.world.capabilities.entitypatch.HumanoidMobPatch;

public class PWitherSkeletonMinionRenderer extends PHumanoidRenderer<PathfinderMob, HumanoidMobPatch<PathfinderMob>, HumanoidModel<?>, HumanoidMobRenderer<PathfinderMob, ?, ?>, HumanoidMesh> {
	public PWitherSkeletonMinionRenderer(EntityRendererProvider.Context context, EntityType<?> entityType) {
		super(Meshes.SKELETON, context, entityType);
	}

	@Override
	public void setJointTransforms(HumanoidMobPatch<PathfinderMob> entitypatch, Armature armature, Pose pose, float partialTicks) {
		Vec3f rootScale = pose.orElseEmpty("Root").scale();
		pose.orElseEmpty("Head").jointLocal(JointTransform.scale(new Vec3f(1.0F / rootScale.x, 1.0F / rootScale.y, 1.0F / rootScale.z)), OpenMatrix4f::mul);
	}
}