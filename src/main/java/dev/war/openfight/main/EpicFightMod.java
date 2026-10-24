package dev.war.openfight.main;

import java.io.IOException;
import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import dev.war.openfight.EpicFight;
import dev.war.openfight.api.animation.*;
import dev.war.openfight.api.data.reloader.*;
import dev.war.openfight.api.event.EpicFightEventHooks;
import dev.war.openfight.api.ex_cap.listeners.*;
import dev.war.openfight.api.ex_cap.modules.core.events.ExCapBuilderCreationEvent;
import dev.war.openfight.config.*;
import dev.war.openfight.data.loot.EpicFightLootTables;
import dev.war.openfight.gameasset.*;
import dev.war.openfight.network.*;
import dev.war.openfight.platform.fabric.FabricModPlatform;
import dev.war.openfight.platform.fabric.event.FabricGameplayEvents;
import dev.war.openfight.platform.fabric.registry.RegistryBuilder;
import dev.war.openfight.platform.fabric.network.PacketDistributor;
import dev.war.openfight.registry.EpicFightRegistries;
import dev.war.openfight.registry.entries.*;
import dev.war.openfight.server.commands.*;
import dev.war.openfight.skill.*;
import dev.war.openfight.world.capabilities.EpicFightCapabilities;
import dev.war.openfight.world.capabilities.entitypatch.*;
import dev.war.openfight.world.capabilities.item.*;
import dev.war.openfight.world.capabilities.item.CapabilityItem.Styles;
import dev.war.openfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import dev.war.openfight.world.gamerule.EpicFightGameRules;

/** Fabric common entrypoint. Rendering/input bridges are initialized separately on the physical client. */
public class EpicFightMod implements ModInitializer {
    public static final String MOD_ID = EpicFight.MOD_ID;
    public static final String NAMESPACE = EpicFight.NAMESPACE;

	/// @deprecated Use [dev.war.openfight.EpicFight#LOGGER] instead
	@Deprecated(forRemoval = true, since = "26.1")
	public static final Logger LOGGER = EpicFight.LOGGER;

	public static String prefix(String s) {
		return String.format("%s:%s", NAMESPACE, s);
	}

    /// @deprecated Consider using the generated object [LangKeys],
    /// which is type-safe and not error-prone to runtime bugs or crashes.
    @Deprecated(forRemoval = true)
	public static String format(String s) {
		return String.format(s, NAMESPACE);
	}



	public static void logAndStacktraceIfDevSide(BiConsumer<Logger, String> logFunction, String message, Function<String, Throwable> exceptionProvider) {
		logAndStacktraceIfDevSide(logFunction, message, exceptionProvider, message);
	}

	public static void logAndStacktraceIfDevSide(BiConsumer<Logger, String> logFunction, String message, Function<String, Throwable> exceptionProvider, String stackTraceMessage) {
		logFunction.accept(LOGGER, message);
		stacktraceIfDevSide(message, exceptionProvider, stackTraceMessage);
	}

	public static void stacktraceIfDevSide(String message, Function<String, Throwable> exceptionProvider) {
		stacktraceIfDevSide(message, exceptionProvider, message);
	}

	public static void stacktraceIfDevSide(String message, Function<String, Throwable> exceptionProvider, String stackTraceMessage) {
		if (exceptionProvider != null && EpicFightSharedConstants.IS_DEV_ENV) {
			exceptionProvider.apply(stackTraceMessage).printStackTrace();
		}
	}

