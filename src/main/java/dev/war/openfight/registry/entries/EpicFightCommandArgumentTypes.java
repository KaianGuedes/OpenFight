package dev.war.openfight.registry.entries;

import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import dev.war.openfight.platform.fabric.registry.DeferredRegister;
import dev.war.openfight.main.EpicFightMod;
import dev.war.openfight.server.commands.arguments.AnimationArgument;
import dev.war.openfight.server.commands.arguments.SkillArgument;

public final class EpicFightCommandArgumentTypes {
	private EpicFightCommandArgumentTypes() {}
	
	/** Kept (empty) for the registry list; command argument types are registered through Fabric's ArgumentTypeRegistry. */
	public static final DeferredRegister<net.minecraft.commands.synchronization.ArgumentTypeInfo<?, ?>> REGISTRY = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, EpicFightMod.NAMESPACE);
	
	public static void registerArgumentTypes() {
		net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry.registerArgumentType(EpicFightMod.identifier("skill"), SkillArgument.class, SingletonArgumentInfo.contextFree(SkillArgument::skill));
		net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry.registerArgumentType(EpicFightMod.identifier("animation"), AnimationArgument.class, SingletonArgumentInfo.contextFree(AnimationArgument::animation));
	}
}
