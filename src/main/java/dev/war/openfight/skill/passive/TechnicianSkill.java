package dev.war.openfight.skill.passive;

import net.minecraft.core.particles.ParticleTypes;
import dev.war.openfight.api.animation.types.DodgeAnimation;
import dev.war.openfight.api.event.EntityEventListener;
import dev.war.openfight.api.event.EpicFightEventHooks;
import dev.war.openfight.network.EntityPairingPacketTypes;
import dev.war.openfight.network.EpicFightNetworkManager;
import dev.war.openfight.network.server.SPEntityPairingPacket;
import dev.war.openfight.registry.entries.EpicFightSounds;
import dev.war.openfight.skill.SkillBuilder;
import dev.war.openfight.skill.SkillContainer;
import dev.war.openfight.skill.SkillSlots;

public class TechnicianSkill extends PassiveSkill {
    public TechnicianSkill(SkillBuilder<?> builder) {
        super(builder);
    }

    @Override
    public void onInitiate(SkillContainer skillContainer, EntityEventListener eventListener) {
        super.onInitiate(skillContainer, eventListener);

        eventListener.registerEvent(
            EpicFightEventHooks.Entity.ON_DODGE,
            event -> {
                skillContainer.getExecutor().playSound(EpicFightSounds.TECHNICIAN.get(), 1.0F, 1.0F, 1.0F);
                skillContainer.getServerExecutor().getOriginal().level().sendParticles(ParticleTypes.POOF, event.getLocation().x(), event.getLocation().y(), event.getLocation().z(), 4, 0.0D, 0.0D, 0.0D, 0.075D);
                float consumption = skillContainer.getExecutor().getModifiedStaminaConsume(skillContainer.getExecutor().getSkill(SkillSlots.DODGE).getSkill().getConsumption());
                skillContainer.getExecutor().setStamina(skillContainer.getExecutor().getStamina() + consumption);
            },
            this
        );

        eventListener.registerEvent(
            EpicFightEventHooks.Animation.BEGIN,
            event -> {
                if (!skillContainer.getExecutor().isLogicalClient() && event.getAnimation().checkType(DodgeAnimation.class)) {
                    EpicFightNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(new SPEntityPairingPacket(skillContainer.getServerExecutor().getOriginal().getId(), EntityPairingPacketTypes.TECHNICIAN_ACTIVATED), skillContainer.getServerExecutor().getOriginal());
                }
            },
            this
        );
    }
}