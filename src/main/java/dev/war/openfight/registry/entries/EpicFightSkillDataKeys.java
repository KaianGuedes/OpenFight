package dev.war.openfight.registry.entries;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import dev.war.openfight.platform.fabric.registry.DeferredHolder;
import dev.war.openfight.platform.fabric.registry.DeferredRegister;
import dev.war.openfight.api.utils.ByteBufCodecsExtends;
import dev.war.openfight.main.EpicFightMod;
import dev.war.openfight.registry.EpicFightRegistries;
import dev.war.openfight.skill.SkillDataKey;
import dev.war.openfight.skill.common.ComboAttacks;
import dev.war.openfight.skill.guard.GuardSkill;
import dev.war.openfight.skill.guard.ImpactGuardSkill;
import dev.war.openfight.skill.guard.ParryingSkill;
import dev.war.openfight.skill.identity.MeteorSlamSkill;
import dev.war.openfight.skill.identity.RevelationSkill;
import dev.war.openfight.skill.mover.DemolitionLeapSkill;
import dev.war.openfight.skill.mover.PhantomAscentSkill;
import dev.war.openfight.skill.passive.AdaptiveSkinSkill;
import dev.war.openfight.skill.passive.AdrenalineFiendSkill;
import dev.war.openfight.skill.passive.BonebreakerSkill;
import dev.war.openfight.skill.passive.VengeanceSkill;
import dev.war.openfight.skill.weapon_passive.BattojutsuPassive;
import dev.war.openfight.skill.weaponinnate.BattojutsuSkill;
import dev.war.openfight.skill.weaponinnate.BladeRushSkill;
import dev.war.openfight.skill.weaponinnate.EverlastingAllegiance;
import dev.war.openfight.skill.weaponinnate.GraspingSpireSkill;
import dev.war.openfight.world.damagesource.EpicFightDamageTypeTags;

public final class EpicFightSkillDataKeys {
	private EpicFightSkillDataKeys() {}
	
	public static final DeferredRegister<SkillDataKey<?>> REGISTRY = DeferredRegister.create(EpicFightRegistries.SKILL_DATA_KEY, EpicFightMod.NAMESPACE);
	
	public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> COMBO_COUNTER = REGISTRY.register("combo_counter", () -> 
		SkillDataKey.createSkillDataKey(ByteBufCodecs.INT, 0, false, ComboAttacks.class, BladeRushSkill.class)
	);
	
	public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> SHEATH = REGISTRY.register("sheath", () -> 
		SkillDataKey.createSkillDataKey(ByteBufCodecs.BOOL, false, false, BattojutsuPassive.class, BattojutsuSkill.class)
	);
	
	public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> PENALTY_RESTORE_COUNTER = REGISTRY.register("penalty_restore_counter", () -> 
		SkillDataKey.createSkillDataKey(ByteBufCodecs.INT, 0, false, GuardSkill.class)
	);
	
	public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Float>> PENALTY = REGISTRY.register("penalty", () -> 
		SkillDataKey.createSkillDataKey(ByteBufCodecs.FLOAT, 0.0F, false, GuardSkill.class, ImpactGuardSkill.class)
	);
	
	public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> LAST_ACTIVE = REGISTRY.register("last_active", () -> 
		SkillDataKey.createSkillDataKey(ByteBufCodecs.INT, 0, false, ParryingSkill.class)
	);
	
	public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> PARRY_MOTION_COUNTER = REGISTRY.register("parry_motion_counter", () -> 
		SkillDataKey.createSkillDataKey(ByteBufCodecs.INT, 0, false, ParryingSkill.class)
	);
	
	public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Float>> FALL_DISTANCE = REGISTRY.register("fall_distance", () -> 
		SkillDataKey.createSkillDataKey(ByteBufCodecs.FLOAT, 0.0F, true, MeteorSlamSkill.class)
	);
	
	public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> PROTECT_NEXT_FALL = REGISTRY.register("slam_protect_next_fall", () -> 
		SkillDataKey.createSkillDataKey(ByteBufCodecs.BOOL, false, false, MeteorSlamSkill.class, DemolitionLeapSkill.class, PhantomAscentSkill.class)
	);
	
	public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> STACKS = REGISTRY.register("stacks", () -> 
		SkillDataKey.createSkillDataKey(ByteBufCodecs.INT, 0, false, AdaptiveSkinSkill.class, BonebreakerSkill.class, RevelationSkill.class)
	);
	
	public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> JUMP_KEY_PRESSED_LAST_TICK = REGISTRY.register("jump_key_pressed_last_tick", () -> 
		SkillDataKey.createSkillDataKey(ByteBufCodecs.BOOL, false, false, PhantomAscentSkill.class)
	);
	
	public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> JUMP_COUNT = REGISTRY.register("jump_count", () -> 
		SkillDataKey.createSkillDataKey(ByteBufCodecs.INT, 0, false, PhantomAscentSkill.class)
	);
	
	public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> THROWN_TRIDENT_ENTITY_ID = REGISTRY.register("thrown_trident_entity_id", () -> 
		SkillDataKey.createSkillDataKey(ByteBufCodecs.INT, -1, false, EverlastingAllegiance.class)
	);
	
	public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> LAST_HIT_COUNT = REGISTRY.register("last_hit_count", () -> 
		SkillDataKey.createSkillDataKey(ByteBufCodecs.INT, 0, false, GraspingSpireSkill.class)
	);
	
	public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<TagKey<DamageType>>> RESISTING_DAMAGE_TYPE = REGISTRY.register("resisting_damage_type", () ->
		SkillDataKey.createSkillDataKey(ByteBufCodecsExtends.tagKey(Registries.DAMAGE_TYPE), EpicFightDamageTypeTags.NONE, true, AdaptiveSkinSkill.class)
	);
	
	public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> TICK_RECORD = REGISTRY.register("tick_record", () ->
		SkillDataKey.createSkillDataKey(ByteBufCodecs.INT, 0, false, AdaptiveSkinSkill.class, AdrenalineFiendSkill.class, VengeanceSkill.class)
	);
	
	public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> ENTITY_ID = REGISTRY.register("entity_id", () ->
		SkillDataKey.createSkillDataKey(ByteBufCodecs.INT, -1, false, BonebreakerSkill.class, VengeanceSkill.class)
	);
}