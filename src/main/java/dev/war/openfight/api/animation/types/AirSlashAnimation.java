package dev.war.openfight.api.animation.types;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import dev.war.openfight.api.animation.AnimationManager.AnimationAccessor;
import dev.war.openfight.api.animation.Joint;
import dev.war.openfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import dev.war.openfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import dev.war.openfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import dev.war.openfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.collider.Collider;
import dev.war.openfight.api.model.Armature;
import dev.war.openfight.api.utils.math.ValueModifier;
import dev.war.openfight.gameasset.Animations;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public class AirSlashAnimation extends AttackAnimation {
	public AirSlashAnimation(float transitionTime, float antic, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, AnimationAccessor<? extends AirSlashAnimation> accessor, AssetAccessor<? extends Armature> armature) {
		this(transitionTime, antic, antic, contact, recovery, true, collider, colliderJoint, accessor, armature);
	}
	
	public AirSlashAnimation(float transitionTime, float antic, float preDelay, float contact, float recovery, boolean directional, @Nullable Collider collider, Joint colliderJoint, AnimationAccessor<? extends AirSlashAnimation> accessor, AssetAccessor<? extends Armature> armature) {
		this(transitionTime, accessor, armature, new Phase(0.0F, antic, preDelay, contact, recovery, Float.MAX_VALUE, colliderJoint, collider));
		
		if (directional) {
			this.addProperty(StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER);
		}
	}
	
	public AirSlashAnimation(float transitionTime, AnimationAccessor<? extends AirSlashAnimation> accessor, AssetAccessor<? extends Armature> armature, Phase... phases) {
		super(transitionTime, accessor, armature, phases);
		
		this.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F));
		this.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F);
		this.addProperty(ActionAnimationProperty.STOP_MOVEMENT, false);
		this.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true);
	}
	
	public AirSlashAnimation(float transitionTime, String path, AssetAccessor<? extends Armature> armature, Phase... phases) {
		super(transitionTime, path, armature, phases);
		
		this.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F));
		this.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F);
		this.addProperty(ActionAnimationProperty.STOP_MOVEMENT, false);
		this.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true);
	}
	
	@Override
	protected void spawnHitParticle(ServerLevel world, LivingEntityPatch<?> attackerpatch, Entity hit, Phase phase) {
		super.spawnHitParticle(world, attackerpatch, hit, phase);
		world.sendParticles(ParticleTypes.CRIT, hit.getX(), hit.getY(), hit.getZ(), 15, 0.0D, 0.0D, 0.0D, 1.0D);
	}
	
	@Override
	public boolean isComboAttackAnimation() {
		return true;
	}
}