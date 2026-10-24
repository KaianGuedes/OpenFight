package dev.war.openfight.registry.entries;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import dev.war.openfight.world.capabilities.EpicFightCapabilities;
import dev.war.openfight.world.capabilities.provider.AttachmentEntityPatchProvider;

public final class EpicFightAttachmentTypes {
    private EpicFightAttachmentTypes() {}
    public static final AttachmentType<AttachmentEntityPatchProvider> ENTITY_PATCH =
        AttachmentRegistry.create(Identifier.fromNamespaceAndPath("epicfight", "entitypatch"));
    public static AttachmentEntityPatchProvider get(Entity entity) {
        AttachmentTarget target = (AttachmentTarget)entity;
        AttachmentEntityPatchProvider attached = target.getAttached(ENTITY_PATCH);
        if (attached != null) {
            return attached;
        }

        // Most entity types have no Epic Fight patch. Avoid retaining an empty provider on every such entity.
        if (!EpicFightCapabilities.ENTITY_PATCH_PROVIDER.hasCapabilityProvider(entity)) {
            return AttachmentEntityPatchProvider.EMPTY;
        }

        return target.getAttachedOrCreate(ENTITY_PATCH, () -> new AttachmentEntityPatchProvider(entity));
    }
}
