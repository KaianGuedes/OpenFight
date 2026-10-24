package yesman.epicfight.client.gui;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;

/**
 * Triangle based GUI primitives. 26.3's GuiGraphicsExtractor only offers rectangles/blits, so Epic Fight's fan-shaped
 * gauges and rhombus widgets are queued as custom {@link GuiElementRenderState}s.
 */
public final class GuiShapes {
	private GuiShapes() {}

	/** A list of triangles in GUI space (x, y pairs), optionally textured, with one tint color. */
	private record TrianglesState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2fc pose, float[] xy, @Nullable float[] uv, int color, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements GuiElementRenderState {
		@Override
		public void buildVertices(VertexConsumer consumer) {
			int count = this.xy.length / 2;

			for (int i = 0; i < count; i++) {
				consumer.addVertexWith2DPose(this.pose, this.xy[i * 2], this.xy[i * 2 + 1]);

				if (this.uv != null) {
					consumer.setUv(this.uv[i * 2], this.uv[i * 2 + 1]);
				}

				consumer.setColor(this.color);
			}
		}
	}

	private static ScreenRectangle boundsOf(float[] xy, Matrix3x2fc pose, @Nullable ScreenRectangle scissor) {
		float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;

		for (int i = 0; i < xy.length; i += 2) {
			minX = Math.min(minX, xy[i]);
			maxX = Math.max(maxX, xy[i]);
			minY = Math.min(minY, xy[i + 1]);
			maxY = Math.max(maxY, xy[i + 1]);
		}

		ScreenRectangle bounds = new ScreenRectangle((int)Math.floor(minX), (int)Math.floor(minY), (int)Math.ceil(maxX - minX) + 1, (int)Math.ceil(maxY - minY) + 1).transformMaxBounds(pose);
		return scissor != null ? scissor.intersection(bounds) : bounds;
	}

	/** Draws independent triangles (three vertices each). */
	public static void triangles(GuiGraphicsExtractor graphics, float[] xy, int argb) {
		Matrix3x2f pose = new Matrix3x2f(graphics.pose());
		ScreenRectangle scissor = graphics.scissorStack.peek();
		graphics.guiRenderState.addGuiElement(new TrianglesState(EpicFightRenderTypes.guiColoredTrianglePipeline(), TextureSetup.noTexture(), pose, xy, null, argb, scissor, boundsOf(xy, pose, scissor)));
	}

	/** Draws a textured triangle fan; the first vertex is the hub. */
	public static void texturedFan(GuiGraphicsExtractor graphics, Identifier texture, float[] xy, float[] uv, int argb) {
		int count = xy.length / 2;

		if (count < 3) {
			return;
		}

		float[] fanXy = new float[(count - 2) * 6];
		float[] fanUv = new float[(count - 2) * 6];

		for (int i = 0; i < count - 2; i++) {
			int[] order = {0, i + 1, i + 2};

			for (int k = 0; k < 3; k++) {
				fanXy[i * 6 + k * 2] = xy[order[k] * 2];
				fanXy[i * 6 + k * 2 + 1] = xy[order[k] * 2 + 1];
				fanUv[i * 6 + k * 2] = uv[order[k] * 2];
				fanUv[i * 6 + k * 2 + 1] = uv[order[k] * 2 + 1];
			}
		}

		AbstractTexture tex = Minecraft.getInstance().getTextureManager().getTexture(texture);
		Matrix3x2f pose = new Matrix3x2f(graphics.pose());
		ScreenRectangle scissor = graphics.scissorStack.peek();
		graphics.guiRenderState.addGuiElement(new TrianglesState(EpicFightRenderTypes.guiTexturedTrianglePipeline(), TextureSetup.singleTexture(tex.getTextureView(), tex.getSampler()), pose, fanXy, fanUv, argb, scissor, boundsOf(fanXy, pose, scissor)));
	}
}
