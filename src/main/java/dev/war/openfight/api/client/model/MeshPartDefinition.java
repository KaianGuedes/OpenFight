package dev.war.openfight.api.client.model;

import java.util.function.Supplier;

import dev.war.openfight.api.utils.math.OpenMatrix4f;

public interface MeshPartDefinition {
	String partName();
	Mesh.RenderProperties renderProperties();
	Supplier<OpenMatrix4f> getModelPartAnimationProvider();
}