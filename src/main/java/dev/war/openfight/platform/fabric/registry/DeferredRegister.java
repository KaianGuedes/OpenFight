package dev.war.openfight.platform.fabric.registry;

import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

/** Collects static declarations and installs them in deterministic order during Fabric initialization. */
public class DeferredRegister<T> {
    private final ResourceKey<? extends Registry<T>> key;
    private final String namespace;
    private final Map<DeferredHolder<T,? extends T>,Supplier<? extends T>> entries = new LinkedHashMap<>();
    private boolean registered;
    protected DeferredRegister(ResourceKey<? extends Registry<T>> key, String namespace) { this.key=key; this.namespace=namespace; }
    public static <T> DeferredRegister<T> create(ResourceKey<? extends Registry<T>> key, String namespace) { return new DeferredRegister<>(key,namespace); }
    public static <T> DeferredRegister<T> create(Registry<T> registry, String namespace) { return create(registry.key(),namespace); }
    public <I extends T> DeferredHolder<T,I> register(String name, Supplier<? extends I> supplier) {
        if (registered) throw new IllegalStateException("Late registry declaration: " + name);
        ResourceKey<T> entryKey=ResourceKey.create(key,Identifier.fromNamespaceAndPath(namespace,name));
        if (entries.keySet().stream().anyMatch(holder -> holder.getKey().equals(entryKey))) throw new IllegalArgumentException("Duplicate registry entry: " + entryKey);
        DeferredHolder<T,I> holder=DeferredHolder.create(entryKey);
        entries.put(holder,supplier);
        return holder;
    }
    public <I extends T> DeferredHolder<T,I> register(String name, Function<Identifier,? extends I> factory) {
        return register(name,()->factory.apply(Identifier.fromNamespaceAndPath(namespace,name)));
    }
    @SuppressWarnings("unchecked") public void registerAll() {
        if (registered) throw new IllegalStateException("Registry declarations already installed: " + key);
        Registry<T> registry=(Registry<T>)BuiltInRegistries.REGISTRY.getValue(key.identifier());
        if (registry==null) throw new IllegalStateException("Missing registry: " + key);
        entries.forEach((holder,supplier)->Registry.register(registry,holder.getKey(),supplier.get()));
        registered=true;
    }
    public String getNamespace() { return namespace; }
    public ResourceKey<? extends Registry<T>> getRegistryKey() { return key; }
    public Collection<DeferredHolder<T,? extends T>> getEntries() { return java.util.Collections.unmodifiableSet(entries.keySet()); }
}
