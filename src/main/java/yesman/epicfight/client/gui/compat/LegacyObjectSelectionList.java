package yesman.epicfight.client.gui.compat;

import yesman.epicfight.client.gui.compat.LegacyObjectSelectionList;
import net.minecraft.client.Minecraft;

/** The 1.21.1 ObjectSelectionList: a selection list whose entries are plain (non-container) elements. */
public abstract class LegacyObjectSelectionList<E extends LegacyObjectSelectionList.Entry<E>> extends LegacySelectionList<E> {
	public LegacyObjectSelectionList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
		super(minecraft, width, height, y, itemHeight);
	}

	public abstract static class Entry<E extends Entry<E>> extends LegacySelectionList.Entry<E> {
	}
}
