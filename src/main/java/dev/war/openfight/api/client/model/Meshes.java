package dev.war.openfight.api.client.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.asset.JsonAssetLoader;
import dev.war.openfight.api.client.model.Mesh.RenderProperties;
import dev.war.openfight.api.client.physics.cloth.ClothSimulatable;
import dev.war.openfight.api.client.physics.cloth.ClothSimulator.ClothObject;
import dev.war.openfight.api.client.physics.cloth.ClothSimulator.ClothObjectBuilder;
import dev.war.openfight.client.mesh.CreeperMesh;
import dev.war.openfight.client.mesh.DragonMesh;
import dev.war.openfight.client.mesh.EndermanMesh;
import dev.war.openfight.client.mesh.HoglinMesh;
import dev.war.openfight.client.mesh.HumanoidMesh;
import dev.war.openfight.client.mesh.IronGolemMesh;
import dev.war.openfight.client.mesh.PiglinMesh;
import dev.war.openfight.client.mesh.RavagerMesh;
import dev.war.openfight.client.mesh.SpiderMesh;
import dev.war.openfight.client.mesh.VexMesh;
import dev.war.openfight.client.mesh.VillagerMesh;
import dev.war.openfight.client.mesh.WitherMesh;
import dev.war.openfight.main.EpicFightMod;

public class Meshes implements PreparableReloadListener {
	private static final Map<Identifier, MeshAccessor<? extends Mesh>> ACCESSORS = new HashMap<> ();
	private static final Map<MeshAccessor<? extends Mesh>, Mesh> MESHES = new HashMap<> ();
	private static ResourceManager resourceManager = null;
	
	//For resource reloader
	public static final Meshes INSTANCE = new Meshes();
	