    @Override public void onInitialize() {
        net.fabricmc.fabric.api.event.registry.DynamicRegistries.registerSynced(EpicFightRegistries.Keys.EMOTE, dev.war.openfight.client.cosmetics.Emote.CODEC);
        EpicFight.initialize(new FabricModPlatform());
        LivingMotion.ENUM_MANAGER.registerEnumCls(NAMESPACE,LivingMotions.class);
        SkillCategory.ENUM_MANAGER.registerEnumCls(NAMESPACE,SkillCategories.class);
        SkillSlot.ENUM_MANAGER.registerEnumCls(NAMESPACE,SkillSlots.class);
        Style.ENUM_MANAGER.registerEnumCls(NAMESPACE,Styles.class);
        WeaponCategory.ENUM_MANAGER.registerEnumCls(NAMESPACE,WeaponCategories.class);
        Faction.ENUM_MANAGER.registerEnumCls(NAMESPACE,Factions.class);
        EntityPairingPacketType.ENUM_MANAGER.registerEnumCls(NAMESPACE,EntityPairingPacketTypes.class);
        LivingMotion.ENUM_MANAGER.loadEnum();
        SkillCategory.ENUM_MANAGER.loadEnum();
        SkillSlot.ENUM_MANAGER.loadEnum();
        Style.ENUM_MANAGER.loadEnum();
        WeaponCategory.ENUM_MANAGER.loadEnum();
        Faction.ENUM_MANAGER.loadEnum();
        EntityPairingPacketType.ENUM_MANAGER.loadEnum();
        try {
            CommonConfig.SPEC.load(FabricLoader.getInstance().getConfigDir().resolve("epicfight-common.json"));
            ServerConfig.SPEC.load(FabricLoader.getInstance().getConfigDir().resolve("epicfight-server.json"));
        } catch (IOException e) { throw new java.io.UncheckedIOException(e); }
        CommonConfig.reload();
        ServerConfig.reload();
        var event=new AnimationManager.AnimationRegistryEvent();
        Animations.registerAnimations(event);
        event.getBuilders().stream().sorted(Comparator.comparing(AnimationManager.AnimationBuilder::namespace)).forEach(builder -> builder.task().accept(builder));
        EpicFightRegistries.DEFERRED_REGISTRIES.forEach(registry -> registry.registerAll());
        EpicFightAttributes.EventBus.entityAttributeCreationEvent(new dev.war.openfight.platform.fabric.event.EntityAttributeCreationEvent());
        net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.MODIFY.register(context ->
            EpicFightAttributes.EventBus.entityAttributeModificationEvent(new dev.war.openfight.platform.fabric.event.EntityAttributeModificationEvent(context)));
        RegistryBuilder.bakeAll();
        // The preset registry is populated above; publish it to the manager before weapon types export it.
        dev.war.openfight.api.ex_cap.managers.ItemPresetManager.acceptEvent(
            EpicFightEventHooks.Registry.EX_CAP_BUILDER_CREATION.post(new ExCapBuilderCreationEvent()));
        Armatures.registerEntityTypes();

        EpicFightCommandArgumentTypes.registerArgumentTypes();
        EpicFightEntityTypes.registerSpawnPlacements();
        EpicFightPotions.addRecipes();
        EpicFightCapabilities.ITEM_CAPABILITY_PROVIDER.registerWeaponTypesByClass();
        EpicFightCapabilities.ENTITY_PATCH_PROVIDER.registerVanillaEntityPatches();
        EpicFightGameRules.registerGameRules();
        WeaponTypeReloadListener.registerDefaultWeaponTypes();
        EpicFightMobEffects.addOffhandModifier();
        EpicFightExtensibleEnums.initExtensibleEnums();
        EpicFightEventHooks.Registry.SKILLBOOK_LOOT_TABLE.registerEvent(EpicFightLootTables::createSkillLootTable);
        net.fabricmc.fabric.api.loot.v3.LootTableEvents.MODIFY.register(EpicFightLootTables::onLootTableRegistry);
        net.fabricmc.fabric.api.loot.v3.LootTableEvents.ALL_LOADED.register((resourceManager, lootTables) -> dev.war.openfight.data.loot.OnSkillBookDroppedByEntity.registerEntitySkillLootTable());
        net.fabricmc.fabric.api.loot.v3.LootTableEvents.MODIFY_DROPS.register(dev.war.openfight.data.loot.OnSkillBookDroppedByEntity::modifyDrops);
        EpicFightNetworkManager.register();
        PacketDistributor.initialize();
        FabricGameplayEvents.initialize();
        CommandRegistrationCallback.EVENT.register((dispatcher,context,environment)-> {
            PlayerModeCommand.register(dispatcher);
            PlayerSkillCommand.register(dispatcher);
            PlayerStaminaCommand.register(dispatcher);
            AnimatorCommand.register(dispatcher);
        });
        var reload=ResourceLoader.get(PackType.SERVER_DATA);
        reload.registerReloadListener(identifier("colliders"),new ColliderPreset());
        reload.registerReloadListener(identifier("skill_parameters"),new SkillReloadListener());
        reload.registerReloadListener(identifier("item_presets"),new ItemPresetReloadListener());
        reload.registerReloadListener(identifier("weapon_modifiers"),new WeaponModifierReloadListener());
        reload.registerReloadListener(identifier("conditionals"),new ConditionalReloadListener());
        reload.registerReloadListener(identifier("movesets"),new MovesetReloadListener());
        reload.registerReloadListener(identifier("weapon_types"),new WeaponTypeReloadListener());
        reload.registerReloadListener(identifier("item_keywords"),new ItemKeywordReloadListener());
        reload.registerReloadListener(identifier("item_capabilities"),new ItemCapabilityReloadListener());
        reload.registerReloadListener(identifier("mob_patches"),new MobPatchReloadListener());
        if (!EpicFightSharedConstants.isPhysicalClient()) reload.registerReloadListener(identifier("animations"),AnimationManager.getInstance());
    }
    public static @NotNull Identifier identifier(@NotNull String path) { return EpicFight.identifier(path); }
    public static @NotNull Identifier rl(@NotNull String path) { return identifier(path); }
}
