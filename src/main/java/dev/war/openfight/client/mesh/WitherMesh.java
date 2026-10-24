package dev.war.openfight.client.mesh;

import java.util.List;
import java.util.Map;

import dev.war.openfight.api.client.model.MeshPartDefinition;
import dev.war.openfight.api.client.model.SkinnedMesh;
import dev.war.openfight.api.client.model.VertexBuilder;

public class WitherMesh extends SkinnedMesh {
	public final SkinnedMeshPart centerHead;
	public final SkinnedMeshPart leftHead;
	public final SkinnedMeshPart rightHead;
	public final SkinnedMeshPart ribcage;
	public final SkinnedMeshPart tail;
	
	public WitherMesh(Map<String, Number[]> arrayMap, Map<MeshPartDefinition, List<VertexBuilder>> parts, SkinnedMesh parent, RenderProperties properties) {
		super(arrayMap, parts, parent, properties);
		
		this.centerHead = this.getOrLogException(this.parts, "centerHead");
		this.leftHead = this.getOrLogException(this.parts, "leftHead");
		this.rightHead = this.getOrLogException(this.parts, "rightHead");
		this.ribcage = this.getOrLogException(this.parts, "ribcage");
		this.tail = this.getOrLogException(this.parts, "tail");
	}
}