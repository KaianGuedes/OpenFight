package dev.war.openfight.world.capabilities.item;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemUseAnimation;
import dev.war.openfight.api.animation.AnimationManager.AnimationAccessor;
import dev.war.openfight.api.animation.LivingMotion;
import dev.war.openfight.api.animation.LivingMotions;
import dev.war.openfight.api.animation.types.StaticAnimation;
import dev.war.openfight.gameasset.Animations;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ShieldCapability extends WeaponCapability {
	public ShieldCapability(WeaponCapability.Builder builder) {
		super(builder);
	}
	
	/*
	 * Avoid duplicated usage with guard skill
	 */
	@Override
	public ItemUseAnimation getUseAnimation(LivingEntityPatch<?> entityPatch) {
		return ItemUseAnimation.NONE;
	}

	@Override
	public Map<LivingMotion, AnimationAccessor<? extends StaticAnimation>> getLivingMotionModifier(LivingEntityPatch<?> playerdata, InteractionHand hand) {
		return ImmutableMap.of(LivingMotions.BLOCK_SHIELD, Animations.BIPED_BLOCK);
	}
}