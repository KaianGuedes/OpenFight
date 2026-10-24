package dev.war.openfight.skill.dodge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.Attributes;
import dev.war.openfight.api.animation.types.EntityState;
import dev.war.openfight.api.client.input.InputManager;
import dev.war.openfight.api.client.input.MovementDirection;
import dev.war.openfight.api.utils.side.ClientOnly;
import dev.war.openfight.client.events.engine.ControlEngine;
import dev.war.openfight.client.input.InputUtils;
import dev.war.openfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import dev.war.openfight.skill.SkillContainer;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;

public class KnockdownWakeupSkill extends DodgeSkill {
	public KnockdownWakeupSkill(DodgeSkill.Builder<?> builder) {
		super(builder);
	}
	
	@Override @ClientOnly
	public void gatherArguments(SkillContainer container, ControlEngine controlEngine, CompoundTag arguments) {
		LocalPlayerPatch executor = container.getClientExecutor();
		LocalPlayer localPlayer = executor.getOriginal();
		float pulse = (float)executor.getOriginal().getAttributeValue(Attributes.SNEAKING_SPEED);
		InputUtils.sneakingTick(localPlayer, false, pulse);
		
		final MovementDirection movementDirection = MovementDirection.fromInputState(InputManager.getInputState(localPlayer.input));
        final int horizon = movementDirection.horizontal();
        final float yRot = Minecraft.getInstance().gameRenderer.mainCamera().yRot();
		
		arguments.putInt("direction", horizon >= 0 ? 0 : 1);
		arguments.putFloat("yRot", yRot);
	}
	
	@Override
	public boolean isExecutableState(PlayerPatch<?> executor) {
		EntityState playerState = executor.getEntityState();
		float elapsedTime = executor.getAnimator().getPlayerFor(null).getElapsedTime();
		// See DodgeSkill#isExecutableState: require onGround() rather than the laxer isInAir().
		return executor.getOriginal().onGround()
			&& !(playerState.hurt() && !playerState.knockDown())
			&& !executor.getOriginal().isInWater()
			&& !executor.getOriginal().onClimbable()
			&& elapsedTime > 0.7F;
	}
}