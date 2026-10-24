package yesman.epicfight.client.renderer.compat;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;

/**
 * Holds the render-state/submit-node context of the entity currently being submitted, so that the patched renderers
 * (which were written around the 1.21.1 immediate renderer) can reach it without every signature changing.
 * Rendering happens on the render thread only.
 */
public final class EpicRenderContext {
	private static EntityRenderState state;
	private static SubmitNodeCollector collector;
	private static CameraRenderState camera;
	
	private EpicRenderContext() {}
	
	public static void begin(EntityRenderState state, SubmitNodeCollector collector, CameraRenderState camera) {
		EpicRenderContext.state = state;
		EpicRenderContext.collector = collector;
		EpicRenderContext.camera = camera;
	}
	
	public static void end() {
		state = null;
		collector = null;
		camera = null;
	}
	
	@Nullable
	public static EntityRenderState state() {
		return state;
	}
	
	@Nullable
	public static SubmitNodeCollector collector() {
		return collector;
	}
	
	@Nullable
	public static CameraRenderState camera() {
		return camera;
	}
}
