package dev.war.openfight.skill.guard;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import dev.war.openfight.api.animation.AnimationManager.AnimationAccessor;
import dev.war.openfight.api.animation.LivingMotions;
import dev.war.openfight.api.animation.types.StaticAnimation;
import dev.war.openfight.api.client.event.EpicFightClientEventHooks;
import dev.war.openfight.api.client.input.InputManager;
import dev.war.openfight.api.client.input.PlayerInputState;
import dev.war.openfight.api.event.EntityEventListener;
import dev.war.openfight.api.event.EpicFightEventHooks;
import dev.war.openfight.api.event.types.entity.TakeDamageEvent;
import dev.war.openfight.api.event.types.player.SkillCancelEvent;
import dev.war.openfight.api.utils.AttackResult;
import dev.war.openfight.api.utils.side.ClientOnly;
import dev.war.openfight.client.events.engine.ControlEngine;
import dev.war.openfight.client.gui.BattleModeGui;
import dev.war.openfight.client.gui.screen.SkillBookScreen;
import dev.war.openfight.client.input.EpicFightKeyMappings;
import dev.war.openfight.gameasset.Animations;
import dev.war.openfight.network.EpicFightNetworkManager;
import dev.war.openfight.network.server.SPSetSkillContainerValue;
import dev.war.openfight.network.server.SPSkillFeedback;
import dev.war.openfight.particle.HitParticleType;
import dev.war.openfight.registry.entries.EpicFightParticles;
import dev.war.openfight.registry.entries.EpicFightSkillDataKeys;
import dev.war.openfight.registry.entries.EpicFightSkills;
import dev.war.openfight.registry.entries.EpicFightSounds;
import dev.war.openfight.skill.Skill;
import dev.war.openfight.skill.SkillBuilder;
import dev.war.openfight.skill.SkillCategories;
import dev.war.openfight.skill.SkillContainer;
import dev.war.openfight.skill.modules.HoldableSkill;
import dev.war.openfight.world.capabilities.EpicFightCapabilities;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;
import dev.war.openfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import dev.war.openfight.world.capabilities.item.CapabilityItem;
import dev.war.openfight.world.capabilities.item.CapabilityItem.Styles;
import dev.war.openfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import dev.war.openfight.world.capabilities.item.WeaponCapability;
import dev.war.openfight.world.capabilities.item.WeaponCategory;
import dev.war.openfight.world.damagesource.EpicFightDamageSource;
import dev.war.openfight.world.damagesource.EpicFightDamageTypeTags;

