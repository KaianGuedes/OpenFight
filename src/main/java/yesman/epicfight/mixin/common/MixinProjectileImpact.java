package yesman.epicfight.mixin.common;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.event.impl.VanillaEntityEventHooks;

@Mixin(Projectile.class)
public abstract class MixinProjectileImpact {
    @Inject(method = "hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;",
        at = @At("HEAD"), cancellable = true)
    private void epicfight$projectileImpact(HitResult hitResult, CallbackInfoReturnable<ProjectileDeflection> cir) {
        Projectile self = (Projectile)(Object)this;
        if (!self.level().isClientSide() && VanillaEntityEventHooks.onProjectileImpacts(hitResult, self)) {
            cir.setReturnValue(ProjectileDeflection.NONE);
        }
    }
}
