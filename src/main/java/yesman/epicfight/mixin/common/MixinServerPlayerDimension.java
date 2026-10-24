package yesman.epicfight.mixin.common;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.event.impl.VanillaPlayerEventHooks;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayerDimension {
    @Unique private ServerLevel epicfight$oldLevel;

    @Inject(method = "teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/server/level/ServerPlayer;", at = @At("HEAD"))
    private void epicfight$beforeDimensionChange(TeleportTransition transition, CallbackInfoReturnable<ServerPlayer> cir) {
        this.epicfight$oldLevel = ((ServerPlayer)(Object)this).level();
    }

    @Inject(method = "teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/server/level/ServerPlayer;", at = @At("TAIL"))
    private void epicfight$afterDimensionChange(TeleportTransition transition, CallbackInfoReturnable<ServerPlayer> cir) {
        ServerPlayer self = (ServerPlayer)(Object)this;
        if (cir.getReturnValue() != null && this.epicfight$oldLevel != self.level()) {
            VanillaPlayerEventHooks.onChagneDimension(self);
        }
        this.epicfight$oldLevel = null;
    }
}
