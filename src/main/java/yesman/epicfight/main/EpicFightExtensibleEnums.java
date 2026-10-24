package yesman.epicfight.main;

import net.minecraft.world.item.Rarity;

/**
 * NeoForge's extensible-enum mechanism (EnumProxy) has no Fabric counterpart. The custom "epicfight:unique" rarity
 * (green name) cannot be added to the vanilla {@link Rarity} enum, so it falls back to a vanilla rarity.
 */
public class EpicFightExtensibleEnums {
	public static void initExtensibleEnums() {
		EpicFightRairity.init();
	}
	
	public static class EpicFightRairity {
		public static Rarity UNIQUE;
		
		private static void init() {
			UNIQUE = Rarity.UNCOMMON;
		}
	}
}
