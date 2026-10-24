package dev.war.openfight.data.conditions.entity;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import dev.war.openfight.api.utils.side.ClientOnly;
import dev.war.openfight.data.conditions.Condition;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;
import java.util.function.Function;

public class CustomCondition<T extends LivingEntityPatch<?>> implements Condition<T> {
	private final Function<T, Boolean> predicate;
	
	public CustomCondition(Function<T, Boolean> predicate) {
		this.predicate = predicate;
	}
	
	@Override
	public CustomCondition<T> read(CompoundTag tag) {
		// This condition doesn't support json conversion
		return null;
	}
	
	@Override
	public CompoundTag serializePredicate() {
		return null;
	}
	
	@Override
	public boolean predicate(T target) {
		return predicate.apply(target);
	}
	
	@Override @ClientOnly
    @Environment(EnvType.CLIENT) // TODO: Remove OnlyIn annotation and completely decouple the widget provider code
	public List<ParameterEditor> getAcceptingParameters(Screen screen) {
		return null;
	}
}