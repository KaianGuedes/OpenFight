package dev.war.openfight.platform.fabric.resource;

import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** Preserves the original flat entity NBT keys through Minecraft's new typed storage interfaces. */
public final class RootNbtStorage {
    private RootNbtStorage() {}
    private static final MapCodec<CompoundTag> CODEC=MapCodec.assumeMapUnsafe(CompoundTag.CODEC);
    public static void write(ValueOutput output,CompoundTag data) { output.store(CODEC,data); }
    public static CompoundTag read(ValueInput input) { return input.read(CODEC).orElseGet(CompoundTag::new); }
}
