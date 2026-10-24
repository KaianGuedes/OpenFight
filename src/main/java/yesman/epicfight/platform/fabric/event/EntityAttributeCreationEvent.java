package yesman.epicfight.platform.fabric.event;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

/** Installs the original custom-entity attribute suppliers through Fabric. */
public final class EntityAttributeCreationEvent {
    public void put(EntityType<? extends LivingEntity> type,AttributeSupplier supplier) {
        FabricDefaultAttributeRegistry.register(type,supplier);
    }
}
