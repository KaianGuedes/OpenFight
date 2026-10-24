package dev.war.openfight.skill.identity;

import net.minecraft.client.renderer.RenderPipelines;
import com.google.common.collect.Maps;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import dev.war.openfight.api.animation.AnimationManager.AnimationAccessor;
import dev.war.openfight.api.animation.types.StaticAnimation;
import dev.war.openfight.api.client.event.EpicFightClientEventHooks;
import dev.war.openfight.api.client.event.types.hud.TickTargetIndicatorEvent;
import dev.war.openfight.api.event.EntityEventListener;
import dev.war.openfight.api.event.EpicFightEventHooks;
import dev.war.openfight.api.utils.AttackResult.ResultType;
import dev.war.openfight.api.utils.side.ClientOnly;
import dev.war.openfight.client.events.engine.ControlEngine;
import dev.war.openfight.client.gui.BattleModeGui;
import dev.war.openfight.gameasset.Animations;
import dev.war.openfight.main.EpicFightMod;
import dev.war.openfight.registry.entries.EpicFightSkillDataKeys;
import dev.war.openfight.skill.Skill;
import dev.war.openfight.skill.SkillBuilder;
import dev.war.openfight.skill.SkillCategories;
import dev.war.openfight.skill.SkillContainer;
import dev.war.openfight.world.capabilities.EpicFightCapabilities;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;
import dev.war.openfight.world.capabilities.item.CapabilityItem;
import dev.war.openfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import dev.war.openfight.world.capabilities.item.WeaponCapability;
import dev.war.openfight.world.capabilities.item.WeaponCategory;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class RevelationSkill extends Skill {
    public static RevelationSkill.Builder createRevelationSkillBuilder() {
        return new Builder(RevelationSkill::new)
            .addMotion(WeaponCategories.LONGSWORD, (item, player) -> Animations.REVELATION_TWOHAND)
            .addMotion(WeaponCategories.GREATSWORD, (item, player) -> Animations.REVELATION_TWOHAND)
            .addMotion(WeaponCategories.TACHI, (item, player) -> Animations.REVELATION_TWOHAND)
            .setCategory(SkillCategories.IDENTITY)
            .setActivateType(ActivateType.DURATION)
            .setResource(Resource.NONE);
    }

    public static class Builder extends SkillBuilder<RevelationSkill.Builder> {
        protected final Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, AnimationAccessor<? extends StaticAnimation>>> motions = Maps.newHashMap();

        public Builder(Function<RevelationSkill.Builder, ? extends Skill> constructor) {
            super(constructor);
        }

        public Builder addMotion(WeaponCategory weaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, AnimationAccessor<? extends StaticAnimation>> function) {
            this.motions.put(weaponCategory, function);
            return this;
        }
    }

    protected final Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, AnimationAccessor<? extends StaticAnimation>>> motions;
    protected final Map<EntityType<?>, Integer> maxRevelationStacks = Maps.newHashMap();
    protected int blockStack;
    protected int parryStack;
    protected int dodgeStack;
    protected int defaultRevelationStacks;

    public RevelationSkill(Builder builder) {
        super(builder);

        this.motions = builder.motions;
    }

    @Override
    public void loadDatapackParameters(CompoundTag parameters) {
        super.loadDatapackParameters(parameters);

        this.maxRevelationStacks.clear();
        this.blockStack = parameters.getIntOr("block_stacks", 0);
        this.parryStack = parameters.getIntOr("parry_stacks", 0);
        this.dodgeStack = parameters.getIntOr("dodge_stacks", 0);
        this.defaultRevelationStacks = parameters.getIntOr("default_revelation_stacks", 0);

        CompoundTag maxStacks = parameters.getCompoundOrEmpty("max_revelations");

        for (String registryName : maxStacks.keySet()) {
            EntityType<?> entityType = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getOptional(net.minecraft.resources.Identifier.parse(registryName)).orElse(null);

            if (entityType != null) {
                this.maxRevelationStacks.put(entityType, maxStacks.getIntOr(registryName, 0));
            } else {
                EpicFightMod.LOGGER.warn("Revelation registry error: no entity type named {}", registryName);
            }
        }
    }

    @Override
    public void onInitiate(SkillContainer skillContainer, EntityEventListener eventListener) {
        super.onInitiate(skillContainer, eventListener);

        eventListener.registerEvent(
            EpicFightEventHooks.Player.CAST_SKILL,
            event -> {
                if (skillContainer.getExecutor().isLogicalClient()) {
                    Skill skill = event.getSkillContainer().getSkill();

                    if (skill.getCategory() != SkillCategories.WEAPON_INNATE) {
                        return;
                    }

                    if (skillContainer.getExecutor().getTarget() != null) {
                        EpicFightCapabilities.getUnparameterizedEntityPatch(skillContainer.getExecutor().getTarget(), LivingEntityPatch.class).ifPresent(entitypatch -> {
                            if (this.isActivated(skillContainer)) {
                                if (skillContainer.sendCastRequest(skillContainer.getClientExecutor(), ControlEngine.getInstance()).isExecutable()) {
                                    skillContainer.setDuration(0);
                                    event.cancel();
                                }
                            }
                        });
                    }
                }
            },
            this
        );

        eventListener.registerEvent(
            EpicFightEventHooks.Player.SET_TARGET,
            event -> skillContainer.getDataManager().setDataSync(EpicFightSkillDataKeys.STACKS, 0),
            this
        );

        eventListener.registerEvent(
            EpicFightEventHooks.Entity.ON_DODGE,
            event -> {
                LivingEntity target = skillContainer.getExecutor().getTarget();

                if (target != null && target.is(event.getDamageSource().getDirectEntity())) {
                    this.checkStackAndActivate(skillContainer, target, skillContainer.getDataManager().getDataValue(EpicFightSkillDataKeys.STACKS), this.dodgeStack);
                }
            },
            this
        );

        eventListener.registerContextAwareEvent(
            EpicFightEventHooks.Entity.TAKE_DAMAGE_INCOME,
            (event, eventContext) -> {
                if (event.getResult() == ResultType.BLOCKED) {
                    LivingEntity target = skillContainer.getExecutor().getTarget();

                    if (target != null && target.is(event.getDamageSource().getDirectEntity())) {
                        int stacks = event.isParried() ? this.parryStack : this.blockStack;

                        this.checkStackAndActivate(skillContainer, target, skillContainer.getDataManager().getDataValue(EpicFightSkillDataKeys.STACKS), stacks);
                    }
                }
            },
            this
        );

        eventListener.registerEvent(
            EpicFightClientEventHooks.HUD.TARGET_INDICATOR_TICK,
            event -> {
                if (this.isActivated(skillContainer)) {
                    event.setType(TickTargetIndicatorEvent.Type.FLASH);
                }
            },
            this
        );
    }

	@Override
	public void executeOnServer(SkillContainer container, CompoundTag arguments) {
		super.executeOnServer(container, arguments);
		
		// Source the revelation animation map from the active combat cap so a longsword in
		// the offhand resolves the correct category-specific motion.
		CapabilityItem holdingItem = container.getExecutor().getPrimaryItemCapability();
		AnimationAccessor<? extends StaticAnimation> animation = this.motions.containsKey(holdingItem.getWeaponCategory()) ? this.motions.get(holdingItem.getWeaponCategory()).apply(holdingItem, container.getExecutor()) : Animations.REVELATION_ONEHAND;
		if (holdingItem instanceof WeaponCapability weaponCap)
        {
            animation = weaponCap.getCurrentSet(container.getServerExecutor()).getRevelation() != null ? weaponCap.getCurrentSet(container.getServerExecutor()).getRevelation() : animation;
        }
		container.getExecutor().playAnimationSynchronized(animation, 0.0F);
	}

	public void checkStackAndActivate(SkillContainer container, LivingEntity target, int stacks, int addStacks) {
		int maxStackSize = this.maxRevelationStacks.getOrDefault(target.getType(), this.defaultRevelationStacks);
		int plusStack = stacks + addStacks;
		
		if (plusStack < maxStackSize) {
			container.getDataManager().setDataSync(EpicFightSkillDataKeys.STACKS, plusStack);
		} else {
			if (!this.isActivated(container)) {
				this.setDurationSynchronize(container, this.maxDuration);
			}
			
			container.getDataManager().setDataSync(EpicFightSkillDataKeys.STACKS, 0);
		}
	}

	@ClientOnly @Override
	public boolean shouldDraw(SkillContainer container) {
		return container.getExecutor().getTarget() != null;
	}

    @ClientOnly @Override
	public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphicsExtractor guiGraphics, float x, float y, float partialTick) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.getSkillTexture(), (int)x, (int)y, (float)(0), (float)(0), 24, 24, 1, 1, 1, 1);
		int stacks = container.getRemainDuration() > 0 ? 0 : this.maxRevelationStacks.getOrDefault(container.getExecutor().getTarget().getType(), this.defaultRevelationStacks) - container.getDataManager().getDataValue(EpicFightSkillDataKeys.STACKS);
		guiGraphics.text(gui.getFont(), String.format("%d", stacks), (int)((int)(x + 18)), (int)((int)(y + 14)), 16777215, true);
	}
}