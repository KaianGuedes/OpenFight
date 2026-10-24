package dev.war.openfight.main;

import net.minecraft.world.item.CreativeModeTab;

import dev.war.openfight.platform.fabric.registry.DeferredHolder;
import dev.war.openfight.registry.entries.EpicFightCreativeTabs;

/**
 * @Param skillBookCreativeTab : decides which creative tab will display the skills that belong to the mod {@link EpicFightCreativeTabs}}
 */
public record EpicFightExtensions(DeferredHolder<CreativeModeTab, CreativeModeTab> skillBookCreativeTab) {
}