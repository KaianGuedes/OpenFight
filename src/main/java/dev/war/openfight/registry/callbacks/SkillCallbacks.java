package dev.war.openfight.registry.callbacks;

import net.minecraft.core.Registry;
import dev.war.openfight.platform.fabric.registry.callback.BakeCallback;
import dev.war.openfight.skill.Skill;

public class SkillCallbacks implements BakeCallback<Skill> {
	private static final SkillCallbacks INSTANCE = new SkillCallbacks();
	
	public static SkillCallbacks getSkillCallback() {
		return SkillCallbacks.INSTANCE;
	}
	
	@Override
	public void onBake(Registry<Skill> registry) {
		// dereferencing holder in skill instance
		registry.listElements().forEach(holder -> {
			holder.value().setHolder(holder);
		});
	}
}
