package yesman.epicfight.main;

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
import yesman.epicfight.EpicFight;
import yesman.epicfight.api.animation.*;
import yesman.epicfight.api.data.reloader.*;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.ex_cap.listeners.*;
import yesman.epicfight.api.ex_cap.modules.core.events.ExCapBuilderCreationEvent;
import yesman.epicfight.config.*;
import yesman.epicfight.data.loot.EpicFightLootTables;
import yesman.epicfight.gameasset.*;
import yesman.epicfight.network.*;
import yesman.epicfight.platform.fabric.FabricModPlatform;
import yesman.epicfight.platform.fabric.event.FabricGameplayEvents;
import yesman.epicfight.platform.fabric.registry.RegistryBuilder;
import yesman.epicfight.platform.fabric.network.PacketDistributor;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.registry.entries.*;
import yesman.epicfight.server.commands.*;
import yesman.epicfight.skill.*;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.*;
import yesman.epicfight.world.capabilities.item.*;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.gamerule.EpicFightGameRules;

/** Fabric common entrypoint. Rendering/input bridges are initialized separately on the physical client. */
public class EpicFightMod implements ModInitializer {
	/// @deprecated Use [yesman.epicfight.EpicFight#MODID] instead
	@Deprecated(forRemoval = true)
	public static final String MODID = EpicFight.MODID;

	/// @deprecated Use [yesman.epicfight.EpicFight#EPICSKINS_MODID] instead
	@Deprecated(forRemoval = true)
	public static final String EPICSKINS_MODID = EpicFight.EPICSKINS_MODID;

	/// @deprecated Use [yesman.epicfight.EpicFight#LOGGER] instead
	@Deprecated(forRemoval = true, since = "26.1")
	public static final Logger LOGGER = EpicFight.LOGGER;

	public static String prefix(String s) {
		return String.format("%s:%s", MODID, s);
	}

    /// @deprecated Consider using the generated object [LangKeys],
    /// which is type-safe and not error-prone to runtime bugs or crashes.
    @Deprecated(forRemoval = true)
	public static String format(String s) {
		return String.format(s, MODID);
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
        net.fabricmc.fabric.api.event.registry.DynamicRegistries.registerSynced(EpicFightRegistries.Keys.EMOTE, yesman.epicfight.client.online.cosmetics.Emote.CODEC);
        EpicFight.initialize(new FabricModPlatform());
        LivingMotion.ENUM_MANAGER.registerEnumCls(MODID,LivingMotions.class);
        SkillCategory.ENUM_MANAGER.registerEnumCls(MODID,SkillCategories.class);
        SkillSlot.ENUM_MANAGER.registerEnumCls(MODID,SkillSlots.class);
        Style.ENUM_MANAGER.registerEnumCls(MODID,Styles.class);
        WeaponCategory.ENUM_MANAGER.registerEnumCls(MODID,WeaponCategories.class);
        Faction.ENUM_MANAGER.registerEnumCls(MODID,Factions.class);
        EntityPairingPacketType.ENUM_MANAGER.registerEnumCls(MODID,EntityPairingPacketTypes.class);
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
        AnimationManager.addNoWarningModId(EPICSKINS_MODID);
        var event=new AnimationManager.AnimationRegistryEvent();
        Animations.registerAnimations(event);
        yesman.epicfight.epicskins.animation.EpicSkinsAnimations.registerAnimations(event);
        event.getBuilders().stream().sorted(Comparator.comparing(AnimationManager.AnimationBuilder::namespace)).forEach(builder -> builder.task().accept(builder));
        EpicFightRegistries.DEFERRED_REGISTRIES.forEach(registry -> registry.registerAll());
        EpicFightAttributes.EventBus.entityAttributeCreationEvent(new yesman.epicfight.platform.fabric.event.EntityAttributeCreationEvent());
        net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.MODIFY.register(context ->
            EpicFightAttributes.EventBus.entityAttributeModificationEvent(new yesman.epicfight.platform.fabric.event.EntityAttributeModificationEvent(context)));
        RegistryBuilder.bakeAll();
        // The preset registry is populated above; publish it to the manager before weapon types export it.
        yesman.epicfight.api.ex_cap.managers.ItemPresetManager.acceptEvent(
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
        net.fabricmc.fabric.api.loot.v3.LootTableEvents.ALL_LOADED.register((resourceManager, lootTables) -> yesman.epicfight.data.loot.OnSkillBookDroppedByEntity.registerEntitySkillLootTable());
        net.fabricmc.fabric.api.loot.v3.LootTableEvents.MODIFY_DROPS.register(yesman.epicfight.data.loot.OnSkillBookDroppedByEntity::modifyDrops);
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
