package dev.war.openfight.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import dev.war.openfight.api.client.camera.EpicFightCameraAPI;
import dev.war.openfight.world.capabilities.EpicFightCapabilities;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = Player.class)
public abstract class MixinPlayer {
	@WrapOperation(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/damagesource/CombatTracker;recordDamage(Lnet/minecraft/world/damagesource/DamageSource;F)V"
		),
		method = "actuallyHurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)V"
	)
	private void epicfight$recordDamage(CombatTracker self, DamageSource damagesource, float damage, Operation<Void> operation) {
		EpicFightCapabilities.getUnparameterizedEntityPatch(damagesource.getEntity(), LivingEntityPatch.class).ifPresent(entitypatch -> {
			entitypatch.setLastAttackEntity(self.mob);
		});
		
		operation.call(self, damagesource, damage);
	}

    @WrapOperation(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;getYRot()F"
        ),
        method = "aiStep()V"
    )
    private float epicfight$serverAiStep(Player player, Operation<Float> originalOperation) {
        if (player.isLocalPlayer()) {
            return EpicFightCameraAPI.getInstance().getYRotForHead(player);
        }

        return player.getYRot();
    }
}