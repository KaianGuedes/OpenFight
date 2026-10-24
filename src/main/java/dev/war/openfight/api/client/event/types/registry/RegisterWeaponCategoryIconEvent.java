package dev.war.openfight.api.client.event.types.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import dev.war.openfight.api.event.Event;
import dev.war.openfight.world.capabilities.item.WeaponCategory;

import java.util.Map;

public class RegisterWeaponCategoryIconEvent extends Event {
	final Map<WeaponCategory, ItemStack> registry;
	
	public RegisterWeaponCategoryIconEvent(Map<WeaponCategory, ItemStack> registry) {
		this.registry = registry;
	}
	
	public void registerCategory(WeaponCategory weaponCategory, Item item) {
		this.registry.put(weaponCategory, new ItemStack(item));
	}
	
	public void registerCategory(WeaponCategory weaponCategory, ItemStack itemstack) {
		this.registry.put(weaponCategory, itemstack);
	}
}
