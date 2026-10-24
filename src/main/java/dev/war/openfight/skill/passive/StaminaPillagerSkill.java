package dev.war.openfight.skill.passive;

import net.minecraft.nbt.CompoundTag;
import dev.war.openfight.api.event.EntityEventListener;
import dev.war.openfight.api.event.EpicFightEventHooks;
import dev.war.openfight.api.event.IdentifierProvider;
import dev.war.openfight.api.utils.side.ClientOnly;
import dev.war.openfight.main.EpicFightMod;
import dev.war.openfight.network.EntityPairingPacketTypes;
import dev.war.openfight.network.EpicFightNetworkManager;
import dev.war.openfight.network.server.SPEntityPairingPacket;
import dev.war.openfight.registry.entries.EpicFightSounds;
import dev.war.openfight.skill.SkillBuilder;
import dev.war.openfight.skill.SkillContainer;

import java.util.List;

public class StaminaPillagerSkill extends PassiveSkill {
    public static final IdentifierProvider ASHEN_DECORATIONS = IdentifierProvider.constant(EpicFightMod.identifier("stamina_pillager_ashen"));

    protected float regenRate;

    public StaminaPillagerSkill(SkillBuilder<?> builder) {
        super(builder);
    }

    @Override
    public void loadDatapackParameters(CompoundTag parameters) {
        super.loadDatapackParameters(parameters);
        this.regenRate = parameters.getFloatOr("regen_rate", 0.0F);
    }

    @Override
    public void onInitiate(SkillContainer container, EntityEventListener eventListener) {
        super.onInitiate(container, eventListener);

        eventListener.registerEvent(
            EpicFightEventHooks.Entity.KILL_ENTITY,
            event -> {
                float currentStamina = container.getExecutor().getStamina();
                float staminaLoss = container.getExecutor().getMaxStamina() - currentStamina;
                container.getExecutor().setStamina(currentStamina + Math.min(staminaLoss * this.regenRate * 0.01F, 2.0F));
                event.getKilledEntity().playSound(EpicFightSounds.STAMINA_PILLAGER_DEATH.get());
                EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(new SPEntityPairingPacket(event.getKilledEntity().getId(), EntityPairingPacketTypes.STAMINA_PILLAGER_BODY_ASHES), event.getKilledEntity());

                SPEntityPairingPacket pairingPacket = new SPEntityPairingPacket(container.getExecutor().getOriginal().getId(), EntityPairingPacketTypes.FLASH_WHITE);

                // durationTick, maxOverlay, maxBrightness, disableRedOverlay
                pairingPacket.buffer().writeInt(8);
                pairingPacket.buffer().writeInt(3);
                pairingPacket.buffer().writeInt(6);
                pairingPacket.buffer().writeBoolean(false);

                EpicFightNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(pairingPacket, container.getServerExecutor().getOriginal());
            },
            this
        );
    }

    @Override @ClientOnly
    public List<Object> getTooltipArgsOfScreen(List<Object> list) {
        list.add(String.format("%.0f", this.regenRate));

        return list;
    }
}