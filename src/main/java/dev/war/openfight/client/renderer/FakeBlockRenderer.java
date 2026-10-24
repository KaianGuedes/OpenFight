package dev.war.openfight.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Camera;
import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface FakeBlockRenderer {
	public void render(Camera camera, PoseStack poseStack, MultiBufferSource buffers, Level level, BlockPos bp, float r, float g, float b, float a);
}
