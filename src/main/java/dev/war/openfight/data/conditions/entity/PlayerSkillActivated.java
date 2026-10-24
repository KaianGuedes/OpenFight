package dev.war.openfight.data.conditions.entity;

import dev.war.openfight.platform.fabric.compat.TagCompat;
import net.minecraft.client.Minecraft;
import com.ibm.icu.text.MessageFormat;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import dev.war.openfight.api.data.reloader.SkillReloadListener;
import dev.war.openfight.api.utils.ParseUtil;
import dev.war.openfight.api.utils.side.ClientOnly;
import dev.war.openfight.client.gui.datapack.widgets.PopupBox;
import dev.war.openfight.data.conditions.Condition.EntityPatchCondition;
import dev.war.openfight.registry.EpicFightRegistries;
import dev.war.openfight.skill.Skill;
import dev.war.openfight.skill.SkillContainer;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class PlayerSkillActivated extends EntityPatchCondition {
	private Skill skill;
	
	@Override
	public PlayerSkillActivated read(CompoundTag tag) {
		String skillName = this.assertTag("skill", "string", tag, StringTag.class, (t, k) -> t.getStringOr(k, ""));
		
		if ((this.skill = SkillReloadListener.getSkill(skillName)) == null) {
			throw new NoSuchElementException(MessageFormat.format("{} condition error: Skill named {} does not exist", this.getClass().getSimpleName(), skillName));
		}
		
		return this;
	}
	
	@Override
	public CompoundTag serializePredicate() {
		CompoundTag tag = new CompoundTag();
		tag.putString("skill", this.skill.getRegistryName().toString());
		
		return tag;
	}
	
	@Override
	public boolean predicate(LivingEntityPatch<?> target) {
		if (target instanceof PlayerPatch<?> playerpatch) {
			Optional<SkillContainer> skill = playerpatch.getSkillContainerFor(this.skill);
			
			if (skill.isEmpty()) {
				return false;
			} else {
				return skill.get().isActivated();
			}
		}
		
		return false;
	}

    @Override @ClientOnly
    @Environment(EnvType.CLIENT) // TODO: Remove OnlyIn annotation and completely decouple the widget provider code
	public List<ParameterEditor> getAcceptingParameters(Screen screen) {
		AbstractWidget popupBox = new PopupBox.RegistryPopupBox<>(screen, Minecraft.getInstance().font, 0, 0, 0, 0, null, null, Component.literal("skill"), EpicFightRegistries.SKILL, null);
		return List.of(ParameterEditor.of(skill -> StringTag.valueOf(skill.toString()), tag -> EpicFightRegistries.SKILL.getValue(Identifier.parse(ParseUtil.nullOrToString(tag, TagCompat::getAsString))), popupBox));
	}
}