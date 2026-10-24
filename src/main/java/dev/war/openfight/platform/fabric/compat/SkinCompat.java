package dev.war.openfight.platform.fabric.compat;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;

/** 1.21.1 PlayerSkin accessors (texture(), capeTexture(), elytraTexture()) on the 26.3 asset-based PlayerSkin. */
public final class SkinCompat {
	private SkinCompat() {}
	
	public static Identifier texture(PlayerSkin skin) {
		return skin.body().texturePath();
	}
	
	@Nullable
	public static Identifier capeTexture(PlayerSkin skin) {
		ClientAsset.Texture cape = skin.cape();
		return cape == null ? null : cape.texturePath();
	}
	
	@Nullable
	public static Identifier elytraTexture(PlayerSkin skin) {
		ClientAsset.Texture elytra = skin.elytra();
		return elytra == null ? null : elytra.texturePath();
	}
}
