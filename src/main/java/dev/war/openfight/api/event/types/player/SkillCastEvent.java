package dev.war.openfight.api.event.types.player;

import net.minecraft.nbt.CompoundTag;
import dev.war.openfight.api.event.CancelableEvent;
import dev.war.openfight.api.event.LivingEntityPatchEvent;
import dev.war.openfight.skill.SkillContainer;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;

public class SkillCastEvent extends LivingEntityPatchEvent implements CancelableEvent {
	private final SkillContainer skillContainer;
	private final CompoundTag arguments;
	private boolean skillExecutable;
	private boolean stateExecutable;

	public SkillCastEvent(PlayerPatch<?> playerPatch, SkillContainer skillContainer, CompoundTag arguments) {
		super(playerPatch);
		this.skillContainer = skillContainer;
		this.arguments = arguments;
	}

	public SkillContainer getSkillContainer() {
		return this.skillContainer;
	}

	public boolean isSkillExecutable() {
		return this.skillExecutable;
	}

	public boolean isStateExecutable() {
		return this.stateExecutable;
	}

	public void setSkillExecutable(boolean skillExecutable) {
		this.skillExecutable = skillExecutable;
	}

	public void setStateExecutable(boolean stateExecutable) {
		this.stateExecutable = stateExecutable;
	}

	public boolean isExecutable() {
		return this.skillExecutable && this.stateExecutable;
	}

	public boolean shouldReserveKey() {
		return !this.isExecutable() && !this.isCanceled();
	}

	public CompoundTag getArguments() {
		return this.arguments;
	}

    public final PlayerPatch<?> getPlayerPatch() {
        return (PlayerPatch<?>)this.getEntityPatch();
    }
}