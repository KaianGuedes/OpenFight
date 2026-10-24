package yesman.epicfight.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;

@Mixin(value = EntityRenderer.class)
public interface MixinEntityRenderer {
	@Invoker("shouldShowName")
	public boolean invokeShouldShowName(Entity entity, double distanceToCameraSq);
	
	@Invoker("submitNameDisplay")
	public void invokeSubmitNameDisplay(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera);
}
