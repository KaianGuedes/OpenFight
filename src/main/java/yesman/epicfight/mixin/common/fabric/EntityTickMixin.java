package yesman.epicfight.mixin.common.fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import yesman.epicfight.api.event.impl.VanillaEntityEventHooks;

/** Wrap each actual tick, including passenger ticks, after vanilla commonTick bookkeeping. */
@Mixin(ServerLevel.class)
public abstract class EntityTickMixin {
    @WrapOperation(method="tickNonPassenger(Lnet/minecraft/world/entity/Entity;)V",
        at=@At(value="INVOKE",target="Lnet/minecraft/world/entity/Entity;tick()V"))
    private void epicfight$tick(Entity entity,Operation<Void> original) {
        VanillaEntityEventHooks.preTick(entity);
        original.call(entity);
        VanillaEntityEventHooks.postTick(entity);
    }
    @WrapOperation(method="tickPassenger(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)V",
        at=@At(value="INVOKE",target="Lnet/minecraft/world/entity/Entity;rideTick()V"))
    private void epicfight$rideTick(Entity entity,Operation<Void> original) {
        VanillaEntityEventHooks.preTick(entity);
        original.call(entity);
        VanillaEntityEventHooks.postTick(entity);
    }
}
