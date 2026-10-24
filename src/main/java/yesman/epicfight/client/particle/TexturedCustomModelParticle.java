package yesman.epicfight.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.ClassicMesh;

public abstract class TexturedCustomModelParticle extends CustomModelParticle<ClassicMesh> {
	protected final Identifier texture;
	
	public TexturedCustomModelParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, AssetAccessor<ClassicMesh> particleMesh, Identifier texture) {
		super(level, x, y, z, xd, yd, zd, particleMesh);
		this.texture = texture;
	}
	
	@Override
	public void prepareDraw(PoseStack poseStack, float partialTicks) {
		// Texture binding belongs to the extracted RenderType (EpicFightParticleRenderTypes).
	}
}