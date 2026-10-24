package yesman.epicfight.mixin.common;

import net.minecraft.world.entity.monster.Enderman;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.event.impl.VanillaEntityEventHooks;

@Mixin(Enderman.class)
public abstract class MixinEndermanTeleport {
    @Inject(method = "teleport(DDD)Z", at = @At("HEAD"), cancellable = true)
    private void epicfight$guardPatchTeleport(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        Enderman self = (Enderman)(Object)this;
        if (!self.level().isClientSide() && VanillaEntityEventHooks.onEndermanTeleports(self)) {
            cir.setReturnValue(false);
        }
    }
}
