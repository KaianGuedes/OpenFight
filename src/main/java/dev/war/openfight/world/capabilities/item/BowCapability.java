package dev.war.openfight.world.capabilities.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemUseAnimation;
import dev.war.openfight.api.animation.LivingMotion;
import dev.war.openfight.api.animation.LivingMotions;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public class BowCapability extends RangedWeaponCapability {
	public BowCapability(RangedWeaponCapability.Builder builder) {
		super(builder);
	}

    @Override
	public LivingMotion getLivingMotion(LivingEntityPatch<?> entitypatch, InteractionHand hand) {
		return entitypatch.getOriginal().isUsingItem() && entitypatch.getOriginal().getUseItem().getUseAnimation() == ItemUseAnimation.BOW ? LivingMotions.AIM : null;
	}
}