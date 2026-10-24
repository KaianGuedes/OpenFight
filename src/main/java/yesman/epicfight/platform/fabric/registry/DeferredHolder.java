package yesman.epicfight.platform.fabric.registry;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

/** A lazy reference to the canonical vanilla registry holder, never a second registered value. */
public class DeferredHolder<B, T extends B> extends Holder.Reference<B> implements Supplier<T> {
    private final ResourceKey<B> resourceKey;
    protected DeferredHolder(ResourceKey<B> key) {
        super(Holder.Reference.Type.STAND_ALONE, new HolderOwner<B>() {}, key, null);
        this.resourceKey = key;
    }
    public static <B, T extends B> DeferredHolder<B,T> create(ResourceKey<B> key) { return new DeferredHolder<>(key); }
    @SuppressWarnings("unchecked") private Holder.Reference<B> delegate() {
        Registry<B> registry = (Registry<B>) BuiltInRegistries.REGISTRY.getValue(resourceKey.registry());
        if (registry == null) throw new IllegalStateException("Registry not initialized: " + resourceKey.registry());
        return registry.get(resourceKey.identifier()).orElseThrow(() -> new IllegalStateException("Unbound registry entry: " + resourceKey));
    }
    @SuppressWarnings("unchecked") @Override public T value() { return (T) delegate().value(); }
    @Override public T get() { return value(); }
    public Identifier getId() { return resourceKey.identifier(); }
    public ResourceKey<B> getKey() { return resourceKey; }
    public Holder<B> getDelegate() { return delegate(); }
    @Override public ResourceKey<B> key() { return resourceKey; }
    @Override public Optional<ResourceKey<B>> unwrapKey() { return Optional.of(resourceKey); }
    @Override public Either<ResourceKey<B>,B> unwrap() { return Either.left(resourceKey); }
    @Override public boolean isBound() {
        try { return delegate().isBound(); } catch (IllegalStateException e) { return false; }
    }
    @Override public boolean areComponentsBound() { return delegate().areComponentsBound(); }
    @Override public DataComponentMap components() { return delegate().components(); }
    @Override public Stream<TagKey<B>> tags() { return delegate().tags(); }
    @Override public boolean is(TagKey<B> tag) { return delegate().is(tag); }
    @Override public boolean canSerializeIn(HolderOwner<B> owner) { return delegate().canSerializeIn(owner); }
}
