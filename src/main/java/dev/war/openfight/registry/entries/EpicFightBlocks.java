package dev.war.openfight.registry.entries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import dev.war.openfight.platform.fabric.registry.DeferredHolder;
import dev.war.openfight.platform.fabric.registry.DeferredRegister;
import dev.war.openfight.main.EpicFightMod;
import dev.war.openfight.world.level.block.FractureBlock;

public final class EpicFightBlocks {
	private EpicFightBlocks() {}
	
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(Registries.BLOCK, EpicFightMod.NAMESPACE);
	
	public static final DeferredHolder<Block, FractureBlock> FRACTURE = REGISTRY.register("fracture_block", id -> new FractureBlock(BlockBehaviour.Properties.of().setId(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,id))));
}