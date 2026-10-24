package dev.war.openfight.client.gui.screen;

import dev.war.openfight.platform.fabric.compat.SkinCompat;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import dev.war.openfight.client.gui.compat.LegacyObjectSelectionList;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import dev.war.openfight.EpicFight;
import dev.war.openfight.api.client.model.Meshes;
import dev.war.openfight.api.utils.math.Vec4f;
import dev.war.openfight.client.gui.datapack.screen.MessageScreen;
import dev.war.openfight.client.gui.widgets.AnchoredButton;
import dev.war.openfight.client.gui.widgets.EmoteWheelTab;
import dev.war.openfight.client.gui.widgets.ModelPreviewer;
import dev.war.openfight.client.gui.widgets.common.AnchoredWidget;
import dev.war.openfight.client.gui.widgets.common.PressableWidget;
import dev.war.openfight.client.cosmetics.Emote;
import dev.war.openfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import dev.war.openfight.gameasset.Armatures;
import dev.war.openfight.generated.LangKeys;
import dev.war.openfight.network.EpicFightNetworkManager;
import dev.war.openfight.network.common.BiDirectionalSyncEmoteSlots;
import dev.war.openfight.registry.EpicFightRegistries;
import dev.war.openfight.world.capabilities.emote.PlayerEmoteSlots;

import org.jetbrains.annotations.Nullable;

import static dev.war.openfight.generated.LangKeys.*;

public class EmoteEditScreen extends Screen {
    private static final int EMOTE_PREVIEWER_WIDTH = 120;

    private final EmoteSelectionList emoteSelectionList;
    private final EmoteWheelTab emoteWheelTab;
    private final ModelPreviewer emotePreviewer;
    private final AnchoredButton saveButton;
    private final AnchoredButton discardButton;

    protected EmoteEditScreen(LocalPlayerPatch playerpatch) {
        super(Component.translatable(LangKeys.GUI_TITLE_EMOTE_SETTINGS));

        // Load minecraft instance preemptively

        this.emoteSelectionList = new EmoteSelectionList(
            this.minecraft,
            200,
            15,
            50,
            35,
            AnchoredWidget.HorizontalAnchorType.LEFT_RIGHT,
            AnchoredWidget.VerticalAnchorType.TOP_BOTTOM
        );

        this.emoteWheelTab = new EmoteWheelTab(
            this.minecraft.font,
            playerpatch.getEmoteSlots(),
            playerpatch.getOriginal().getSkin(),
            10,
            180,
            25,
            0,
            AnchoredWidget.HorizontalAnchorType.LEFT_WIDTH,
            AnchoredWidget.VerticalAnchorType.TOP_BOTTOM,
            emote -> {
                this.emoteSelectionList.setSelected(null);
                this.emoteSelectionList.setFocused(null);
            },
            true
        );

        this.emotePreviewer = new ModelPreviewer(
            this.font,
            0,
            EMOTE_PREVIEWER_WIDTH,
            20,
            20,
            AnchoredWidget.HorizontalAnchorType.RIGHT_WIDTH,
            AnchoredWidget.VerticalAnchorType.TOP_BOTTOM,
            Armatures.BIPED,
            Meshes.BIPED
        );

        this.emotePreviewer.setBackgroundClearColor(new Vec4f(0.0F, 0.0F, 0.0F, 0.0F));
        this.emotePreviewer.setFigureTexture(playerpatch.getOriginal().getSkin().body().texturePath());
        this.emotePreviewer.setMesh(playerpatch.getOriginal().getSkin().model() == PlayerModelType.SLIM ? Meshes.ALEX : Meshes.BIPED);

        this.saveButton = AnchoredButton.buttonBuilder(
                Component.translatable(GUI_WIDGET_COMMON_SAVE),
                button -> {
                    this.saveChanges(playerpatch);
                    EmoteWheelScreen screen = new EmoteWheelScreen(playerpatch);
                    screen.relieveHolding();
                    this.minecraft.gui.setScreen(screen);
                }
            )
            .xParams(120, 80)
            .yParams(10, 20)
            .horizontalAnchorType(AnchoredWidget.HorizontalAnchorType.RIGHT_WIDTH)
            .verticalAnchorType(AnchoredWidget.VerticalAnchorType.BOTTOM_HEGIHT)
            .theme(AnchoredButton.BuiltInTheme.BLACK)
            .highlihgtFontWhen(AbstractWidget::isHoveredOrFocused)
            .alpha(0.6F)
            .build();

        this.discardButton = AnchoredButton.buttonBuilder(
                Component.translatable(GUI_WIDGET_COMMON_DISCARD),
                button -> {
                    if (this.emoteWheelTab.isEdited()) {
                        this.minecraft.gui.setScreen(new MessageScreen<>(
                            "",
                            Component.translatable(GUI_MESSAGE_SETTINGS_DISCARD_CHANGES_NOTIFICATION),
                            this,
                            button$2 -> {
                                EmoteWheelScreen screen = new EmoteWheelScreen(playerpatch);
                                screen.relieveHolding();
                                this.minecraft.gui.setScreen(screen);
                            },
                            button$2 -> {
                                this.minecraft.gui.setScreen(this);
                            },
                            180,
                            0
                        ).autoCalculateHeight());
                    } else {
                        EmoteWheelScreen screen = new EmoteWheelScreen(playerpatch);
                        screen.relieveHolding();
                        this.minecraft.gui.setScreen(screen);
                    }
                }
            )
            .xParams(15, 100)
            .yParams(10, 20)
            .horizontalAnchorType(AnchoredWidget.HorizontalAnchorType.RIGHT_WIDTH)
            .verticalAnchorType(AnchoredWidget.VerticalAnchorType.BOTTOM_HEGIHT)
            .theme(AnchoredButton.BuiltInTheme.BLACK)
            .highlihgtFontWhen(AbstractWidget::isHoveredOrFocused)
            .alpha(0.6F)
            .build();

        playerpatch.getLevel().holderLookup(EpicFightRegistries.Keys.EMOTE).listElements().forEach(this.emoteSelectionList::addEntry);
    }

