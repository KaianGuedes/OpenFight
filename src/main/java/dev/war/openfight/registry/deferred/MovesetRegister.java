package dev.war.openfight.registry.deferred;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import dev.war.openfight.platform.fabric.registry.DeferredRegister;
import dev.war.openfight.api.ex_cap.data.Moveset;
import dev.war.openfight.registry.EpicFightRegistries;
import dev.war.openfight.registry.deferred.holders.DeferredMoveset;

import java.util.function.Supplier;

public final class MovesetRegister extends DeferredRegister<Moveset.Builder> {
    private MovesetRegister(ResourceKey<? extends Registry<Moveset.Builder>> registryKey, String namespace) {
        super(registryKey, namespace);
    }

    public static MovesetRegister create(String namespace)
    {
        return new MovesetRegister(EpicFightRegistries.Keys.MOVESETS, namespace);
    }

    public DeferredMoveset registerMoveset(String name, Supplier<Moveset.Builder> builder) {
        this.register(name, builder);
        ResourceKey<Moveset.Builder> key = ResourceKey.create(
                this.getRegistryKey(),
                Identifier.fromNamespaceAndPath(this.getNamespace(), name)
        );
        return new DeferredMoveset(key);
    }
}
