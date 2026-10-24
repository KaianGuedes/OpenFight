package dev.war.openfight.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import dev.war.openfight.client.renderer.compat.StateEntityAccess;

@Mixin(EntityRenderState.class)
public abstract class MixinEntityRenderState implements StateEntityAccess {
	@Unique private Entity epicfight$entity;
	@Unique private float epicfight$partialTick;
	
	@Override
	public Entity epicfight$getEntity() {
		return this.epicfight$entity;
	}
	
	@Override
	public float epicfight$getPartialTick() {
		return this.epicfight$partialTick;
	}
	
	@Override
	public void epicfight$capture(Entity entity, float partialTick) {
		this.epicfight$entity = entity;
		this.epicfight$partialTick = partialTick;
	}
}
