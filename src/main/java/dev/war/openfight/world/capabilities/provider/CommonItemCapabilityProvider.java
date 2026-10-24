package dev.war.openfight.world.capabilities.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.ShieldItem;
import dev.war.openfight.registry.deferred.holders.DeferredPreset;
import dev.war.openfight.registry.entries.EpicFightItemCapabilityPresets;
import dev.war.openfight.world.capabilities.item.*;

public final class CommonItemCapabilityProvider {
	public static final CommonItemCapabilityProvider INSTANCE = new CommonItemCapabilityProvider();
	
	private CommonItemCapabilityProvider() {}
	
	private final Map<Class<? extends Item>, DeferredPreset<? extends CapabilityItem.Builder<?>>> typedCapabilities = new HashMap<> ();
	private final Map<Item, CapabilityItem> capabilities = new HashMap<> ();
	
	public void registerWeaponTypesByClass() {
		this.typedCapabilities.put(ShieldItem.class, EpicFightItemCapabilityPresets.SHIELD);
        this.typedCapabilities.put(BowItem.class, EpicFightItemCapabilityPresets.BOW);
        this.typedCapabilities.put(CrossbowItem.class, EpicFightItemCapabilityPresets.CROSSBOW);
		this.typedCapabilities.put(MapItem.class, EpicFightItemCapabilityPresets.MAP);
	}
	
	public void put(Item item, CapabilityItem cap) {
		this.capabilities.put(item, cap);
	}
	
	public CapabilityItem get(Item item) {
		CapabilityItem cap = capabilities.get(item);
		return cap != null ? cap : getDefault(item);
	}

	private CapabilityItem getDefault(Item item)
	{
		DeferredPreset<? extends CapabilityItem.Builder<?>> builderEntry = this.typedCapabilities.getOrDefault(item.getClass(), null);
        if (builderEntry == null) {
            // Item components are only bound after the server's datapack reload has completed.
            if (item.builtInRegistryHolder().areComponentsBound() && item.components().has(DataComponents.EQUIPPABLE) && item.components().has(DataComponents.ATTRIBUTE_MODIFIERS)) builderEntry = EpicFightItemCapabilityPresets.ARMOR;
            else if (item.builtInRegistryHolder().is(ItemTags.SWORDS)) builderEntry = EpicFightItemCapabilityPresets.BOKKEN;
            else if (item.builtInRegistryHolder().is(ItemTags.PICKAXES)) builderEntry = EpicFightItemCapabilityPresets.PICKAXE;
            else if (item.builtInRegistryHolder().is(ItemTags.AXES)) builderEntry = EpicFightItemCapabilityPresets.AXE;
            else if (item.builtInRegistryHolder().is(ItemTags.SHOVELS)) builderEntry = EpicFightItemCapabilityPresets.SHOVEL;
            else if (item.builtInRegistryHolder().is(ItemTags.HOES)) builderEntry = EpicFightItemCapabilityPresets.HOE;
        }
		CapabilityItem.Builder<?> result = null;
		if (builderEntry != null)
		{
			if (builderEntry.value() instanceof WeaponCapability.Builder)
			{
				result = WeaponCapabilityPresets.registerPreset(builderEntry.value(), item);
			}
			else if (builderEntry.value() instanceof ArmorCapability.Builder builder)
			{
				result = builder.copy().byItem(item);
			}
			else
			{
				result = builderEntry.value();
			}
		}

		return result != null ? result.build() : null;
	}
	
	public void clear() {
		this.capabilities.clear();
	}
	
	public void addDefaultItems() {
		BuiltInRegistries.ITEM.entrySet().stream().filter(entry -> !this.capabilities.containsKey(entry.getValue())).forEach(entry -> {
			Function<Item, ? extends CapabilityItem.Builder<?>> type = null;
			Item item = entry.getValue();
			
			if (item instanceof BlockItem) {
				return;
			}
			
			for (Map.Entry<Identifier, ItemKeywordReloadListener.ItemRegex> regexEntry : ItemKeywordReloadListener.getRegexes().entrySet()) {
				if (regexEntry.getValue().matchesAny(entry.getKey().identifier().toString())) {
					type = WeaponTypeReloadListener.get(regexEntry.getKey());
					
					if (type != null) {
						this.capabilities.put(item, type.apply(item).build());
						break;
					}
				}
			}
			
			if (type == null) {
				Class<?> clazz = item.getClass();
				CapabilityItem capability = getDefault(item);
				
				for (; clazz != null && capability == null; clazz = clazz.getSuperclass()) {
					if (this.typedCapabilities.containsKey(clazz)) {
						capability = getDefault(item);
					}
				}
				
				if (capability != null) {
					this.capabilities.put(item, capability);
				}
			}
		});
	}
	
	public @Nullable CapabilityItem getCapability(ItemStack itemstack, Void context) {
		CapabilityItem itemCapability = this.capabilities.get(itemstack.getItem());
		if (itemCapability == null) itemCapability = get(itemstack.getItem());
		if (itemCapability != null) {
			
			if (itemCapability instanceof RuntimeCapability) {
				return itemCapability.findRecursive(itemstack);
			}
			
			return itemCapability;
		}
		
		return null;
	}
}
