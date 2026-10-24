package dev.war.openfight.client.gui.screen.config;

import dev.war.openfight.client.gui.compat.LegacyButton;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec2;
import dev.war.openfight.api.utils.math.Vec2i;
import dev.war.openfight.client.gui.ScreenCalculations.AlignDirection;
import dev.war.openfight.client.gui.ScreenCalculations.HorizontalBasis;
import dev.war.openfight.client.gui.ScreenCalculations.VerticalBasis;
import dev.war.openfight.config.ClientConfig;
import dev.war.openfight.config.OptionHandler;
import dev.war.openfight.generated.LangKeys;
import dev.war.openfight.main.EpicFightMod;

public class HUDLocationsScreen extends Screen {
	protected final Screen parentScreen;
	private HUDComponent draggingButton;

	public HUDLocationsScreen(Screen parentScreen) {
		super(Component.literal(LangKeys.GUI_TITLE_SETTINGS_UI_HUD_LOCATIONS));
		
		this.parentScreen = parentScreen;
	}

	@Override
	public void init() {
		this.renderables.clear();
		
		int weaponInnateX = ClientConfig.weaponInnateBaseX.positionGetter.apply(this.width, ClientConfig.weaponInnateX);
		int weaponInnateY = ClientConfig.weaponInnateBaseY.positionGetter.apply(this.height, ClientConfig.weaponInnateY);
		
		OptionHandler<Integer> weaponInnateXHandler = OptionHandler.of(ClientConfig.weaponInnateX, (val) -> ClientConfig.weaponInnateX = val);
		OptionHandler<Integer> weaponInnateYHandler = OptionHandler.of(ClientConfig.weaponInnateY, (val) -> ClientConfig.weaponInnateY = val);
		OptionHandler<HorizontalBasis> weaponInnateBaseXHandler = OptionHandler.of(ClientConfig.weaponInnateBaseX, (val) -> ClientConfig.weaponInnateBaseX = val);
		OptionHandler<VerticalBasis> weaponInnateBaseYHandler = OptionHandler.of(ClientConfig.weaponInnateBaseY, (val) -> ClientConfig.weaponInnateBaseY = val);
		
		// Weapon innate icon
		this.addRenderableWidget(new HUDComponent(weaponInnateX, weaponInnateY, weaponInnateXHandler, weaponInnateYHandler, weaponInnateBaseXHandler, weaponInnateBaseYHandler,
			32, 32, 0, 0, 1, 1, 1, 1, 0, 163, 184, this, EpicFightMod.identifier("textures/gui/skills/weapon_innate/sweeping_edge.png")
		));
		
		int staminaX = ClientConfig.staminaBarBaseX.positionGetter.apply(this.width, ClientConfig.staminaBarX);
		int staminaY = ClientConfig.staminaBarBaseY.positionGetter.apply(this.height, ClientConfig.staminaBarY);
		OptionHandler<Integer> staminaBarXHandler = OptionHandler.of(ClientConfig.staminaBarX, (val) -> ClientConfig.staminaBarX = val);
		OptionHandler<Integer> staminaBarYHandler = OptionHandler.of(ClientConfig.staminaBarY, (val) -> ClientConfig.staminaBarY = val);
		OptionHandler<HorizontalBasis> staminaBarBaseXHandler = OptionHandler.of(ClientConfig.staminaBarBaseX, (val) -> ClientConfig.staminaBarBaseX = val);
		OptionHandler<VerticalBasis> staminaBarBaseYHandler = OptionHandler.of(ClientConfig.staminaBarBaseY, (val) -> ClientConfig.staminaBarBaseY = val);
		
		// Stamina bar
		this.addRenderableWidget(new HUDComponent(staminaX, staminaY, staminaBarXHandler, staminaBarYHandler, staminaBarBaseXHandler, staminaBarBaseYHandler,
			118, 4, 2, 38, 237, 9, 256, 256, 255, 128, 64, this, EpicFightMod.identifier("textures/gui/battle_icons.png")
		));
		
		int chargingBarX = ClientConfig.chargingBarBaseX.positionGetter.apply(this.width, ClientConfig.chargingBarX);
		int chargingBarY = ClientConfig.chargingBarBaseY.positionGetter.apply(this.height, ClientConfig.chargingBarY);
		OptionHandler<Integer> chargingBarXHandler = OptionHandler.of(ClientConfig.chargingBarX, (val) -> ClientConfig.chargingBarX = val);
		OptionHandler<Integer> chargingBarYHandler = OptionHandler.of(ClientConfig.chargingBarY, (val) -> ClientConfig.chargingBarY = val);
		OptionHandler<HorizontalBasis> chargingBarBaseXHandler = OptionHandler.of(ClientConfig.chargingBarBaseX, (val) -> ClientConfig.chargingBarBaseX = val);
		OptionHandler<VerticalBasis> chargingBarBaseYHandler = OptionHandler.of(ClientConfig.chargingBarBaseY, (val) -> ClientConfig.chargingBarBaseY = val);
		
		// Charging bar
		this.addRenderableWidget(new HUDComponent(chargingBarX, chargingBarY, chargingBarXHandler, chargingBarYHandler, chargingBarBaseXHandler, chargingBarBaseYHandler,
			238, 13, 1, 71, 237, 13, 256, 256, 255, 255, 255, this, EpicFightMod.identifier("textures/gui/battle_icons.png")
		));
		
		int passiveX = ClientConfig.passiveBaseX.positionGetter.apply(this.width, ClientConfig.passiveX);
		int passiveY = ClientConfig.passiveBaseY.positionGetter.apply(this.height, ClientConfig.passiveY);
		OptionHandler<Integer> passiveXHandler = OptionHandler.of(ClientConfig.passiveX, (val) -> ClientConfig.passiveX = val);
		OptionHandler<Integer> passiveYHandler = OptionHandler.of(ClientConfig.passiveY, (val) -> ClientConfig.passiveY = val);
		OptionHandler<HorizontalBasis> passiveBaseXHandler = OptionHandler.of(ClientConfig.passiveBaseX, (val) -> ClientConfig.passiveBaseX = val);
		OptionHandler<VerticalBasis> passiveBaseYHandler = OptionHandler.of(ClientConfig.passiveBaseY, (val) -> ClientConfig.passiveBaseY = val);
		OptionHandler<AlignDirection> passiveAlignDirectionHandler = OptionHandler.of(ClientConfig.passiveAlignDirection, (val) -> ClientConfig.passiveAlignDirection = val);
		
		// Passive skill icons
		this.addRenderableWidget(new PassiveUIComponent(passiveX, passiveY, passiveXHandler, passiveYHandler, passiveBaseXHandler, passiveBaseYHandler, passiveAlignDirectionHandler
			, 24, 24, 0, 0, 1, 1, 1, 1, 255, 255, 255, this, EpicFightMod.identifier("textures/gui/skills/guard/guard.png"), EpicFightMod.identifier("textures/gui/skills/passive/berserker.png")
		));
		
		this.addRenderableWidget(
			Button.builder(Component.literal("⟳"), button -> {
				ClientConfig.weaponInnateX = ClientConfig.WEAPON_INNATE_X.getDefault();
				ClientConfig.weaponInnateY = ClientConfig.WEAPON_INNATE_Y.getDefault();
				ClientConfig.weaponInnateBaseX = ClientConfig.WEAPON_INNATE_BASE_X.getDefault();
				ClientConfig.weaponInnateBaseY = ClientConfig.WEAPON_INNATE_BASE_Y.getDefault();
				ClientConfig.staminaBarX = ClientConfig.STAMINA_BAR_X.getDefault();
				ClientConfig.staminaBarY = ClientConfig.STAMINA_BAR_Y.getDefault();
				ClientConfig.staminaBarBaseX = ClientConfig.STAMINA_BAR_BASE_X.getDefault();
				ClientConfig.staminaBarBaseY = ClientConfig.STAMINA_BAR_BASE_Y.getDefault();
				ClientConfig.chargingBarX = ClientConfig.CHARGING_BAR_X.getDefault();
				ClientConfig.chargingBarY = ClientConfig.CHARGING_BAR_Y.getDefault();
				ClientConfig.chargingBarBaseX = ClientConfig.CHARGING_BAR_BASE_X.getDefault();
				ClientConfig.chargingBarBaseY = ClientConfig.CHARGING_BAR_BASE_Y.getDefault();
				ClientConfig.passiveX = ClientConfig.PASSIVE_X.getDefault();
				ClientConfig.passiveY = ClientConfig.PASSIVE_Y.getDefault();
				ClientConfig.passiveBaseX = ClientConfig.PASSIVE_BASE_X.getDefault();
				ClientConfig.passiveBaseY = ClientConfig.PASSIVE_BASE_Y.getDefault();
				ClientConfig.passiveAlignDirection = ClientConfig.PASSIVE_ALIGN_DIRECTION.getDefault();
				this.init();
			}).bounds(this.width-14, 0, 14, 14).build()
		);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
double x = event.x(); double y = event.y(); int pressType = event.button();
		for (GuiEventListener guieventlistener : this.children()) {
			if (guieventlistener instanceof HUDComponent HUDComponent) {
				if (HUDComponent.popupScreen.isOpen() && HUDComponent.popupScreen.mouseClicked(event, doubleClick)) {
					this.setFocused(guieventlistener);

					if (pressType == 0) {
						this.setDragging(true);
					}

					return true;
				}
			}

			if (guieventlistener.mouseClicked(event, doubleClick)) {
				this.setFocused(guieventlistener);

				if (pressType == 0) {
					this.setDragging(true);
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.extractBackground(guiGraphics, mouseX, mouseY, partialTicks);
		super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void onClose() {
		this.minecraft.gui.setScreen(this.parentScreen);
	}

	public void beginToDrag(HUDComponent button) {
		this.draggingButton = button;
	}

	public void endDragging() {
		this.draggingButton = null;
	}

	public boolean isDraggingComponent(HUDComponent button) {
		return this.draggingButton == button;
	}

    public static class HUDComponent extends LegacyButton {
        protected final HUDLocationsScreen parentScreen;
        protected final Identifier texture;
        protected int texU;
        protected int texV;
        protected int texW;
        protected int texH;
        protected int resolutionDivW;
        protected int resolutionDivH;
        protected int draggingTime;
        protected float r;
        protected float g;
        protected float b;
        private double pressX;
        private double pressY;
        public final OptionHandler<Integer> xCoord;
        public final OptionHandler<Integer> yCoord;
        public final OptionHandler<HorizontalBasis> horizontalBasis;
        public final OptionHandler<VerticalBasis> verticalBasis;

        public UIComponentPop<?> popupScreen;

        public HUDComponent(
            int x,
            int y,
            OptionHandler<Integer> xCoord,
            OptionHandler<Integer> yCoord,
            OptionHandler<HorizontalBasis> horizontalBasis,
            OptionHandler<VerticalBasis> verticalBasis,
            int width,
            int height,
            int texU,
            int texV,
            int texW,
            int texH,
            int resolutionDivW,
            int resolutionDivH,
            int r,
            int g,
            int b,
            HUDLocationsScreen parentScreen,
            Identifier texture
        ) {

            super(x, y, width, height, Component.literal(""), (button) -> {}, Button.DEFAULT_NARRATION);

            this.texture = texture;
            this.texU = texU;
            this.texV = texV;
            this.texW = texW;
            this.texH = texH;
            this.resolutionDivW = resolutionDivW;
            this.resolutionDivH = resolutionDivH;
            this.r = r / 255.0F;
            this.g = g / 255.0F;
            this.b = b / 255.0F;

            this.xCoord = xCoord;
            this.yCoord = yCoord;
            this.horizontalBasis = horizontalBasis;
            this.verticalBasis = verticalBasis;
            this.parentScreen = parentScreen;
            this.popupScreen = new UIComponentPop<>(30, 30, this);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
double mouseX = event.x(); double mouseY = event.y(); int button = event.button();
            if (this.active && this.visible) {
                if (this.isValidClickButton(event.buttonInfo())) {
                    this.draggingTime = 0;

                    if (this.isMouseOver(mouseX, mouseY)) {
                        this.parentScreen.beginToDrag(this);
                        this.pressX = mouseX - this.getX();
                        this.pressY = mouseY - this.getY();
                        this.playDownSound(Minecraft.getInstance().getSoundManager());

                        if (!this.popupScreen.isHoverd(getX(), getY())) {
                            this.popupScreen.closePop();
                        }

                        return true;
                    }
                }

                return false;
            } else {
                return false;
            }
        }

        @Override
        protected void onDrag(MouseButtonEvent event, double dx, double dy) {
double x = event.x(); double y = event.y();
            if (this.parentScreen.isDraggingComponent(this)) {
                this.setX((int)(x - this.pressX));
                this.setY((int)(y - this.pressY));
                this.draggingTime++;
            }
        }

        @Override
        public boolean mouseReleased(MouseButtonEvent event) {
double mouseX = event.x(); double mouseY = event.y(); int button = event.button();
            if (this.isValidClickButton(event.buttonInfo())) {
                this.onRelease(event);
                this.parentScreen.endDragging();

                int xCoord = this.horizontalBasis.getValue().saveCoordGetter.apply(this.parentScreen.width, getX());
                int yCoord = this.verticalBasis.getValue().saveCoordGetter.apply(this.parentScreen.height, getY());

                this.xCoord.setValue(xCoord);
                this.yCoord.setValue(yCoord);

                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onRelease(MouseButtonEvent event) {
double x = event.x(); double y = event.y();
            if (!this.popupScreen.isOpen() && this.draggingTime < 2) {
                if (x + this.popupScreen.width > this.parentScreen.width) {
                    this.popupScreen.x = (int)x - this.popupScreen.width;
                } else {
                    this.popupScreen.x = (int)x;
                }

                if (y + this.popupScreen.height > this.parentScreen.height) {
                    this.popupScreen.y = (int)y - this.popupScreen.height;
                } else {
                    this.popupScreen.y = (int)y;
                }

                this.popupScreen.openPop();
            }
        }

        public void drawOutline(GuiGraphicsExtractor guiGraphics) {
            int x=this.getX()-1, y=this.getY()-1, r=this.getRight()+1, b=this.getBottom()+1, color=0xFF45A6F4;
            guiGraphics.fill(x,y,r,y+2,color); guiGraphics.fill(x,b-2,r,b,color);
            guiGraphics.fill(x,y,x+2,b,color); guiGraphics.fill(r-2,y,r,b,color);
            int ax=this.horizontalBasis.getValue()==HorizontalBasis.CENTER?this.parentScreen.width/2:this.horizontalBasis.getValue()==HorizontalBasis.LEFT?0:this.parentScreen.width;
            int ay=this.verticalBasis.getValue()==VerticalBasis.CENTER?this.parentScreen.height/2:this.verticalBasis.getValue()==VerticalBasis.TOP?0:this.parentScreen.height;
            guiGraphics.fill(Math.min(x,ax),y,Math.max(x,ax)+1,y+1,color);
            guiGraphics.fill(x,Math.min(y,ay),x+1,Math.max(y,ay)+1,color);
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor guiGraphics, int x, int y, float partialTicks) {

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), (float)(this.texU), (float)(this.texV), this.width, this.height, this.texW, this.texH, this.resolutionDivW, this.resolutionDivH);

            if (this.isHoveredOrFocused() || this.popupScreen.isOpen()) {
                this.drawOutline(guiGraphics);
            }

            if (this.popupScreen.isOpen()) {
                this.popupScreen.extractRenderState(guiGraphics, x, y, partialTicks);
            }
        }
    }

    public static class PassiveUIComponent extends HUDComponent {
        public final OptionHandler<AlignDirection> alignDirection;
        protected final Identifier texture2;

        public PassiveUIComponent(int x, int y, OptionHandler<Integer> xCoord, OptionHandler<Integer> yCoord, OptionHandler<HorizontalBasis> horizontalBasis, OptionHandler<VerticalBasis> verticalBasis, OptionHandler<AlignDirection> alignDirection
            , int width, int height, int texU, int texV, int texW, int texH, int resolutionDivW, int resolutionDivH, int r, int g, int b, HUDLocationsScreen parentScreen, Identifier texture, Identifier texture2) {
            super(x, y, xCoord, yCoord, horizontalBasis, verticalBasis, width, height, texU, texV, texW, texH, resolutionDivW, resolutionDivH, r, g, b, parentScreen, texture);

            this.popupScreen = new PassivesUIComponentPop(30, 44, this);
            this.alignDirection = alignDirection;
            this.texture2 = texture2;
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor guiGraphics, int x, int y, float partialTicks) {
            Vec2i startPos = this.alignDirection.getValue().startCoordGetter.get(getX(), getY(), this.width, this.height, 2, this.horizontalBasis.getValue(), this.verticalBasis.getValue());


            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.texture, startPos.x, startPos.y, (float)(this.texU), (float)(this.texV), this.width, this.height, this.texW, this.texH, this.resolutionDivW, this.resolutionDivH);

            if (this.isHoveredOrFocused() || this.popupScreen.isOpen()) {
                this.drawOutline(guiGraphics);
            }

            if (this.popupScreen.isOpen()) {
                this.popupScreen.extractRenderState(guiGraphics, x, y, partialTicks);
            }

            Vec2i nextPos = this.alignDirection.getValue().nextPositionGetter.getNext(this.horizontalBasis.getValue(), this.verticalBasis.getValue(), startPos, this.width, this.height);

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture2, nextPos.x, nextPos.y, (float)(this.texU), (float)(this.texV), this.width, this.height, this.texW, this.texH, this.resolutionDivW, this.resolutionDivH);
        }
    }

    public static class UIComponentPop<T extends HUDComponent> extends Screen implements ContainerEventHandler {
        protected final T parentWidget;
        protected int width;
        protected int height;
        public int x;
        public int y;
        private boolean enable;

        public UIComponentPop(int width, int height, T parentWidget) {
            super(Component.literal(""));

            this.width = width;
            this.height = height;
            this.parentWidget = parentWidget;

            this.init();
        }

        @Override
        public void init() {
            this.clearWidgets();

            this.addRenderableWidget(createButton(this.x + 10, this.y - 2, 11, 8, (button) -> {
                this.parentWidget.verticalBasis.setValue(VerticalBasis.TOP);
                this.parentWidget.yCoord.setValue(VerticalBasis.TOP.saveCoordGetter.apply(this.parentWidget.parentScreen.height, this.y));
            }));

            this.addRenderableWidget(createButton(this.x - 2, this.y + 11, 11, 7, (button) -> {
                this.parentWidget.horizontalBasis.setValue(HorizontalBasis.LEFT);
                this.parentWidget.xCoord.setValue(HorizontalBasis.LEFT.saveCoordGetter.apply(this.parentWidget.parentScreen.width, this.x));
            }));

            this.addRenderableWidget(createButton(this.x + 22, this.y + 11, 11, 7, (button) -> {
                this.parentWidget.horizontalBasis.setValue(HorizontalBasis.RIGHT);
                this.parentWidget.xCoord.setValue(HorizontalBasis.RIGHT.saveCoordGetter.apply(this.parentWidget.parentScreen.width, this.x));
            }));

            this.addRenderableWidget(createButton(this.x + 10, this.y + 24, 11, 8, (button) -> {
                this.parentWidget.verticalBasis.setValue(VerticalBasis.BOTTOM);
                this.parentWidget.yCoord.setValue(VerticalBasis.BOTTOM.saveCoordGetter.apply(this.parentWidget.parentScreen.height, this.y));
            }));

            this.addRenderableWidget(createButton(this.x + 10, this.y + 11, 11, 7, (button) -> {
                this.parentWidget.verticalBasis.setValue(VerticalBasis.CENTER);
                this.parentWidget.horizontalBasis.setValue(HorizontalBasis.CENTER);
                this.parentWidget.xCoord.setValue(HorizontalBasis.CENTER.saveCoordGetter.apply(this.parentWidget.parentScreen.width, this.x));
                this.parentWidget.yCoord.setValue(VerticalBasis.CENTER.saveCoordGetter.apply(this.parentWidget.parentScreen.height, this.y));
            }));
        }

        public static Button createButton(int x, int y, int width, int height, Button.OnPress onpress) {
            return Button.builder(Component.literal(""), onpress).bounds(x, y, width, height).build();
        }

        public void openPop() {
            this.enable = true;
            this.init();
        }

        public void closePop() {
            this.enable = false;
        }

        protected boolean isHoverd(double x, double y) {
            return this.enable && x >= this.x && y >= this.y && x < (this.x + this.width) && y < (this.y + this.height);
        }

        public boolean isOpen() {
            return this.enable;
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
double x = event.x(); double y = event.y(); int pressType = event.button();
            if (this.enable) {
                boolean clicked = false;

                for (GuiEventListener listener : this.children()) {
                    clicked |= listener.mouseClicked(event, doubleClick);
                }

                return clicked;
            } else {
                return false;
            }
        }

        @Override
        public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
            if (this.enable) {
                boolean popupOut = mouseX < this.x - 3 || mouseY < this.y - 3 || mouseX >= this.x + this.width + 3 || mouseY >= this.y + this.height + 3;
                boolean parentOut = mouseX < this.parentWidget.getX() - 3 || mouseY < this.parentWidget.getY() - 3
                    || mouseX >= this.parentWidget.getX() + this.parentWidget.getWidth() + 3 || mouseY >= this.parentWidget.getY() + this.parentWidget.getHeight() + 3;

                if (popupOut && parentOut) {
                    this.enable = false;
                }

                org.joml.Matrix3x2fStack poseStack = guiGraphics.pose();
                poseStack.pushMatrix();
                guiGraphics.nextStratum(); // zlevel
                this.renderPopup(guiGraphics, this.x, this.y, this.width, this.height);

                for(Renderable renderable : this.renderables) {
                    renderable.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
                }

                poseStack.popMatrix();
            }
        }

        protected void renderPopup(GuiGraphicsExtractor guiGraphics, int x, int y, int width, int height) {
            int i = width;
            int j = height;
            int j2 = x;
            int k2 = y;


            int backgroundStart = 0xf0100010;
            int backgroundEnd = 0xf0100010;
            int boarderStart = 0x505000FF;
            int boarderEnd = 0x5028007F;
            guiGraphics.fillGradient(j2 - 3, k2 - 4, j2 + i + 3, k2 - 3, 0, backgroundStart);
            guiGraphics.fillGradient(j2 - 3, k2 + j + 3, j2 + i + 3, k2 + j + 4, 0, backgroundEnd);
            guiGraphics.fillGradient(j2 - 3, k2 - 3, j2 + i + 3, k2 + j + 3, 0, backgroundStart);
            guiGraphics.fillGradient(j2 - 4, k2 - 3, j2 - 3, k2 + j + 3, 0, backgroundStart);
            guiGraphics.fillGradient(j2 + i + 3, k2 - 3, j2 + i + 4, k2 + j + 3, 0, backgroundStart);
            guiGraphics.fillGradient(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + j + 3 - 1, 0, boarderStart);
            guiGraphics.fillGradient(j2 + i + 2, k2 - 3 + 1, j2 + i + 3, k2 + j + 3 - 1, 0, boarderStart);
            guiGraphics.fillGradient(j2 - 3, k2 - 3, j2 + i + 3, k2 - 3 + 1, 0, boarderStart);
            guiGraphics.fillGradient(j2 - 3, k2 + j + 2, j2 + i + 3, k2 + j + 3, 0, boarderEnd);
        }
    }

    public static class PassivesUIComponentPop extends UIComponentPop<PassiveUIComponent> {
        public PassivesUIComponentPop(int width, int height, PassiveUIComponent parentWidget) {
            super(width, height, parentWidget);
        }

        @Override
        protected void renderPopup(GuiGraphicsExtractor guiGraphics, int x, int y, int width, int height) {
            super.renderPopup(guiGraphics, x, y + 14, width, height - 14);
        }

        @Override
        public void init() {
            super.init();

            for (GuiEventListener gui : this.children()) {
                if (gui instanceof AbstractWidget widget) {
                    widget.setY(widget.getY() + 14);
                }
            }

            this.addRenderableWidget(new PassivesUIComponentPop.AlignButton(this.x - 3, this.y, 12, 10, this.parentWidget.horizontalBasis, this.parentWidget.verticalBasis, this.parentWidget.alignDirection, (button) -> {
                AlignDirection newAlignDirection = AlignDirection.values()[(this.parentWidget.alignDirection.getValue().ordinal() + 1) % AlignDirection.values().length];
                this.parentWidget.alignDirection.setValue(newAlignDirection);
            }));
        }

        public static class AlignButton extends LegacyButton {
            private static final Identifier BATTLE_ICONS = EpicFightMod.identifier("textures/gui/battle_icons.png");
            private final OptionHandler<HorizontalBasis> horBasis;
            private final OptionHandler<VerticalBasis> verBasis;
            private final OptionHandler<AlignDirection> alignDirection;

            public AlignButton(int x, int y, int width, int height, OptionHandler<HorizontalBasis> horBasis, OptionHandler<VerticalBasis> verBasis, OptionHandler<AlignDirection> alignDirection, OnPress onpress) {
                super(x, y, width, height, Component.literal(""), onpress, Button.DEFAULT_NARRATION);

                this.horBasis = horBasis;
                this.verBasis = verBasis;
                this.alignDirection = alignDirection;
            }

            @Override
            protected void extractContents(GuiGraphicsExtractor guiGraphics, int x, int y, float partialTicks) {

                Vec2[] texCoords = new Vec2[4];

                float startX;
                float startY;
                float width;
                float height;

                if (this.isHovered) {
                    startX = 132 / 255.0F;
                    startY = 0;
                    width = 36 / 255.0F;
                    height = 36 / 255.0F;

                    //GuiComponent.blit(poseStack, this.x, this.y, this.width, this.height, 132, 0, 36, 36, 255, 255);
                } else {
                    startX = 97 / 255.0F;
                    startY = 2 / 255.0F;
                    width = 31 / 255.0F;
                    height = 31 / 255.0F;

                    //GuiComponent.blit(poseStack, this.x, this.y, this.width, this.height, 97, 2, 31, 31, 255, 255);
                }

                Vec2 uv0 = new Vec2(startX, startY);
                Vec2 uv1 = new Vec2(startX + width, startY);
                Vec2 uv2 = new Vec2(startX + width, startY + height);
                Vec2 uv3 = new Vec2(startX, startY + height);

                texCoords[0] = uv0;
                texCoords[1] = uv1;
                texCoords[2] = uv2;
                texCoords[3] = uv3;

                if (this.alignDirection.getValue() == AlignDirection.HORIZONTAL) {
                    if (this.horBasis.getValue() == HorizontalBasis.LEFT) {
                        texCoords[0] = uv1;
                        texCoords[1] = uv2;
                        texCoords[2] = uv3;
                        texCoords[3] = uv0;
                    } else {
                        texCoords[0] = uv3;
                        texCoords[1] = uv0;
                        texCoords[2] = uv1;
                        texCoords[3] = uv2;
                    }
                } else {
                    if (this.verBasis.getValue() == VerticalBasis.BOTTOM) {
                        texCoords[0] = uv2;
                        texCoords[1] = uv3;
                        texCoords[2] = uv0;
                        texCoords[3] = uv1;
                    }
                }

                this.blitRotate(guiGraphics, texCoords);
            }

            public void blitRotate(GuiGraphicsExtractor guiGraphics, Vec2[] texCoords) {
                float u = Math.min(Math.min(texCoords[0].x, texCoords[1].x), Math.min(texCoords[2].x, texCoords[3].x));
                float v = Math.min(Math.min(texCoords[0].y, texCoords[1].y), Math.min(texCoords[2].y, texCoords[3].y));
                int turns = texCoords[0].x == u ? (texCoords[0].y == v ? 0 : 3) : (texCoords[0].y == v ? 1 : 2);
                float size = (this.isHovered ? 36 : 31);
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(this.getX() + this.width / 2.0F, this.getY() + this.height / 2.0F);
                guiGraphics.pose().rotate((float)(turns * Math.PI / 2));
                guiGraphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, BATTLE_ICONS, -this.width/2, -this.height/2, u * 255, v * 255, this.width, this.height, (int)size, (int)size, 255, 255);
                guiGraphics.pose().popMatrix();
            }

            public int getBlitOffset() {
                return 0;
            }
        }
    }
}