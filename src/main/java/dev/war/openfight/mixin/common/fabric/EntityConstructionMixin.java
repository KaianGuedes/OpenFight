package dev.war.openfight.mixin.common.fabric;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.war.openfight.api.event.impl.VanillaEntityEventHooks;

@Mixin(Entity.class)
public abstract class EntityConstructionMixin {
    @Inject(method="<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V",at=@At("RETURN"))
    private void epicfight$construct(EntityType<?> type,Level level,CallbackInfo info) {
        VanillaEntityEventHooks.onConstruct((Entity)(Object)this);
    }
}
