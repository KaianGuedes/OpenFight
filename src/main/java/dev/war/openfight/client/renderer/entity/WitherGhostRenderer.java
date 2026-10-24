package dev.war.openfight.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import dev.war.openfight.client.renderer.patched.entity.PWitherRenderer;
import dev.war.openfight.world.entity.WitherGhostClone;

public class WitherGhostRenderer extends NoopLivingEntityRenderer<WitherGhostClone> {
	public WitherGhostRenderer(Context context) {
		super(context, 1.0F);
	}
	
	@Override
	protected int getBlockLightLevel(WitherGhostClone witherBoss, BlockPos blockpos) {
		return 15;
	}
	
	@Override
	public Identifier getTextureLocation(net.minecraft.client.renderer.entity.state.LivingEntityRenderState entity) {
		return PWitherRenderer.WITHER_INVULNERABLE_LOCATION;
	}
}