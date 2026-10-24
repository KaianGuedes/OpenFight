package dev.war.openfight.data.conditions.entity;

import dev.war.openfight.platform.fabric.compat.TagCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import dev.war.openfight.api.utils.ParseUtil;
import dev.war.openfight.api.utils.side.ClientOnly;
import dev.war.openfight.client.gui.datapack.widgets.ResizableEditBox;
import dev.war.openfight.data.conditions.Condition.EntityPatchCondition;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.List;

public class PlayerName extends EntityPatchCondition {
	private String name;
	
	@Override
	public PlayerName read(CompoundTag tag) {
		this.name = this.assertTag("identifier", "string", tag, StringTag.class, (t, k) -> t.getStringOr(k, ""));
		return this;
	}
	
	@Override
	public CompoundTag serializePredicate() {
		CompoundTag tag = new CompoundTag();
		tag.putString("identifier", this.name);
		
		return tag;
	}
	
	@Override
	public boolean predicate(LivingEntityPatch<?> target) {
		if (target instanceof PlayerPatch<?> playerpatch) {
			return playerpatch.getOriginal().getName().getString().equals(this.name);
		}
		
		return false;
	}

    @Override @ClientOnly
    @Environment(EnvType.CLIENT) // TODO: Remove OnlyIn annotation and completely decouple the widget provider code
	public List<ParameterEditor> getAcceptingParameters(Screen screen) {
		ResizableEditBox editbox = new ResizableEditBox(Minecraft.getInstance().font, 0, 0, 0, 0, Component.literal("identifier"), null, null);
		return List.of(ParameterEditor.of((name) -> StringTag.valueOf(name.toString()), (tag) -> ParseUtil.nullOrToString(tag, TagCompat::getAsString), editbox));
	}
}