package yesman.epicfight.platform.fabric.registry;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelReader;

/** Replacement for NeoForge's LevelReader holder lookup extension. */
public final class RegistryHolders {
    private RegistryHolders() {}
    public static <T> Holder.Reference<T> of(LevelReader level,ResourceKey<T> key) {
        return of(level.registryAccess(), key);
    }
    public static <T> Holder.Reference<T> of(RegistryAccess access,ResourceKey<T> key) {
        return access.lookupOrThrow(key.registryKey()).getOrThrow(key);
    }
    public static <T> java.util.Optional<Holder.Reference<T>> find(RegistryAccess access,ResourceKey<T> key) {
        return access.lookupOrThrow(key.registryKey()).get(key);
    }
}