    @Override
    public void init() {
        this.addRenderableWidget(this.emoteWheelTab);
        this.addRenderableWidget(this.emoteSelectionList);
        this.addRenderableWidget(this.emotePreviewer);
        this.addRenderableWidget(this.saveButton);
        this.addRenderableWidget(this.discardButton);
        this.repositionElements();
    }

    @Override
    public void tick() {
        this.emotePreviewer.tick();
    }

    @Override
    protected void rebuildWidgets() {
        this.emoteWheelTab.relocate(this.getRectangle());
        this.emoteSelectionList.relocate(this.getRectangle());
        this.emotePreviewer.relocate(this.emoteSelectionList.getRectangle());
        this.saveButton.relocate(this.getRectangle());
        this.discardButton.relocate(this.getRectangle());
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, Screen.HEADER_SEPARATOR, 4, 18, (float)(0.0F), (float)(0.0F), this.width - 8, 2, 32, 2);
        guiGraphics.text(this.font, this.title, 6, 7, 16777215);
    }

    @Override
    public void onClose() {
        super.onClose();
        this.emotePreviewer.onDestroy();
    }

    private void saveChanges(LocalPlayerPatch playerpatch) {
        PlayerEmoteSlots emoteSlots = playerpatch.getEmoteSlots();
        emoteSlots.reset(this.emoteWheelTab.pages());
        this.emoteWheelTab.listEmotes(emoteSlots::setEmote);

        EpicFightNetworkManager.sendToServer(new BiDirectionalSyncEmoteSlots(playerpatch));
    }

    private class EmoteSelectionList extends LegacyObjectSelectionList<EmoteSelectionList.Entry> implements AnchoredWidget {
        public EmoteSelectionList(
            Minecraft minecraft,
            int x1,
            int x2,
            int y1,
            int y2,
            AnchoredWidget.HorizontalAnchorType horizontalAnchor,
            AnchoredWidget.VerticalAnchorType verticalAnchor
        ) {
            super(minecraft, 0, 0, 0, 21);
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.horizontalAnchorType = horizontalAnchor;
            this.verticalAnchorType = verticalAnchor;
        }

        public void addEntry(Holder.Reference<Emote> emote) {
            if (emote.value().animation().get() == null) {
                EpicFight.LOGGER.error("Emote animation not found: {}", emote.value().animation().registryName());
                return;
            }

            this.children().add(new Entry(emote));
        }

        @Override
        protected void extractListItems(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
            guiGraphics.enableScissor(this.getX(), this.getY(), this.getX() + this.getRowWidth() + 2, this.getY() + this.getHeight());
            super.extractListItems(guiGraphics, mouseX, mouseY, partialTick);
            guiGraphics.disableScissor();
        }

        @Override
        protected void renderSelection(GuiGraphicsExtractor guiGraphics, int top, int width, int height, int outerColor, int innerColor) {
            if (!this.isFocused()) {
                return;
            }

            int left = this.getX();
            int right = this.getRowRight();

            guiGraphics.fill(left, top - 2, right, top + height + 2, outerColor);
            guiGraphics.fill(left + 1, top - 1, right - 1, top + height + 1, innerColor);
        }

        @Override
        public int getRowLeft() {
            return this.getX() + 2;
        }

        @Override
        public int getRowWidth() {
            return this.getWidth() - EMOTE_PREVIEWER_WIDTH;
        }

        @Override @Nullable
        protected EmoteSelectionList.Entry getEntryAtPosition(double mouseX, double mouseY) {
            int xStart = this.getX();
            int xEnd = xStart + this.getRowWidth();
            int mouseRelativeY = Mth.floor(mouseY - (double)this.getY()) - this.headerHeight + (int)this.scrollAmount() - 4;
            int rowPosition = mouseRelativeY / this.defaultEntryHeight;
            return mouseX >= xStart && mouseX <= xEnd && rowPosition >= 0 && mouseRelativeY >= 0 && rowPosition < this.getItemCount() ? this.children().get(rowPosition) : null;
        }

        @Override
        public @NotNull ScreenRectangle getRectangle() {
            return new ScreenRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }

        @Override
        protected int getScrollbarPosition() {
            return this.getRight() - 6;
        }

        private class Entry extends LegacyObjectSelectionList.Entry<EmoteSelectionList.Entry> implements PressableWidget {
            private final Holder.Reference<Emote> emote;

            private Entry(Holder.Reference<Emote> emote) {
                this.emote = emote;
            }

            @Override
            public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
double mouseX = event.x(); double mouseY = event.y(); int button = event.button();
                this.onPressed();
                return true;
            }

            @Override
            public boolean keyPressed(KeyEvent event) {
int keyCode = event.key(); int scanCode = event.keycode(); int modifiers = event.modifiers();
                if (event.isSelection()) {
                    this.onPressed();
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onPressed() {
                if (emoteWheelTab.getLastPressedWheel() != null) {
                    emoteWheelTab.getLastPressedWheel().setEmote(this.emote);
                }
            }

            @Override
            public void setFocused(boolean flag) {
                super.setFocused(flag);

                if (flag) {
                    emotePreviewer.setCameraTransform(
                        this.emote.value().previewCameraTransform().zoom(),
                        this.emote.value().previewCameraTransform().xRot(),
                        this.emote.value().previewCameraTransform().yRot(),
                        this.emote.value().previewCameraTransform().xMove(),
                        this.emote.value().previewCameraTransform().yMove()
                    );

                    emotePreviewer.clearAnimations();
                    emotePreviewer.addAnimationToPlay(this.emote.value().animation());
                }
            }

            @Override
            public @NotNull Component getNarration() {
                return Component.translatable("narrator.select", this.emote.value().title());
            }

            @Override
            public void extractRenderState(GuiGraphicsExtractor guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
                guiGraphics.text(font, Component.translatable(this.emote.value().title()), left + 2, top + height / 2 - 3, -1);
            }
        }

        /// ***************************************************************
        /// [AnchoredWidget] implementations                              *
        /// ***************************************************************
        private int x1;
        private int x2;
        private int y1;
        private int y2;
        private final AnchoredWidget.HorizontalAnchorType horizontalAnchorType;
        private final AnchoredWidget.VerticalAnchorType verticalAnchorType;

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
}
