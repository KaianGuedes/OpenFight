package dev.war.openfight.api.ex_cap.data.modifier;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import dev.war.openfight.client.renderer.compat.ItemRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import dev.war.openfight.api.utils.math.OpenMatrix4f;
import dev.war.openfight.client.renderer.patched.item.RenderItemBase;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

@FunctionalInterface
public interface RenderModifier {
    /// Returns true if the original item renderer needs to be canceled, otherwise continues with the render chain.
    boolean modify(RenderItemBase itemBaseRenderer, ItemStack stack, LivingEntityPatch<?> livingEntityPatch, InteractionHand hand, OpenMatrix4f[] poses, MultiBufferSource source, PoseStack poseStack, int packedLight, float partialTicks, ItemRenderer renderer, ItemRenderer handRenderer);
}
