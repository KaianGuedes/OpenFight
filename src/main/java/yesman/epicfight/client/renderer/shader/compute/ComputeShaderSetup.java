package yesman.epicfight.client.renderer.shader.compute;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.rendertype.RenderType;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Mesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.compat.MultiBufferSource;
import yesman.epicfight.main.EpicFightSharedConstants;

/**
 * STUB. The original GPU mesh-skinning path used OpenGL compute shaders, which the RenderPearl abstraction of 26.3 does
 * not expose. Epic Fight falls back to its CPU skinning path ({@link SkinnedMesh#drawPosed}); the original sources are
 * kept in src/disabled/compute-shader-original. This class only keeps the scratch pose arrays used by the CPU path.
 */
public abstract class ComputeShaderSetup {
	public static final OpenMatrix4f[] TOTAL_POSES = OpenMatrix4f.allocateMatrixArray(EpicFightSharedConstants.MAX_JOINTS);
	public static final OpenMatrix4f[] TOTAL_NORMALS = OpenMatrix4f.allocateMatrixArray(EpicFightSharedConstants.MAX_JOINTS);
	
	public void destroyBuffers() {
	}
	
	public void drawWithShader(SkinnedMesh mesh, PoseStack poseStack, MultiBufferSource bufferSources, RenderType renderType, int packedLight, float r, float g, float b, float a, int overlay, @Nullable Armature armature, OpenMatrix4f[] poses) {
		// Keep this entry point safe if a future backend reports support before it has a valid GPU implementation.
		// The original OpenGL compute path cannot be used through RenderPearl's 26.3 backend abstraction.
		mesh.drawPosed(poseStack, bufferSources.getBuffer(renderType), Mesh.DrawingFunction.NEW_ENTITY, packedLight, r, g, b, a, overlay, armature, poses);
	}
	
	/** Placeholder for the per-part GPU vertex buffer. */
	public static class MeshPartBuffer {
	}
}
