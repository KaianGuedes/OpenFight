package dev.war.openfight.config;

import net.fabricmc.api.EnvType;
import dev.war.openfight.platform.fabric.config.ModConfigSpec;
import dev.war.openfight.main.EpicFightMod;

public class ServerConfig {
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
	public static final ModConfigSpec.BooleanValue ALLOW_CUSTOM_ANIMATIONS = BUILDER.define("allow_custom_animations", false);
	public static final ModConfigSpec SPEC = BUILDER.build();
	
	public static boolean allowCustomAnimations;
    public static void reload() {
		
		allowCustomAnimations = ALLOW_CUSTOM_ANIMATIONS.get();
	}
}
