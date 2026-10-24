from pathlib import Path
import re
root=Path(__file__).resolve().parents[1]
src=root/'src/main/java/dev/war/openfight'

# Only the optional integrations and data generators are omitted; gameplay/render source is retained.
p=src/'compat/MinecraftMod.java'
s=p.read_text(encoding='utf-8')
s=re.sub(r'import yesman\.epicfight\.compat\.[a-z][^;]*;\n','',s)
s=s.replace('import net.neoforged.fml.ModList;','import net.fabricmc.loader.api.FabricLoader;')
s=re.sub(r'([A-Z_]+\("[^"]+", (?:true|false)), \w+\.class\)',r'\1, null)',s)
s=s.replace('return ModList.get().getModFileById(modId).versionString();','return FabricLoader.getInstance().getModContainer(modId).map(mod -> mod.getMetadata().getVersion().getFriendlyString()).orElse("0.0.0");')
s=s.replace('public @NotNull Class<? extends ICompatModule> getCompatibilityModule()', 'public @Nullable Class<? extends ICompatModule> getCompatibilityModule()')
s=s.replace('private final @NotNull Class<? extends ICompatModule> compatibilityModule','private final @Nullable Class<? extends ICompatModule> compatibilityModule')
p.write_text(s,encoding='utf-8')
(src/'compat/ICompatModule.java').write_text('package dev.war.openfight.compat;\n\n/** Optional integrations are disabled for this personal Fabric port. */\npublic interface ICompatModule {}\n',encoding='utf-8')

for name in ['EpicFightInputAction','MinecraftInputAction']:
    p=src/f'api/client/input/action/{name}.java'
    s=p.read_text(encoding='utf-8').replace('import dev.war.openfight.compat.controlify.EpicFightControlifyControllerMod;\n','')
    s=re.sub(r'public (?:@NotNull )?Optional<@NotNull ControllerBinding> controllerBinding\(\) \{.*?return Optional\.of\(EpicFightControlifyControllerMod.getBinding\(this\)\);\s*\}', '''public Optional<@NotNull ControllerBinding> controllerBinding() {
        return Optional.empty(); // Controlify integration is omitted; keyboard and mouse remain available.
    }''',s,flags=re.S)
    p.write_text(s,encoding='utf-8')

for name in ['CommonConfig','ClientConfig','ServerConfig']:
    p=src/f'config/{name}.java'
    s=p.read_text(encoding='utf-8')
    s=re.sub(r'import net\.neoforged\.[^;]+;\n','',s)
    s=re.sub(r'@EventBusSubscriber\([^\n]*\)\n','',s)
    s=re.sub(r'\s*@SubscribeEvent\s+(?:static|public static) void (?:onLoad|epicfight\$modConfigLoading)\(final ModConfigEvent.Loading event\) \{\s*if \(event.getConfig\(\).getType\(\) != ModConfig.Type.\w+\) \{\s*return;\s*\}', '\n    public static void reload() {',s)
    p.write_text(s,encoding='utf-8')

p=src/'main/EpicFightMod.java'
s=p.read_text(encoding='utf-8')
a=s.index('    public EpicFightMod(IEventBus')
# Preserve all upstream public utility methods preceding the legacy constructor.
methods=s[s.index('\t/// @deprecated Use'):a]
header='''package dev.war.openfight.main;

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
'''
body='''    @Override public void onInitialize() {
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
        EpicFightRegistries.DEFERRED_REGISTRIES.forEach(registry -> registry.registerAll());
        RegistryBuilder.bakeAll();
        Armatures.registerEntityTypes();
        AnimationManager.addNoWarningModId(EPICSKINS_MODID);
        var event=new AnimationManager.AnimationRegistryEvent();
        Animations.registerAnimations(event);
        event.getBuilders().stream().sorted(Comparator.comparing(AnimationManager.AnimationBuilder::namespace)).forEach(builder -> builder.task().accept(builder));
        EpicFightCommandArgumentTypes.registerArgumentTypes();
        EpicFightPotions.addRecipes();
        EpicFightCapabilities.ITEM_CAPABILITY_PROVIDER.registerWeaponTypesByClass();
        EpicFightCapabilities.ENTITY_PATCH_PROVIDER.registerVanillaEntityPatches();
        EpicFightGameRules.registerGameRules();
        WeaponTypeReloadListener.registerDefaultWeaponTypes();
        EpicFightMobEffects.addOffhandModifier();
        EpicFightExtensibleEnums.initExtensibleEnums();
        EpicFightEventHooks.Registry.SKILLBOOK_LOOT_TABLE.registerEvent(EpicFightLootTables::createSkillLootTable);
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
'''
p.write_text(header+methods+body,encoding='utf-8')
print('Migrated common/config entrypoints; optional integration declarations disabled')
