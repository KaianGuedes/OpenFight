package yesman.epicfight.platform.fabric.registry.callback;
import net.minecraft.core.Registry;
@FunctionalInterface public interface ClearCallback<T> { void onClear(Registry<T> registry, boolean full); }
