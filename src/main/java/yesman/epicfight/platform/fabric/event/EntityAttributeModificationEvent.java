package yesman.epicfight.platform.fabric.event;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;

/** Applies upstream per-mob impact, armor, strike and stamina defaults to Fabric's builder callbacks. */
public final class EntityAttributeModificationEvent {
    private final FabricDefaultAttributeRegistry.ModifyContext context;
    public EntityAttributeModificationEvent(FabricDefaultAttributeRegistry.ModifyContext context) { this.context=context; }
    public void add(EntityType<? extends LivingEntity> type,Holder<Attribute> attribute) {
        context.modify(type,(entity,builder)->builder.add(attribute));
    }
    public void add(EntityType<? extends LivingEntity> type,Holder<Attribute> attribute,double value) {
        context.modify(type,(entity,builder)->builder.add(attribute,value));
    }
}
