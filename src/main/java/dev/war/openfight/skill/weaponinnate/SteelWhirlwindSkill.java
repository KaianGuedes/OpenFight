package dev.war.openfight.skill.weaponinnate;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import dev.war.openfight.api.animation.AnimationManager.AnimationAccessor;
import dev.war.openfight.api.animation.types.AttackAnimation;
import dev.war.openfight.api.animation.types.AttackAnimation.Phase;
import dev.war.openfight.api.animation.types.StaticAnimation;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.client.event.EpicFightClientEventHooks;
import dev.war.openfight.api.client.input.InputManager;
import dev.war.openfight.api.client.input.PlayerInputState;
import dev.war.openfight.api.event.EntityEventListener;
import dev.war.openfight.api.utils.side.ClientOnly;
import dev.war.openfight.client.events.engine.ControlEngine;
import dev.war.openfight.client.input.EpicFightKeyMappings;
import dev.war.openfight.gameasset.Animations;
import dev.war.openfight.network.server.SPSkillFeedback;
import dev.war.openfight.registry.entries.EpicFightSynchedAnimationVariableKeys;
import dev.war.openfight.skill.SkillContainer;
import dev.war.openfight.skill.modules.ChargeableSkill;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;
import dev.war.openfight.world.capabilities.item.CapabilityItem;

import java.util.List;

public class SteelWhirlwindSkill extends WeaponInnateSkill implements ChargeableSkill {
    private AnimationAccessor<? extends StaticAnimation> chargingAnimation;
    private AnimationAccessor<? extends AttackAnimation> attackAnimation;

    public SteelWhirlwindSkill(WeaponInnateSkill.Builder<?> builder) {
        super(builder);

        this.chargingAnimation = Animations.STEEL_WHIRLWIND_CHARGING;
        this.attackAnimation = Animations.STEEL_WHIRLWIND;
    }

    @Override
    public void onInitiate(SkillContainer skillContainer, EntityEventListener eventListener) {
        super.onInitiate(skillContainer, eventListener);

        eventListener.registerEvent(
            EpicFightClientEventHooks.Control.MAPPED_MOVEMENT_INPUT_UPDATE,
            event -> {
                if (skillContainer.getExecutor().isHoldingSkill(this)) {
                    LocalPlayer clientPlayer = skillContainer.getClientExecutor().getOriginal();
                    clientPlayer.setSprinting(false);
                    clientPlayer.sprintTriggerTime = -1;
                    ControlEngine.setSprintingKeyStateNotDown();

                    final PlayerInputState current = event.getInputState();
                    final PlayerInputState updated = current
                        .withForwardImpulse(current.forwardImpulse() * (1.0F - 0.8F * skillContainer.getExecutor().getSkillChargingTicks() / 30.0F));
                    InputManager.setInputState(updated);
                }
            },
            this
        );
    }

    @Override
    public WeaponInnateSkill registerPropertiesToAnimation() {
        AttackAnimation anim = this.attackAnimation.get();

        for (Phase phase : anim.phases) {
            phase.addProperties(this.properties.get(0).entrySet());
        }

        return this;
    }

    @Override
    public int getAllowedMaxChargingTicks() {
        return 60;
    }

    @Override
    public int getMaxChargingTicks() {
        return 30;
    }

    @Override
    public int getMinChargingTicks() {
        return 6;
    }

    @Override
    public void startHolding(SkillContainer container) {
        AssetAccessor<? extends StaticAnimation> currentPlaying = container.getExecutor().getAnimator().getPlayerFor(null).getRealAnimation();

        if (currentPlaying.get().isMainFrameAnimation()) {
            container.getExecutor().stopPlaying(currentPlaying);
        }

        container.getExecutor().playAnimationSynchronized(this.chargingAnimation, 0.0F);
    }

    @Override
    public void resetHolding(SkillContainer container) {
        if (container.getExecutor().isLogicalClient()) {
            container.getExecutor().getAnimator().stopPlaying(this.chargingAnimation);
        } else {
            container.getExecutor().stopPlaying(this.chargingAnimation);
        }
    }

    @Override
    public void onStopHolding(SkillContainer container, SPSkillFeedback feedback) {
        container.getExecutor().getAnimator().getVariables().put(EpicFightSynchedAnimationVariableKeys.CHARGING_TICKS.get(), this.attackAnimation, container.getExecutor().getAccumulatedChargeTicks());
        container.getExecutor().playAnimationSynchronized(this.attackAnimation, 0.0F);
        this.cancelOnServer(container, null);
    }

    @Override
    public void holdTick(SkillContainer container) {
        ChargeableSkill.super.holdTick(container);
    }

    @Override @ClientOnly
    public KeyMapping getKeyMapping() {
        return EpicFightKeyMappings.WEAPON_INNATE_SKILL;
    }

    @Override
    public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
        List<Component> list = super.getTooltipOnItem(itemStack, cap, playerCap);
        this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(0), "Each Strike:");

        return list;
    }
}