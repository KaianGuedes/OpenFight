package dev.war.openfight.skill.weapon_passive;

import dev.war.openfight.api.event.EntityEventListener;
import dev.war.openfight.api.event.EpicFightEventHooks;
import dev.war.openfight.gameasset.Animations;
import dev.war.openfight.registry.entries.EpicFightSkillDataKeys;
import dev.war.openfight.skill.Skill;
import dev.war.openfight.skill.SkillBuilder;
import dev.war.openfight.skill.SkillContainer;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;

public class BattojutsuPassive extends Skill {
    public BattojutsuPassive(SkillBuilder<?> builder) {
        super(builder);
    }

    @Override
    public void onInitiate(SkillContainer skillContainer, EntityEventListener eventListener) {
        super.onInitiate(skillContainer, eventListener);

        eventListener.registerEvent(
            EpicFightEventHooks.Animation.START_ACTION,
            event -> {
                if (!event.getEntityPatch().isLogicalClient()) {
                    this.setConsumptionSynchronize(skillContainer, 0.0F);
                    this.setStackSynchronize(skillContainer, 0);
                }
            },
            this
        );

        eventListener.registerEvent(
            EpicFightEventHooks.Player.USE_ITEM,
            event -> {
                this.onReset(skillContainer);
            },
            this
        );
    }

    @Override
    public void onReset(SkillContainer container) {
        container.runOnServer(serverExecutor -> {
            if (container.getDataManager().getDataValue(EpicFightSkillDataKeys.SHEATH)) {
                container.getDataManager().setDataSync(EpicFightSkillDataKeys.SHEATH, false);
                serverExecutor.modifyLivingMotionByCurrentItem(false);
            }

            container.getSkill().setConsumptionSynchronize(container, 0);
        });
    }

    @Override
    public void setConsumption(SkillContainer container, float value) {
        container.runOnServer(serverExecutor -> {
            if (container.getMaxResource() < value) {
                container.getDataManager().setDataSync(EpicFightSkillDataKeys.SHEATH, true);
                serverExecutor.modifyLivingMotionByCurrentItem(false);
                serverExecutor.playAnimationInClientSide(Animations.BIPED_UCHIGATANA_SCRAP, 0.0F);
            }
        });

        super.setConsumption(container, value);
    }

    @Override
    public boolean shouldDeactivateAutomatically(PlayerPatch<?> executer) {
        return true;
    }

    @Override
    public float getCooldownRegenPerSecond(PlayerPatch<?> playerpatch) {
        return (playerpatch.getEntityState().inaction() || playerpatch.isHoldingAny()) ? 0.0F : 1.0F;
    }
}