package dev.war.openfight.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.war.openfight.api.client.event.impl.VanillaGeneralClientEventHooks;

@Mixin(value = ClientPacketListener.class)
public abstract class MixinClientPacketListener {
	@Unique
	private LocalPlayer epicfight$oldPlayer;

	@Inject(at = @At(value = "HEAD"), method = "handleRespawn(Lnet/minecraft/network/protocol/game/ClientboundRespawnPacket;)V", cancellable = false)
	private void epicfight_handleRespawn(ClientboundRespawnPacket clientboundRespawnPacket, CallbackInfo info) {
        VanillaGeneralClientEventHooks.packet = clientboundRespawnPacket;
        this.epicfight$oldPlayer = Minecraft.getInstance().player;
	}

	/// Equivalent of NeoForge's ClientPlayerNetworkEvent.Clone: fired once the replacement LocalPlayer is fully set up.
	@Inject(at = @At(value = "RETURN"), method = "handleRespawn(Lnet/minecraft/network/protocol/game/ClientboundRespawnPacket;)V", cancellable = false)
	private void epicfight_handleRespawnEnd(ClientboundRespawnPacket clientboundRespawnPacket, CallbackInfo info) {
        LocalPlayer oldPlayer = this.epicfight$oldPlayer;
        LocalPlayer newPlayer = Minecraft.getInstance().player;
        this.epicfight$oldPlayer = null;

        if (oldPlayer != null && newPlayer != null && oldPlayer != newPlayer) {
            VanillaGeneralClientEventHooks.onClonedInClient(oldPlayer, newPlayer);
        }
	}
}
