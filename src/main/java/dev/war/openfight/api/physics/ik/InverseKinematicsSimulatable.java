package dev.war.openfight.api.physics.ik;

import net.minecraft.world.entity.Entity;
import dev.war.openfight.api.physics.SimulatableObject;
import dev.war.openfight.api.utils.math.OpenMatrix4f;

public interface InverseKinematicsSimulatable extends SimulatableObject {
	public float getRootXRot();
	public float getRootXRotO();
	
	public float getRootZRot();
	public float getRootZRotO();
	
	public OpenMatrix4f getModelMatrix(float partialTick);
	
	InverseKinematicsSimulator getIKSimulator();
	
	Entity toEntity();
}
