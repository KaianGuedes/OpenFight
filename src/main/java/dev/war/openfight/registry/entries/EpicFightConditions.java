package dev.war.openfight.registry.entries;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

import net.minecraft.resources.Identifier;
import dev.war.openfight.platform.fabric.registry.DeferredHolder;
import dev.war.openfight.platform.fabric.registry.DeferredRegister;
import dev.war.openfight.data.conditions.Condition;
import dev.war.openfight.data.conditions.entity.HealthPoint;
import dev.war.openfight.data.conditions.entity.OffhandItemCategory;
import dev.war.openfight.data.conditions.entity.PlayerName;
import dev.war.openfight.data.conditions.entity.PlayerSkillActivated;
import dev.war.openfight.data.conditions.entity.RandomChance;
import dev.war.openfight.data.conditions.entity.TargetInDistance;
import dev.war.openfight.data.conditions.entity.TargetInEyeHeight;
import dev.war.openfight.data.conditions.entity.TargetInPov;
import dev.war.openfight.main.EpicFightMod;
import dev.war.openfight.registry.EpicFightRegistries;

public final class EpicFightConditions {
	private EpicFightConditions() {}
	
	public static final DeferredRegister<Supplier<Condition<?>>> REGISTRY = DeferredRegister.create(EpicFightRegistries.CONDITION, EpicFightMod.NAMESPACE);
	
	public static <T extends Condition<?>> Supplier<T> getConditionOrThrow(Identifier key) throws NoSuchElementException, ClassCastException {
		if (!EpicFightRegistries.CONDITION.containsKey(key)) {
			throw new NoSuchElementException("No condition named " + key);
		}
		
		return getConditionOrNull(key);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Condition<?>> Supplier<T> getConditionOrNull(Identifier key) throws ClassCastException {
		return (Supplier<T>) EpicFightRegistries.CONDITION.getValue(key);
	}
	
	//EntityPatch conditions
	public static final DeferredHolder<Supplier<Condition<?>>, Supplier<Condition<?>>> OFFHAND_ITEM_CATEGORY = REGISTRY.register("offhand_item_category", () -> OffhandItemCategory::new);
	
	public static final DeferredHolder<Supplier<Condition<?>>, Supplier<Condition<?>>> PLAYER_SKILL_ACTIVATED = REGISTRY.register("skill_active", () -> PlayerSkillActivated::new);
	public static final DeferredHolder<Supplier<Condition<?>>, Supplier<Condition<?>>> PLAYER_NAME = REGISTRY.register("player_name", () -> PlayerName::new);
	public static final DeferredHolder<Supplier<Condition<?>>, Supplier<Condition<?>>> HEALTH_POINT = REGISTRY.register("health", () -> HealthPoint::new);
	public static final DeferredHolder<Supplier<Condition<?>>, Supplier<Condition<?>>> RANDOM = REGISTRY.register("random_chance", () -> RandomChance::new);
	public static final DeferredHolder<Supplier<Condition<?>>, Supplier<Condition<?>>> TARGET_IN_DISTANCE = REGISTRY.register("within_distance", () -> TargetInDistance::new);
	public static final DeferredHolder<Supplier<Condition<?>>, Supplier<Condition<?>>> TARGET_IN_EYE_HEIGHT = REGISTRY.register("within_eye_height", () -> TargetInEyeHeight::new);
	public static final DeferredHolder<Supplier<Condition<?>>, Supplier<Condition<?>>> TARGET_IN_POV = REGISTRY.register("within_angle", () -> TargetInPov::new);
	public static final DeferredHolder<Supplier<Condition<?>>, Supplier<Condition<?>>> TARGET_IN_POV_HORIZONTAL = REGISTRY.register("within_angle_horizontal", () -> TargetInPov.TargetInPovHorizontal::new);
}
