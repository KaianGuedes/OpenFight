package dev.war.openfight.api.animation.types.procedural;

import dev.war.openfight.api.animation.AnimationManager;
import dev.war.openfight.api.animation.AnimationManager.AnimationAccessor;
import dev.war.openfight.api.animation.Pose;
import dev.war.openfight.api.animation.types.DynamicAnimation;
import dev.war.openfight.api.animation.types.LongHitAnimation;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.asset.JsonAssetLoader;
import dev.war.openfight.api.model.Armature;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public class EnderDragonDeathAnimation extends LongHitAnimation {
	public EnderDragonDeathAnimation(float convertTime, AnimationAccessor<? extends EnderDragonDeathAnimation> accessor, AssetAccessor<? extends Armature> armature) {
		super(convertTime, accessor, armature);
	}
	
	@Override
	public void loadAnimation() {
		this.animationClip = AnimationManager.getInstance().loadAnimationClip(this, JsonAssetLoader::loadAllJointsClipForAnimation);
	}
	
	@Override
	public void modifyPose(DynamicAnimation animation, Pose pose, LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
		
	}
}