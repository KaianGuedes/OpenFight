package yesman.epicfight.platform.fabric.compat;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;

public final class AttributeKeys {
	private AttributeKeys() {}
	
	public static ResourceKey<Attribute> keyOf(Holder<Attribute> attribute) {
		return attribute.unwrapKey().orElseThrow();
	}
}
