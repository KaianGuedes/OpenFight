package dev.war.openfight.client.mesh;

import java.util.List;
import java.util.Map;

import net.minecraft.world.entity.EquipmentSlot;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.client.model.MeshPartDefinition;
import dev.war.openfight.api.client.model.Meshes;
import dev.war.openfight.api.client.model.SkinnedMesh;
import dev.war.openfight.api.client.model.VertexBuilder;

public class PiglinMesh extends HumanoidMesh {
	public PiglinMesh(Map<String, Number[]> arrayMap, Map<MeshPartDefinition, List<VertexBuilder>> parts, SkinnedMesh parent, RenderProperties properties) {
		super(arrayMap, parts, parent, properties);
	}
	
	@Override
	public AssetAccessor<? extends SkinnedMesh> getHumanoidArmorModel(EquipmentSlot slot) {
		switch (slot) {
		case HEAD:
			return Meshes.HELMET_PIGLIN;
		case CHEST:
			return Meshes.CHESTPLATE;
		case LEGS:
			return Meshes.LEGGINS;
		case FEET:
			return Meshes.BOOTS;
		default:
			return null;
		}
	}
}