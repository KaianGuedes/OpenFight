package dev.war.openfight.data.conditions.entity;

import dev.war.openfight.platform.fabric.compat.TagCompat;
import net.minecraft.client.Minecraft;
import io.netty.util.internal.StringUtil;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import dev.war.openfight.api.utils.ParseUtil;
import dev.war.openfight.api.utils.side.ClientOnly;
import dev.war.openfight.client.gui.datapack.widgets.ResizableEditBox;
import dev.war.openfight.data.conditions.Condition.EntityPatchCondition;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;

public class RandomChance extends EntityPatchCondition {
	private double chance;
	
	public RandomChance() {
		this.chance = 0.0D;
	}
	
	public RandomChance(double chance) {
		this.chance = chance;
	}
	
	@Override
	public RandomChance read(CompoundTag tag) {
		this.chance = this.assertTag("chance", "decimal", tag, NumericTag.class, (t, k) -> t.getDoubleOr(k, 0.0D));
		return this;
	}
	
	@Override
	public CompoundTag serializePredicate() {
		CompoundTag tag = new CompoundTag();
		tag.putDouble("chance", this.chance);
		
		return tag;
	}
	
	@Override
	public boolean predicate(LivingEntityPatch<?> target) {
		return target.getOriginal().getRandom().nextDouble() < this.chance;
	}
	
	@Override @ClientOnly
    @Environment(EnvType.CLIENT) // TODO: Remove OnlyIn annotation and completely decouple the widget provider code
	public List<ParameterEditor> getAcceptingParameters(Screen screen) {
		ResizableEditBox editbox = new ResizableEditBox(Minecraft.getInstance().font, 0, 0, 0, 0, Component.literal("chance"), null, null);
		editbox.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Double::parseDouble));
		
		return List.of(ParameterEditor.of((value) -> ParseUtil.parseOrGet(value.toString(), (v) -> DoubleTag.valueOf(Double.parseDouble(value.toString())), StringTag.valueOf("")), (tag) -> ParseUtil.valueOfOmittingType(ParseUtil.nullOrToString(tag, TagCompat::getAsString)), editbox));
	}
}
