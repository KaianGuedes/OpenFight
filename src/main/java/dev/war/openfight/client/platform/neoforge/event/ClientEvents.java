package dev.war.openfight.client.platform.neoforge.event;
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
