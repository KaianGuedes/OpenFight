package yesman.epicfight.platform.fabric.registry;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import yesman.epicfight.platform.fabric.registry.callback.AddCallback;
import yesman.epicfight.platform.fabric.registry.callback.BakeCallback;

public final class RegistryBuilder<T> {
    private static final List<Runnable> BAKES=new ArrayList<>();
    private final ResourceKey<Registry<T>> key;
    private final List<Object> callbacks=new ArrayList<>();
    private boolean synced;
    public RegistryBuilder(ResourceKey<Registry<T>> key) { this.key=key; }
    public RegistryBuilder<T> sync(boolean synced) { this.synced=synced; return this; }
    public RegistryBuilder<T> callback(Object callback) { callbacks.add(callback); return this; }
    @SuppressWarnings("unchecked") public Registry<T> create() {
        var builder=FabricRegistryBuilder.create(key);
        if (synced) builder.attribute(RegistryAttribute.SYNCED);
        Registry<T> registry=builder.buildAndRegister();
        for (Object callback:callbacks) {
            if (callback instanceof AddCallback<?> add) {
                RegistryEntryAddedCallback.event(registry).register((id,name,value)->
                    ((AddCallback<T>)add).onAdd(registry,id,ResourceKey.create(key,name),value));
            }
            if (callback instanceof BakeCallback<?> bake) BAKES.add(()->((BakeCallback<T>)bake).onBake(registry));
        }
        return registry;
    }
    /** Call after every Epic Fight deferred registry has been populated. */
    public static void bakeAll() { BAKES.forEach(Runnable::run); }
}
