package dev.war.openfight.api.animation.types;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import dev.war.openfight.api.animation.AnimationManager.AnimationAccessor;
import dev.war.openfight.api.animation.Joint;
import dev.war.openfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import dev.war.openfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import dev.war.openfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import dev.war.openfight.api.animation.types.EntityState.StateFactor;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.client.animation.Layer;
import dev.war.openfight.api.client.animation.property.JointMaskEntry;
import dev.war.openfight.api.client.input.InputManager;
import dev.war.openfight.api.client.input.PlayerInputState;
import dev.war.openfight.api.collider.Collider;
import dev.war.openfight.api.model.Armature;
import dev.war.openfight.api.utils.datastructure.ParameterizedHashMap;
import dev.war.openfight.api.utils.side.ClientOnly;
import dev.war.openfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import dev.war.openfight.gameasset.Animations;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;
import dev.war.openfight.world.gamerule.EpicFightGameRules;

import org.jetbrains.annotations.Nullable;
import java.util.Locale;
import java.util.Optional;

public class ComboAttackAnimation extends AttackAnimation {
	public ComboAttackAnimation(float transitionTime, float antic, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, AnimationAccessor<? extends ComboAttackAnimation> accessor, AssetAccessor<? extends Armature> armature) {
		this(transitionTime, antic, antic, contact, recovery, collider, colliderJoint, accessor, armature);
	}
	
	public ComboAttackAnimation(float transitionTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, AnimationAccessor<? extends ComboAttackAnimation> accessor, AssetAccessor<? extends Armature> armature) {
		super(transitionTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature);
		
		this.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true);
		this.addProperty(ActionAnimationProperty.MOVE_VERTICAL, false);
		this.addProperty(StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER);
	}
	
	public ComboAttackAnimation(float transitionTime, float antic, float contact, float recovery, InteractionHand hand, @Nullable Collider collider, Joint colliderJoint, AnimationAccessor<? extends ComboAttackAnimation> accessor, AssetAccessor<? extends Armature> armature) {
		super(transitionTime, antic, antic, contact, recovery, hand, collider, colliderJoint, accessor, armature);
		
		this.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true);
		this.addProperty(ActionAnimationProperty.MOVE_VERTICAL, false);
		this.addProperty(StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER);
	}
	
	public ComboAttackAnimation(float transitionTime, AnimationAccessor<? extends ComboAttackAnimation> accessor, AssetAccessor<? extends Armature> armature, Phase... phases) {
		super(transitionTime, accessor, armature, phases);
		
		this.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true);
		this.addProperty(ActionAnimationProperty.MOVE_VERTICAL, false);
		this.addProperty(StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER);
	}
	
	public ComboAttackAnimation(float transitionTime, String path, AssetAccessor<? extends Armature> armature, Phase... phases) {
		super(transitionTime, path, armature, phases);
		
		this.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true);
		this.addProperty(ActionAnimationProperty.MOVE_VERTICAL, false);
		this.addProperty(StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER);
	}
	
	@Override
	protected void bindPhaseState(Phase phase) {
		float preDelay = phase.preDelay;
		
		this.stateSpectrumBlueprint
			.newTimePair(phase.start, preDelay)
			.addState(EntityState.PHASE_LEVEL, 1)
			.newTimePair(phase.start, phase.contact)
			.addState(EntityState.SKILL_EXECUTABLE, false)
			.newTimePair(phase.start, phase.recovery)
			.addState(EntityState.MOVEMENT_LOCKED, true)
			.addState(EntityState.UPDATE_LIVING_MOTION, false)
			.addState(EntityState.COMBO_ATTACKS_DOABLE, false)
			.newTimePair(phase.start, phase.end)
			.addState(EntityState.INACTION, true)
			.newTimePair(preDelay, phase.contact)
			.addState(EntityState.ATTACKING, true)
			.addState(EntityState.PHASE_LEVEL, 2)
			.newTimePair(phase.contact, phase.end)
			.addState(EntityState.PHASE_LEVEL, 3)
			.addState(EntityState.TURNING_LOCKED, true);
	}
	
	@Override
	public void loadAnimation() {
		super.loadAnimation();
		
		if (!this.properties.containsKey(AttackAnimationProperty.BASIS_ATTACK_SPEED)) {
			float basisSpeed = Float.parseFloat(String.format(Locale.US, "%.2f", (1.0F / this.getTotalTime())));
			this.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, basisSpeed);
		}
	}
	
	@Override
	public ParameterizedHashMap<StateFactor<?>> getStatesMap(LivingEntityPatch<?> entitypatch, float time) {
		ParameterizedHashMap<StateFactor<?>> stateMap = super.getStatesMap(entitypatch, time);
		
		if (!EpicFightGameRules.STIFF_COMBO_ATTACKS.getRuleValue(entitypatch.getOriginal().level())) {
			stateMap.put(EntityState.MOVEMENT_LOCKED, (Object)false);
			stateMap.put(EntityState.UPDATE_LIVING_MOTION, (Object)true);
		}
		
		return stateMap;
	}
	
	@Override
	protected Vec3 getCoordVector(LivingEntityPatch<?> entitypatch, AssetAccessor<? extends DynamicAnimation> dynamicAnimation) {
		Vec3 vec3 = super.getCoordVector(entitypatch, dynamicAnimation);
		
		if (entitypatch.shouldBlockMoving() && this.getProperty(ActionAnimationProperty.CANCELABLE_MOVE).orElse(false)) {
			vec3 = vec3.scale(0.0F);
		}
		
		return vec3;
	}
	
	@Override
	public Optional<JointMaskEntry> getJointMaskEntry(LivingEntityPatch<?> entitypatch, boolean useCurrentMotion) {
		if (entitypatch.isLogicalClient()) {
			if (entitypatch.getClientAnimator().getPriorityFor(this.getAccessor()) == Layer.Priority.HIGHEST) {
				return Optional.of(JointMaskEntry.COMBO_ATTACK_MASK);
			}
		}
		
		return super.getJointMaskEntry(entitypatch, useCurrentMotion);
	}

	@Override
	public boolean isComboAttackAnimation() {
		return true;
	}
	
	@Override @ClientOnly
	public boolean shouldPlayerMove(LocalPlayerPatch playerpatch) {
		if (playerpatch.isLogicalClient()) {
            return EpicFightGameRules.STIFF_COMBO_ATTACKS.getRuleValue(playerpatch.getOriginal().level());
		}
		
		return true;
	}

    @ClientOnly
    private static boolean isPlayerMoving(LocalPlayerPatch localPlayerPatch) {
        final PlayerInputState inputState = InputManager.getInputState(localPlayerPatch.getOriginal());
        return inputState.forwardImpulse() != 0.0F || inputState.leftImpulse() != 0.0F;
    }
}