package dev.war.openfight.skill;

import dev.war.openfight.api.utils.ExtensibleEnum;
import dev.war.openfight.api.utils.ExtensibleEnumManager;

public interface SkillSlot extends ExtensibleEnum {
	ExtensibleEnumManager<SkillSlot> ENUM_MANAGER = new ExtensibleEnumManager<> ("skill_slot");
	
	SkillCategory category();
}