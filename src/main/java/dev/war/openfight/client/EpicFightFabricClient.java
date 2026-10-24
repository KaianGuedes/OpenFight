package dev.war.openfight.client;

import java.io.IOException;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.server.packs.PackType;
import dev.war.openfight.EpicFightClient;
import dev.war.openfight.EpicFight;
import dev.war.openfight.api.animation.AnimationManager;
import dev.war.openfight.api.client.animation.property.JointMaskReloadListener;
import dev.war.openfight.api.client.model.ItemSkinsReloadListener;
import dev.war.openfight.api.client.model.Meshes;
import dev.war.openfight.api.client.input.action.*;
import dev.war.openfight.config.ClientConfig;
import dev.war.openfight.gameasset.Armatures;
import dev.war.openfight.client.input.EpicFightKeyMappings;
import dev.war.openfight.main.EpicFightMod;
import dev.war.openfight.platform.fabric.client.FabricClientModPlatform;
import dev.war.openfight.platform.fabric.network.PayloadRegistrar;
import dev.war.openfight.api.event.impl.VanillaEntityEventHooks;
import dev.war.openfight.world.capabilities.EpicFightCapabilities;
import dev.war.openfight.world.capabilities.entitypatch.EntityPatch;

public final class EpicFightFabricClient implements ClientModInitializer {
    @Override public void onInitializeClient() {
        PortClientSmoke.register();
        dev.war.openfight.world.capabilities.provider.CommonEntityPatchProvider.ClientModule.registerClientPlayerPatches();
        EpicFightClient.initialize(new FabricClientModPlatform());
        InputAction.ENUM_MANAGER.registerEnumCls(EpicFightMod.NAMESPACE,EpicFightInputAction.class);
        InputAction.ENUM_MANAGER.registerEnumCls("minecraft",MinecraftInputAction.class);
        InputAction.ENUM_MANAGER.loadEnum();
        try { ClientConfig.SPEC.load(FabricLoader.getInstance().getConfigDir().resolve("epicfight-client.json")); }
        catch (IOException e) { throw new java.io.UncheckedIOException(e); }
        ClientConfig.reload();
        EpicFightKeyMappings.registerKeys();
        dev.war.openfight.client.renderer.patched.item.EpicFightItemProperties.registerItemProperties();
        PayloadRegistrar.initializeClientReceivers();
        ClientEntityEvents.ENTITY_LOAD.register((entity,level)-> {
            VanillaEntityEventHooks.onJoinLevel(entity,level,false);
            EpicFightCapabilities.getUnparameterizedEntityPatch(entity,EntityPatch.class).ifPresent(EntityPatch::onAddedToLevel);
        });
        var reload=ResourceLoader.get(PackType.CLIENT_RESOURCES);
        reload.registerReloadListener(EpicFightMod.identifier("meshes"),Meshes.INSTANCE);
        reload.registerReloadListener(EpicFightMod.identifier("animations"),AnimationManager.getInstance());
        reload.registerReloadListener(EpicFightMod.identifier("joint_masks"),new JointMaskReloadListener());
        reload.registerReloadListener(EpicFightMod.identifier("item_skins"),ItemSkinsReloadListener.INSTANCE);
        // AnimationManager.apply reloads Armatures itself before resolving animation joint data.
        reload.addListenerOrdering(EpicFightMod.identifier("joint_masks"),EpicFightMod.identifier("animations"));
        reload.addListenerOrdering(EpicFightMod.identifier("meshes"),EpicFightMod.identifier("item_skins"));
        ResourceLoader.registerBuiltinPack(EpicFightMod.identifier("epicfight_legacy"),FabricLoader.getInstance().getModContainer(EpicFight.MOD_ID).orElseThrow(),PackActivationType.NORMAL);
        dev.war.openfight.client.platform.neoforge.event.ClientModBusEvent.register();
        dev.war.openfight.client.platform.neoforge.event.ClientEvents.register();
        dev.war.openfight.client.events.engine.ControlEngine.getInstance().registerFabricCallbacks();
        dev.war.openfight.client.events.engine.RenderEngine.getInstance().registerFabricCallbacks();
    }
}
