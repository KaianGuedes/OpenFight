package dev.war.openfight.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import dev.war.openfight.platform.fabric.network.IPayloadContext;
import dev.war.openfight.api.animation.AnimationManager;
import dev.war.openfight.network.client.CPAnimatorControl;
import dev.war.openfight.network.client.CPChangePlayerMode;
import dev.war.openfight.network.client.CPChangeSkill;
import dev.war.openfight.network.client.CPHandleSkillData;
import dev.war.openfight.network.client.CPModifyEntityModelYRot;
import dev.war.openfight.network.client.CPPairingAnimationRegistry;
import dev.war.openfight.network.client.CPSetPlayerTarget;
import dev.war.openfight.network.client.CPSetStamina;
import dev.war.openfight.network.client.CPSkillRequest;
import dev.war.openfight.network.client.CPUpdatePlayerInput;
import dev.war.openfight.network.common.BiDirectionalAnimationVariable;
import dev.war.openfight.network.common.BiDirectionalSyncAnimationPositionPacket;
import dev.war.openfight.network.common.BiDirectionalSyncEmoteSlots;
import dev.war.openfight.network.server.SPAnimatorControl;
import dev.war.openfight.network.server.SPModifyPlayerData;
import dev.war.openfight.network.server.SPSetRemotePlayerSkill;
import dev.war.openfight.network.server.SPSetSkillContainerValue;
import dev.war.openfight.network.server.SPUpdatePlayerInput;
import dev.war.openfight.skill.Skill;
import dev.war.openfight.skill.SkillContainer;
import dev.war.openfight.skill.SkillDataManager;
import dev.war.openfight.world.capabilities.EpicFightCapabilities;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;
import dev.war.openfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import dev.war.openfight.world.gamerule.EpicFightGameRules;

public interface EpicFightServerBoundPayloadHandler {
	static <T> void handleAnimationVariablePacket(final BiDirectionalAnimationVariable data, final IPayloadContext context) {
		EpicFightCapabilities.getUnparameterizedEntityPatch(context.player(), ServerPlayerPatch.class).ifPresent(playerpatch -> {
			data.commonProcess(playerpatch);
			EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(data, playerpatch.getOriginal());
		});
	}
	
	static void handleAnimatorControl(final CPAnimatorControl data, final IPayloadContext context) {
		EpicFightCapabilities.getUnparameterizedEntityPatch(context.player(), ServerPlayerPatch.class).ifPresent(playerpatch -> {
			if (!data.isClientOnly()) {
				data.animationVariables().forEach(animationVariable -> handleAnimationVariablePacket(animationVariable, context));
				data.commonProcess(playerpatch);
			}
			
			SPAnimatorControl payload = new SPAnimatorControl(data.action(), data.animation(), playerpatch.getOriginal().getId(), data.transitionTimeModifier(), data.pause());
			payload.animationVariables().addAll(data.animationVariables());
			
			EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(payload, playerpatch.getOriginal());
			
			if (data.responseToSender()) {
				payload.animationVariables().clear();
				EpicFightNetworkManager.sendToPlayer(payload, playerpatch.getOriginal());
			}
		});
	}
	
	static void handleChangePlayerMode(final CPChangePlayerMode data, final IPayloadContext context) {
		EpicFightCapabilities.getUnparameterizedEntityPatch(context.player(), ServerPlayerPatch.class).ifPresent(playerpatch -> {
			playerpatch.toMode(data.mode(), false);
			EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(SPModifyPlayerData.setPlayerMode(playerpatch.getOriginal().getId(), playerpatch.getPlayerMode()), playerpatch.getOriginal());
		});
	}
	
	static void handleChangeSkill(final CPChangeSkill data, final IPayloadContext context) {
		EpicFightCapabilities.getUnparameterizedEntityPatch(context.player(), ServerPlayerPatch.class).ifPresent(playerpatch -> {
			Skill skill = Skill.skillOrNull(data.skill());
			SkillContainer skillContainer = playerpatch.getSkill(data.skillSlot());
			boolean skillEquipped = (!skillContainer.onReplaceCooldown() || data.skillBookSlotIndex() >= 0) && skillContainer.setSkill(skill);
			
			if (skill != null) {
				if (data.skill().value().getCategory().learnable()) {
					playerpatch.getPlayerSkills().addLearnedSkill(skill);
				}
				
				if (skillEquipped && data.skillBookSlotIndex() >= 0) {
					if (!playerpatch.getOriginal().isCreative()) playerpatch.getOriginal().getInventory().removeItem(playerpatch.getOriginal().getInventory().getItem(data.skillBookSlotIndex()));
				}
			}
			
			if (skillEquipped) {
				skillContainer.setReplaceCooldown(EpicFightGameRules.SKILL_REPLACE_COOLDOWN.getRuleValue(playerpatch.getOriginal().level()));
				EpicFightNetworkManager.sendToPlayer(SPSetSkillContainerValue.replaceCooldown(skillContainer.getSlot(), skillContainer.getReplaceCooldown(), playerpatch.getOriginal().getId()), playerpatch.getOriginal());
				EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(new SPSetRemotePlayerSkill(data.skillSlot(), playerpatch.getOriginal().getId(), data.skill()), playerpatch.getOriginal());
			}
		});
	}
	
