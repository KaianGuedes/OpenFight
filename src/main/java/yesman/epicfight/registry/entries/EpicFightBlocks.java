package yesman.epicfight.registry.entries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import yesman.epicfight.platform.fabric.registry.DeferredHolder;
import yesman.epicfight.platform.fabric.registry.DeferredRegister;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.level.block.FractureBlock;

public final class EpicFightBlocks {
	private EpicFightBlocks() {}
	
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(Registries.BLOCK, EpicFightMod.MODID);
	
	public static final DeferredHolder<Block, FractureBlock> FRACTURE = REGISTRY.register("fracture_block", id -> new FractureBlock(BlockBehaviour.Properties.of().setId(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,id))));
}