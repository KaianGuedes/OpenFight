package dev.war.openfight.client.renderer.compat;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import com.mojang.renderpearl.api.vertex.VertexFormatElement;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;

/**
 * Records vertices (already transformed to their final space) so they can be replayed later, inside the
 * callback of {@code SubmitNodeCollector#submitCustomGeometry}.
 */
public final class RecordingVertexConsumer implements VertexConsumer {
	private static final int STRIDE = 16;
	private static final int F_X = 0, F_Y = 1, F_Z = 2, F_U = 3, F_V = 4, F_NX = 5, F_NY = 6, F_NZ = 7, F_LW = 8;
	private static final int I_COLOR = 9, I_OVERLAY = 10, I_LIGHT = 11, I_U3 = 12, I_V3 = 13, I_FLAGS = 14;
	
	private static final int FLAG_COLOR = 1, FLAG_UV = 2, FLAG_OVERLAY = 4, FLAG_LIGHT = 8, FLAG_NORMAL = 16, FLAG_UV3 = 32, FLAG_LINEWIDTH = 64;
	
	private float[] data = new float[STRIDE * 64];
	private int count;
	private int pending = -1;
	
	public int vertexCount() {
		this.finishPending();
		return this.count;
	}
	
	private void finishPending() {
		this.pending = -1;
	}
	
	private void ensure() {
		if ((this.count + 1) * STRIDE > this.data.length) {
			float[] grown = new float[this.data.length * 2];
			System.arraycopy(this.data, 0, grown, 0, this.data.length);
			this.data = grown;
		}
	}
	
	private int cur() {
		if (this.pending < 0) {
			throw new IllegalStateException("addVertex must be called first");
		}
		
		return this.pending * STRIDE;
	}
	
	private void setInt(int base, int slot, int value) {
		this.data[base + slot] = Float.intBitsToFloat(value);
	}
	
	private int getInt(int base, int slot) {
		return Float.floatToRawIntBits(this.data[base + slot]);
	}
	
	@Override
	public VertexConsumer addVertex(float x, float y, float z) {
		this.ensure();
		this.pending = this.count++;
		int base = this.pending * STRIDE;
		this.data[base + F_X] = x;
		this.data[base + F_Y] = y;
		this.data[base + F_Z] = z;
		this.setInt(base, I_FLAGS, 0);
		return this;
	}
	
	private void flag(int base, int flag) {
		this.setInt(base, I_FLAGS, this.getInt(base, I_FLAGS) | flag);
	}
	
	@Override
	public VertexConsumer setColor(int r, int g, int b, int a) {
		return this.setColor((a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF));
	}
	
	@Override
	public VertexConsumer setColor(int color) {
		int base = this.cur();
		this.setInt(base, I_COLOR, color);
		this.flag(base, FLAG_COLOR);
		return this;
	}
	
	@Override
	public VertexConsumer setUv(float u, float v) {
		int base = this.cur();
		this.data[base + F_U] = u;
		this.data[base + F_V] = v;
		this.flag(base, FLAG_UV);
		return this;
	}
	
	@Override
	public VertexConsumer setUv1(int u, int v) {
		int base = this.cur();
		this.setInt(base, I_OVERLAY, (v & 0xFFFF) << 16 | (u & 0xFFFF));
		this.flag(base, FLAG_OVERLAY);
		return this;
	}
	
	@Override
	public VertexConsumer setUv2(int u, int v) {
		int base = this.cur();
		this.setInt(base, I_LIGHT, (v & 0xFFFF) << 16 | (u & 0xFFFF));
		this.flag(base, FLAG_LIGHT);
		return this;
	}
	
	@Override
	public VertexConsumer setUv3(float u, float v) {
		int base = this.cur();
		this.data[base + I_U3] = u;
		this.data[base + I_V3] = v;
		this.flag(base, FLAG_UV3);
		return this;
	}
	
	@Override
	public VertexConsumer setNormal(float x, float y, float z) {
		int base = this.cur();
		this.data[base + F_NX] = x;
		this.data[base + F_NY] = y;
		this.data[base + F_NZ] = z;
		this.flag(base, FLAG_NORMAL);
		return this;
	}
	
	@Override
	public VertexConsumer setLineWidth(float width) {
		int base = this.cur();
		this.data[base + F_LW] = width;
		this.flag(base, FLAG_LINEWIDTH);
		return this;
	}
	
	/**
	 * Replays every recorded vertex into the destination consumer. Attributes the target vertex format requires but
	 * that were never written receive neutral defaults, exactly like the old fixed-function buffers did.
	 */
	public void replay(VertexConsumer out, VertexFormat format) {
		this.finishPending();
		boolean wantColor = false, wantUv0 = false, wantUv1 = false, wantUv2 = false, wantUv3 = false, wantNormal = false, wantLineWidth = false;
		
		for (VertexFormatElement element : format.getElements()) {
			switch (element.name()) {
			case DefaultVertexFormat.COLOR_SEMANTIC_NAME -> wantColor = true;
			case DefaultVertexFormat.UV0_SEMANTIC_NAME -> wantUv0 = true;
			case DefaultVertexFormat.UV1_SEMANTIC_NAME -> wantUv1 = true;
			case DefaultVertexFormat.UV2_SEMANTIC_NAME -> wantUv2 = true;
			case DefaultVertexFormat.UV3_SEMANTIC_NAME -> wantUv3 = true;
			case DefaultVertexFormat.NORMAL_SEMANTIC_NAME -> wantNormal = true;
			case DefaultVertexFormat.LINE_WIDTH_SEMANTIC_NAME -> wantLineWidth = true;
			default -> {
			}
			}
		}
		
		for (int i = 0; i < this.count; i++) {
			int base = i * STRIDE;
			int flags = this.getInt(base, I_FLAGS);
			out.addVertex(this.data[base + F_X], this.data[base + F_Y], this.data[base + F_Z]);
			
			if (wantColor) {
				out.setColor((flags & FLAG_COLOR) != 0 ? this.getInt(base, I_COLOR) : -1);
			}
			
			if (wantUv0) {
				if ((flags & FLAG_UV) != 0) {
					out.setUv(this.data[base + F_U], this.data[base + F_V]);
				} else {
					out.setUv(0.0F, 0.0F);
				}
			}
			
			if (wantUv3) {
				if ((flags & FLAG_UV3) != 0) {
					out.setUv3(this.data[base + I_U3], this.data[base + I_V3]);
				} else {
					out.setUv3(0.0F, 0.0F);
				}
			}
			
			if (wantUv1) {
				out.setOverlay((flags & FLAG_OVERLAY) != 0 ? this.getInt(base, I_OVERLAY) : OverlayTexture.NO_OVERLAY);
			}
			
			if (wantUv2) {
				out.setLight((flags & FLAG_LIGHT) != 0 ? this.getInt(base, I_LIGHT) : LightCoordsUtil.FULL_BRIGHT);
			}
			
			if (wantNormal) {
				if ((flags & FLAG_NORMAL) != 0) {
					out.setNormal(this.data[base + F_NX], this.data[base + F_NY], this.data[base + F_NZ]);
				} else {
					out.setNormal(0.0F, 1.0F, 0.0F);
				}
			}
			
			if (wantLineWidth) {
				out.setLineWidth((flags & FLAG_LINEWIDTH) != 0 ? this.data[base + F_LW] : 1.0F);
			}
		}
	}
}
