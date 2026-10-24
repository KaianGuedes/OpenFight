package dev.war.openfight.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import dev.war.openfight.api.client.event.EpicFightClientEventHooks;
import dev.war.openfight.api.client.event.types.hud.TickTargetIndicatorEvent;
import dev.war.openfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import dev.war.openfight.config.ClientConfig;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

import org.jetbrains.annotations.Nullable;

public class TargetIndicator extends EntityUI {
	@Override
	public boolean shouldDraw(LivingEntity entity, @Nullable LivingEntityPatch<?> entitypatch, LocalPlayerPatch playerpatch, float partialTicks) {
		if (!ClientConfig.showTargetIndicator) {
			return false;
		} else if (playerpatch == null) {
            return false;
        } else {
			if (entity != playerpatch.getTarget()) {
				return false;
			} else if (entity.isInvisibleTo(playerpatch.getOriginal()) || !entity.isAlive() || entity == playerpatch.getOriginal()) {
				return true;
			} else if (entity.distanceToSqr(Minecraft.getInstance().getCameraEntity()) >= 400) {
				return false;
			} else if (entity instanceof Player player) {
				return !player.isSpectator();
			}
		}
		
		return true;
	}
	
	@Override
	public void draw(LivingEntity entity, @Nullable LivingEntityPatch<?> entitypatch, LocalPlayerPatch playerpatch, PoseStack poseStack, MultiBufferSource buffers, float partialTicks) {
		poseStack.pushPose();
		
		setupPoseStack(poseStack, entity, 0.0F, entity.getBbHeight() + 0.45F, 0.0F, true, partialTicks);
		
		if (entitypatch == null) {
			drawUIAsLevelModel(poseStack.last(), BATTLE_ICON, buffers, -0.1F, -0.1F, 0.1F, 0.1F, 97, 2, 128, 33, 256);
		} else {
            TickTargetIndicatorEvent event = new TickTargetIndicatorEvent(playerpatch, entitypatch);
            EpicFightClientEventHooks.HUD.TARGET_INDICATOR_TICK.postWithListener(event, playerpatch.getEventListener());

			switch (event.getIndicatorType()) {
			case NORMAL -> {
				drawUIAsLevelModel(poseStack.last(), BATTLE_ICON, buffers, -0.1F, -0.1F, 0.1F, 0.1F, 97, 2, 128, 33, 256);
			}
			case FLASH -> {
				if (entity.tickCount % 2 == 0) {
					drawUIAsLevelModel(poseStack.last(), BATTLE_ICON, buffers, -0.1F, -0.1F, 0.1F, 0.1F, 132, 0, 167, 36, 256);
				} else {
					drawUIAsLevelModel(poseStack.last(), BATTLE_ICON, buffers, -0.1F, -0.1F, 0.1F, 0.1F, 97, 2, 128, 33, 256);
				}
			}
			}
		}
		
		poseStack.popPose();
	}
}