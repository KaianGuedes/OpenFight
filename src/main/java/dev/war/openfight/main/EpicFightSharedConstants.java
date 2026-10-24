package dev.war.openfight.main;

import dev.war.openfight.api.animation.Animator;
import dev.war.openfight.api.animation.ServerAnimator;
import dev.war.openfight.api.client.animation.ClientAnimator;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;


public class EpicFightSharedConstants {
	// Model variables
	public static final int MAX_WEIGHTS = 3;
	public static final int MAX_JOINTS = 1000;
	
	// Ingame variables
	public static final float A_TICK = 0.05F;
	public static final float GENERAL_ANIMATION_TRANSITION_TIME = 0.15F;
	public static final float EXECUTION_DAMAGE = 2147483647F;
	
	// Environment varables
	public static final boolean IS_DEV_ENV = net.fabricmc.loader.api.FabricLoader.getInstance().isDevelopmentEnvironment();

	// Sided variables
	private static final java.util.function.Function<LivingEntityPatch<?>, Animator> ANIMATOR_PROVIDER;
	
	static {
		ANIMATOR_PROVIDER = isPhysicalClient() ? ClientAnimator::getAnimator : ServerAnimator::getAnimator;
	}
	
	public static Animator getAnimator(LivingEntityPatch<?> entitypatch) {
		return ANIMATOR_PROVIDER.apply(entitypatch);
	}
	
	public static boolean isPhysicalClient() {
		return net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT;
	}
}
