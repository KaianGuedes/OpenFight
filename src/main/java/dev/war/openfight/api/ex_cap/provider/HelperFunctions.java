package dev.war.openfight.api.ex_cap.provider;

import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.ApiStatus;
import dev.war.openfight.skill.Skill;
import dev.war.openfight.skill.SkillSlot;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;
import dev.war.openfight.world.capabilities.item.WeaponCategory;
@ApiStatus.Internal
public class HelperFunctions
{
	//Initial Helper Functions
	public static boolean itemCheck(LivingEntityPatch<?> entityPatch, WeaponCategory category, InteractionHand hand)
	{
		return entityPatch.getHoldingItemCapability(hand).isWeaponCategory(category);
	}
	public static boolean skillCheck(LivingEntityPatch<?> entityPatch, Skill skill, SkillSlot slot)
	{
		if (entityPatch instanceof PlayerPatch<?> patch)
		{
			if (slot == null)
				return (patch.getSkill(skill)) != null;
			return patch.getSkill(slot).hasSkill(skill);
		}
		return false;
	}
}
