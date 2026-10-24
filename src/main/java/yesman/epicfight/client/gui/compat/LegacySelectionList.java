package yesman.epicfight.client.gui.compat;

import yesman.epicfight.client.gui.compat.LegacyContainerObjectSelectionList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

/**
 * Compatibility base reproducing the 1.21.1 AbstractSelectionList contract (uniform row height, row-index based API).
 * 26.3 replaced it with entries that own their own layout, which the Epic Fight screens and datapack widgets do not use.
 * Rendering methods keep the old names except that per-entry rendering is called extractRenderState, like everywhere else in this port.
 */
public abstract class LegacySelectionList<E extends LegacySelectionList.Entry<E>> extends AbstractContainerWidget {
	private static final Identifier MENU_LIST_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/menu_list_background.png");
	private static final Identifier INWORLD_MENU_LIST_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/inworld_menu_list_background.png");

	protected final Minecraft minecraft;
	protected int defaultEntryHeight;
	private final List<E> children = new TrackedList();
	protected boolean centerListVertically = true;
	protected int headerHeight;
	protected boolean renderHeader;
	@Nullable
	private E selected;
	@Nullable
	private E hovered;

	public LegacySelectionList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
		super(0, y, width, height, Component.empty(), defaultSettings(Math.max(1, itemHeight / 2)));
		this.minecraft = minecraft;
		this.defaultEntryHeight = itemHeight;
	}

	protected void setRenderHeader(boolean renderHeader, int headerHeight) {
		this.renderHeader = renderHeader;
		this.headerHeight = renderHeader ? headerHeight : 0;
	}

	public int getRowWidth() {
		return 220;
	}

	@Nullable
	public E getSelected() {
		return this.selected;
	}

	public void setSelected(@Nullable E selected) {
		this.selected = selected;
	}

	@Override
	public final List<E> children() {
		return this.children;
	}

	protected void clearEntries() {
		this.children.clear();
		this.selected = null;
	}

	public void replaceEntries(Collection<E> newChildren) {
		this.clearEntries();
		this.children.addAll(newChildren);
	}

	protected E getEntry(int index) {
		return this.children().get(index);
	}

	protected int addEntry(E entry) {
		this.children.add(entry);
		return this.children.size() - 1;
	}

	protected void addEntryToTop(E entry) {
		double d = (double) this.maxScrollAmount() - this.scrollAmount();
		this.children.add(0, entry);
		this.setScrollAmount((double) this.maxScrollAmount() - d);
	}

	protected boolean removeEntryFromTop(E entry) {
		double d = (double) this.maxScrollAmount() - this.scrollAmount();
		boolean removed = this.removeEntry(entry);
		this.setScrollAmount((double) this.maxScrollAmount() - d);
		return removed;
	}

	protected boolean removeEntry(E entry) {
		boolean removed = this.children.remove(entry);

		if (removed && entry == this.getSelected()) {
			this.setSelected(null);
		}

		return removed;
	}

	protected int getItemCount() {
		return this.children().size();
	}

	protected boolean isSelectedItem(int index) {
		return Objects.equals(this.getSelected(), this.children().get(index));
	}

	protected int getMaxPosition() {
		return this.getItemCount() * this.defaultEntryHeight + this.headerHeight;
	}

	/** The old name of {@link #maxScrollAmount()}; subclasses override it to change the scroll limit. */
	public int getMaxScroll() {
		return Math.max(0, this.getMaxPosition() - (this.getHeight() - 4));
	}

	@Override
	public int maxScrollAmount() {
		return this.getMaxScroll();
	}

	@Override
	protected int contentHeight() {
		return this.getMaxPosition();
	}

	protected int getScrollbarPosition() {
		return this.getX() + this.width / 2 + this.getRowWidth() / 2 + 6;
	}

	@Override
	protected int scrollBarX() {
		return this.getScrollbarPosition();
	}

	@Nullable
	protected E getEntryAtPosition(double x, double y) {
		int halfRow = this.getRowWidth() / 2;
		int center = this.getX() + this.width / 2;
		int left = center - halfRow;
		int right = center + halfRow;
		int relY = Mth.floor(y - (double) this.getY()) - this.headerHeight + (int) this.scrollAmount() - 4;
		int index = relY / this.defaultEntryHeight;

		return x < (double) this.getScrollbarPosition() && x >= (double) left && x <= (double) right && index >= 0 && relY >= 0 && index < this.getItemCount() ? this.children().get(index) : null;
	}

	public int getRowLeft() {
		return this.getX() + this.width / 2 - this.getRowWidth() / 2 + 2;
	}

	public int getRowRight() {
		return this.getRowLeft() + this.getRowWidth();
	}

	public int getRowTop(int index) {
		return this.getY() - (int) this.scrollAmount() + index * this.defaultEntryHeight + this.headerHeight;
	}

	public int getRowBottom(int index) {
		return this.getRowTop(index) + this.defaultEntryHeight;
	}

	protected void ensureVisible(E entry) {
		int rowTop = this.getRowTop(this.children().indexOf(entry));
		int topDelta = rowTop - this.getY() - 4 - this.defaultEntryHeight;

		if (topDelta < 0) {
			this.setScrollAmount(this.scrollAmount() + topDelta);
		}

		int bottomDelta = this.getBottom() - rowTop - this.defaultEntryHeight - this.defaultEntryHeight;

		if (bottomDelta < 0) {
			this.setScrollAmount(this.scrollAmount() - bottomDelta);
		}
	}

	protected void centerScrollOn(E entry) {
		this.setScrollAmount((double) (this.children().indexOf(entry) * this.defaultEntryHeight + this.defaultEntryHeight / 2 - this.getHeight() / 2));
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		boolean scrolling = this.updateScrolling(event);

		if (!this.visible || !this.isMouseOver(event.x(), event.y())) {
			return false;
		}

		E entry = this.getEntryAtPosition(event.x(), event.y());

		if (entry != null) {
			if (entry.mouseClicked(event, doubleClick)) {
				GuiEventListener focused = this.getFocused();

				if (focused != entry && focused instanceof LegacyContainerObjectSelectionList.Entry<?> containerEntry) {
					containerEntry.setFocused((GuiEventListener) null);
				}

				this.setFocused(entry);
				this.setDragging(true);

				return true;
			}
		} else if (this.clickedHeader((int) (event.x() - (double) (this.getX() + this.width / 2 - this.getRowWidth() / 2)), (int) (event.y() - (double) this.getY()) + (int) this.scrollAmount() - 4)) {
			return true;
		}

		return scrolling;
	}

	protected boolean clickedHeader(int x, int y) {
		return false;
	}

	@Override
	public Optional<GuiEventListener> getChildAt(double x, double y) {
		return Optional.ofNullable(this.getEntryAtPosition(x, y));
	}

	protected void extractListBackground(GuiGraphicsExtractor guiGraphics) {
		Identifier texture = this.minecraft.level == null ? MENU_LIST_BACKGROUND : INWORLD_MENU_LIST_BACKGROUND;
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.getX(), this.getY(), (float) this.getRight(), (float) (this.getBottom() + (int) this.scrollAmount()), this.getWidth(), this.getHeight(), 32, 32);
	}

	protected void extractListSeparators(GuiGraphicsExtractor guiGraphics) {
	}

	public void updateSizeAndPosition(int width, int height, int y) {
		this.setSize(width, height);
		this.setY(y);
		this.refreshScrollAmount();
	}

	public void updateSizeAndPosition(int width, int height, int x, int y) {
		this.setX(x);
		this.updateSizeAndPosition(width, height, y);
	}

	protected void extractHeader(GuiGraphicsExtractor guiGraphics, int x, int y) {
	}

	@Override
	public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.hovered = this.isMouseOver((double) mouseX, (double) mouseY) ? this.getEntryAtPosition((double) mouseX, (double) mouseY) : null;
		this.extractListBackground(guiGraphics);
		guiGraphics.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());

		if (this.renderHeader) {
			this.extractHeader(guiGraphics, this.getRowLeft(), this.getY() + 4 - (int) this.scrollAmount());
		}

		this.extractListItems(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.disableScissor();
		this.extractListSeparators(guiGraphics);
		this.extractScrollbar(guiGraphics, mouseX, mouseY);
	}

	protected void extractListItems(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
		int rowLeft = this.getRowLeft();
		int rowWidth = this.getRowWidth();
		int itemHeight = this.defaultEntryHeight;
		int itemCount = this.getItemCount();

		for (int index = 0; index < itemCount; ++index) {
			int rowTop = this.getRowTop(index);
			int rowBottom = this.getRowBottom(index);

			if (rowBottom >= this.getY() && rowTop <= this.getBottom()) {
				this.renderItem(guiGraphics, mouseX, mouseY, partialTick, index, rowLeft, rowTop, rowWidth, itemHeight);
			}
		}
	}

	protected void renderItem(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick, int index, int rowLeft, int rowTop, int rowWidth, int itemHeight) {
		E entry = this.getEntry(index);
		entry.renderBack(guiGraphics, index, rowTop, rowLeft, rowWidth, itemHeight, mouseX, mouseY, Objects.equals(this.hovered, entry), partialTick);

		if (this.isSelectedItem(index)) {
			int outline = this.isFocused() ? -1 : -8355712;
			this.renderSelection(guiGraphics, rowTop, rowWidth, itemHeight, outline, -16777216);
		}

		entry.extractRenderState(guiGraphics, index, rowTop, rowLeft, rowWidth, itemHeight, mouseX, mouseY, Objects.equals(this.hovered, entry), partialTick);
	}

	protected void renderSelection(GuiGraphicsExtractor guiGraphics, int top, int width, int height, int outerColor, int innerColor) {
		int left = this.getX() + (this.width - width) / 2;
		int right = this.getX() + (this.width + width) / 2;
		guiGraphics.fill(left, top - 2, right, top + height + 2, outerColor);
		guiGraphics.fill(left + 1, top - 1, right - 1, top + height + 1, innerColor);
	}

	@Nullable
	protected E getHovered() {
		return this.hovered;
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		output.add(NarratedElementType.TITLE, Component.empty());
	}

	/** Binds entries to this list when they are inserted, as the vanilla tracked list did. */
	private class TrackedList extends ArrayList<E> {
		@Override
		public boolean add(E e) {
			e.list = LegacySelectionList.this;
			return super.add(e);
		}

		@Override
		public void add(int index, E e) {
			e.list = LegacySelectionList.this;
			super.add(index, e);
		}

		@Override
		public E set(int index, E e) {
			e.list = LegacySelectionList.this;
			return super.set(index, e);
		}

		@Override
		public boolean addAll(Collection<? extends E> c) {
			c.forEach(e -> e.list = LegacySelectionList.this);
			return super.addAll(c);
		}
	}

	public abstract static class Entry<E extends Entry<E>> implements GuiEventListener {
		@SuppressWarnings("rawtypes")
		LegacySelectionList list;

		public abstract void extractRenderState(GuiGraphicsExtractor guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick);

		public void renderBack(GuiGraphicsExtractor guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
		}

		public Component getNarration() {
			return Component.empty();
		}

		@Override
		public boolean isMouseOver(double mouseX, double mouseY) {
			return this.list != null && Objects.equals(this.list.getEntryAtPosition(mouseX, mouseY), this);
		}

		@Override
		public void setFocused(boolean focused) {
		}

		@Override
		public boolean isFocused() {
			return this.list != null && this.list.getFocused() == this;
		}
	}
}
