package yesman.epicfight.client.gui.compat;

import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

/**
 * The 1.21.1 button contract on top of the 26.3 {@link Button}: subclasses can be constructed from a builder, override
 * {@link #extractContents} after calling super (the old renderWidget), query {@link #getFGColor()} and call {@link #renderString}.
 */
public class LegacyButton extends Button {
	protected int packedFGColor = -1;

	protected LegacyButton(int x, int y, int width, int height, Component message, Button.OnPress onPress, Button.CreateNarration createNarration) {
		super(x, y, width, height, message, onPress, createNarration);
	}

	protected LegacyButton(Builder builder) {
		this(builder.x, builder.y, builder.width, builder.height, builder.message, builder.onPress, builder.createNarration);
		this.setTooltip(builder.tooltip);
	}

	public static Builder legacyBuilder(Component message, Button.OnPress onPress) {
		return new Builder(message, onPress);
	}

	public void setFGColor(int color) {
		this.packedFGColor = color;
	}

	public void clearFGColor() {
		this.packedFGColor = -1;
	}

	protected boolean clicked(double x, double y) { return this.isActive() && this.isMouseOver(x, y); }

	public int getFGColor() {
		if (this.packedFGColor != -1) {
			return this.packedFGColor;
		}

		return this.active ? 0xFFFFFFFF : 0xFFA0A0A0;
	}

	/** Old no-argument press entry point. */
	public void onPress() {
		this.onPress((InputWithModifiers) null);
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.extractDefaultSprite(guiGraphics);
		this.renderString(guiGraphics, Minecraft.getInstance().font, this.getFGColor() | ARGB.as8BitChannel(this.alpha) << 24);
	}

	public void renderString(GuiGraphicsExtractor guiGraphics, Font font, int color) {
		int availableWidth = this.getWidth() - 4;
		Component message = this.getMessage();

		if (font.width(message) > availableWidth) {
			guiGraphics.text(font, font.substrByWidth(message, availableWidth).getString(), this.getX() + 2, this.getY() + (this.getHeight() - font.lineHeight) / 2 + 1, color);
		} else {
			guiGraphics.centeredText(font, message, this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - font.lineHeight) / 2 + 1, color);
		}
	}

	/** The 1.21.1 Button.Builder, whose fields are no longer accessible. */
	public static class Builder extends Button.Builder {
		protected final Component message;
		protected final Button.OnPress onPress;
		@Nullable
		protected Tooltip tooltip;
		protected int x;
		protected int y;
		protected int width = 150;
		protected int height = 20;
		protected Button.CreateNarration createNarration = Button.DEFAULT_NARRATION;

		public Builder(Component message, Button.OnPress onPress) {
			super(message, onPress);
			this.message = message;
			this.onPress = onPress;
		}

		public Builder pos(int x, int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		public Builder width(int width) {
			this.width = width;
			return this;
		}

		public Builder size(int width, int height) {
			this.width = width;
			this.height = height;
			return this;
		}

		public Builder bounds(int x, int y, int width, int height) {
			return this.pos(x, y).size(width, height);
		}

		public Builder tooltip(@Nullable Tooltip tooltip) {
			this.tooltip = tooltip;
			return this;
		}

		public Builder createNarration(Button.CreateNarration createNarration) {
			this.createNarration = createNarration;
			return this;
		}

		public LegacyButton build() {
			return this.build(LegacyButton::new);
		}

		public <B extends Button> B build(Function<Builder, B> builder) {
			return builder.apply(this);
		}
	}
}
