package yesman.epicfight.world.capabilities.provider;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.types.registry.EntityPatchRegistryEvent;
import yesman.epicfight.api.utils.side.ClientOnly;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.registry.entries.EpicFightEntityTypes;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.GlobalMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.boss.WitherGhostPatch;
import yesman.epicfight.world.capabilities.entitypatch.boss.WitherPatch;
import yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon.EnderDragonPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.*;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.projectile.*;
import yesman.epicfight.world.gamerule.EpicFightGameRules;

import java.util.*;
import java.util.function.Function;

public final class CommonEntityPatchProvider {
    public static final CommonEntityPatchProvider INSTANCE = new CommonEntityPatchProvider();

    private CommonEntityPatchProvider() {}

    private final Map<EntityType<?>, Function<Entity, EntityPatch<?>>> capabilities = new HashMap<> ();
    private final Map<EntityType<?>, Function<Entity, EntityPatch<?>>> datapackCapabilities = new HashMap<> ();
    private final Map<Class<? extends Projectile>, Function<Projectile, ProjectilePatch<?>>> typedCapabilities = new HashMap<> ();

    public void registerVanillaEntityPatches() {
        Map<EntityType<?>, Function<Entity, EntityPatch<?>>> registry = new HashMap<> ();
        registerEntityPatchUnsafe(registry, EntityTypes.PLAYER, entity -> new ServerPlayerPatch((ServerPlayer) entity));
        registerEntityPatch(registry, EntityTypes.ZOMBIE, ZombiePatch::new);
        registerEntityPatch(registry, EntityTypes.CREEPER, CreeperPatch::new);
        registerEntityPatch(registry, EntityTypes.ENDERMAN, EndermanPatch::new);
        registerEntityPatch(registry, EntityTypes.SKELETON, SkeletonPatch::new);
        registerEntityPatch(registry, EntityTypes.WITHER_SKELETON, WitherSkeletonPatch::new);
        registerEntityPatch(registry, EntityTypes.STRAY, StrayPatch::new);
        registerEntityPatch(registry, EntityTypes.ZOMBIFIED_PIGLIN, ZombifiedPiglinPatch::new);
        registerEntityPatch(registry, EntityTypes.ZOMBIE_VILLAGER, ZombiePatch::new);
        registerEntityPatch(registry, EntityTypes.HUSK, ZombiePatch::new);
        registerEntityPatch(registry, EntityTypes.SPIDER, SpiderPatch::new);
        registerEntityPatch(registry, EntityTypes.CAVE_SPIDER, CaveSpiderPatch::new);
        registerEntityPatch(registry, EntityTypes.IRON_GOLEM, IronGolemPatch::new);
        registerEntityPatch(registry, EntityTypes.VINDICATOR, VindicatorPatch::new);
        registerEntityPatch(registry, EntityTypes.EVOKER, EvokerPatch::new);
        registerEntityPatch(registry, EntityTypes.WITCH, WitchPatch::new);
        registerEntityPatch(registry, EntityTypes.DROWNED, DrownedPatch::new);
        registerEntityPatch(registry, EntityTypes.PILLAGER, PillagerPatch::new);
        registerEntityPatch(registry, EntityTypes.RAVAGER, RavagerPatch::new);
        registerEntityPatch(registry, EntityTypes.VEX, VexPatch::new);
        registerEntityPatch(registry, EntityTypes.PIGLIN, PiglinPatch::new);
        registerEntityPatch(registry, EntityTypes.PIGLIN_BRUTE, PiglinBrutePatch::new);
        registerEntityPatch(registry, EntityTypes.HOGLIN, HoglinPatch::new);
        registerEntityPatch(registry, EntityTypes.ZOGLIN, ZoglinPatch::new);
        registerEntityPatch(registry, EntityTypes.ENDER_DRAGON, entity -> {
            if (entity instanceof EnderDragon enderdragon) {
                return new EnderDragonPatch(enderdragon);
            }
            return null;
        });
        registerEntityPatch(registry, EntityTypes.WITHER, WitherPatch::new);
        registerEntityPatch(registry, EpicFightEntityTypes.WITHER_SKELETON_MINION.get(), WitherSkeletonPatch::new);
        registerEntityPatch(registry, EpicFightEntityTypes.WITHER_GHOST_CLONE.get(), WitherGhostPatch::new);
        registerEntityPatch(registry, EntityTypes.ARROW, ArrowPatch::new);
        registerEntityPatch(registry, EntityTypes.SPECTRAL_ARROW, ArrowPatch::new);
        registerEntityPatch(registry, EntityTypes.WITHER_SKULL, WitherSkullPatch::new);
        registerEntityPatch(registry, EntityTypes.DRAGON_FIREBALL, DragonFireballPatch::new);
        registerEntityPatch(registry, EntityTypes.TRIDENT, ThrownTridentPatch::new);

        this.typedCapabilities.put(AbstractArrow.class, entity -> new ArrowPatch<>((AbstractArrow) entity));

        EntityPatchRegistryEvent entitypatchRegistryEvent = new EntityPatchRegistryEvent(registry);
        EpicFightEventHooks.Registry.ENTITY_PATCH.post(entitypatchRegistryEvent);

        this.capabilities.putAll(registry);
    }

