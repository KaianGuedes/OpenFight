package dev.war.openfight.registry.entries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import dev.war.openfight.platform.fabric.registry.DeferredHolder;
import dev.war.openfight.platform.fabric.registry.DeferredRegister;
import dev.war.openfight.main.EpicFightMod;
import dev.war.openfight.world.effect.VisibleMobEffect;

public final class EpicFightMobEffects {
	private EpicFightMobEffects() {}
	
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(Registries.MOB_EFFECT, EpicFightMod.NAMESPACE);
	
	public static final DeferredHolder<MobEffect, VisibleMobEffect> STUN_IMMUNITY = REGISTRY.register("stun_immunity", () -> 
		new VisibleMobEffect(
                MobEffectCategory.BENEFICIAL
                , 16758016
                , EpicFightMod.identifier("textures/mob_effect/stun_immunity.png")
        )
	);
	
	//public static final RegistryObject<MobEffect> BLOOMING = EFFECTS.register("blooming", () -> 
	//	new VisibleMobEffect(MobEffectCategory.BENEFICIAL, 16735744, new Identifier(EpicFightMod.NAMESPACE, "textures/mob_effect/blooming.png")));
	
	public static final DeferredHolder<MobEffect, VisibleMobEffect> INSTABILITY = REGISTRY.register("instability", () -> 
		new VisibleMobEffect(
                MobEffectCategory.HARMFUL
                , 0
                , (effectInstance) ->
                Math.min(effectInstance.getAmplifier(), 2)
                , EpicFightMod.identifier("textures/mob_effect/instability1.png")
                , EpicFightMod.identifier("textures/mob_effect/instability2.png")
                , EpicFightMod.identifier("textures/mob_effect/instability3.png")
        )
	);
	
	public static void addOffhandModifier() {
		MobEffects.HASTE.value().addAttributeModifier(EpicFightAttributes.OFFHAND_ATTACK_SPEED, EpicFightMod.identifier("offhand_dig_modifier"), 0.1D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
		MobEffects.MINING_FATIGUE.value().addAttributeModifier(EpicFightAttributes.OFFHAND_ATTACK_SPEED, EpicFightMod.identifier("offhand_dig_modifier"), -0.1D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
	}
}