package dev.war.openfight.gameasset;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import dev.war.openfight.api.animation.Joint;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.asset.JsonAssetLoader;
import dev.war.openfight.api.model.Armature;
import dev.war.openfight.main.EpicFightMod;
import dev.war.openfight.model.armature.CreeperArmature;
import dev.war.openfight.model.armature.DragonArmature;
import dev.war.openfight.model.armature.EndermanArmature;
import dev.war.openfight.model.armature.HoglinArmature;
import dev.war.openfight.model.armature.HumanoidArmature;
import dev.war.openfight.model.armature.IronGolemArmature;
import dev.war.openfight.model.armature.PiglinArmature;
import dev.war.openfight.model.armature.RavagerArmature;
import dev.war.openfight.model.armature.SpiderArmature;
import dev.war.openfight.model.armature.VexArmature;
import dev.war.openfight.model.armature.WitherArmature;
import dev.war.openfight.registry.entries.EpicFightEntityTypes;
import dev.war.openfight.world.capabilities.entitypatch.EntityPatch;

public class Armatures {
	public static final Armatures INSTANCE = new Armatures();
	private static ResourceManager resourceManager = null;
	
	@FunctionalInterface
	public interface ArmatureContructor<T extends Armature> {
		T invoke(String name, int jointNumber, Joint joint, Map<String, Joint> jointMap);
	}
	
	private static final Map<Identifier, ArmatureAccessor<? extends Armature>> ACCESSORS = new HashMap<> ();
	private static final Map<ArmatureAccessor<? extends Armature>, Armature> ARMATURES = new HashMap<> ();
	private static final Map<EntityType<?>, AssetAccessor<? extends Armature>> ENTITY_TYPE_ARMATURE_MAPPER = new HashMap<> ();
	
	public static final ArmatureAccessor<HumanoidArmature> BIPED = ArmatureAccessor.create(EpicFightMod.NAMESPACE, "entity/biped", HumanoidArmature::new);
	public static final ArmatureAccessor<CreeperArmature> CREEPER = ArmatureAccessor.create(EpicFightMod.NAMESPACE, "entity/creeper", CreeperArmature::new);
	public static final ArmatureAccessor<EndermanArmature> ENDERMAN = ArmatureAccessor.create(EpicFightMod.NAMESPACE, "entity/enderman", EndermanArmature::new);
	public static final ArmatureAccessor<HumanoidArmature> SKELETON = ArmatureAccessor.create(EpicFightMod.NAMESPACE, "entity/skeleton", HumanoidArmature::new);
	public static final ArmatureAccessor<SpiderArmature> SPIDER = ArmatureAccessor.create(EpicFightMod.NAMESPACE, "entity/spider", SpiderArmature::new);
	public static final ArmatureAccessor<IronGolemArmature> IRON_GOLEM = ArmatureAccessor.create(EpicFightMod.NAMESPACE, "entity/iron_golem", IronGolemArmature::new);
	public static final ArmatureAccessor<RavagerArmature> RAVAGER = ArmatureAccessor.create(EpicFightMod.NAMESPACE, "entity/ravager", RavagerArmature::new);
	public static final ArmatureAccessor<VexArmature> VEX = ArmatureAccessor.create(EpicFightMod.NAMESPACE, "entity/vex", VexArmature::new);
	public static final ArmatureAccessor<PiglinArmature> PIGLIN = ArmatureAccessor.create(EpicFightMod.NAMESPACE, "entity/piglin", PiglinArmature::new);
	public static final ArmatureAccessor<HoglinArmature> HOGLIN = ArmatureAccessor.create(EpicFightMod.NAMESPACE, "entity/hoglin", HoglinArmature::new);
	public static final ArmatureAccessor<DragonArmature> DRAGON = ArmatureAccessor.create(EpicFightMod.NAMESPACE, "entity/dragon", DragonArmature::new);
	public static final ArmatureAccessor<WitherArmature> WITHER = ArmatureAccessor.create(EpicFightMod.NAMESPACE, "entity/wither", WitherArmature::new);
	
	public static void registerEntityTypes() {
		registerEntityTypeArmature(EntityTypes.CAVE_SPIDER, SPIDER);
		registerEntityTypeArmature(EntityTypes.CREEPER, CREEPER);
		registerEntityTypeArmature(EntityTypes.DROWNED, BIPED);
		registerEntityTypeArmature(EntityTypes.ENDERMAN, ENDERMAN);
		registerEntityTypeArmature(EntityTypes.EVOKER, BIPED);
		registerEntityTypeArmature(EntityTypes.HOGLIN, HOGLIN);
		registerEntityTypeArmature(EntityTypes.HUSK, BIPED);
		registerEntityTypeArmature(EntityTypes.IRON_GOLEM, IRON_GOLEM);
		registerEntityTypeArmature(EntityTypes.PIGLIN_BRUTE, PIGLIN);
		registerEntityTypeArmature(EntityTypes.PIGLIN, PIGLIN);
		registerEntityTypeArmature(EntityTypes.PILLAGER, BIPED);
		registerEntityTypeArmature(EntityTypes.RAVAGER, RAVAGER);
		registerEntityTypeArmature(EntityTypes.SKELETON, SKELETON);
		registerEntityTypeArmature(EntityTypes.SPIDER, SPIDER);
		registerEntityTypeArmature(EntityTypes.STRAY, SKELETON);
		registerEntityTypeArmature(EntityTypes.VEX, VEX);
		registerEntityTypeArmature(EntityTypes.VINDICATOR, BIPED);
		registerEntityTypeArmature(EntityTypes.WITCH, BIPED);
		registerEntityTypeArmature(EntityTypes.WITHER_SKELETON, SKELETON);
		registerEntityTypeArmature(EntityTypes.ZOGLIN, HOGLIN);
		registerEntityTypeArmature(EntityTypes.ZOMBIE, BIPED);
		registerEntityTypeArmature(EntityTypes.ZOMBIE_VILLAGER, BIPED);
		registerEntityTypeArmature(EntityTypes.ZOMBIFIED_PIGLIN, PIGLIN);
		registerEntityTypeArmature(EntityTypes.PLAYER, BIPED);
		registerEntityTypeArmature(EntityTypes.ENDER_DRAGON, DRAGON);
		registerEntityTypeArmature(EntityTypes.WITHER, WITHER);
		registerEntityTypeArmature(EpicFightEntityTypes.WITHER_SKELETON_MINION.get(), SKELETON);
		registerEntityTypeArmature(EpicFightEntityTypes.WITHER_GHOST_CLONE.get(), WITHER);
	}
	
