package dev.war.openfight.registry.entries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import dev.war.openfight.platform.fabric.event.EntityAttributeCreationEvent;
import dev.war.openfight.platform.fabric.event.EntityAttributeModificationEvent;
import dev.war.openfight.platform.fabric.registry.DeferredHolder;
import dev.war.openfight.platform.fabric.registry.DeferredRegister;
import dev.war.openfight.EpicFight;
import dev.war.openfight.main.EpicFightMod;
import dev.war.openfight.world.capabilities.entitypatch.boss.WitherGhostPatch;
import dev.war.openfight.world.capabilities.entitypatch.boss.WitherPatch;
import dev.war.openfight.world.capabilities.entitypatch.boss.enderdragon.EnderDragonPatch;
import dev.war.openfight.world.capabilities.entitypatch.mob.CreeperPatch;
import dev.war.openfight.world.capabilities.entitypatch.mob.EndermanPatch;
import dev.war.openfight.world.capabilities.entitypatch.mob.HoglinPatch;
import dev.war.openfight.world.capabilities.entitypatch.mob.IronGolemPatch;
import dev.war.openfight.world.capabilities.entitypatch.mob.PiglinBrutePatch;
import dev.war.openfight.world.capabilities.entitypatch.mob.PiglinPatch;
import dev.war.openfight.world.capabilities.entitypatch.mob.RavagerPatch;
import dev.war.openfight.world.capabilities.entitypatch.mob.VindicatorPatch;
import dev.war.openfight.world.capabilities.entitypatch.mob.WitherSkeletonPatch;
import dev.war.openfight.world.capabilities.entitypatch.mob.ZoglinPatch;
import dev.war.openfight.world.capabilities.entitypatch.mob.ZombiePatch;
import dev.war.openfight.world.entity.WitherGhostClone;

public final class EpicFightAttributes {
	private EpicFightAttributes() {}
	
	static {
		Attributes.ATTACK_DAMAGE.value().setSyncable(true);
	}
	
	public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(Registries.ATTRIBUTE, EpicFightMod.NAMESPACE);
	
