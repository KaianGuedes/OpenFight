package dev.war.openfight.world.damagesource;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageType;
import dev.war.openfight.main.EpicFightMod;

public final class EpicFightDamageTypes {
	private EpicFightDamageTypes() {}
	
	public static final ResourceKey<DamageType> SHOCKWAVE = ResourceKey.create(Registries.DAMAGE_TYPE, EpicFightMod.identifier("shockwave"));
	public static final ResourceKey<DamageType> WITHER_BEAM = ResourceKey.create(Registries.DAMAGE_TYPE, EpicFightMod.identifier("wither_beam"));
}