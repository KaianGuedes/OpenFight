package yesman.epicfight.world.capabilities.provider;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

import org.jetbrains.annotations.Nullable;

public final class AttachmentEntityPatchProvider {
	public static final AttachmentEntityPatchProvider EMPTY = new AttachmentEntityPatchProvider();

	@Nullable
	private EntityPatch<?> entitypatch;

	private AttachmentEntityPatchProvider() {
	}
	
	public AttachmentEntityPatchProvider(Entity entity) {

        if (entity instanceof EnderDragonPart) {
            return;
        }

		this.entitypatch = EpicFightCapabilities.ENTITY_PATCH_PROVIDER.getCapability(entity);
	}
	
	public EntityPatch<?> getCapability() {
		return this.entitypatch;
	}
}
