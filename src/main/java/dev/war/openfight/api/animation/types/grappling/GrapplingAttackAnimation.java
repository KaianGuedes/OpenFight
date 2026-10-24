package dev.war.openfight.api.animation.types.grappling;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import dev.war.openfight.api.animation.AnimationManager.AnimationAccessor;
import dev.war.openfight.api.animation.AnimationPlayer;
import dev.war.openfight.api.animation.Keyframe;
import dev.war.openfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import dev.war.openfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import dev.war.openfight.api.animation.property.MoveCoordFunctions;
import dev.war.openfight.api.animation.types.AttackAnimation;
import dev.war.openfight.api.animation.types.DynamicAnimation;
import dev.war.openfight.api.animation.types.EntityState;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.model.Armature;
import dev.war.openfight.api.utils.math.Vec3f;
import dev.war.openfight.api.utils.side.ClientOnly;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;
import dev.war.openfight.world.damagesource.EpicFightDamageSource;

public class GrapplingAttackAnimation extends AttackAnimation {
	public GrapplingAttackAnimation(float contact, float recovery, AnimationAccessor<? extends GrapplingAttackAnimation> accessor, AssetAccessor<? extends Armature> armature) {
		this(contact, recovery, InteractionHand.MAIN_HAND, accessor, armature);
	}
	
	public GrapplingAttackAnimation(float contact, float recovery, InteractionHand hand, AnimationAccessor<? extends GrapplingAttackAnimation> accessor, AssetAccessor<? extends Armature> armature) {
		super(0.0F, 0.0F, contact, contact, recovery, hand, null, armature.get().rootJoint, accessor, armature);
		
		this.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F);
		this.addProperty(ActionAnimationProperty.MOVE_ON_LINK, false);
		this.addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD);
		this.addProperty(ActionAnimationProperty.COORD_SET_TICK, null);
		this.addProperty(ActionAnimationProperty.COORD_GET, MoveCoordFunctions.ATTACHED);
	}
	
	@Override
	public void begin(LivingEntityPatch<?> entitypatch) {
		if (entitypatch.shouldMoveOnCurrentSide(this)) {
			Keyframe[] grapplingAnimCoord = entitypatch.getAnimator().getVariables().getOrDefaultSharedVariable(ACTION_ANIMATION_COORD).getKeyframes();
			Vec3f translation = grapplingAnimCoord[grapplingAnimCoord.length - 1].transform().translation();
			entitypatch.getOriginal().setDeltaMovement(0.0D, 0.0D, 0.0D);
			entitypatch.getOriginal().setPos(translation.toDoubleVector());
		}
		
		super.begin(entitypatch);
	}
	
	@Override
	public void end(LivingEntityPatch<?> entitypatch, AssetAccessor<? extends DynamicAnimation> nextAnimation, boolean isEnd) {
		super.end(entitypatch, nextAnimation, isEnd);
		entitypatch.setGrapplingTarget(null);
	}
	
	@Override
	protected void attackTick(LivingEntityPatch<?> entitypatch, AssetAccessor<? extends DynamicAnimation> animation) {
		AnimationPlayer player = entitypatch.getAnimator().getPlayerFor(this.getAccessor());
		float elapsedTime = player.getElapsedTime();
		float prevElapsedTime = player.getPrevElapsedTime();
		EntityState state = animation.get().getState(entitypatch, elapsedTime);
		EntityState prevState = animation.get().getState(entitypatch, prevElapsedTime);
		Phase phase = this.getPhaseByTime(elapsedTime);
		
		if (prevState.attacking() || state.attacking() || (prevState.getLevel() < 2 && state.getLevel() > 2)) {
			LivingEntity grapplingTarget = entitypatch.getGrapplingTarget();
			
			if (grapplingTarget != null) {
				EpicFightDamageSource source = this.getEpicFightDamageSource(entitypatch, grapplingTarget, phase);
				source.setInitialPosition(null);
				int prevInvulTime = grapplingTarget.invulnerableTime;
				grapplingTarget.invulnerableTime = 0;
				entitypatch.attack(source, grapplingTarget, InteractionHand.MAIN_HAND);
				grapplingTarget.invulnerableTime = prevInvulTime;
			}
		}
	}
	
	@Override @ClientOnly
	public void renderDebugging(PoseStack poseStack, MultiBufferSource buffer, LivingEntityPatch<?> entitypatch, float playTime, float partialTicks) {
	}
}