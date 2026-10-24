package dev.war.openfight.platform.fabric.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.entity.monster.Enderman;
import dev.war.openfight.api.animation.AnimationManager;
import dev.war.openfight.api.data.reloader.ItemCapabilityReloadListener;
import dev.war.openfight.api.data.reloader.MobPatchReloadListener;
import dev.war.openfight.api.data.reloader.SkillReloadListener;
import dev.war.openfight.api.event.impl.VanillaEntityEventHooks;
import dev.war.openfight.api.event.impl.VanillaPlayerEventHooks;
import dev.war.openfight.network.EpicFightNetworkManager;
import dev.war.openfight.network.server.SPDatapackSync;
import dev.war.openfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import dev.war.openfight.world.capabilities.item.ItemKeywordReloadListener;
import dev.war.openfight.world.capabilities.item.WeaponTypeReloadListener;
import dev.war.openfight.world.capabilities.skill.PlayerSkills;
import dev.war.openfight.world.gamerule.EpicFightGameRules;
import dev.war.openfight.world.capabilities.EpicFightCapabilities;
import dev.war.openfight.world.capabilities.entitypatch.EntityPatch;

/** Native Fabric equivalents for common entity/player lifecycle callbacks. */
public final class FabricGameplayEvents {
    private FabricGameplayEvents() {}
    public static void initialize() {
        ServerEntityEvents.ALLOW_LOAD.register((entity,level,reason,loaded)-> {
            VanillaEntityEventHooks.onJoinLevel(entity,level,loaded);
            return !(entity instanceof Enderman enderMan) || !VanillaEntityEventHooks.onEnderManSapwns(enderMan);
        });
        ServerEntityEvents.ENTITY_LOAD.register((entity,level)->
            EpicFightCapabilities.getUnparameterizedEntityPatch(entity,EntityPatch.class).ifPresent(EntityPatch::onAddedToLevel));
        ServerEntityEvents.EQUIPMENT_CHANGE.register((entity,slot,previous,current)->VanillaEntityEventHooks.onEquipmentChanged(entity,previous,current,slot));
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity,source,amount)->!VanillaEntityEventHooks.onDamageIncomes(entity,source,amount));
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity,source,base,taken,blocked)->VanillaEntityEventHooks.onCalculateDamagePost(entity,source,taken));
        ServerLivingEntityEvents.AFTER_DEATH.register(VanillaEntityEventHooks::onLivingDeath);
        ServerPlayerEvents.COPY_FROM.register((oldPlayer,newPlayer,alive)->VanillaPlayerEventHooks.onCloned(oldPlayer,newPlayer,!alive));
        EntityTrackingEvents.START_TRACKING.register(VanillaPlayerEventHooks::onStartTracking);
        EntityTrackingEvents.STOP_TRACKING.register(VanillaPlayerEventHooks::onStopTracking);
        ServerPlayConnectionEvents.JOIN.register((handler,sender,server) -> {
            VanillaPlayerEventHooks.onLoad(handler.player,
                server.getWorldPath(LevelResource.PLAYER_DATA_DIR).toFile(), handler.player.getUUID().toString());
            var patch = EpicFightCapabilities.getEntityPatch(handler.player, ServerPlayerPatch.class);
            if (patch != null) patch.onPlayerJoined();
        });
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player,joined) -> syncDataPackContents(player));
    }

    /** Mirrors NeoForgeWorldEvent.sendLevelData and the per-player gamerule sync on login/reload. */
    private static void syncDataPackContents(ServerPlayer player) {
        var server = player.level().getServer();
        var rules = EpicFightNetworkManager.PayloadBundleBuilder.create();
        EpicFightGameRules.GAME_RULES.values().stream().filter(rule -> rule.shouldSync())
            .forEach(rule -> rules.and(rule.getSyncPacket(player)));
        rules.send((first, others) -> EpicFightNetworkManager.sendToPlayer(first, player, others));

        if (server.isSingleplayerOwner(player.nameAndId())) {
            EpicFightCapabilities.getUnparameterizedEntityPatch(player, ServerPlayerPatch.class).ifPresent(patch -> {
                PlayerSkills skills = patch.getPlayerSkills();
                skills.listSkillContainers().forEach(container -> {
                    if (container.getSkill() != null) container.setSkill(container.getSkill(), true);
                });
            });
            return;
        }

        EpicFightCapabilities.getUnparameterizedEntityPatch(player, ServerPlayerPatch.class).ifPresent(patch -> {
            PlayerSkills skills = patch.getPlayerSkills();
            skills.listSkillContainers().forEach(container -> {
                if (container.getSkill() != null) container.setSkill(container.getSkill(), true);
            });
            SPDatapackSync skillParams = new SPDatapackSync(SPDatapackSync.PacketType.SKILL_PARAMS);
            SkillReloadListener.getSkillParams().forEach(skillParams::addTag);
            EpicFightNetworkManager.sendToPlayer(skillParams, player);
        });

        SPDatapackSync animations = new SPDatapackSync(server.isResourcePackRequired()
            ? SPDatapackSync.PacketType.MANDATORY_RESOURCE_PACK_ANIMATION
            : SPDatapackSync.PacketType.RESOURCE_PACK_ANIMATION);
        SPDatapackSync armor = new SPDatapackSync(SPDatapackSync.PacketType.ARMOR);
        SPDatapackSync weapons = new SPDatapackSync(SPDatapackSync.PacketType.WEAPON);
        SPDatapackSync mobs = new SPDatapackSync(SPDatapackSync.PacketType.MOB);
        SPDatapackSync presets = new SPDatapackSync(SPDatapackSync.PacketType.ITEM_PRESET);
        SPDatapackSync conditionals = new SPDatapackSync(SPDatapackSync.PacketType.PROVIDER_CONDITIONAL);
        SPDatapackSync movesets = new SPDatapackSync(SPDatapackSync.PacketType.MOVESET);
        SPDatapackSync modifiers = new SPDatapackSync(SPDatapackSync.PacketType.MODIFIER);
        SPDatapackSync weaponTypes = new SPDatapackSync(SPDatapackSync.PacketType.WEAPON_TYPE);
        SPDatapackSync keywords = new SPDatapackSync(SPDatapackSync.PacketType.ITEM_KEYWORD);

        AnimationManager.getInstance().getResourcepackAnimationStream().forEach(animations::addTag);
        ItemCapabilityReloadListener.getArmorDataStream().forEach(armor::addTag);
        ItemCapabilityReloadListener.getWeaponDataStream().forEach(weapons::addTag);
        MobPatchReloadListener.getDataStream().forEach(mobs::addTag);
        WeaponTypeReloadListener.getWeaponTypeDataStream().forEach(weaponTypes::addTag);
        ItemKeywordReloadListener.getCompounds().forEach(keywords::addTag);

        EpicFightNetworkManager.PayloadBundleBuilder.beginWith(animations)
            .and(presets).and(modifiers).and(conditionals).and(movesets).and(weaponTypes)
            .and(armor).and(weapons).and(mobs).and(keywords)
            .send((first, others) -> EpicFightNetworkManager.sendToPlayer(first, player, others));
    }
}
