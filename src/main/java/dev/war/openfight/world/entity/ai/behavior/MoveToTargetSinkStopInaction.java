package dev.war.openfight.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import dev.war.openfight.world.capabilities.EpicFightCapabilities;
import dev.war.openfight.world.capabilities.entitypatch.MobPatch;

public class MoveToTargetSinkStopInaction extends MoveToTargetSink {
	@Override
	protected boolean canStillUse(ServerLevel level, Mob mob, long gameTime) {
		if (super.canStillUse(level, mob, gameTime)) {
			MobPatch<?> mobpatch = EpicFightCapabilities.getEntityPatch(mob, MobPatch.class);
			
			if (mobpatch != null) {
				return !mobpatch.getEntityState().inaction();
			}
		}
		
		return false;
	}
}