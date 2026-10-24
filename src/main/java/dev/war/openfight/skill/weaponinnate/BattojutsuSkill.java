package dev.war.openfight.skill.weaponinnate;

import dev.war.openfight.registry.entries.EpicFightSkillDataKeys;
import dev.war.openfight.skill.SkillSlots;
import dev.war.openfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class BattojutsuSkill extends ConditionalWeaponInnateSkill {
	public BattojutsuSkill(ConditionalWeaponInnateSkill.Builder builder) {
		super(builder);
	}
	
	@Override
	public void playSkillAnimation(ServerPlayerPatch executor) {
		boolean isSheathed = executor.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().getDataValue(EpicFightSkillDataKeys.SHEATH);
		
		if (isSheathed) {
			executor.playAnimationSynchronized(this.attackAnimations[this.getAnimationInCondition(executor)], -0.65F);
		} else {
			executor.playAnimationSynchronized(this.attackAnimations[this.getAnimationInCondition(executor)], 0);
		}
	}
}