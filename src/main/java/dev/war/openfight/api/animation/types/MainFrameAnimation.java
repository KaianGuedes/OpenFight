package dev.war.openfight.api.animation.types;

import dev.war.openfight.api.animation.AnimationManager.AnimationAccessor;
import dev.war.openfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import dev.war.openfight.api.animation.types.EntityState.StateFactor;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.client.animation.Layer;
import dev.war.openfight.api.client.animation.property.ClientAnimationProperties;
import dev.war.openfight.api.event.EpicFightEventHooks;
import dev.war.openfight.api.event.types.animation.StartActionEvent;
import dev.war.openfight.api.model.Armature;
import dev.war.openfight.api.utils.datastructure.ParameterizedHashMap;
import dev.war.openfight.api.utils.side.ClientOnly;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;

public class MainFrameAnimation extends StaticAnimation {
    public MainFrameAnimation(float convertTime, AnimationAccessor<? extends MainFrameAnimation> accessor, AssetAccessor<? extends Armature> armature) {
        super(convertTime, false, accessor, armature);
    }

    public MainFrameAnimation(float convertTime, String path, AssetAccessor<? extends Armature> armature) {
        super(convertTime, false, path, armature);
    }

    @Override
    public void begin(LivingEntityPatch<?> entitypatch) {
        if (entitypatch.getAnimator().getPlayerFor(null).getAnimation().get() == this) {
            ParameterizedHashMap<StateFactor<?>> stateMap = this.stateSpectrum.getStateMap(entitypatch, 0.0F);
            ParameterizedHashMap<StateFactor<?>> modifiedStateMap = new ParameterizedHashMap<> ();
            stateMap.forEach((k, v) -> modifiedStateMap.put(k, this.getModifiedLinkState(k, v, entitypatch, 0.0F)));
            entitypatch.updateEntityState(new EntityState(modifiedStateMap));
        }

        if (entitypatch.isLogicalClient()) {
            entitypatch.updateMotion(false);

            this.getProperty(StaticAnimationProperty.RESET_LIVING_MOTION).ifPresentOrElse(livingMotion -> {
                entitypatch.getClientAnimator().forceResetBeforeAction(livingMotion, livingMotion);
            }, () -> {
                entitypatch.getClientAnimator().resetMotion(true);
                entitypatch.getClientAnimator().resetCompositeMotion();
            });

            entitypatch.getClientAnimator().getPlayerFor(this.getAccessor()).setReversed(false);
        }

        super.begin(entitypatch);

        StartActionEvent startActionEvent = new StartActionEvent(entitypatch, this.getAccessor());
        EpicFightEventHooks.Animation.START_ACTION.postWithListener(startActionEvent, entitypatch.getEventListener());

        if (startActionEvent.shouldResetActionTick() && entitypatch instanceof PlayerPatch<?> playerPatch) {
            playerPatch.resetActionTick();
        }
    }

    @Override
    public void tick(LivingEntityPatch<?> entitypatch) {
        super.tick(entitypatch);

        if (entitypatch.getEntityState().movementLocked()) {
            entitypatch.getOriginal().walkAnimation.setSpeed(0);
        }
    }

    @Override
    public boolean isMainFrameAnimation() {
        return true;
    }

    @Override @ClientOnly
    public Layer.Priority getPriority() {
        return this.getProperty(ClientAnimationProperties.PRIORITY).orElse(Layer.Priority.HIGHEST);
    }
}