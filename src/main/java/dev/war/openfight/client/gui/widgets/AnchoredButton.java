package dev.war.openfight.client.gui.widgets;

import dev.war.openfight.client.gui.compat.LegacyButton;
import net.minecraft.client.renderer.RenderPipelines;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import dev.war.openfight.client.gui.widgets.common.AnchoredWidget;
import dev.war.openfight.client.gui.widgets.common.ThemeApplicableWidget;
import dev.war.openfight.client.gui.widgets.common.WidgetTheme;
import dev.war.openfight.main.EpicFightMod;

import org.jetbrains.annotations.Nullable;
import java.util.function.Function;

public class AnchoredButton extends LegacyButton implements AnchoredWidget, ThemeApplicableWidget<AnchoredButton.ButtonTheme> {
    protected final Function<AbstractWidget, Boolean> highlightWhen;
    protected ButtonTheme theme;

    protected AnchoredButton(
        int x1,
        int x2,
        int y1,
        int y2,
        Component message,
        Button.OnPress onPress,
        Button.CreateNarration createNarration,
        AnchoredWidget.HorizontalAnchorType horizontalAnchorType,
        VerticalAnchorType verticalAnchorType,
        AnchoredButton.ButtonTheme theme,
        Function<AbstractWidget, Boolean> highlightWhen
    ) {
        super(0, 0, 0, 0, message, onPress, createNarration);

        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.horizontalAnchorType = horizontalAnchorType;
        this.verticalAnchorType = verticalAnchorType;
        this.theme = theme;
        this.highlightWhen = highlightWhen;
    }

