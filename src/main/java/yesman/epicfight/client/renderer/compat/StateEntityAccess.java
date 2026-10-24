package yesman.epicfight.client.renderer.compat;

import net.minecraft.world.entity.Entity;

/** Duck interface mixed into EntityRenderState: remembers the entity and partial tick the state was extracted from. */
public interface StateEntityAccess {
	Entity epicfight$getEntity();
	
	float epicfight$getPartialTick();
	
	void epicfight$capture(Entity entity, float partialTick);
}
