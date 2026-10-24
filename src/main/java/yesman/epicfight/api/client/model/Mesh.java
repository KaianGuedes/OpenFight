package yesman.epicfight.api.client.model;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.jetbrains.annotations.Nullable;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import yesman.epicfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;

import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;

public interface Mesh {
	
	void initialize();
	
	/* Draw wihtout mesh deformation */
	void draw(PoseStack poseStack, VertexConsumer vertexConsumer, Mesh.DrawingFunction drawingFunction, int packedLight, float r, float g, float b, float a, int overlay);
	
	/* Draw with mesh deformation */
	void drawPosed(PoseStack poseStack, VertexConsumer vertexConsumer, Mesh.DrawingFunction drawingFunction, int packedLight, float r, float g, float b, float a, int overlay, @Nullable Armature armature, OpenMatrix4f[] poses);
	
	/* Universal method */
	default void draw(PoseStack poseStack, MultiBufferSource bufferSources, RenderType renderType, Mesh.DrawingFunction drawingFunction, int packedLight, float r, float g, float b, float a, int overlay, @Nullable Armature armature, OpenMatrix4f[] poses) {
		this.drawPosed(poseStack, bufferSources.getBuffer(EpicFightRenderTypes.getTriangulated(renderType)), drawingFunction, packedLight, r, g, b, a, overlay, armature, poses);
	}
	
	public static record RenderProperties(Identifier customTexturePath, Vec3f customColor, boolean isTransparent) {
		public static class Builder {
			protected String customTexturePath;
			protected Vec3f customColor = new Vec3f();
			protected boolean isTransparent;
			
			public RenderProperties.Builder customTexturePath(String path) {
				this.customTexturePath = path;
				return this;
			}
			
			public RenderProperties.Builder transparency(boolean isTransparent) {
				this.isTransparent = isTransparent;
				return this;
			}
			
			public RenderProperties.Builder customColor(float r, float g, float b) {
				this.customColor.x = r;
				this.customColor.y = g;
				this.customColor.z = b;
				return this;
			}
			
			public RenderProperties build() {
				return new RenderProperties(this.customTexturePath == null ? null : Identifier.parse(this.customTexturePath), this.customColor, this.isTransparent);
			}
			
			public static RenderProperties.Builder create() {
				return new RenderProperties.Builder();
			}
		}
	}
	
	@FunctionalInterface
	public interface DrawingFunction {
		public static final DrawingFunction NEW_ENTITY = (builder, posX, posY, posZ, normX, normY, normZ, packedLight, r, g, b, a, u, v, overlay) -> {
			builder.addVertex(posX, posY, posZ)
			        .setColor(r, g, b, a)
			        .setUv(u, v)
			        .setOverlay(overlay)
			        .setLight(packedLight)
			        .setNormal(normX, normY, normZ);
		};
		
		public static final DrawingFunction POSITION_TEX = (builder, posX, posY, posZ, normX, normY, normZ, packedLight, r, g, b, a, u, v, overlay) -> {
			builder.addVertex(posX, posY, posZ)
					.setUv(u, v);
		};
		
		public static final DrawingFunction POSITION_TEX_COLOR_NORMAL = (builder, posX, posY, posZ, normX, normY, normZ, packedLight, r, g, b, a, u, v, overlay) -> {
			builder.addVertex(posX, posY, posZ)
					.setUv(u, v)
					.setColor(r, g, b, a)
					.setNormal(normX, normY, normZ);
		};
		
		public static final DrawingFunction POSITION_TEX_COLOR_LIGHTMAP = (builder, posX, posY, posZ, normX, normY, normZ, packedLight, r, g, b, a, u, v, overlay) -> {
			builder.addVertex(posX, posY, posZ)
					.setUv(u, v)
					.setColor(r, g, b, a)
					.setLight(packedLight);
		};
		
		public static final DrawingFunction POSITION_COLOR_LIGHTMAP = (builder, posX, posY, posZ, normX, normY, normZ, packedLight, r, g, b, a, u, v, overlay) -> {
			builder.addVertex(posX, posY, posZ)
					.setColor(r, g, b, a)
					.setLight(packedLight);
		};
		
		public static final DrawingFunction POSITION_COLOR_NORMAL = (builder, posX, posY, posZ, normX, normY, normZ, packedLight, r, g, b, a, u, v, overlay) -> {
			builder.addVertex(posX, posY, posZ)
					.setColor(r, g, b, a)
					.setNormal(normX, normY, normZ);
		};
		
		public static final DrawingFunction POSITION_COLOR_TEX_LIGHTMAP = (builder, posX, posY, posZ, normX, normY, normZ, packedLight, r, g, b, a, u, v, overlay) -> {
			builder.addVertex(posX, posY, posZ)
					.setColor(r, g, b, a)
					.setUv(u, v)
					.setLight(packedLight);
		};
		
		public void draw(VertexConsumer vertexConsumer, float posX, float posY, float posZ, float normX, float normY, float normZ, int packedLight, float r, float g, float b, float a, float u, float v, int overlay);
		
	    default void putBulkData(PoseStack.Pose pose, BakedQuad bakedQuad, VertexConsumer vertexConsumer, float red, float green, float blue, float alpha, int packedLight, int packedOverlay, boolean readExistingColor) {
	    	putBulkDataWithDrawingFunction(this, vertexConsumer, pose, bakedQuad, new float[] { 1.0F, 1.0F, 1.0F, 1.0F }, red, green, blue, alpha, new int[] { packedLight, packedLight, packedLight, packedLight }, packedOverlay, readExistingColor);
	    }
	    
		static void putBulkDataWithDrawingFunction(DrawingFunction drawingFunction, VertexConsumer builder, PoseStack.Pose pose, BakedQuad quad, float[] shade, float r, float g, float b, float alpha, int[] lights, int overlay, boolean mulColor) {
            var n=quad.direction().getUnitVec3i();
            Vector3f normal=pose.normal().transform(new Vector3f(n.getX(), n.getY(), n.getZ()));
            for(int i=0;i<4;i++) {
                Vector3f position=new Vector3f(quad.position(i)).mulPosition(pose.pose());
                long uv=quad.packedUV(i);
                drawingFunction.draw(builder,position.x,position.y,position.z,normal.x,normal.y,normal.z,lights[i],r*shade[i],g*shade[i],b*shade[i],alpha,Float.intBitsToFloat((int)uv),Float.intBitsToFloat((int)(uv>>>32)),overlay);
            }
        }
		
		/**
		 * Code copy from {@link IForgeVertexConsumer#applyBakedLighting}
		 */
		static int applyBakedLighting(int packedLight, ByteBuffer data) {
	        int bl = packedLight & 0xFFFF;
	        int sl = (packedLight >> 16) & 0xFFFF;
	        int offset = 24; // int offset for vertex 0 * 4 bytes per int
	        int blBaked = Short.toUnsignedInt(data.getShort(offset));
	        int slBaked = Short.toUnsignedInt(data.getShort(offset + 2));
	        bl = Math.max(bl, blBaked);
	        sl = Math.max(sl, slBaked);
	        return bl | (sl << 16);
	    }
		
		/**
		 * Code copy from {@link IForgeVertexConsumer#applyBakedNormals}
		 */
	    static void applyBakedNormals(Vector3f generated, ByteBuffer data, Matrix3f normalTransform) {
	        byte nx = data.get(28);
	        byte ny = data.get(29);
	        byte nz = data.get(30);
	        if (nx != 0 || ny != 0 || nz != 0)
	        {
	            generated.set(nx / 127f, ny / 127f, nz / 127f);
	            generated.mul(normalTransform);
	        }
	    }
	}
}