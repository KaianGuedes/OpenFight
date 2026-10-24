package dev.war.openfight.api.animation.types;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.war.openfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.resources.Identifier;
import dev.war.openfight.api.animation.AnimationClip;
import dev.war.openfight.api.animation.AnimationManager.AnimationAccessor;
import dev.war.openfight.api.animation.AnimationPlayer;
import dev.war.openfight.api.animation.Pose;
import dev.war.openfight.api.animation.TransformSheet;
import dev.war.openfight.api.animation.property.AnimationProperty;
import dev.war.openfight.api.animation.types.EntityState.StateFactor;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.client.animation.property.JointMaskEntry;
import dev.war.openfight.api.utils.datastructure.ParameterizedMap;
import dev.war.openfight.api.utils.side.ClientOnly;
import dev.war.openfight.main.EpicFightMod;
import dev.war.openfight.main.EpicFightSharedConstants;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

import org.jetbrains.annotations.Nullable;
import java.util.Map;
import java.util.Optional;

public abstract class DynamicAnimation {
    protected final boolean isRepeat;
    protected final float transitionTime;
    protected AnimationClip animationClip;

    public DynamicAnimation() {
        this(EpicFightSharedConstants.GENERAL_ANIMATION_TRANSITION_TIME, false);
    }

    public DynamicAnimation(float transitionTime, boolean isRepeat) {
        this.isRepeat = isRepeat;
        this.transitionTime = transitionTime;
    }

    public final Pose getRawPose(float time) {
        return this.getAnimationClip().getPoseInTime(time);
    }

    public Pose getPoseByTime(LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
        Pose pose = this.getRawPose(time);
        this.modifyPose(this, pose, entitypatch, time, partialTicks);

        return pose;
    }

    /** ModifyEntity the pose both this and link animation. **/
    public void modifyPose(DynamicAnimation animation, Pose pose, LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
    }

    public void putOnPlayer(AnimationPlayer animationPlayer, LivingEntityPatch<?> entitypatch) {
        animationPlayer.setPlayAnimation(this.getAccessor());
        animationPlayer.tick(entitypatch);
        animationPlayer.begin(this.getAccessor(), entitypatch);
    }

    /**
     * Called before the animation put on the {@link AnimationPlayer}
     * @param entitypatch
     */
    public void begin(LivingEntityPatch<?> entitypatch) {}

    /**
     * Called each tick when the animation is played
     * @param entitypatch
     */
    public void tick(LivingEntityPatch<?> entitypatch) {}

    /**
     * Called when both the animation finished or stopped by other animation.
     * @param entitypatch
     * @param nextAnimation the next animation to play after the animation ends
     * @param isEnd whether the animation completed or not
     *
     * if @param isEnd true, nextAnimation is null
     * if @param isEnd false, nextAnimation is not null
     */
    public void end(LivingEntityPatch<?> entitypatch, @Nullable AssetAccessor<? extends DynamicAnimation> nextAnimation, boolean isEnd) {}
    public void linkTick(LivingEntityPatch<?> entitypatch, AssetAccessor<? extends DynamicAnimation> linkAnimation) {};

    public boolean hasTransformFor(String joint) {
        return this.getTransfroms().containsKey(joint);
    }

    @ClientOnly
    public Optional<JointMaskEntry> getJointMaskEntry(LivingEntityPatch<?> entitypatch, boolean useCurrentMotion) {
        return Optional.empty();
    }

    public EntityState getState(LivingEntityPatch<?> entitypatch, float time) {
        return EntityState.DEFAULT_STATE;
    }

    public ParameterizedMap<StateFactor<?>> getStatesMap(LivingEntityPatch<?> entitypatch, float time) {
        return EntityState.DEFAULT_STATE.getStateMap();
    }

    public <T> T getState(StateFactor<T> stateFactor, LivingEntityPatch<?> entitypatch, float time) {
        return stateFactor.defaultValue();
    }

    public AnimationClip getAnimationClip() {
        return this.animationClip;
    }

    public Map<String, TransformSheet> getTransfroms() {
        return this.getAnimationClip().getJointTransforms();
    }

    public float getPlaySpeed(LivingEntityPatch<?> entitypatch, DynamicAnimation animation) {
        return 1.0F;
    }

    public TransformSheet getCoord() {
        return this.getTransfroms().containsKey("Root") ? this.getTransfroms().get("Root") : TransformSheet.EMPTY_SHEET;
    }

    public void setTotalTime(float totalTime) {
        this.getAnimationClip().setClipTime(totalTime);
    }

    public float getTotalTime() {
        return this.getAnimationClip().getClipTime();
    }

    public float getTransitionTime() {
        return this.transitionTime;
    }

    public boolean isRepeat() {
        return this.isRepeat;
    }

    public boolean canBePlayedReverse() {
        return false;
    }

    public Identifier getRegistryName() {
        return EpicFightMod.identifier("");
    }

    public int getId() {
        return -1;
    }

    public <V> Optional<V> getProperty(AnimationProperty<V> propertyType) {
        return Optional.empty();
    }

    public boolean isComboAttackAnimation() {
        return false;
    }

    public boolean isMainFrameAnimation() {
        return false;
    }

    public boolean isReboundAnimation() {
        return false;
    }

    public boolean isMetaAnimation() {
        return false;
    }

    public boolean isClientAnimation() {
        return false;
    }

    public boolean isStaticAnimation() {
        return false;
    }

    public abstract <A extends DynamicAnimation> AnimationAccessor<? extends DynamicAnimation> getAccessor();
    public abstract AssetAccessor<? extends StaticAnimation> getRealAnimation();

    public boolean isLinkAnimation() {
        return false;
    }

    public boolean doesHeadRotFollowEntityHead() {
        return false;
    }

    @ClientOnly
    public void renderDebugging(PoseStack poseStack, MultiBufferSource buffer, LivingEntityPatch<?> entitypatch, float playTime, float partialTicks) {
    }
}