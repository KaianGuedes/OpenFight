package yesman.epicfight.api.client.model.transformer;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import yesman.epicfight.api.client.model.Mesh.RenderProperties;
import yesman.epicfight.api.client.model.MeshPartDefinition;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.utils.math.OpenMatrix4f;

/**
 * STUB. The original transformer baked arbitrary (third party) humanoid armor models into skinned meshes by walking
 * the vanilla {@code ModelPart} cube/vertex data. 26.3 turned {@code ModelPart.Vertex} into a final record and
 * reworked model parts, and the only consumers were replaced client armor models (a NeoForge-only mechanism); vanilla
 * armor always uses Epic Fight's bundled armor meshes. The original implementation is kept in
 * {@code src/disabled/VanillaModelTransformer.java.txt}.
 */
public class VanillaModelTransformer extends HumanoidModelTransformer {
	@Override
	public SkinnedMesh transformArmorModel(HumanoidModel<?> humanoidModel) {
		throw new UnsupportedOperationException("Vanilla armor model transformation is not available on 26.3");
	}
	
	/** Mesh part definition used by the asset loader; the animated model part tracking is not supported. */
	public record VanillaMeshPartDefinition(String partName, RenderProperties renderProperties, List<String> path, OpenMatrix4f invertedParentTransform, ModelPart root) implements MeshPartDefinition {
		public static MeshPartDefinition of(String partName, RenderProperties renderProperties) {
			return new VanillaMeshPartDefinition(partName, renderProperties, null, null, null);
		}
		
		public static MeshPartDefinition of(String partName) {
			return new VanillaMeshPartDefinition(partName, null, null, null, null);
		}
		
		public static MeshPartDefinition of(String partName, List<String> path, OpenMatrix4f invertedParentTransform, ModelPart root) {
			return new VanillaMeshPartDefinition(partName, null, path, invertedParentTransform, root);
		}
		
		@Override
		public Supplier<OpenMatrix4f> getModelPartAnimationProvider() {
			return () -> null;
		}
	}
}
