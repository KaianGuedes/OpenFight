package dev.war.openfight.registry.deferred;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import dev.war.openfight.platform.fabric.registry.DeferredRegister;
import dev.war.openfight.registry.EpicFightRegistries;
import dev.war.openfight.registry.deferred.holders.DeferredCustomData;
import dev.war.openfight.world.capabilities.item.custom.CustomData;

import java.util.function.Supplier;

public final class CustomDataRegister extends DeferredRegister<CustomData<?>> {

    private CustomDataRegister(ResourceKey<? extends Registry<CustomData<?>>> registryKey, String namespace) {
        super(registryKey, namespace);
    }

    public static CustomDataRegister createWeapon(String namespace)
    {
        return new CustomDataRegister(EpicFightRegistries.Keys.WEAPON_DATA, namespace);
    }

    public static CustomDataRegister createMoveset(String namespace)
    {
        return new CustomDataRegister(EpicFightRegistries.Keys.MOVESET_DATA, namespace);
    }


    public <T> DeferredCustomData<CustomData<T>> registerCustomData(String name, Supplier<CustomData<T>> data) {
        this.register(name, data);
        ResourceKey<CustomData<?>> key = ResourceKey.create(
                this.getRegistryKey(),
                Identifier.fromNamespaceAndPath(this.getNamespace(), name)
        );

        return new DeferredCustomData<>(key);
    }
}