    protected AnchoredButton(AnchoredButton.Builder builder) {
        this(
            builder.x1,
            builder.x2,
            builder.y1,
            builder.y2,
            builder.message,
            builder.onPress,
            builder.createNarration,
            builder.horizontalAnchorType,
            builder.verticalAnchorType,
            builder.buttonTheme,
            builder.highlihgtFontWhen
        );

        this.setTooltip(builder.tooltip);
        this.setAlpha(builder.alpha);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.theme.renderBackground(this, guiGraphics, mouseX, mouseY, partialTick);
        int color = this.getFGColor();
        Font font = Minecraft.getInstance().font;
        int textColor = (color & 0x00FFFFFF) | Mth.ceil(this.alpha * 255.0F) << 24;
        guiGraphics.centeredText(font, this.getMessage(), this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - font.lineHeight) / 2, textColor);
    }

    public int getFGColor() {
        return this.highlightWhen.apply(this) ? 0xFFFFFFFF : this.active ? 0xFFB6B6B6 : 0xFF1A1A1A;
    }

    @Override
    public boolean isSupportedTheme(WidgetTheme theme) {
        return ButtonTheme.class.isAssignableFrom(theme.getClass());
    }

    @Override
    public ButtonTheme getTheme() {
        return this.theme;
    }

    @Override
    public void setTheme(ButtonTheme theme) {
        this.theme = theme;
    }

    public interface ButtonTheme extends WidgetTheme {
        void renderBackground(AnchoredButton widget, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick);
    }

    @FunctionalInterface
    public interface ButtonRenderer {
        void renderBackground(AnchoredButton widget, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick);
    }

    static final WidgetSprites NUMBERED_TAB_SPRITES = new WidgetSprites(
        EpicFightMod.identifier("widget/numbered_tab_button"),
        EpicFightMod.identifier("widget/numbered_tab_button_disabled"),
        EpicFightMod.identifier("widget/numbered_tab_button_highlighted")
    );

    static final WidgetSprites EMOTE_WHEEL_UP_SPRITES = new WidgetSprites(
        EpicFightMod.identifier("widget/emote_wheel_up"),
        EpicFightMod.identifier("widget/emote_wheel_up"),
        EpicFightMod.identifier("widget/emote_wheel_up_highlighted")
    );

    static final WidgetSprites EMOTE_WHEEL_DOWN_SPRITES = new WidgetSprites(
        EpicFightMod.identifier("widget/emote_wheel_down"),
        EpicFightMod.identifier("widget/emote_wheel_down"),
        EpicFightMod.identifier("widget/emote_wheel_down_highlighted")
    );

    public enum BuiltInTheme implements ButtonTheme {
        VANILLA(
            (AnchoredButton widget, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) -> {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITES.get(widget.active, widget.highlightWhen.apply(widget)), widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight());
            }
        ),
        BLACK(
            (AnchoredButton widget, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) -> {
                int color = widget.highlightWhen.apply(widget) ? 0xFF2A2A2A : widget.active ? 0xFF0A0A0A : 0xFF000000;
                guiGraphics.fill(widget.getX(), widget.getY(), widget.getX() + widget.getWidth(), widget.getY() + widget.getHeight(), color);
            }
        ),
        NUMBERED_TAB(
            (AnchoredButton widget, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) -> {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, NUMBERED_TAB_SPRITES.get(widget.active, widget.highlightWhen.apply(widget)), widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight());
            }
        ),
        UPSIDE_EMOTE_WHEEL(
            (AnchoredButton widget, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) -> {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, EMOTE_WHEEL_UP_SPRITES.get(widget.isActive(), widget.highlightWhen.apply(widget)), widget.getX() - 9, widget.getY() + 1, 61, 54);
            }
        ),
        DOWNSIDE_EMOTE_WHEEL(
            (AnchoredButton widget, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) -> {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, EMOTE_WHEEL_DOWN_SPRITES.get(widget.isActive(), widget.highlightWhen.apply(widget)), widget.getX() - 9, widget.getY() + 1, 61, 54);
            }
        ),
        TRANSPARENT(
            (AnchoredButton widget, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) -> {
            }
        );

        final ButtonRenderer buttonRenderer;
        final int id;

        BuiltInTheme(ButtonRenderer buttonRenderer) {
            this.buttonRenderer = buttonRenderer;
            this.id = WidgetTheme.ENUM_MANAGER.assign(this);
        }

        @Override
        public void renderBackground(AnchoredButton widget, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
            this.buttonRenderer.renderBackground(widget, guiGraphics, mouseX, mouseY, partialTick);
        }

        @Override
        public int universalOrdinal() {
            return id;
        }
    }

    public static AnchoredButton.Builder buttonBuilder(Component message, OnPress onPress) {
        return new AnchoredButton.Builder(message, onPress);
    }

    public static class Builder {
        private final Component message;
        private final OnPress onPress;
        @Nullable
        private Tooltip tooltip;
        private int x1;
        private int x2;
        private int y1;
        private int y2;
        private CreateNarration createNarration;
        private AnchoredWidget.HorizontalAnchorType horizontalAnchorType;
        private VerticalAnchorType verticalAnchorType;
        private ButtonTheme buttonTheme;
        private Function<AbstractWidget, Boolean> highlihgtFontWhen;
        private float alpha;

        private Builder(Component message, OnPress onPress) {
            this.createNarration = Button.DEFAULT_NARRATION;
            this.message = message;
            this.onPress = onPress;
            this.horizontalAnchorType = HorizontalAnchorType.LEFT_WIDTH;
            this.verticalAnchorType = VerticalAnchorType.TOP_HEIGHT;
            this.buttonTheme = BuiltInTheme.VANILLA;
            this.highlihgtFontWhen = AbstractWidget::isHoveredOrFocused;
            this.alpha = 1.0F;
        }

        public AnchoredButton.Builder xParams(int x1, int x2) {
            this.x1 = x1;
            this.x2 = x2;
            return this;
        }

        public AnchoredButton.Builder yParams(int y1, int y2) {
            this.y1 = y1;
            this.y2 = y2;
            return this;
        }

        public AnchoredButton.Builder horizontalAnchorType(AnchoredWidget.HorizontalAnchorType horizontalAnchorType) {
            this.horizontalAnchorType = horizontalAnchorType;
            return this;
        }

        public AnchoredButton.Builder verticalAnchorType(VerticalAnchorType verticalAnchorType) {
            this.verticalAnchorType = verticalAnchorType;
            return this;
        }

        public AnchoredButton.Builder tooltip(@Nullable Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public AnchoredButton.Builder createNarration(CreateNarration createNarration) {
            this.createNarration = createNarration;
            return this;
        }

        public AnchoredButton.Builder theme(ButtonTheme theme) {
            this.buttonTheme = theme;
            return this;
        }

        public AnchoredButton.Builder highlihgtFontWhen(Function<AbstractWidget, Boolean> function) {
            this.highlihgtFontWhen = function;
            return this;
        }

        public AnchoredButton.Builder alpha(float alpha) {
            this.alpha = alpha;
            return this;
        }

        public AnchoredButton build() {
            return this.build(AnchoredButton::new);
        }

        public AnchoredButton build(Function<AnchoredButton.Builder, AnchoredButton> builder) {
            return builder.apply(this);
        }
    }

    /*******************************************************************
     * {@link AnchoredWidget} implementations                         *
     *******************************************************************/
    private int x1;
    private int x2;
    private int y1;
    private int y2;
    private final AnchoredWidget.HorizontalAnchorType horizontalAnchorType;
    private final VerticalAnchorType verticalAnchorType;

    @Override
    public int getX1() {
        return this.x1;
    }

    @Override
    public int getX2() {
        return this.x2;
    }

    @Override
    public int getY1() {
        return this.y1;
    }

    @Override
    public int getY2() {
        return this.y2;
    }

    @Override
    public void setX1(int i) {
        this.x1 = i;
    }

    @Override
    public void setX2(int i) {
        this.x2 = i;
    }

    @Override
    public void setY1(int i) {
        this.y1 = i;
    }

    @Override
    public void setY2(int i) {
        this.y2 = i;
    }

    @Override
    public HorizontalAnchorType getHorizontalAnchorType() {
        return this.horizontalAnchorType;
    }

    @Override
    public VerticalAnchorType getVerticalAnchorType() {
        return this.verticalAnchorType;
    }
}
