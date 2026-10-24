package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.entity.EntityType;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.layer.PatchedArrowLayer;
import yesman.epicfight.client.renderer.patched.layer.PatchedBeeStingerLayer;
import yesman.epicfight.client.renderer.patched.layer.PatchedCapeLayer;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;

public class PPlayerRenderer extends PHumanoidRenderer<AbstractClientPlayer, AbstractClientPlayerPatch<AbstractClientPlayer>, PlayerModel, AvatarRenderer<AbstractClientPlayer>, HumanoidMesh> {
	public PPlayerRenderer(EntityRendererProvider.Context context, EntityType<?> entityType) {
		super(Meshes.BIPED, context, entityType);
		
		this.addPatchedLayer(ArrowLayer.class, new PatchedArrowLayer<> (context));
		this.addPatchedLayer(BeeStingerLayer.class, new PatchedBeeStingerLayer<> ());
		this.addPatchedLayer(CapeLayer.class, new PatchedCapeLayer());
		this.addPatchedLayer(PlayerItemInHandLayer.class, new PatchedItemInHandLayer<> ());
	}
	
	@Override
	protected void prepareModel(HumanoidMesh mesh, AbstractClientPlayer entity, AbstractClientPlayerPatch<AbstractClientPlayer> entitypatch, AvatarRenderer<AbstractClientPlayer> renderer) {
		super.prepareModel(mesh, entity, entitypatch, renderer);
		
		net.minecraft.client.renderer.entity.state.AvatarRenderState state = (net.minecraft.client.renderer.entity.state.AvatarRenderState)yesman.epicfight.client.renderer.compat.EpicRenderContext.state();
		boolean spectator = state != null && state.isSpectator;
		
		mesh.head.setHidden(spectator);
		mesh.hat.setHidden(spectator || (state != null && !state.showHat));
		mesh.jacket.setHidden(spectator || (state != null && !state.showJacket));
		mesh.torso.setHidden(spectator);
		mesh.leftArm.setHidden(spectator);
		mesh.leftLeg.setHidden(spectator);
		mesh.leftPants.setHidden(spectator || (state != null && !state.showLeftPants));
		mesh.leftSleeve.setHidden(spectator || (state != null && !state.showLeftSleeve));
		mesh.rightArm.setHidden(spectator);
		mesh.rightLeg.setHidden(spectator);
		mesh.rightPants.setHidden(spectator || (state != null && !state.showRightPants));
		mesh.rightSleeve.setHidden(spectator || (state != null && !state.showRightSleeve));
	}
	
	@Override
	public AssetAccessor<HumanoidMesh> getMeshProvider(AbstractClientPlayerPatch<AbstractClientPlayer> entitypatch) {
		return PlayerModelType.SLIM.equals(entitypatch.getOriginal().getSkin().model()) ? Meshes.ALEX : Meshes.BIPED;
	}
	
	@Override
	public AssetAccessor<HumanoidMesh> getDefaultMesh() {
		return Meshes.BIPED;
	}
}