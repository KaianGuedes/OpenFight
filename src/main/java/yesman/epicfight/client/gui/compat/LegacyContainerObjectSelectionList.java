package yesman.epicfight.client.gui.compat;

import yesman.epicfight.client.gui.compat.LegacyContainerObjectSelectionList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

/** The 1.21.1 ContainerObjectSelectionList: entries are containers of child widgets. */
public abstract class LegacyContainerObjectSelectionList<E extends LegacyContainerObjectSelectionList.Entry<E>> extends LegacySelectionList<E> {
	public LegacyContainerObjectSelectionList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
		super(minecraft, width, height, y, itemHeight);
	}

	public abstract static class Entry<E extends Entry<E>> extends LegacySelectionList.Entry<E> implements ContainerEventHandler {
		@Nullable
		protected GuiEventListener focused;
		private boolean dragging;

		public abstract List<? extends GuiEventListener> children();

		public List<? extends NarratableEntry> narratables() {
			return List.of();
		}

		@Override
		public boolean isDragging() {
			return this.dragging;
		}

		@Override
		public void setDragging(boolean dragging) {
			this.dragging = dragging;
		}

		@Override
		public void setFocused(@Nullable GuiEventListener focused) {
			if (this.focused != null) {
				this.focused.setFocused(false);
			}

			if (focused != null) {
				focused.setFocused(true);
			}

			this.focused = focused;
		}

		@Override
		public void setFocused(boolean focused) {
			if (!focused) {
				this.setFocused((GuiEventListener) null);
			}
		}

		@Override
		public boolean isFocused() {
			return this.list != null && this.list.getFocused() == this;
		}

		@Nullable
		@Override
		public GuiEventListener getFocused() {
			return this.focused;
		}

		@Override
		public boolean isMouseOver(double mouseX, double mouseY) {
			return super.isMouseOver(mouseX, mouseY);
		}
	}
}
