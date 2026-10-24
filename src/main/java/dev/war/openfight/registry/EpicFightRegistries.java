package dev.war.openfight.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import dev.war.openfight.platform.fabric.registry.DeferredRegister;
import dev.war.openfight.platform.fabric.registry.RegistryBuilder;
import dev.war.openfight.EpicFight;
import dev.war.openfight.api.animation.SynchedAnimationVariableKey;
import dev.war.openfight.api.ex_cap.data.Moveset;
import dev.war.openfight.api.ex_cap.data.modifier.WeaponModifier;
import dev.war.openfight.api.ex_cap.provider.ProviderConditional;
import dev.war.openfight.client.cosmetics.Emote;
import dev.war.openfight.data.conditions.Condition;
import dev.war.openfight.registry.callbacks.SkillCallbacks;
import dev.war.openfight.registry.callbacks.SkillDataKeyCallbacks;
import dev.war.openfight.registry.callbacks.SynchedAnimationVariableKeyCallbacks;
import dev.war.openfight.registry.entries.*;
import dev.war.openfight.skill.Skill;
import dev.war.openfight.skill.SkillDataKey;
import dev.war.openfight.world.capabilities.item.CapabilityItem;
import dev.war.openfight.world.capabilities.item.custom.CustomData;
import dev.war.openfight.world.entity.data.ExpandedEntityDataAccessor;

import java.util.List;
import java.util.function.Supplier;

public abstract class EpicFightRegistries {
    public static final Registry<Supplier<Condition<?>>> CONDITION = new RegistryBuilder<> (Keys.CONDITION).create();
    public static final Registry<ExpandedEntityDataAccessor<?>> EXPANDED_ENTITY_DATA_ACCESSOR = new RegistryBuilder<> (Keys.EXPANDED_ENTITY_DATA_ACCESSOR).sync(true).create();
    public static final Registry<Skill> SKILL = new RegistryBuilder<> (Keys.SKILL).callback(SkillCallbacks.getSkillCallback()).sync(true).create();
    public static final Registry<SkillDataKey<?>> SKILL_DATA_KEY = new RegistryBuilder<> (Keys.SKILL_DATA_KEY).callback(SkillDataKeyCallbacks.getRegistryCallback()).sync(true).create();
    public static final Registry<SynchedAnimationVariableKey<?>> SYNCHED_ANIMATION_VARIABLE = new RegistryBuilder<> (Keys.SYNCHED_ANIMATION_VARIABLE_KEY).callback(SynchedAnimationVariableKeyCallbacks.getRegistryCallback()).sync(true).create();
    public static final Registry<CapabilityItem.Builder<?>> BUILDERS = new RegistryBuilder<>(Keys.BUILDERS).sync(true).create();
    public static final Registry<Moveset.Builder> MOVESETS = new RegistryBuilder<>(Keys.MOVESETS).sync(true).create();
    public static final Registry<ProviderConditional.Builder> PROVIDER_CONDITIONALS = new RegistryBuilder<>(Keys.PROVIDER_CONDITIONALS).sync(true).create();
    public static final Registry<WeaponModifier.Builder> MODIFIERS = new RegistryBuilder<>(Keys.MODIFIERS).sync(true).create();
    public static final Registry<CustomData<?>> WEAPON_DATA = new RegistryBuilder<>(Keys.WEAPON_DATA).sync(true).create();
    public static final Registry<CustomData<?>> MOVESET_DATA = new RegistryBuilder<>(Keys.MOVESET_DATA).sync(true).create();

    // Deferred Registries
    public static final List<DeferredRegister<?>> DEFERRED_REGISTRIES = List.of(
        EpicFightAttributes.REGISTRY,
        EpicFightSounds.REGISTRY,
        EpicFightBlocks.REGISTRY,
        EpicFightBlockEntities.REGISTRY,
        EpicFightCommandArgumentTypes.REGISTRY,
        EpicFightConditions.REGISTRY,
        EpicFightCreativeTabs.REGISTRY,
        EpicFightDataComponentTypes.REGISTRY,
        EpicFightEntityTypes.REGISTRY,
        EpicFightExpandedEntityDataAccessors.REGISTRY,
        EpicFightItems.REGISTRY,
        EpicFightLootItemFunctions.REGISTRY,
        EpicFightMobEffects.REGISTRY,
        EpicFightParticles.REGISTRY,
        EpicFightPotions.REGISTRY,
        EpicFightSkillDataKeys.REGISTRY,
        EpicFightSkills.REGISTRY,
        EpicFightSynchedAnimationVariableKeys.REGISTRY,
        EpicFightMovesets.REGISTRY,
        EpicFightProviderConditionals.REGISTRY,
        EpicFightItemCapabilityPresets.REGISTRY,
        EpicFightModifiers.REGISTRY,
        EpicFightMovesetData.REGISTER
    );

    public interface Keys {
        ResourceKey<Registry<CapabilityItem.Builder<?>>> BUILDERS = key("item_capability_builder");
        ResourceKey<Registry<Moveset.Builder>> MOVESETS = key("moveset");
        ResourceKey<Registry<ProviderConditional.Builder>> PROVIDER_CONDITIONALS = key("provider_conditional");
        ResourceKey<Registry<WeaponModifier.Builder>> MODIFIERS = key("modifiers");
        ResourceKey<Registry<CustomData<?>>> WEAPON_DATA = key("weapon_data");
        ResourceKey<Registry<CustomData<?>>> MOVESET_DATA = key("moveset_data");
        ResourceKey<Registry<Supplier<Condition<?>>>> CONDITION = key("condition");
        ResourceKey<Registry<ExpandedEntityDataAccessor<?>>> EXPANDED_ENTITY_DATA_ACCESSOR = key("expanded_entity_data_accessor");
        ResourceKey<Registry<SynchedAnimationVariableKey<?>>> SYNCHED_ANIMATION_VARIABLE_KEY = key("synched_animation_variable_key");
        ResourceKey<Registry<Skill>> SKILL = key("skill");
        ResourceKey<Registry<SkillDataKey<?>>> SKILL_DATA_KEY = key("skill_data_key");

        // Data Pack Registries
        ResourceKey<Registry<Emote>> EMOTE = key("emote");

        private static <T> ResourceKey<Registry<T>> key(String name) {
            return ResourceKey.createRegistryKey(EpicFight.identifier(name));
        }
    }
}
