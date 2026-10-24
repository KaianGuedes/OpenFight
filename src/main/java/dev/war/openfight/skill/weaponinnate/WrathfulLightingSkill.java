package dev.war.openfight.skill.weaponinnate;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import dev.war.openfight.api.animation.types.AttackAnimation;
import dev.war.openfight.api.animation.types.AttackAnimation.Phase;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;
import dev.war.openfight.world.capabilities.item.CapabilityItem;

public class WrathfulLightingSkill extends SimpleWeaponInnateSkill {
	public WrathfulLightingSkill(Builder builder) {
		super(builder);
	}
	
	@Override
	public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem itemCap, PlayerPatch<?> playerpatch) {
		List<Component> list = super.getTooltipOnItem(itemStack, itemCap, playerpatch);
		this.generateTooltipforPhase(list, itemStack, itemCap, playerpatch, this.properties.get(1), "Thunder:");
		
		return list;
	}
	
	@Override
	public WeaponInnateSkill registerPropertiesToAnimation() {
		AttackAnimation anim = this.attackAnimation.get();
		
		for (Phase phase : anim.phases) {
			phase.addProperties(this.properties.get(0).entrySet());
			phase.addProperties(this.properties.get(1).entrySet());
		}
		
		return this;
	}
}