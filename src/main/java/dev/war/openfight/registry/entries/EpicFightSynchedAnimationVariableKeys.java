package dev.war.openfight.registry.entries;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.phys.Vec3;
import dev.war.openfight.platform.fabric.registry.DeferredHolder;
import dev.war.openfight.platform.fabric.registry.DeferredRegister;
import dev.war.openfight.api.animation.SynchedAnimationVariableKey;
import dev.war.openfight.api.animation.SynchedAnimationVariableKey.SynchedIndependentAnimationVariableKey;
import dev.war.openfight.api.utils.ByteBufCodecsExtends;
import dev.war.openfight.main.EpicFightMod;
import dev.war.openfight.registry.EpicFightRegistries;

public final class EpicFightSynchedAnimationVariableKeys {
	private EpicFightSynchedAnimationVariableKeys() {}
	
	public static final DeferredRegister<SynchedAnimationVariableKey<?>> REGISTRY = DeferredRegister.create(EpicFightRegistries.SYNCHED_ANIMATION_VARIABLE, EpicFightMod.NAMESPACE);
	
	public static final DeferredHolder<SynchedAnimationVariableKey<?>, SynchedIndependentAnimationVariableKey<Vec3>> DESTINATION =
		REGISTRY.register("destination", () -> 
			SynchedAnimationVariableKey.independent(animator -> animator.getEntityPatch().getOriginal().position(), true, ByteBufCodecsExtends.VEC3)
		);
	
	public static final DeferredHolder<SynchedAnimationVariableKey<?>, SynchedIndependentAnimationVariableKey<Integer>> TARGET_ENTITY =
		REGISTRY.register("target_entity", () ->
			SynchedAnimationVariableKey.independent(animator -> -1, true, ByteBufCodecs.INT)
		);
	
	public static final DeferredHolder<SynchedAnimationVariableKey<?>, SynchedIndependentAnimationVariableKey<Float>> Y_ROT =
		REGISTRY.register("y_rot", () ->
			SynchedAnimationVariableKey.independent(animator -> animator.getEntityPatch().getOriginal().getYRot(), true, ByteBufCodecs.FLOAT)
		);
	
	public static final DeferredHolder<SynchedAnimationVariableKey<?>, SynchedIndependentAnimationVariableKey<Integer>> CHARGING_TICKS =
		REGISTRY.register("charging_ticks", () ->
			SynchedAnimationVariableKey.independent(animator ->  0, true, ByteBufCodecs.INT)
		);
}
