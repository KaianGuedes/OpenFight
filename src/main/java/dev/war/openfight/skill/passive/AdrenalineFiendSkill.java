package dev.war.openfight.skill.passive;

import dev.war.openfight.api.event.EntityEventListener;
import dev.war.openfight.api.event.EpicFightEventHooks;
import dev.war.openfight.network.EntityPairingPacketTypes;
import dev.war.openfight.network.server.SPEntityPairingPacket;
import dev.war.openfight.registry.entries.EpicFightSkillDataKeys;
import dev.war.openfight.skill.SkillBuilder;
import dev.war.openfight.skill.SkillContainer;

public class AdrenalineFiendSkill extends PassiveSkill {
	public AdrenalineFiendSkill(SkillBuilder<?> builder) {
        super(builder);
    }

    @Override
    public void onInitiate(SkillContainer container, EntityEventListener eventListener) {
        super.onInitiate(container, eventListener);

        eventListener.registerEvent(
            EpicFightEventHooks.Entity.KILL_ENTITY,
            event -> {
                if (!container.getExecutor().isLogicalClient() && container.getExecutor().getStaminaRegenAwaitTicks() > 0) {
                    if (container.getExecutor().getStamina() < container.getExecutor().getMaxStamina()) {
                        container.getExecutor().setStaminaRegenAwaitTicks(0);
                        container.getDataManager().setData(EpicFightSkillDataKeys.TICK_RECORD, container.getExecutor().getOriginal().tickCount);
                        container.getExecutor().sendToAllPlayersTrackingMe(new SPEntityPairingPacket(container.getExecutor().getOriginal().getId(), EntityPairingPacketTypes.ADRENALINE_ACTIVATED));
                    }
                }
            },
            this
        );

        eventListener.registerEvent(
            EpicFightEventHooks.Animation.START_ACTION,
            event -> {
                if (container.getDataManager().getDataValue(EpicFightSkillDataKeys.TICK_RECORD) + 30 > container.getExecutor().getOriginal().tickCount) {
                    event.resetActionTick(false);
                }
            },
            this
        );
    }
}
