package yesman.epicfight.platform.fabric.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.saveddata.WeatherData;

/** Replacements for NeoForge's EventHooks/level helpers that gameplay code relied on. */
public final class RuleCompat {
	private RuleCompat() {}
	
	/** Game rules only exist on the logical server in 26.3; clients see the rule's default. */
	public static <T> T get(Level level, GameRule<T> rule) {
		return level instanceof ServerLevel serverLevel ? serverLevel.getGameRules().get(rule) : rule.defaultValue();
	}
	
	/** EventHooks#canEntityGrief without the cancellable event. */
	public static boolean mobGriefing(Level level, Entity entity) {
		return get(level, GameRules.MOB_GRIEFING);
	}
	
	/** Wither-style block destruction check (NeoForge BlockState#canEntityDestroy + onEntityDestroyBlock). */
	public static boolean canEntityDestroy(Level level, BlockPos pos, Entity entity, BlockState state) {
		return !state.isAir() && !state.is(BlockTags.WITHER_IMMUNE);
	}
	
	public static void setStorm(Level level, int durationTicks) {
		if (level instanceof ServerLevel serverLevel) {
			WeatherData weather = serverLevel.getWeatherData();
			weather.setClearWeatherTime(0);
			weather.setRainTime(durationTicks);
			weather.setThunderTime(durationTicks);
			weather.setRaining(true);
			weather.setThundering(true);
		}
	}
}
