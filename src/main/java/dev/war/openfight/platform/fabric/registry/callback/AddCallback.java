package dev.war.openfight.platform.fabric.registry.callback;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
@FunctionalInterface public interface AddCallback<T> { void onAdd(Registry<T> registry, int id, ResourceKey<T> key, T value); }