	public static void reload(ResourceManager resourceManager) {
		Armatures.resourceManager = resourceManager;
		
		ACCESSORS.entrySet().removeIf(entry -> !entry.getValue().inRegistry);
		ARMATURES.clear();
	}
	
	public static void registerEntityTypeArmature(EntityType<?> entityType, AssetAccessor<? extends Armature> armatureAccessor) {
		ENTITY_TYPE_ARMATURE_MAPPER.put(entityType, armatureAccessor);
	}
	
	//For presets
	public static void registerEntityTypeArmatureByPreset(EntityType<?> entityType, String presetName) {
		EntityType<?> presetEntityType = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(presetName));
		ENTITY_TYPE_ARMATURE_MAPPER.put(entityType, ENTITY_TYPE_ARMATURE_MAPPER.get(presetEntityType));
	}
	
	@SuppressWarnings("unchecked")
	public static <A extends Armature> A getArmatureFor(EntityPatch<?> entitypatch) {
		return (A)ENTITY_TYPE_ARMATURE_MAPPER.get(entitypatch.getOriginal().getType()).get().deepCopy();
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
	public static <A extends Armature> AssetAccessor<A> get(Identifier id) {
		return (AssetAccessor<A>) ACCESSORS.get(id);
	}
	
	@SuppressWarnings("unchecked")
	public static <A extends Armature> AssetAccessor<A> getOrCreate(Identifier id, ArmatureContructor<A> armatureConstructor) {
		return ACCESSORS.containsKey(id) ? (AssetAccessor<A>)ACCESSORS.get(id) : ArmatureAccessor.create(id, armatureConstructor, false);
	}
	
	@SuppressWarnings("unchecked")
	public static <A extends Armature> Set<Pair<Identifier, AssetAccessor<A>>> entry() {
		Set<Pair<Identifier, AssetAccessor<A>>> newset = Sets.newHashSet();
		
		for (AssetAccessor<? extends Armature> accessor : ACCESSORS.values()) {
			try {
				AssetAccessor<A> casted = (AssetAccessor<A>)accessor;
				newset.add(Pair.of(casted.registryName(), casted));
			} catch(ClassCastException e) {
			}
		}
		
		return newset;
	}
	
	public static Identifier wrapLocation(Identifier rl) {
		return rl.getPath().matches("animmodels/.*\\.json") ? rl : Identifier.fromNamespaceAndPath(rl.getNamespace(), "animmodels/" + rl.getPath() + ".json");
	}
	
	public static record ArmatureAccessor<A extends Armature> (Identifier registryName, ArmatureContructor<A> armatureConstructor, boolean inRegistry) implements AssetAccessor<A> {
		public static <A extends Armature> ArmatureAccessor<A> create(String namespaceId, String path, ArmatureContructor<A> armatureConstructor) {
			return create(Identifier.fromNamespaceAndPath(namespaceId, path), armatureConstructor, true);
		}
		
		private static <A extends Armature> ArmatureAccessor<A> create(Identifier id, ArmatureContructor<A> armatureConstructor, boolean inRegistry) {
			ArmatureAccessor<A> accessor = new ArmatureAccessor<A> (id, armatureConstructor, inRegistry);
			ACCESSORS.put(id, accessor);
			return accessor;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public A get() {
			if (ARMATURES.get(this) == null) {
				JsonAssetLoader jsonAssetLoader = new JsonAssetLoader(resourceManager, wrapLocation(this.registryName()));
				ARMATURES.put(this, jsonAssetLoader.loadArmature(this.armatureConstructor));
			}
			
			return (A)ARMATURES.get(this);
		}
		
		public String toString() {
			return this.registryName.toString();
		}
		
		public int hashCode() {
			return this.registryName.hashCode();
		}
		
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			} else if (obj instanceof ArmatureAccessor armatureAccessor) {
				return this.registryName.equals(armatureAccessor.registryName());
			} else if (obj instanceof Identifier rl) {
				return this.registryName.equals(rl);
			} else if (obj instanceof String name) {
				return this.registryName.toString().equals(name);
			} else {
				return false;
			}
		}
	}
}