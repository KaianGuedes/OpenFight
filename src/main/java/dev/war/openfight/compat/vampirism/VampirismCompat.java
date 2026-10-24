package dev.war.openfight.compat.vampirism;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.renderer.entity.layers.VampirePlayerHeadLayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.neoforged.bus.api.IEventBus;
import dev.war.openfight.api.client.event.EpicFightClientEventHooks;
import dev.war.openfight.api.client.model.Meshes;
import dev.war.openfight.api.utils.math.OpenMatrix4f;
import dev.war.openfight.client.mesh.HumanoidMesh;
import dev.war.openfight.client.renderer.EpicFightRenderTypes;
import dev.war.openfight.client.renderer.patched.entity.PPlayerRenderer;
import dev.war.openfight.client.renderer.patched.layer.ModelRenderLayer;
import dev.war.openfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;
import dev.war.openfight.compat.ICompatModule;
import dev.war.openfight.compat.vampirism.mixin.MixinVampirePlayerHeadLayer;

public class VampirismCompat implements ICompatModule {
	@Override
	public void onModEventBus(IEventBus eventBus) {
		
	}

	@Override
	public void onGameEventBus(IEventBus eventBus) {
		
	}
	
	@Override
	public void onModEventBusClient(IEventBus eventBus) {
        EpicFightClientEventHooks.Registry.MODIFY_PATCHED_ENTITY.registerEvent(event -> {
            if (event.get(EntityTypes.PLAYER) instanceof PPlayerRenderer playerrenderer) {
                playerrenderer.addPatchedLayerAlways(VampirePlayerHeadLayer.class, new EpicFightVampirePlayerHeadLayer());
            }
        });
	}
	
	@Override
	public void onGameEventBusClient(IEventBus eventBus) {
		
	}
	
	public static class EpicFightVampirePlayerHeadLayer extends ModelRenderLayer<AbstractClientPlayer, AbstractClientPlayerPatch<AbstractClientPlayer>, PlayerModel<AbstractClientPlayer>, VampirePlayerHeadLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>, HumanoidMesh> {
		public EpicFightVampirePlayerHeadLayer() {
			super(Meshes.BIPED);
		}
		
		@Override
		protected void renderLayer( AbstractClientPlayerPatch<AbstractClientPlayer> entitypatch
				                  , AbstractClientPlayer entityliving
				                  , VampirePlayerHeadLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> vanillaLayer
				                  , PoseStack poseStack
				                  , MultiBufferSource buffer
				                  , int packedLight
				                  , OpenMatrix4f[] poses
				                  , float bob
				                  , float yRot
				                  , float xRot
				                  , float partialTicks
				                  )
		{
			if (!VampirismConfig.CLIENT.renderVampireEyes.get() || !entityliving.isAlive()) {
				return;
			}
			
	        VampirismPlayerAttributes atts = VampirismPlayerAttributes.get(entityliving);
	        
	        if (atts.vampireLevel > 0 && !atts.getVampSpecial().disguised && !entityliving.isInvisible()) {
	        	MixinVampirePlayerHeadLayer accessor = ((MixinVampirePlayerHeadLayer)vanillaLayer);
	        	
	        	int eyeType = Math.max(0, Math.min(atts.getVampSpecial().eyeType, accessor.getEyeOverlays().length - 1));
	            int fangType = Math.max(0, Math.min(atts.getVampSpecial().fangType, accessor.getFangOverlays().length - 1));
	            int packerOverlay = LivingEntityRenderer.getOverlayCoords(entityliving, 0);
	            
	            RenderType eyeRenderType = EpicFightRenderTypes.getTriangulated(atts.getVampSpecial().glowingEyes ? RenderType.eyes(accessor.getEyeOverlays()[eyeType]) : RenderType.entityCutoutNoCull(accessor.getEyeOverlays()[eyeType]));
	            this.mesh.get().draw(poseStack, buffer, eyeRenderType, packedLight, 1.0F, 1.0F, 1.0F, 1.0F, packerOverlay, entitypatch.getArmature(), poses);
	            
	            RenderType fangRenderType = EpicFightRenderTypes.getTriangulated(RenderType.entityCutoutNoCull(accessor.getFangOverlays()[fangType]));
	            this.mesh.get().draw(poseStack, buffer, fangRenderType, packedLight, 1.0F, 1.0F, 1.0F, 1.0F, packerOverlay, entitypatch.getArmature(), poses);
	            
	        }
		}
	}
}
