package dev.war.openfight.api.physics;

import net.minecraft.resources.Identifier;
import dev.war.openfight.api.animation.Joint;
import dev.war.openfight.api.client.model.SoftBodyTranslatable;
import dev.war.openfight.api.client.physics.cloth.ClothSimulatable;
import dev.war.openfight.api.client.physics.cloth.ClothSimulator;
import dev.war.openfight.api.physics.ik.InverseKinematicsProvider;
import dev.war.openfight.api.physics.ik.InverseKinematicsSimulatable;
import dev.war.openfight.api.physics.ik.InverseKinematicsSimulator;

public interface SimulationTypes<KEY, O, PV extends SimulationProvider<O, DATA, B, PV>, B extends SimulationObject.SimulationObjectBuilder, DATA extends SimulationObject<B, PV, O>, SIM extends PhysicsSimulator<KEY, B, PV, O, DATA>> {
	SimulationTypes<Identifier, ClothSimulatable, SoftBodyTranslatable, ClothSimulator.ClothObjectBuilder, ClothSimulator.ClothObject, ClothSimulator> CLOTH = new SimulationTypes<> () {};
	SimulationTypes<Joint, InverseKinematicsSimulatable, InverseKinematicsProvider, InverseKinematicsSimulator.InverseKinematicsBuilder, InverseKinematicsSimulator.InverseKinematicsObject, InverseKinematicsSimulator> INVERSE_KINEMATICS = new SimulationTypes<> () {};
}