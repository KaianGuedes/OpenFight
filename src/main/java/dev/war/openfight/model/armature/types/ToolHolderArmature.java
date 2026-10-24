package dev.war.openfight.model.armature.types;

import dev.war.openfight.api.animation.Joint;

public interface ToolHolderArmature {
	public Joint leftToolJoint();
	public Joint rightToolJoint();
	public Joint backToolJoint();
}
