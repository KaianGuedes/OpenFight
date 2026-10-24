package dev.war.openfight.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.war.openfight.api.client.camera.EpicFightCameraAPI;
import dev.war.openfight.api.event.EpicFightEventHooks;
import dev.war.openfight.api.event.impl.VanillaEntityEventHooks;
import dev.war.openfight.api.event.types.entity.EntityRemovedEvent;
import dev.war.openfight.api.utils.AttackResult;
import dev.war.openfight.network.EpicFightNetworkManager;
import dev.war.openfight.network.server.SPAbsorption;
import dev.war.openfight.registry.entries.EpicFightAttributes;
import dev.war.openfight.world.capabilities.EpicFightCapabilities;
import dev.war.openfight.world.capabilities.entitypatch.HurtableEntityPatch;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;
import dev.war.openfight.world.damagesource.EpicFightDamageSource;

@Mixin(value = LivingEntity.class)
public abstract class MixinLivingEntity {
	@Shadow
	protected abstract void hurtArmor(DamageSource damageSource, float amount);

    @Inject(at = @At("HEAD"), method = "knockback(DDDLnet/minecraft/world/damagesource/DamageSource;FZ)V", cancellable = true)
    private void epicfight$cancelPatchKnockback(double power, double x, double z, DamageSource source, float damage,
                                                  boolean comesFromEffect, CallbackInfo ci) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (!self.level().isClientSide() && VanillaEntityEventHooks.onKnockedBack(self)) ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "dropAllDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V")
    private void epicfight$beginDropCapture(net.minecraft.server.level.ServerLevel level, DamageSource source, CallbackInfo ci) {
        VanillaEntityEventHooks.beginDeathDropCapture();
    }

    @Inject(at = @At("TAIL"), method = "dropAllDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V")
    private void epicfight$finishDropCapture(net.minecraft.server.level.ServerLevel level, DamageSource source, CallbackInfo ci) {
        VanillaEntityEventHooks.finishDeathDropCapture((LivingEntity)(Object)this, source);
    }

	@Inject(at = @At(value = "TAIL"), method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V")
	private void epicfight$constructor(EntityType<?> entityType, Level level, CallbackInfo info) {
		LivingEntity self = (LivingEntity)((Object)this);
		
		EpicFightCapabilities.getUnparameterizedEntityPatch(self, HurtableEntityPatch.class).ifPresent(entitypatch -> {
			AttributeSupplier.Builder builder = AttributeSupplier.builder();
			java.util.Set<net.minecraft.resources.ResourceKey<net.minecraft.world.entity.ai.attributes.Attribute>> existingKeys = new java.util.HashSet<> ();
			
			self.getAttributes().supplier.instances.forEach((k, v) -> {
				builder.add(k, v.getBaseValue());
				k.unwrapKey().ifPresent(existingKeys::add);
			});
			
			if (!existingKeys.contains(dev.war.openfight.platform.fabric.compat.AttributeKeys.keyOf(Attributes.ATTACK_DAMAGE))) builder.add(Attributes.ATTACK_DAMAGE);
            if (!existingKeys.contains(dev.war.openfight.platform.fabric.compat.AttributeKeys.keyOf(EpicFightAttributes.WEIGHT))) builder.add(EpicFightAttributes.WEIGHT);
            if (!existingKeys.contains(dev.war.openfight.platform.fabric.compat.AttributeKeys.keyOf(EpicFightAttributes.IMPACT))) builder.add(EpicFightAttributes.IMPACT);
            if (!existingKeys.contains(dev.war.openfight.platform.fabric.compat.AttributeKeys.keyOf(EpicFightAttributes.ARMOR_NEGATION))) builder.add(EpicFightAttributes.ARMOR_NEGATION);
            if (!existingKeys.contains(dev.war.openfight.platform.fabric.compat.AttributeKeys.keyOf(EpicFightAttributes.MAX_STRIKES))) builder.add(EpicFightAttributes.MAX_STRIKES);
            if (!existingKeys.contains(dev.war.openfight.platform.fabric.compat.AttributeKeys.keyOf(EpicFightAttributes.STUN_ARMOR))) builder.add(EpicFightAttributes.STUN_ARMOR);
            if (!existingKeys.contains(dev.war.openfight.platform.fabric.compat.AttributeKeys.keyOf(EpicFightAttributes.ASSASSINATION_RESISTANCE))) builder.add(EpicFightAttributes.ASSASSINATION_RESISTANCE);
            if (!existingKeys.contains(dev.war.openfight.platform.fabric.compat.AttributeKeys.keyOf(EpicFightAttributes.OFFHAND_ARMOR_NEGATION))) builder.add(EpicFightAttributes.OFFHAND_ARMOR_NEGATION);
            if (!existingKeys.contains(dev.war.openfight.platform.fabric.compat.AttributeKeys.keyOf(EpicFightAttributes.OFFHAND_IMPACT))) builder.add(EpicFightAttributes.OFFHAND_IMPACT);
            if (!existingKeys.contains(dev.war.openfight.platform.fabric.compat.AttributeKeys.keyOf(EpicFightAttributes.OFFHAND_MAX_STRIKES))) builder.add(EpicFightAttributes.OFFHAND_MAX_STRIKES);
            if (!existingKeys.contains(dev.war.openfight.platform.fabric.compat.AttributeKeys.keyOf(EpicFightAttributes.OFFHAND_ATTACK_SPEED))) builder.add(EpicFightAttributes.OFFHAND_ATTACK_SPEED);
			
			self.getAttributes().supplier = builder.build();
		});
	}
	
	/** In 26.3 the attacker's method is invoked with the blocking defender as argument (it was reversed in 1.21.1's blockUsingShield). */
	@Inject(at = @At(value = "TAIL"), method = "blockedByItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/damagesource/DamageSource;FZ)V")
	private void epicfight$blockUsingShield(LivingEntity defender, DamageSource damageSource, float damage, boolean fullyBlocked, CallbackInfo info) {
		LivingEntity self = defender;
		LivingEntityPatch<?> opponentEntitypatch = EpicFightCapabilities.getEntityPatch((LivingEntity)((Object)this), LivingEntityPatch.class);
		LivingEntityPatch<?> selfEntitypatch = EpicFightCapabilities.getEntityPatch(self, LivingEntityPatch.class);
		
		if (opponentEntitypatch != null) {
			opponentEntitypatch.setLastAttackResult(AttackResult.blocked(0.0F));
			
			if (selfEntitypatch != null && opponentEntitypatch.getEpicFightDamageSource() != null) {
				opponentEntitypatch.onAttackBlocked(opponentEntitypatch.getEpicFightDamageSource(), selfEntitypatch);
			}
		}
	}
	
	@Inject(at = @At(value = "RETURN"), method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z", cancellable = true)
	private void epicfight$hurt(net.minecraft.server.level.ServerLevel serverLevel, DamageSource damagesource, float amount, CallbackInfoReturnable<Boolean> info) {
		LivingEntity self = (LivingEntity)((Object)this);
		LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(damagesource.getEntity(), LivingEntityPatch.class);
		
		if (entitypatch != null) {
			if (info.getReturnValue()) {
				entitypatch.setLastAttackEntity(self);
			}
		}
	}
	
	@Inject(at = @At(value = "HEAD"), method = "push(Lnet/minecraft/world/entity/Entity;)V", cancellable = true)
	private void epicfight$push(Entity p_20293_, CallbackInfo info) {
		LivingEntity self = (LivingEntity)((Object)this);
		LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(self, LivingEntityPatch.class);
		
		if (entitypatch != null && !entitypatch.canPush(p_20293_)) {
			info.cancel();
		}
	}
	
	@Inject(at = @At(value = "HEAD"), method = "getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F", cancellable = true)
	private void epicfight$getDamageAfterArmorAbsorb(DamageSource source, float amount, CallbackInfoReturnable<Float> info) {
		if (source instanceof EpicFightDamageSource epicFightDamageSource && !source.is(DamageTypeTags.BYPASSES_ARMOR)) {
			this.hurtArmor(source, amount);
			float armorNegationAmount = amount * Math.min(epicFightDamageSource.calculateArmorNegation() * 0.01F , 1.0F);
			float amountElse = amount - armorNegationAmount;
			LivingEntity self = (LivingEntity)((Object)this);
			amountElse = CombatRules.getDamageAfterAbsorb(self, amountElse, source, (float)self.getArmorValue(), (float)self.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
			info.setReturnValue(armorNegationAmount + amountElse);
			info.cancel();
		}
	}
	
	@Inject(at = @At(value = "TAIL"), method = "setAbsorptionAmount(F)V")
	private void epicfight$setAbsorptionAmount(float absorptionAmount, CallbackInfo info) {
		LivingEntity self = (LivingEntity)((Object)this);
		
		if (!self.level().isClientSide()) {
			EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(new SPAbsorption(self.getId(), absorptionAmount), self);
		}
	}
	
	@Inject(at = @At(value = "TAIL"), method = "makePoofParticles()V")
	private void epicfight$makePoofParticles(CallbackInfo info) {
		LivingEntity self = (LivingEntity)((Object)this);
		
		EpicFightCapabilities.getUnparameterizedEntityPatch(self, LivingEntityPatch.class).ifPresent(entitypatch -> {
            // We needed a hook when an entity is removed by death, makePoofParticles provided a perfect place to achieve it
            EpicFightEventHooks.Entity.ON_REMOVED.postWithListener(new EntityRemovedEvent(Entity.RemovalReason.KILLED, entitypatch), entitypatch.getEventListener());
		});
	}

    @WrapOperation(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;getYRot()F"
        ),
        method = "jumpFromGround()V"
    )
    private float epicfight$jumpFromGround(LivingEntity livingEntity, Operation<Float> originalOperation) {
        if (livingEntity instanceof Player player && player.isLocalPlayer()) {
            EpicFightCameraAPI cameraApi = EpicFightCameraAPI.getInstance();
            return cameraApi.isTPSMode() ? cameraApi.getCameraYRot() : livingEntity.getYRot();
        }

        return livingEntity.getYRot();
    }

    @WrapOperation(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;getYRot()F",
            ordinal = 0
        ),
        method = "tick()V"
    )
    private float epicfight$tick(LivingEntity livingEntity, Operation<Float> originalOperation) {
        // returns the basis y rotation as camera in TPS mode
        if (livingEntity instanceof Player player && player.isLocalPlayer()) {
            return EpicFightCameraAPI.getInstance().getYRotForHead(player);
        }

        return livingEntity.getYRot();
    }

    @WrapOperation(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;getYRot()F"
        ),
        method = "tickHeadTurn(F)V"
    )
    protected float epicfight$tickHeadTurn(LivingEntity livingEntity, Operation<Float> original) {
        // returns the basis y rotation as camera in TPS mode
        if (livingEntity instanceof Player player && player.isLocalPlayer()) {
            return EpicFightCameraAPI.getInstance().getYRotForHead(player);
        }

        return livingEntity.getYRot();
    }
}
