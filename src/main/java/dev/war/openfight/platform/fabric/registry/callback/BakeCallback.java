package dev.war.openfight.platform.fabric.registry.callback;
import net.minecraft.core.Registry;
@FunctionalInterface public interface BakeCallback<T> { void onBake(Registry<T> registry); }
