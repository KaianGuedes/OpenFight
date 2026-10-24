"""Replace the two original NeoForge client subscribers with native Fabric registrations."""
from pathlib import Path
import re
root=Path(__file__).resolve().parents[1]/'src/main/java/dev/war/openfight'
p=root/'client/platform/neoforge/event/ClientModBusEvent.java';s=p.read_text(encoding='utf-8')
body=s[s.index('    \tevent.registerSpriteSet'):s.index('\n    }',s.index('    \tevent.registerSpriteSet'))]
body=body.replace('event.registerSpriteSet','particles.register').replace('event.registerSpecial','particles.register')
render=s[s.index('\t\tevent.registerEntityRenderer'):s.index('\n\t}',s.index('\t\tevent.registerEntityRenderer'))]
render=render.replace('event.registerEntityRenderer','EntityRendererRegistry.register').replace('event.registerBlockEntityRenderer','BlockEntityRendererRegistry.register')
imports='''package dev.war.openfight.client.platform.neoforge.event;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.renderer.entity.NoopRenderer;
import dev.war.openfight.client.particle.*;
import dev.war.openfight.client.renderer.entity.*;
import dev.war.openfight.client.renderer.blockentity.FractureBlockRenderer;
import dev.war.openfight.registry.entries.*;
import dev.war.openfight.client.ClientEngine;
public final class ClientModBusEvent {
 private ClientModBusEvent() {}
 public static void register() {
  var particles=ParticleProviderRegistry.getInstance();
'''
p.write_text(imports+body+'\n'+render+'''
  ClientLifecycleEvents.CLIENT_STARTED.register(client -> dev.war.openfight.client.renderer.patched.item.RenderItemBase.initItemRenderers(client));
 }
 public static void reloadLayers() {
  dev.war.openfight.client.renderer.patched.layer.WearableItemLayer.clearModels();
  var subjects=dev.war.openfight.api.client.model.SoftBodyTranslatable.TRACKING_SIMULATION_SUBJECTS;
  subjects.removeIf(dev.war.openfight.api.client.physics.cloth.ClothSimulatable::invalid);
  for(var owner:subjects) owner.getClothSimulator().getAllRunningObjects().forEach(entry->owner.getClothSimulator().restart(entry.getKey()));
 }
}
''',encoding='utf-8')
(root/'client/platform/neoforge/event/ClientEvents.java').write_text('''package dev.war.openfight.client.platform.neoforge.event;
import net.fabricmc.fabric.api.client.screen.v1.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionResult;
import dev.war.openfight.api.client.event.impl.VanillaGUIEventHooks;
import dev.war.openfight.api.client.event.impl.VanillaGeneralClientEventHooks;
public final class ClientEvents {
 private ClientEvents() {}
 public static void register() {
  ScreenEvents.AFTER_INIT.register((client,screen,width,height)-> {
   ScreenMouseEvents.allowMouseClick(screen).register((s,event)->!VanillaGUIEventHooks.onMouseButtonPressedInScreen(s));
   ScreenMouseEvents.allowMouseRelease(screen).register((s,event)->!VanillaGUIEventHooks.onMouseButtonReleasedInScreen(s));
   ScreenKeyboardEvents.allowKeyPress(screen).register((s,event)->!VanillaGUIEventHooks.onKeyboardPressedInScreen(s,event.key()));
  });
  UseItemCallback.EVENT.register((player,level,hand)->VanillaGeneralClientEventHooks.onUseItemInClientSide(player,player.getItemInHand(hand),hand)?InteractionResult.FAIL:InteractionResult.PASS);
  ClientPlayConnectionEvents.JOIN.register((handler,sender,client)->VanillaGeneralClientEventHooks.onPlayerLoggedIn(client.player));
  ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->{if(client.player!=null)VanillaGeneralClientEventHooks.onPlayerLoggedOut(client.player);});
 }
}
''',encoding='utf-8')
p=root/'client/EpicFightFabricClient.java';s=p.read_text(encoding='utf-8').replace('        // Render-state extraction/submission and GUI/input engine callbacks remain in the compile backlog.','''        dev.war.openfight.client.platform.neoforge.event.ClientModBusEvent.register();
        dev.war.openfight.client.platform.neoforge.event.ClientEvents.register();
        dev.war.openfight.client.events.engine.ControlEngine.registerFabricEvents();
        dev.war.openfight.client.events.engine.RenderEngine.registerFabricEvents();''');p.write_text(s,encoding='utf-8')
print('Client Fabric registrations applied')
