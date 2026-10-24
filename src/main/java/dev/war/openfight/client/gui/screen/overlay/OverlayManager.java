package dev.war.openfight.client.gui.screen.overlay;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class OverlayManager {
	private Map<String, OverlayManager.Overlay> overlays = Maps.newHashMap();
	private double modifiedGamma;
	private double originalGamma;
	private boolean isGammaChanged;

	public void renderTick(net.minecraft.client.gui.GuiGraphicsExtractor graphics, int xResolution, int yResolution) {
		this.isGammaChanged = false;
		this.overlays.entrySet().removeIf((entry) -> entry.getValue().render(graphics, xResolution, yResolution));
	}
	
	public void remove(String overlayId) {
		this.overlays.remove(overlayId);
	}
	
	public void setModifiedGamma(double originalGamma) {
		this.isGammaChanged = true;
		this.modifiedGamma = originalGamma;
	}

	public double getModifiedGamma(double originalGamma) {
		this.originalGamma = originalGamma;
		return this.modifiedGamma;
	}

	public double getOriginalGamma() {
		return this.originalGamma;
	}

	public boolean isGammaChanged() {
		return this.isGammaChanged;
	}

	public void blendingTexture(String overlayId, Identifier texture) {
		if (!Minecraft.getInstance().gameRenderer.gameRenderState().guiRenderState.isHudHidden) {
			this.overlays.put(overlayId, new BlendingTextureOverlay(texture));
		}
	}
	
	public void flickering(String overlayId, float deltaTime, float strength) {
		if (!Minecraft.getInstance().gameRenderer.gameRenderState().guiRenderState.isHudHidden) {
			this.overlays.put(overlayId, new FlickeringOverlay(deltaTime, strength));
		}
	}
	
	public abstract static class Overlay {
		public abstract boolean render(net.minecraft.client.gui.GuiGraphicsExtractor graphics, int xResolution, int yResolution);
	}
}