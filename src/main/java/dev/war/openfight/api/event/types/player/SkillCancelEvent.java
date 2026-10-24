package dev.war.openfight.api.event.types.player;

import dev.war.openfight.api.event.LivingEntityPatchEvent;
import dev.war.openfight.skill.SkillContainer;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;

public class SkillCancelEvent extends LivingEntityPatchEvent {
	private final SkillContainer skillContainer;
	
	public SkillCancelEvent(PlayerPatch<?> playerpatch, SkillContainer skillContainer) {
		super(playerpatch);
		
		this.skillContainer = skillContainer;
	}
	
	public SkillContainer getSkillContainer() {
		return this.skillContainer;
	}

    public final PlayerPatch<?> getPlayerPatch() {
        return (PlayerPatch<?>)this.getEntityPatch();
    }
}