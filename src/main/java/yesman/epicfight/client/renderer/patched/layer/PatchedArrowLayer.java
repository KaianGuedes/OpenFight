package yesman.epicfight.client.renderer.patched.layer;

import yesman.epicfight.client.renderer.compat.EpicRenderContext;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.player.PlayerModel;
import yesman.epicfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class PatchedArrowLayer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends PlayerModel> extends PatchedStuckInBodyLayer<E, T, M, ArrowLayer<?>> {
	private final EntityRenderDispatcher dispatcher;
	
	public PatchedArrowLayer(EntityRendererProvider.Context context) {
		this.dispatcher = context.getEntityRenderDispatcher();
	}
	
	@Override
	protected void renderStuckItem(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Entity entity, float f1, float f2, float f3, float partialTick) {
		float f = Mth.sqrt(f1 * f1 + f3 * f3);
		Arrow arrow = new Arrow(entity.level(), entity.getX(), entity.getY(), entity.getZ(), ItemStack.EMPTY, ItemStack.EMPTY);
		arrow.setYRot((float) (Math.atan2((double) f1, (double) f3) * (double) (180F / (float) Math.PI)));
		arrow.setXRot((float) (Math.atan2((double) f2, (double) f) * (double) (180F / (float) Math.PI)));
		arrow.yRotO = arrow.getYRot();
		arrow.xRotO = arrow.getXRot();

		SubmitNodeCollector collector = EpicRenderContext.collector();
		CameraRenderState camera = EpicRenderContext.camera();

		if (collector != null && camera != null) {
			EntityRenderState arrowState = this.dispatcher.extractEntity(arrow, partialTick);
			this.dispatcher.submit(arrowState, camera, 0.0D, 0.0D, 0.0D, poseStack, collector);
		}
	}

	@Override
	protected int numStuck(E entity) {
		return entity.getArrowCount();
	}
}
