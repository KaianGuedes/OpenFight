package dev.war.openfight.registry.entries;

import com.google.common.collect.ImmutableSet;

import net.minecraft.util.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntityType;
import dev.war.openfight.platform.fabric.registry.DeferredHolder;
import dev.war.openfight.platform.fabric.registry.DeferredRegister;
import dev.war.openfight.main.EpicFightMod;
import dev.war.openfight.world.level.block.entity.FractureBlockEntity;
import dev.war.openfight.world.level.block.entity.UniversalBlockEntityType;

public final class EpicFightBlockEntities {
	private EpicFightBlockEntities() {}
	
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EpicFightMod.NAMESPACE);
	
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FractureBlockEntity>> FRACTURE =
		REGISTRY.register(
			  "fracture_block"
			, () ->
				new UniversalBlockEntityType<>(
					  FractureBlockEntity::new
					, ImmutableSet.of(EpicFightBlocks.FRACTURE.get())
					, null /* 26.3 BlockEntityType has no DFU choice-type constructor parameter. */
				)
		);
}