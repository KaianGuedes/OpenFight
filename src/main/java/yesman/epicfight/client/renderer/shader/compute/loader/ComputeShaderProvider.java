package yesman.epicfight.client.renderer.shader.compute.loader;

import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.client.renderer.shader.compute.ComputeShaderSetup;

/** STUB, see {@link ComputeShaderSetup}. Compute shader acceleration is permanently reported as unsupported. */
public final class ComputeShaderProvider {
	private ComputeShaderProvider() {}
	
	public static boolean supportComputeShader() {
		return false;
	}
	
	public static boolean supportPersistentMapping() {
		return false;
	}
	
	public static boolean irisLoaded() {
		return false;
	}
	
	public static void initIris() {
	}
	
	public static ComputeShaderSetup getComputeShaderSetup(SkinnedMesh mesh) {
		return null;
	}
}
