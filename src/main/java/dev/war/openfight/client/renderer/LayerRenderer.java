package dev.war.openfight.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import dev.war.openfight.client.renderer.patched.layer.PatchedLayer;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public interface LayerRenderer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<?>> {
	public void addPatchedLayer(Class<?> originalLayerClass, PatchedLayer<E, T, M, ? extends RenderLayer<?, ?>> patchedLayer);
	
	public void addCustomLayer(PatchedLayer<E, T, M, ? extends RenderLayer<?, ?>> patchedLayer);
}