	static void handlePairingAnimationRegistry(final CPPairingAnimationRegistry data, final IPayloadContext context) {
		AnimationManager.getInstance().validateClientAnimationRegistry(data, ((ServerPlayer)context.player()).connection);
	}
	
	static void handleExecuteSkill(final CPSkillRequest data, final IPayloadContext context) {
		EpicFightCapabilities.getUnparameterizedEntityPatch(context.player(), ServerPlayerPatch.class).ifPresent(playerpatch -> {
			SkillContainer skillContainer = playerpatch.getSkill(data.skillSlot());
			
			switch (data.workType()) {
				case CAST -> skillContainer.requestCasting(playerpatch, data.arguments());
				case CANCEL -> skillContainer.requestCancel(playerpatch, data.arguments());
				case HOLD_START -> skillContainer.requestHold(playerpatch, data.arguments());
			}
		});
	}
	
	static void handleModifyPlayerModelYRot(final CPModifyEntityModelYRot data, final IPayloadContext context) {
		EpicFightCapabilities.getUnparameterizedEntityPatch(context.player(), ServerPlayerPatch.class).ifPresent(playerpatch -> {
			if (data.disable()) {
				playerpatch.disableModelYRot(false);
				EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(SPModifyPlayerData.disablePlayerYRot(playerpatch.getOriginal().getId()), playerpatch.getOriginal());
			} else {
				playerpatch.setModelYRot(data.modelYRot(), false);
				EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(SPModifyPlayerData.setPlayerYRot(playerpatch.getOriginal().getId(), data.modelYRot()), playerpatch.getOriginal());
			}
		});
	}
	
	static void handleSkillData(final CPHandleSkillData data, final IPayloadContext context) {
		Player player = context.player();
		
		EpicFightCapabilities.getUnparameterizedEntityPatch(player, PlayerPatch.class).ifPresent(playerpatch -> {
			SkillDataManager dataManager = playerpatch.getSkill(data.skillSlot()).getDataManager();
			Object value = data.skillDataKey().value().decode(data.buffer());
			dataManager.setDataRawtype(data.skillDataKey(), value);
		});
	}
	
	static void handleSetPlayerTarget(final CPSetPlayerTarget data, final IPayloadContext context) {
		EpicFightCapabilities.getUnparameterizedEntityPatch(context.player(), ServerPlayerPatch.class).ifPresent(entitypatch -> {
			Entity entity = entitypatch.getOriginal().level().getEntity(data.targetEntityId());
			
			if (entity instanceof LivingEntity livingEntity) {
				entitypatch.setAttackTarget(livingEntity);
			} else if (entity == null) {
				entitypatch.setAttackTarget(null);
			}
		});
	}
	
	static void handleSetStamina(final CPSetStamina data, final IPayloadContext context) {
		EpicFightCapabilities.getUnparameterizedEntityPatch(context.player(), ServerPlayerPatch.class).ifPresent(playerpatch -> {
			playerpatch.setStamina(data.consumption());
			
			if (data.resetActionTick()) {
				playerpatch.resetActionTick();
			}
		});
	}
	
	static void handleUpdatePlayerInput(final CPUpdatePlayerInput data, final IPayloadContext context) {
		EpicFightCapabilities.getUnparameterizedEntityPatch(context.player(), ServerPlayerPatch.class).ifPresent(playerpatch -> {
			playerpatch.dx = data.strafe();
			playerpatch.dz = data.forward();
			EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(new SPUpdatePlayerInput(data.entityId(), data.forward(), data.strafe()), playerpatch.getOriginal());
		});
	}
	
	static void handleSyncAnimationPosition(final BiDirectionalSyncAnimationPositionPacket data, final IPayloadContext context) {
		Entity entity = context.player().level().getEntity(data.entityId());
		
		if (entity instanceof Player player) {
			EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(new BiDirectionalSyncAnimationPositionPacket(entity.getId(), data.elapsedTime(), data.position(), data.lerpSteps()), player);
		}
	}

    static void handleSyncEmoteSlot(final BiDirectionalSyncEmoteSlots data, final IPayloadContext context) {
        EpicFightCapabilities.getLocalPlayerPatchAsOptional(context.player().level().getEntity(data.playerId())).ifPresent(playerpatch -> {
            playerpatch.getEmoteSlots().deserialize(data.compoundTag(), playerpatch.getOriginal().registryAccess());
        });
    }
}
