package dev.war.openfight.skill;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import dev.war.openfight.api.utils.ExtensibleEnum;
import dev.war.openfight.api.utils.ExtensibleEnumManager;
import dev.war.openfight.api.utils.ParseUtil;
import dev.war.openfight.client.gui.screen.SkillEditScreen;
import dev.war.openfight.main.EpicFightMod;

public interface SkillCategory extends ExtensibleEnum {
    Identifier DEFAULT_BOOK_ICON = EpicFightMod.identifier("skillbook");
	
	ExtensibleEnumManager<SkillCategory> ENUM_MANAGER = new ExtensibleEnumManager<> ("skill_category");
	
	/// Determines if the skill should be saved in NBT
	boolean shouldSave();
	
	/// Determines if the skill should be synched to clients
	boolean shouldSynchronize();
	
	/// Determines if the skill is modifiable by player, through skill books, commands, or [SkillEditScreen]
	boolean learnable();

    /// Returns a translation key
    /// For backward compatibility, this method is implemented as default
    default Component getTranslationKey() {
        return Component.translatable(String.format("skill.%s.category.%s", EpicFightMod.NAMESPACE, ParseUtil.toLowerCase(this.toString())));
    }

	/// Texture location of the Skill book icon in inventory UI
	default Identifier bookIcon() {
		return DEFAULT_BOOK_ICON;
	}
}