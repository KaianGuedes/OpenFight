package yesman.epicfight.client;

import java.io.IOException;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.server.packs.PackType;
import yesman.epicfight.EpicFightClient;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.client.animation.property.JointMaskReloadListener;
import yesman.epicfight.api.client.model.ItemSkinsReloadListener;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.input.action.*;
import yesman.epicfight.config.ClientConfig;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.platform.fabric.client.FabricClientModPlatform;
import yesman.epicfight.platform.fabric.network.PayloadRegistrar;
import yesman.epicfight.api.event.impl.VanillaEntityEventHooks;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

public final class EpicFightFabricClient implements ClientModInitializer {
    @Override public void onInitializeClient() {
        PortClientSmoke.register();
        yesman.epicfight.world.capabilities.provider.CommonEntityPatchProvider.ClientModule.registerClientPlayerPatches();
        EpicFightClient.initialize(new FabricClientModPlatform());
        InputAction.ENUM_MANAGER.registerEnumCls(EpicFightMod.MODID,EpicFightInputAction.class);
        InputAction.ENUM_MANAGER.registerEnumCls("minecraft",MinecraftInputAction.class);
        InputAction.ENUM_MANAGER.loadEnum();
        try { ClientConfig.SPEC.load(FabricLoader.getInstance().getConfigDir().resolve("epicfight-client.json")); }
        catch (IOException e) { throw new java.io.UncheckedIOException(e); }
        ClientConfig.reload();
        EpicFightKeyMappings.registerKeys();
        yesman.epicfight.client.renderer.patched.item.EpicFightItemProperties.registerItemProperties();
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
        ResourceLoader.registerBuiltinPack(EpicFightMod.identifier("epicfight_legacy"),FabricLoader.getInstance().getModContainer("epicfight").orElseThrow(),PackActivationType.NORMAL);
        yesman.epicfight.client.platform.neoforge.event.ClientModBusEvent.register();
        yesman.epicfight.client.platform.neoforge.event.ClientEvents.register();
        yesman.epicfight.client.events.engine.ControlEngine.getInstance().registerFabricCallbacks();
        yesman.epicfight.client.events.engine.RenderEngine.getInstance().registerFabricCallbacks();
    }
}