    public static final DeferredHolder<Attribute, Attribute> MAX_STAMINA = REGISTRY.register("stamina", () -> new RangedAttribute("attribute.name." + EpicFightMod.NAMESPACE + ".stamina", 15.0D, 0.0D, 1024.0D).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> STAMINA_REGEN = REGISTRY.register("stamina_regen", () -> new RangedAttribute("attribute.name." + EpicFightMod.NAMESPACE + ".stamina_regen", 1.0D, 0.0D, 30.0D).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> STUN_ARMOR = REGISTRY.register("stun_armor", () -> new RangedAttribute("attribute.name." + EpicFightMod.NAMESPACE + ".stun_armor", 0.0D, 0.0D, 1024.0D).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> WEIGHT = REGISTRY.register("weight", () -> new RangedAttribute("attribute.name." + EpicFightMod.NAMESPACE + ".weight", 0.0D, 0.0D, 1024.0).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> MAX_STRIKES = REGISTRY.register("max_strikes", () -> new RangedAttribute("attribute.name." + EpicFightMod.NAMESPACE + ".max_strikes", 1.0D, 1.0D, 1024.0).setSyncable(true));
	public static final DeferredHolder<Attribute, Attribute> ARMOR_NEGATION = REGISTRY.register("armor_negation", () -> new RangedAttribute("attribute.name." + EpicFightMod.NAMESPACE + ".armor_negation", 0.0D, 0.0D, 100.0D).setSyncable(true));
	public static final DeferredHolder<Attribute, Attribute> IMPACT = REGISTRY.register("impact", () -> new RangedAttribute("attribute.name." + EpicFightMod.NAMESPACE + ".impact", 0.5D, 0.0D, 1024.0).setSyncable(true));
	public static final DeferredHolder<Attribute, Attribute> ASSASSINATION_RESISTANCE = REGISTRY.register("execution_resistance", () -> new RangedAttribute("attribute.name." + EpicFightMod.NAMESPACE + ".execution_resistance", 0.0D, 0.0D, 10.0D).setSyncable(true));
	
	public static final DeferredHolder<Attribute, Attribute> OFFHAND_ATTACK_SPEED = REGISTRY.register("offhand_attack_speed", () -> new RangedAttribute("attribute.name." + EpicFightMod.NAMESPACE + ".offhand_attack_speed", 4.0D, 0.0D, 1024.0D).setSyncable(true));
	public static final DeferredHolder<Attribute, Attribute> OFFHAND_MAX_STRIKES = REGISTRY.register("offhand_max_strikes", () -> new RangedAttribute("attribute.name." + EpicFightMod.NAMESPACE + ".offhand_max_strikes", 1.0D, 1.0D, 1024.0).setSyncable(true));
	public static final DeferredHolder<Attribute, Attribute> OFFHAND_ARMOR_NEGATION = REGISTRY.register("offhand_armor_negation", () -> new RangedAttribute("attribute.name." + EpicFightMod.NAMESPACE + ".offhand_armor_negation", 0.0D, 0.0D, 100.0D).setSyncable(true));
	public static final DeferredHolder<Attribute, Attribute> OFFHAND_IMPACT = REGISTRY.register("offhand_impact", () -> new RangedAttribute("attribute.name." + EpicFightMod.NAMESPACE + ".offhand_impact", 0.5D, 0.0D, 1024.0).setSyncable(true));
	
	// Modifier ids for epicfight attribute
	public static final Identifier ARMOR_NEGATION_MODIFIER = EpicFightMod.identifier("armor_negation");
	public static final Identifier MAX_STRIKE_MODIFIER = EpicFight.identifier("max_strikes");
	public static final Identifier IMPACT_MODIFIER = EpicFight.identifier("impact");
	public static final Identifier ATTACK_DAMAGE_MODIFIER = EpicFightMod.identifier("attack_damage");
	public static final Identifier ATTACK_SPEED_MODIFIER = EpicFightMod.identifier("attack_speed");
    
	public static AttributeModifier getArmorNegationModifier(double value) {
		return new AttributeModifier(ARMOR_NEGATION_MODIFIER, value, AttributeModifier.Operation.ADD_VALUE);
	}

	public static AttributeModifier getMaxStrikesModifier(int value) {
		return new AttributeModifier(MAX_STRIKE_MODIFIER, value, AttributeModifier.Operation.ADD_VALUE);
	}

	public static AttributeModifier getImpactModifier(double value) {
		return new AttributeModifier(IMPACT_MODIFIER, value, AttributeModifier.Operation.ADD_VALUE);
	}

	public static AttributeModifier getDamageBonusModifier(double value) {
		return new AttributeModifier(ATTACK_DAMAGE_MODIFIER, value, AttributeModifier.Operation.ADD_VALUE);
	}

	public static AttributeModifier getSpeedBonusModifier(double value) {
		return new AttributeModifier(ATTACK_SPEED_MODIFIER, value, AttributeModifier.Operation.ADD_VALUE);
	}
	
	public static final class EventBus {
		private EventBus() {}
		
		
		public static void entityAttributeCreationEvent(EntityAttributeCreationEvent event) {
			event.put(EpicFightEntityTypes.WITHER_SKELETON_MINION.get(), AbstractSkeleton.createAttributes().build());
			event.put(EpicFightEntityTypes.WITHER_GHOST_CLONE.get(), WitherGhostClone.createAttributes().build());
			event.put(EpicFightEntityTypes.DODGE_LOCATION_INDICATOR.get(), LivingEntity.createLivingAttributes().build());
		}
		
		
		public static void entityAttributeModificationEvent(EntityAttributeModificationEvent event) {
			common(EntityTypes.CAVE_SPIDER, event);
			common(EntityTypes.EVOKER, event);
			common(EntityTypes.IRON_GOLEM, event);
			humanoid(EntityTypes.PILLAGER, event);
			common(EntityTypes.RAVAGER, event);
			common(EntityTypes.SPIDER, event);
			common(EntityTypes.VEX, event);
			humanoid(EntityTypes.VINDICATOR, event);
			humanoid(EntityTypes.WITCH, event);
			common(EntityTypes.HOGLIN, event);
			common(EntityTypes.ZOGLIN, event);
			common(EntityTypes.ENDER_DRAGON, event);
			common(EntityTypes.CREEPER, event);
			humanoid(EntityTypes.DROWNED, event);
			common(EntityTypes.ENDERMAN, event);
			humanoid(EntityTypes.HUSK, event);
			humanoid(EntityTypes.PIGLIN, event);
			humanoid(EntityTypes.PIGLIN_BRUTE, event);
			humanoid(EntityTypes.SKELETON, event);
			humanoid(EntityTypes.STRAY, event);
			humanoid(EntityTypes.WITHER_SKELETON, event);
			humanoid(EntityTypes.ZOMBIE, event);
			humanoid(EntityTypes.ZOMBIE_VILLAGER, event);
			humanoid(EntityTypes.ZOMBIFIED_PIGLIN, event);
			common(EpicFightEntityTypes.WITHER_SKELETON_MINION.get(), event);
			player(EntityTypes.PLAYER, event);
			dragon(EntityTypes.ENDER_DRAGON, event);
			common(EntityTypes.WITHER, event);
			
			CreeperPatch.initAttributes(event);
			EnderDragonPatch.initAttributes(event);
			EndermanPatch.initAttributes(event);
			HoglinPatch.initAttributes(event);
			IronGolemPatch.initAttributes(event);
			PiglinBrutePatch.initAttributes(event);
			PiglinPatch.initAttributes(event);
			RavagerPatch.initAttributes(event);
			VindicatorPatch.initAttributes(event);
			WitherPatch.initAttributes(event);
			WitherGhostPatch.initAttributes(event);
			WitherSkeletonPatch.initAttributes(event);
			ZoglinPatch.initAttributes(event);
			ZombiePatch.initAttributes(event);
		}
	    
	    private static void common(EntityType<? extends LivingEntity> entityType, EntityAttributeModificationEvent event) {
			event.add(entityType, EpicFightAttributes.WEIGHT);
			event.add(entityType, EpicFightAttributes.ARMOR_NEGATION);
			event.add(entityType, EpicFightAttributes.IMPACT);
			event.add(entityType, EpicFightAttributes.MAX_STRIKES);
			event.add(entityType, EpicFightAttributes.STUN_ARMOR);
		}
	    
	    private static void humanoid(EntityType<? extends LivingEntity> entityType, EntityAttributeModificationEvent event) {
	    	common(entityType, event);
			event.add(entityType, EpicFightAttributes.OFFHAND_ATTACK_SPEED);
			event.add(entityType, EpicFightAttributes.OFFHAND_MAX_STRIKES);
			event.add(entityType, EpicFightAttributes.OFFHAND_ARMOR_NEGATION);
			event.add(entityType, EpicFightAttributes.OFFHAND_IMPACT);
		}
	    
	    private static void player(EntityType<? extends LivingEntity> entityType, EntityAttributeModificationEvent event) {
	    	humanoid(entityType, event);
			event.add(entityType, EpicFightAttributes.MAX_STAMINA);
			event.add(entityType, EpicFightAttributes.STAMINA_REGEN);
		}
	    
	    private static void dragon(EntityType<? extends EnderDragon> entityType, EntityAttributeModificationEvent event) {
	    	common(entityType, event);
			event.add(entityType, Attributes.ATTACK_DAMAGE);
		}
	}
}