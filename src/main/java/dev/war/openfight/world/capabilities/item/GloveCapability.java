package dev.war.openfight.world.capabilities.item;

import dev.war.openfight.platform.fabric.compat.ItemClassifier;
import net.minecraft.world.item.ItemStack;
import dev.war.openfight.world.capabilities.EpicFightCapabilities;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public class GloveCapability extends WeaponCapability {

	public GloveCapability(WeaponCapability.Builder builder) {
		super(builder);
	}
	
	@Override
	public boolean checkOffhandValid(LivingEntityPatch<?> entitypatch) {
		ItemStack offhandItme = entitypatch.getOriginal().getOffhandItem();
		CapabilityItem itemCap = EpicFightCapabilities.getItemStackCapability(offhandItme);
		boolean isFist = itemCap.getWeaponCategory() == WeaponCategories.FIST;
		return isFist || !(ItemClassifier.isSword(offhandItme.getItem()) || ItemClassifier.isDigger(offhandItme.getItem()));
	}
	
	@Override
	public boolean canHoldInOffhandAlone() {
		return true;
	}
}