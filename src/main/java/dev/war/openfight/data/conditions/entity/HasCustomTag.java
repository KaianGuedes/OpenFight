package dev.war.openfight.data.conditions.entity;

import dev.war.openfight.platform.fabric.compat.TagCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import dev.war.openfight.api.utils.ParseUtil;
import dev.war.openfight.api.utils.side.ClientOnly;
import dev.war.openfight.client.gui.datapack.widgets.ResizableEditBox;
import dev.war.openfight.data.conditions.Condition;
import dev.war.openfight.data.conditions.Condition.EntityCondition;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HasCustomTag extends EntityCondition {
	private final Set<String> allowedTags;
	
	public HasCustomTag(ListTag allowedTags) {
		this.allowedTags = allowedTags.stream().map(TagCompat::getAsString).collect(Collectors.toUnmodifiableSet());
	}
	
	@Override
	public Condition<Entity> read(CompoundTag tag) {
		// This condition doesn't support json conversion
		return null;
	}
	
	@Override
	public CompoundTag serializePredicate() {
		return null;
	}
	
	@Override
	public boolean predicate(Entity target) {
		for (String tag : this.allowedTags) {
			if (target.entityTags().contains(tag)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override @ClientOnly
    @Environment(EnvType.CLIENT) // TODO: Remove OnlyIn annotation and completely decouple the widget provider code
	public List<ParameterEditor> getAcceptingParameters(Screen screen) {
		ResizableEditBox editbox = new ResizableEditBox(Minecraft.getInstance().font, 0, 0, 0, 0, Component.literal("tag"), null, null);
		return List.of(ParameterEditor.of((value) -> StringTag.valueOf(value.toString()), (tag) -> ParseUtil.nullOrToString(tag, TagCompat::getAsString), (AbstractWidget)editbox));
	}
}