import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class GuardSkill extends Skill implements HoldableSkill {
	public static class Builder extends SkillBuilder<GuardSkill.Builder> {
		protected Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?>> guardMotions = new HashMap<> ();
		protected Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?>> advancedGuardMotions = new HashMap<> ();
		protected Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?>> guardBreakMotions = new HashMap<> ();

		public Builder(Function<Builder, ? extends GuardSkill> constructor) {
			super(constructor);
		}

		public Builder addGuardMotion(WeaponCategory weaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?> function) {
			guardMotions.put(weaponCategory, function);
			return this;
		}

		public Builder addAdvancedGuardMotion(WeaponCategory weaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?> function) {
			advancedGuardMotions.put(weaponCategory, function);
			return this;
		}

		public Builder addGuardBreakMotion(WeaponCategory weaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?> function) {
			guardBreakMotions.put(weaponCategory, function);
			return this;
		}
	}

	public static GuardSkill.Builder createGuardBuilder(Function<GuardSkill.Builder, GuardSkill> constructor) {
		return new GuardSkill.Builder(constructor)
				.addGuardMotion(WeaponCategories.AXE, (item, player) -> Animations.SWORD_GUARD_HIT)
				.addGuardMotion(WeaponCategories.GREATSWORD, (item, player) -> Animations.GREATSWORD_GUARD_HIT)
				.addGuardMotion(WeaponCategories.UCHIGATANA, (item, player) -> Animations.UCHIGATANA_GUARD_HIT)
				.addGuardMotion(WeaponCategories.LONGSWORD, (item, player) -> Animations.LONGSWORD_GUARD_HIT)
				.addGuardMotion(WeaponCategories.SWORD, (item, player) -> item.getStyle(player) == Styles.ONE_HAND ? Animations.SWORD_GUARD_HIT : Animations.SWORD_DUAL_GUARD_HIT)
				.addGuardMotion(WeaponCategories.TACHI, (item, player) -> Animations.LONGSWORD_GUARD_HIT)
				.addGuardBreakMotion(WeaponCategories.AXE, (item, player) -> Animations.BIPED_COMMON_NEUTRALIZED)
				.addGuardBreakMotion(WeaponCategories.GREATSWORD, (item, player) -> Animations.GREATSWORD_GUARD_BREAK)
				.addGuardBreakMotion(WeaponCategories.UCHIGATANA, (item, player) -> Animations.BIPED_COMMON_NEUTRALIZED)
				.addGuardBreakMotion(WeaponCategories.LONGSWORD, (item, player) -> Animations.BIPED_COMMON_NEUTRALIZED)
				.addGuardBreakMotion(WeaponCategories.SPEAR, (item, player) -> Animations.BIPED_COMMON_NEUTRALIZED)
				.addGuardBreakMotion(WeaponCategories.SWORD, (item, player) -> Animations.BIPED_COMMON_NEUTRALIZED)
				.addGuardBreakMotion(WeaponCategories.TACHI, (item, player) -> Animations.BIPED_COMMON_NEUTRALIZED)
				.setCategory(SkillCategories.GUARD)
				.setActivateType(ActivateType.HELD)
				.setResource(Resource.STAMINA);
	}

	protected final Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?>> guardMotions;
	protected final Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?>> advancedGuardMotions;
	protected final Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?>> guardBreakMotions;
	protected float penalizer;

	public GuardSkill(GuardSkill.Builder builder) {
		super(builder);
		this.guardMotions = builder.guardMotions;
		this.advancedGuardMotions = builder.advancedGuardMotions;
		this.guardBreakMotions = builder.guardBreakMotions;
	}

	@Override
	public void loadDatapackParameters(CompoundTag parameters) {
		super.loadDatapackParameters(parameters);
		this.penalizer = parameters.getFloatOr("penalizer", 0.0F);
	}

    @Override
    public void onInitiate(SkillContainer skillContainer, EntityEventListener eventListener) {
        eventListener.registerEvent(
            EpicFightClientEventHooks.Entity.MODIFY_PLAYER_LIVING_MOTION_COMPOSITE,
            event -> {
                if (
                    skillContainer.isActivated() &&
                    // Use the active combat cap so guarding works with an offhand-only weapon
                    // in mirror mode -- otherwise the bare mainhand would fail the available-weapon
                    // check and the BLOCK motion would never trigger on the visual side.
                    this.isHoldingWeaponAvailable(skillContainer.getExecutor(), skillContainer.getExecutor().getPrimaryItemCapability(), GuardSkill.BlockType.GUARD)
                ) {
                    event.setMotion(LivingMotions.BLOCK);
                }
            },
            this
        );

        eventListener.registerEvent(
            EpicFightClientEventHooks.Control.MAPPED_MOVEMENT_INPUT_UPDATE,
            event -> {
                if (skillContainer.isActivated() && skillContainer.getExecutor().getHoldingSkill() == this) {
                    skillContainer.getExecutor().getOriginal().setSprinting(false);
                    skillContainer.getClientExecutor().getOriginal().sprintTriggerTime = -1;

                    ControlEngine.setSprintingKeyStateNotDown();
                    final PlayerInputState current = event.getInputState();
                    final PlayerInputState updated = current
                        .withForwardImpulse(current.forwardImpulse() * 0.5f)
                        .withLeftImpulse(current.leftImpulse() * 0.5f);
                    InputManager.setInputState(updated);
                }
            },
            this
        );

        eventListener.registerEvent(
            EpicFightEventHooks.Entity.TAKE_DAMAGE_INCOME,
            event -> {
                CapabilityItem itemCapability = skillContainer.getExecutor().getPrimaryItemCapability();

                if (skillContainer.isActivated() && skillContainer.getExecutor().getHoldingSkill() == this) {
                    DamageSource damageSource = event.getDamageSource();
                    boolean isFront = false;
                    Vec3 sourceLocation = damageSource.getSourcePosition();

                    if (sourceLocation != null) {
                        Vec3 viewVector = skillContainer.getExecutor().getOriginal().getViewVector(1.0F);
                        viewVector = viewVector.subtract(0, viewVector.y, 0).normalize();
                        Vec3 toSourceLocation = sourceLocation.subtract(skillContainer.getExecutor().getOriginal().position()).normalize();

                        if (toSourceLocation.dot(viewVector) > 0.0D) {
                            isFront = true;
                        }
                    }

                    if (isFront) {
                        float impact = 0.5F;
                        float knockback = 0.25F;

                        if (event.getDamageSource() instanceof EpicFightDamageSource epicfightDamageSource) {
                            if (epicfightDamageSource.is(EpicFightDamageTypeTags.GUARD_PUNCTURE)) {
                                return;
                            }

                            impact = epicfightDamageSource.calculateImpact();
                            knockback += Math.min(impact * 0.1F, 1.0F);
                        }

                        this.guard(skillContainer, itemCapability, skillContainer.getServerExecutor(), event, knockback, impact, false);
                    }
                }
            },
            this,
            1
        );

        eventListener.registerEvent(
            EpicFightEventHooks.Entity.DELIVER_DAMAGE_POST,
            event -> {
                // clears penalty multiplier on hit
                skillContainer.getDataManager().setDataSync(EpicFightSkillDataKeys.PENALTY, 0.0F);
            },
            this
        );
    }

	public void guard(SkillContainer container, CapabilityItem itemCapability, ServerPlayerPatch playerPatch, TakeDamageEvent.Income event, float knockback, float impact, boolean advanced) {
		DamageSource damageSource = event.getDamageSource();
		Entity offender = getOffender(damageSource);
		
		if (offender != null && this.isBlockableSource(damageSource, advanced)) {
			playerPatch.playSound(EpicFightSounds.CLASH.get(), -0.05F, 0.1F);
			ServerPlayer serverPlayer = playerPatch.getOriginal();
			EpicFightParticles.HIT_BLUNT.get().spawnParticleWithArgument(serverPlayer.level(), HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, serverPlayer, offender);
			
			if (offender instanceof LivingEntity livingEntity) {
				float modifiedKnockback = EnchantmentHelper.modifyKnockback((ServerLevel)livingEntity.level(), livingEntity.getItemInHand(livingEntity.getUsedItemHand()), livingEntity, damageSource, knockback);
				knockback = (modifiedKnockback - knockback) * 0.1F;
			}
			
			float penalty = container.getDataManager().getDataValue(EpicFightSkillDataKeys.PENALTY) + this.getPenalizer(itemCapability);
			float consumeAmount = penalty * impact;
			boolean canAfford = playerPatch.consumeForSkill(this, Skill.Resource.STAMINA, consumeAmount);
			
			playerPatch.knockBackEntity(offender.position(), knockback);
			container.getDataManager().setDataSync(EpicFightSkillDataKeys.PENALTY, penalty);
			container.getDataManager().setDataSync(EpicFightSkillDataKeys.PENALTY_RESTORE_COUNTER, container.getServerExecutor().getOriginal().tickCount);
			
			BlockType blockType = canAfford ? BlockType.GUARD : BlockType.GUARD_BREAK;
			AnimationAccessor<? extends StaticAnimation> animation = this.getGuardMotion(container, playerPatch, itemCapability, blockType);
			
			if (animation != null) {
				playerPatch.playAnimationSynchronized(animation, 0.0F);
			}
			
			if (blockType == BlockType.GUARD_BREAK) {
				playerPatch.playSound(EpicFightSounds.NEUTRALIZE_MOBS.get(), 3.0F, 0.0F, 0.1F);
			}
			
			this.dealEvent(playerPatch, event, advanced);
		}
	}

	public void dealEvent(PlayerPatch<?> playerPatch, TakeDamageEvent.Income event, boolean advanced) {
		event.cancel();
		event.setResult(AttackResult.ResultType.BLOCKED);
		playerPatch.countHurtTime(event.getDamage());
		
		EpicFightCapabilities.getUnparameterizedEntityPatch(event.getDamageSource().getEntity(), LivingEntityPatch.class)
			.ifPresent(attackerpatch -> attackerpatch.setLastAttackEntity(playerPatch.getOriginal()));
		
		EpicFightCapabilities.<LivingEntity, LivingEntityPatch<LivingEntity>>getParameterizedEntityPatch(event.getDamageSource().getDirectEntity(), LivingEntity.class, LivingEntityPatch.class)
			.ifPresent(entitypatch -> entitypatch.onAttackBlocked(event.getDamageSource(), playerPatch));
	}
	
	@Override
	public void cancelOnServer(SkillContainer skillContainer, CompoundTag arguments) {
		skillContainer.deactivate();
		skillContainer.getExecutor().resetHolding();

        EpicFightEventHooks.Player.CANCEL_SKILL.postWithListener(new SkillCancelEvent(skillContainer.getServerExecutor(), skillContainer), skillContainer.getExecutor().getEventListener());
		EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(SPSetSkillContainerValue.activate(skillContainer.getSlot(), false, skillContainer.getExecutor().getOriginal().getId()), skillContainer.getExecutor().getOriginal());
	}

    @Override @ClientOnly
	public void cancelOnClient(SkillContainer container, CompoundTag arguments) {
		super.cancelOnClient(container, arguments);
		container.deactivate();
	}
	
	@Override
	public void startHolding(SkillContainer container) {
		container.activate();
		container.runOnServer(serverplayerpatch -> EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(SPSetSkillContainerValue.activate(container.getSlot(), true, serverplayerpatch.getOriginal().getId()), serverplayerpatch.getOriginal()));
	}
	
	@Override
	public void holdTick(SkillContainer container) {
		container.runOnServer(serverplayerpatch -> {
			if (container.isActivated()) {
				container.getDataManager().setDataSync(EpicFightSkillDataKeys.PENALTY_RESTORE_COUNTER, container.getServerExecutor().getOriginal().tickCount);
			}
		});
	}
	
	@Override
	public void resetHolding(SkillContainer container) {
		container.deactivate();
	}

	public void onStopHolding(SkillContainer container, SPSkillFeedback feedback) {
		container.deactivate();
	}
	
    @Override @ClientOnly
	public KeyMapping getKeyMapping() {
		return EpicFightKeyMappings.GUARD;
	}
	
    @Override
    public boolean canExecute(SkillContainer container) {
		return this.checkExecuteCondition(container) && this.isHoldingWeaponAvailable(container.getExecutor(), container.getExecutor().getPrimaryItemCapability(), BlockType.GUARD);
	}
    
	protected float getPenalizer(CapabilityItem itemCapability) {
		return this.penalizer;
	}

    protected Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?>> getGuardMotionMap(BlockType blockType) {
        return switch (blockType) {
            case GUARD_BREAK -> this.guardBreakMotions;
            case GUARD -> this.guardMotions;
            case ADVANCED_GUARD -> this.advancedGuardMotions;
        };
    }

	public boolean isHoldingWeaponAvailable(PlayerPatch<?> playerpatch, CapabilityItem itemCapability, BlockType blockType) {
		AnimationAccessor<? extends StaticAnimation> anim = itemCapability.getGuardMotion(this, blockType, playerpatch);
		
		if (anim != null) {
			return true;
		}
		
		Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?>> guardMotions = this.getGuardMotionMap(blockType);
		
		if (!guardMotions.containsKey(itemCapability.getWeaponCategory())) {
			return false;
		}

        Object motion = guardMotions.get(itemCapability.getWeaponCategory()).apply(itemCapability, playerpatch);

		return motion != null;
	}
	
	/**
	 * Not safe from null pointer exception
	 * Must call isAvailableState first to check if it's safe
     *
	 * @return AnimationAccessor
	 */
    @SuppressWarnings("unchecked")
    @Nullable
	protected AnimationAccessor<? extends StaticAnimation> getGuardMotion(SkillContainer container, PlayerPatch<?> playerpatch, CapabilityItem itemCapability, BlockType blockType) {
		AnimationAccessor<? extends StaticAnimation> animation = itemCapability.getGuardMotion(this, blockType, playerpatch);

		if (animation != null) {
			return animation;
		}

        //TODO: Improve safety on the guard motions for later and deferring it to another PR.
        return (AnimationAccessor<? extends StaticAnimation>)this.getGuardMotionMap(blockType).getOrDefault(itemCapability.getWeaponCategory(), (a, b) -> null).apply(itemCapability, playerpatch);
    }
	
	@Override
	public void updateContainer(SkillContainer container) {
		super.updateContainer(container);
		
		container.runOnServer(serverplayerpatch -> {
			if (!container.getExecutor().isHoldingSkill(this)) {
				float penalty = container.getDataManager().getDataValue(EpicFightSkillDataKeys.PENALTY);
				
				if (penalty > 0.0F) {
					int hitTick = container.getDataManager().getDataValue(EpicFightSkillDataKeys.PENALTY_RESTORE_COUNTER);
					
					if (container.getExecutor().getOriginal().tickCount - hitTick > 40) {
						container.getDataManager().setDataSync(EpicFightSkillDataKeys.PENALTY, 0.0F);
					}
				}
			} else {
				container.getExecutor().resetActionTick();
			}
		});
	}
	
	@Override
	public boolean isExecutableState(PlayerPatch<?> executor) {
		return executor.isEpicFightMode() && !(executor.isInAir() || executor.getEntityState().hurt()) && executor.getEntityState().canUseSkill() && !executor.isHoldingAny();
	}
	
	protected boolean isBlockableSource(DamageSource damageSource, boolean advanced) {
		return !damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
				&& !damageSource.is(EpicFightDamageTypeTags.UNBLOCKALBE)
				&& !damageSource.is(DamageTypeTags.BYPASSES_ARMOR)
				&& !damageSource.is(DamageTypeTags.IS_PROJECTILE)
				&& !damageSource.is(DamageTypeTags.IS_EXPLOSION)
				&& !damageSource.is(DamageTypes.MAGIC)
				&& !damageSource.is(DamageTypeTags.IS_FIRE);
	}
	
	@Override
	public boolean shouldDraw(SkillContainer container) {
		return container.getDataManager().getDataValue(EpicFightSkillDataKeys.PENALTY) > 0.0F;
	}
	
	@Override @ClientOnly
	public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphicsExtractor guiGraphics, float x, float y, float partialTick) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, EpicFightSkills.GUARD.get().getSkillTexture(), (int)x, (int)y, (float)(0), (float)(0), 24, 24, 1, 1, 1, 1);
		guiGraphics.text(gui.getFont(), String.format("x%.1f", container.getDataManager().getDataValue(EpicFightSkillDataKeys.PENALTY)), (int)((int)(x)), (int)((int)(y + 6)), 16777215, true);
	}
	
	
	public static Entity getOffender(DamageSource damageSource) {
		return damageSource.getDirectEntity() == null ? damageSource.getEntity() : damageSource.getDirectEntity();
	}
	
	@Override
	public Set<WeaponCategory> getAvailableWeaponCategories() {
		return this.guardMotions.keySet();
	}
	
	@Override @ClientOnly
	public boolean getCustomConsumptionTooltips(SkillBookScreen.AttributeIconList consumptionList) {
		consumptionList.add(Component.translatable("attribute.name.epicfight.stamina.consume.tooltip"), Component.translatable("skill.epicfight.guard.consume.tooltip"), SkillBookScreen.STAMINA_TEXTURE_INFO);
		return true;
	}
	
	protected boolean isAdvancedGuard() {
		return false;
	}
	
	public enum BlockType {
		GUARD_BREAK, GUARD, ADVANCED_GUARD
	}
}