	// Entities
	public static final MeshAccessor<HumanoidMesh> ALEX = MeshAccessor.create(EpicFightMod.NAMESPACE, "entity/biped_slim_arm", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(HumanoidMesh::new));
	public static final MeshAccessor<HumanoidMesh> BIPED = MeshAccessor.create(EpicFightMod.NAMESPACE, "entity/biped", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(HumanoidMesh::new));
	public static final MeshAccessor<HumanoidMesh> BIPED_OLD_TEX = MeshAccessor.create(EpicFightMod.NAMESPACE, "entity/biped_old_texture", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(HumanoidMesh::new));
	public static final MeshAccessor<HumanoidMesh> BIPED_OUTLAYER = MeshAccessor.create(EpicFightMod.NAMESPACE, "entity/biped_outlayer", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(HumanoidMesh::new));
	public static final MeshAccessor<VillagerMesh> VILLAGER_ZOMBIE = MeshAccessor.create(EpicFightMod.NAMESPACE, "entity/zombie_villager", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(VillagerMesh::new));
	public static final MeshAccessor<CreeperMesh> CREEPER = MeshAccessor.create(EpicFightMod.NAMESPACE, "entity/creeper", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(CreeperMesh::new));
	public static final MeshAccessor<EndermanMesh> ENDERMAN = MeshAccessor.create(EpicFightMod.NAMESPACE, "entity/enderman", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(EndermanMesh::new));
	public static final MeshAccessor<HumanoidMesh> SKELETON = MeshAccessor.create(EpicFightMod.NAMESPACE, "entity/skeleton", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(HumanoidMesh::new));
	public static final MeshAccessor<SpiderMesh> SPIDER = MeshAccessor.create(EpicFightMod.NAMESPACE, "entity/spider", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(SpiderMesh::new));
	public static final MeshAccessor<IronGolemMesh> IRON_GOLEM = MeshAccessor.create(EpicFightMod.NAMESPACE, "entity/iron_golem", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(IronGolemMesh::new));
	public static final MeshAccessor<HumanoidMesh> ILLAGER = MeshAccessor.create(EpicFightMod.NAMESPACE, "entity/illager", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(VillagerMesh::new));
	public static final MeshAccessor<VillagerMesh> WITCH = MeshAccessor.create(EpicFightMod.NAMESPACE, "entity/witch", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(VillagerMesh::new));
	public static final MeshAccessor<RavagerMesh> RAVAGER = MeshAccessor.create(EpicFightMod.NAMESPACE, "entity/ravager",(jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(RavagerMesh::new));
	public static final MeshAccessor<VexMesh> VEX = MeshAccessor.create(EpicFightMod.NAMESPACE, "entity/vex", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(VexMesh::new));
	public static final MeshAccessor<PiglinMesh> PIGLIN = MeshAccessor.create(EpicFightMod.NAMESPACE, "entity/piglin", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(PiglinMesh::new));
	public static final MeshAccessor<HoglinMesh> HOGLIN = MeshAccessor.create(EpicFightMod.NAMESPACE, "entity/hoglin", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(HoglinMesh::new));
	public static final MeshAccessor<DragonMesh> DRAGON = MeshAccessor.create(EpicFightMod.NAMESPACE, "entity/dragon", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(DragonMesh::new));
	public static final MeshAccessor<WitherMesh> WITHER = MeshAccessor.create(EpicFightMod.NAMESPACE, "entity/wither", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(WitherMesh::new));
	
	// Armors
	public static final MeshAccessor<SkinnedMesh> HELMET = MeshAccessor.create(EpicFightMod.NAMESPACE, "armor/helmet", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));
	public static final MeshAccessor<SkinnedMesh> HELMET_PIGLIN = MeshAccessor.create(EpicFightMod.NAMESPACE, "armor/piglin_helmet", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));
	public static final MeshAccessor<SkinnedMesh> HELMET_VILLAGER = MeshAccessor.create(EpicFightMod.NAMESPACE, "armor/villager_helmet", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));
	public static final MeshAccessor<SkinnedMesh> CHESTPLATE = MeshAccessor.create(EpicFightMod.NAMESPACE, "armor/chestplate", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));
	public static final MeshAccessor<SkinnedMesh> LEGGINS = MeshAccessor.create(EpicFightMod.NAMESPACE, "armor/leggins", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));
	public static final MeshAccessor<SkinnedMesh> BOOTS = MeshAccessor.create(EpicFightMod.NAMESPACE, "armor/boots", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));
	
	// Particles
	public static final MeshAccessor<ClassicMesh> AIR_BURST = MeshAccessor.create(EpicFightMod.NAMESPACE, "particle/air_burst", (jsonModelLoader) -> jsonModelLoader.loadClassicMesh(ClassicMesh::new));
	public static final MeshAccessor<ClassicMesh> FORCE_FIELD = MeshAccessor.create(EpicFightMod.NAMESPACE, "particle/force_field", (jsonModelLoader) -> jsonModelLoader.loadClassicMesh(ClassicMesh::new));
	public static final MeshAccessor<ClassicMesh> LASER = MeshAccessor.create(EpicFightMod.NAMESPACE, "particle/laser", (jsonModelLoader) -> jsonModelLoader.loadClassicMesh(ClassicMesh::new));
	
	// Layers
	public static final MeshAccessor<SkinnedMesh> CAPE_DEFAULT = MeshAccessor.create(EpicFightMod.NAMESPACE, "layer/default_cape", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));
	
	public static void reload(ResourceManager resourceManager) {
		Meshes.resourceManager = resourceManager;
		
		ACCESSORS.entrySet().removeIf(entry -> !entry.getValue().inRegistry);
		
		MESHES.values().forEach((mesh) -> {
			if (mesh instanceof SkinnedMesh skinnedMesh) {
				skinnedMesh.destroy();
			}
		});
		
		MESHES.clear();
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
	public static <M extends Mesh> AssetAccessor<M> get(Identifier id) {
		return (AssetAccessor<M>) ACCESSORS.get(id);
	}
	
	@SuppressWarnings("unchecked")
	public static <M extends Mesh> AssetAccessor<M> getOrCreate(Identifier id, Function<JsonAssetLoader, M> jsonLoader) {
		return ACCESSORS.containsKey(id) ? (AssetAccessor<M>)ACCESSORS.get(id) : MeshAccessor.create(id, jsonLoader, false);
	}
	
	@SuppressWarnings("unchecked")
	public static <M extends Mesh> Set<AssetAccessor<M>> entry(Class<? extends Mesh> filter) {
		return ACCESSORS.values().stream().filter((accessor) -> filter.isAssignableFrom(accessor.get().getClass())).map((accessor) -> (AssetAccessor<M>)accessor).collect(Collectors.toSet());
	}
	
	public static Identifier wrapLocation(Identifier rl) {
		return rl.getPath().matches("animmodels/.*\\.json") ? rl : Identifier.fromNamespaceAndPath(rl.getNamespace(), "animmodels/" + rl.getPath() + ".json");
	}
	
	@Override
	public CompletableFuture<Void> reload(PreparableReloadListener.SharedState state, Executor backgroundExecutor, PreparableReloadListener.PreparationBarrier stage, Executor gameExecutor) {
		return stage.wait(net.minecraft.util.Unit.INSTANCE).thenRunAsync(() -> Meshes.reload(state.resourceManager()), gameExecutor);
	}
	
	@FunctionalInterface
	public interface MeshContructor<P extends MeshPart, V extends VertexBuilder, M extends StaticMesh<P>> {
		M invoke(Map<String, Number[]> arrayMap, Map<MeshPartDefinition, List<V>> parts, M parent, RenderProperties properties);
	}
	
	public static record MeshAccessor<M extends Mesh> (Identifier registryName, Function<JsonAssetLoader, M> jsonLoader, boolean inRegistry) implements AssetAccessor<M>, SoftBodyTranslatable {
		public static <M extends Mesh> MeshAccessor<M> create(String namespaceId, String path, Function<JsonAssetLoader, M> jsonLoader) {
			return create(Identifier.fromNamespaceAndPath(namespaceId, path), jsonLoader, true);
		}
		
		private static <M extends Mesh> MeshAccessor<M> create(Identifier id, Function<JsonAssetLoader, M> jsonLoader, boolean inRegistry) {
			MeshAccessor<M> accessor = new MeshAccessor<M> (id, jsonLoader, inRegistry);
			ACCESSORS.put(id, accessor);
			return accessor;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public M get() {
			if (!MESHES.containsKey(this)) {
				JsonAssetLoader jsonModelLoader = new JsonAssetLoader(resourceManager, wrapLocation(this.registryName));
				MESHES.put(this, this.jsonLoader.apply(jsonModelLoader));
			}
			
			return (M)MESHES.get(this);
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
			} else if (obj instanceof MeshAccessor armatureAccessor) {
				return this.registryName.equals(armatureAccessor.registryName());
			} else if (obj instanceof Identifier rl) {
				return this.registryName.equals(rl);
			} else if (obj instanceof String name) {
				return this.registryName.toString().equals(name);
			} else {
				return false;
			}
		}
		
		@Override
		public boolean canStartSoftBodySimulation() {
			Mesh mesh = this.get();
			
			if (mesh instanceof StaticMesh<?> staticMesh) {
				return staticMesh.canStartSoftBodySimulation();
			} else if (mesh instanceof CompositeMesh compositeMesh) {
				return compositeMesh.canStartSoftBodySimulation();
			}
			
			return false;
		}
		
		@Override
		public ClothObject createSimulationData(SoftBodyTranslatable provider, ClothSimulatable simOwner, ClothObjectBuilder simBuilder) {
			Mesh mesh = this.get();
			
			if (mesh instanceof StaticMesh<?> staticMesh) {
				return staticMesh.createSimulationData(provider, simOwner, simBuilder);
			} else if (mesh instanceof CompositeMesh compositeMesh) {
				return compositeMesh.createSimulationData(provider, simOwner, simBuilder);
			}
			
			return null;
		}

		@Override
		public void putSoftBodySimulationInfo(Map<String, ClothSimulationInfo> sofyBodySimulationInfo) {
			Mesh mesh = this.get();
			
			if (mesh instanceof SoftBodyTranslatable softBodyTranslatable) {
				softBodyTranslatable.putSoftBodySimulationInfo(sofyBodySimulationInfo);
			}
		}

		@Override
		public Map<String, ClothSimulationInfo> getSoftBodySimulationInfo() {
			Mesh mesh = this.get();
			
			if (mesh instanceof SoftBodyTranslatable softBodyTranslatable) {
				return softBodyTranslatable.getSoftBodySimulationInfo();
			} else {
				return null;
			}
		}
	}
}