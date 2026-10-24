package dev.war.openfight.skill.passive;

import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundTag;
import dev.war.openfight.api.animation.types.AttackAnimation;
import dev.war.openfight.api.animation.types.DynamicAnimation;
import dev.war.openfight.api.animation.types.EntityState;
import dev.war.openfight.api.event.EntityEventListener;
import dev.war.openfight.api.event.EpicFightEventHooks;
import dev.war.openfight.api.utils.side.ClientOnly;
import dev.war.openfight.network.EntityPairingPacketTypes;
import dev.war.openfight.network.EpicFightNetworkManager;
import dev.war.openfight.network.server.SPEntityPairingPacket;
import dev.war.openfight.registry.entries.EpicFightSounds;
import dev.war.openfight.skill.Skill;
import dev.war.openfight.skill.SkillBuilder;
import dev.war.openfight.skill.SkillCategories;
import dev.war.openfight.skill.SkillContainer;
import dev.war.openfight.world.capabilities.item.CapabilityItem;
import dev.war.openfight.world.capabilities.item.WeaponCategory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class EmergencyEscapeSkill extends PassiveSkill {
    public static Builder createEmergencyEscapeBuilder() {
        return new EmergencyEscapeSkill.Builder(EmergencyEscapeSkill::new).setCategory(SkillCategories.PASSIVE).setResource(Resource.COOLDOWN);
    }

    public static class Builder extends SkillBuilder<EmergencyEscapeSkill.Builder> {
        protected final Set<WeaponCategory> availableWeapons = Sets.newHashSet();

        public Builder(Function<EmergencyEscapeSkill.Builder, ? extends Skill> constructor) {
            super(constructor);
        }

        public Builder addAvailableWeaponCategory(WeaponCategory... wc) {
            this.availableWeapons.addAll(Arrays.asList(wc));
            return this;
        }
    }

    private final Set<WeaponCategory> availableWeapons;

    private boolean isAvailableCategory(CapabilityItem item)
    {
        for (var setCategory : availableWeapons)
        {
            if (item.isWeaponCategory(setCategory))
            {
                return true;
            }
        }
        return false;
    }

    public EmergencyEscapeSkill(EmergencyEscapeSkill.Builder builder) {
        super(builder);

        this.availableWeapons = builder.availableWeapons;
    }

    @Override
    public void onInitiate(SkillContainer container, EntityEventListener eventListener) {
        super.onInitiate(container, eventListener);

        eventListener.registerEvent(
            EpicFightEventHooks.Player.CAST_SKILL,
            event -> {
                if (event.getSkillContainer().getSkill().getCategory() == SkillCategories.DODGE) {
                    EntityState state = container.getExecutor().getEntityState();
                    DynamicAnimation animation = container.getExecutor().getAnimator().getPlayerFor(null).getRealAnimation().get();

                    if (
                        (
                            !event.isStateExecutable() && animation instanceof AttackAnimation &&
                                    this.isAvailableCategory(container.getExecutor().getPrimaryItemCapability())
                        ) ||
                        (
                            state.hurt() &&
                            container.getStack() > 0
                        )
                    ) {
                        event.setStateExecutable(true);
                    }
                }
            },
            this
        );

        eventListener.registerEvent(
            EpicFightEventHooks.Player.CONSUME_SKILL,
            event -> {
                if (event.getSkill().getCategory() == SkillCategories.DODGE) {
                    if (!container.getExecutor().getOriginal().isCreative()) {
                        if (event.getSkill().getConsumption() > container.getExecutor().getStamina()) {
                            if (container.getExecutor().consumeForSkill(this, this.resource)) {
                                if (!container.getExecutor().isLogicalClient()) {
                                    this.executeOnServer(container, event.getArguments());
                                }

                                event.setResourceType(Skill.Resource.NONE);
                            }
                        } else if (container.getExecutor().getEntityState().hurt() && container.getExecutor().consumeForSkill(this, this.resource)) {
                            if (!container.getExecutor().isLogicalClient()) {
                                this.executeOnServer(container, event.getArguments());
                            }
                        }
                    }
                }
            },
            this
        );
    }

    @Override
    public void executeOnServer(SkillContainer container, CompoundTag arguments) {
        this.setStackSynchronize(container, container.getStack() - 1);
        float yRot = container.getExecutor().getYRot();

        if (arguments.contains("yRot")) {
            yRot = arguments.getFloatOr("yRot", 0.0F);
        }

        container.getExecutor().playSound(EpicFightSounds.EMERGENCY_ESCAPE.get(), 1.0F, 1.0F);

        SPEntityPairingPacket pairingPacket = new SPEntityPairingPacket(container.getExecutor().getOriginal().getId(), EntityPairingPacketTypes.EMERGENCY_ESCAPE_ACTIVATED);
        pairingPacket.buffer().writeFloat(yRot);

        EpicFightNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(pairingPacket, container.getServerExecutor().getOriginal());
    }

    @ClientOnly
    @Override
    public boolean shouldDraw(SkillContainer container) {
        return container.getStack() == 0;
    }

    @ClientOnly
    @Override
    public List<Object> getTooltipArgsOfScreen(List<Object> list) {
        list.add(String.format("%.1f", this.consumption));
        return list;
    }

    @Override
    public Set<WeaponCategory> getAvailableWeaponCategories() {
        return this.availableWeapons;
    }
}