    /// For more details, refer to [EntityPatchRegistryEvent#registerEntityPatch].
    @ApiStatus.Internal
    public static <T extends Entity> void registerEntityPatch(
            @NotNull Map<EntityType<?>, Function<Entity, EntityPatch<?>>> registry,
            @NotNull EntityType<T> entityType,
            @NotNull Function<T, EntityPatch<T>> entityPatchFactory
    ) {
        //noinspection unchecked
        registry.put(
                entityType, (entity) -> entityPatchFactory.apply((T) entity)
        );
    }

    /// Strongly prefer [#registerEntityPatch] over this unsafe version
    /// for type-safety and strict design. Use this only as a last resort.
    /// For more details, refer to [EntityPatchRegistryEvent#registerEntityPatchUnsafe].
    @ApiStatus.Internal
    public static <T extends Entity> void registerEntityPatchUnsafe(
            @NotNull Map<EntityType<?>, Function<Entity, EntityPatch<?>>> registry,
            @NotNull EntityType<T> entityType,
            @NotNull Function<? super T, ? extends EntityPatch<? extends T>> entityPatchFactory
    ) {
        //noinspection unchecked
        registry.put(
                entityType,
                entity -> entityPatchFactory.apply((T) entity)
        );
    }

    public void clearDatapackEntities() {
        this.datapackCapabilities.clear();
    }

    public void putCustomEntityPatch(EntityType<?> entityType, Function<Entity, EntityPatch<?>> entitypatchProvider) {
        this.datapackCapabilities.put(entityType, entitypatchProvider);
    }

    public Function<Entity, EntityPatch<?>> get(String registryName) {
        Identifier rl = Identifier.parse(registryName);
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.getValue(rl);

        return this.capabilities.get(entityType);
    }

    public List<EntityType<?>> getPatchedEntities() {
        List<EntityType<?>> list = new ArrayList<> ();
        list.add(null);
        this.capabilities.keySet().stream().filter((type) -> type.getCategory() != MobCategory.MISC).sorted(Comparator.comparing(EntityType::getKey)).forEach(list::add);

        return list;
    }

    @SuppressWarnings("rawtypes")
    public @Nullable EntityPatch getCapability(Entity entity) {
        Function<Entity, EntityPatch<?>> provider = this.datapackCapabilities.getOrDefault(entity.getType(), this.capabilities.get(entity.getType()));

        if (provider != null) {
            try {
                return provider.apply(entity);
            } catch (Exception e) {
                EpicFightMod.stacktraceIfDevSide("Can't apply entity patch provider", (s) -> e);
            }
        } else if (entity instanceof Mob mob && EpicFightGameRules.GLOBAL_STUN.getRuleValue(entity.level())) {
            return new GlobalMobPatch(mob);
        }

        return null;
    }

    /** Checks whether a patch factory exists without constructing or caching a patch. */
    public boolean hasCapabilityProvider(Entity entity) {
        return this.datapackCapabilities.containsKey(entity.getType())
                || this.capabilities.containsKey(entity.getType())
                || entity instanceof Mob && EpicFightGameRules.GLOBAL_STUN.getRuleValue(entity.level());
    }

    @ClientOnly
    public static class ClientModule {
        public static void registerClientPlayerPatches() {
            INSTANCE.capabilities.put(EntityTypes.PLAYER, entity -> switch (entity) {
                case LocalPlayer localPlayer -> new LocalPlayerPatch(localPlayer);
                case RemotePlayer remotePlayer -> new AbstractClientPlayerPatch<>(remotePlayer);
                case ServerPlayer serverPlayer -> new ServerPlayerPatch(serverPlayer);
                case null, default -> null;
            });
        }
    }
}
