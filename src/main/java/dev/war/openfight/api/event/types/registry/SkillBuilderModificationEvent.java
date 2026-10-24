package dev.war.openfight.api.event.types.registry;

import net.minecraft.resources.Identifier;
import dev.war.openfight.api.event.Event;
import dev.war.openfight.skill.SkillBuilder;

public class SkillBuilderModificationEvent extends Event {
	private final Identifier registryName;
	private final SkillBuilder<?> skillBuilder;
	
	public SkillBuilderModificationEvent(Identifier registryName, SkillBuilder<?> skillBuilder) {
		this.registryName = registryName;
		this.skillBuilder = skillBuilder;
	}
	
	public Identifier getRegistryName() {
		return this.registryName;
	}
	
	public SkillBuilder<?> getSkillBuilder() {
		return this.skillBuilder;
	}
}
