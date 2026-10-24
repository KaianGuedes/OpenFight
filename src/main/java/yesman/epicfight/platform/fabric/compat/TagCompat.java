package yesman.epicfight.platform.fabric.compat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

/** 1.21.1 NBT helper semantics on top of the 26.3 NBT API. */
public final class TagCompat {
	private TagCompat() {}

	/** 1.21.1 {@code Tag#getAsString}: raw value for strings, SNBT for everything else. */
	public static String getAsString(Tag tag) {
		if (tag instanceof StringTag s) {
			return s.value();
		}

		return tag.toString();
	}

	/** 1.21.1 {@code CompoundTag#contains(String, int)} with a Tag.TAG_* id. */
	public static boolean contains(CompoundTag tag, String key, int type) {
		Tag value = tag.get(key);

		if (value == null) {
			return false;
		}

		if (type == 99) {
			return value instanceof NumericTag;
		}

		return value.getId() == type;
	}
}
