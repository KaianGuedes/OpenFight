package dev.war.openfight.registry.deferred;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import dev.war.openfight.platform.fabric.registry.DeferredRegister;
import dev.war.openfight.api.ex_cap.data.modifier.WeaponModifier;
import dev.war.openfight.registry.EpicFightRegistries;
import dev.war.openfight.registry.deferred.holders.DeferredModifier;

import java.util.function.Supplier;

public final class ModifierRegister extends DeferredRegister<WeaponModifier.Builder> {
    private ModifierRegister(ResourceKey<? extends Registry<WeaponModifier.Builder>> registryKey, String namespace) {
        super(registryKey, namespace);
    }

    public static ModifierRegister create(String namespace)
    {
        return new ModifierRegister(EpicFightRegistries.Keys.MODIFIERS, namespace);
    }

    public DeferredModifier registerModifier(String name, Supplier<WeaponModifier.Builder> builder)
    {
        this.register(name, builder);

        ResourceKey<WeaponModifier.Builder> key = ResourceKey.create(
                this.getRegistryKey(),
                Identifier.fromNamespaceAndPath(this.getNamespace(), name)
        );

        return new DeferredModifier(key);
    }
}
