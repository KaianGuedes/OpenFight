package dev.war.openfight.api.animation.types.grappling;

import dev.war.openfight.api.animation.types.LongHitAnimation;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.model.Armature;

public class GrapplingHitAnimation extends LongHitAnimation {
	public GrapplingHitAnimation(float convertTime, String path, AssetAccessor<? extends Armature> armature) {
		super(convertTime, path, armature);
	}
}