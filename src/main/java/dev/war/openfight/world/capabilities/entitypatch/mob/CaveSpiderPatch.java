package dev.war.openfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.entity.PathfinderMob;
import dev.war.openfight.api.utils.math.OpenMatrix4f;
import dev.war.openfight.gameasset.MobCombatBehaviors;
import dev.war.openfight.world.entity.ai.goal.AnimatedAttackGoal;
import dev.war.openfight.world.entity.ai.goal.TargetChasingGoal;

public class CaveSpiderPatch<T extends PathfinderMob> extends SpiderPatch<T> {
	public CaveSpiderPatch(T original) {
		super(original);
	}
	
	@Override
	protected void initAI() {
		super.initAI();
		this.original.goalSelector.addGoal(0, new AnimatedAttackGoal<>(this, MobCombatBehaviors.SPIDER.build(this)));
		this.original.goalSelector.addGoal(1, new TargetChasingGoal(this, this.getOriginal(), 1.0D, false));
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		return super.getModelMatrix(partialTicks).scale(0.7F, 0.7F, 0.7F);
	}
}