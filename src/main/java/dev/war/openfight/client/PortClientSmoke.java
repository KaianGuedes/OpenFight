package dev.war.openfight.client;

import java.util.IdentityHashMap;
import java.util.Set;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.MixinEnvironment;

import dev.war.openfight.main.EpicFightMod;
import dev.war.openfight.world.capabilities.EpicFightCapabilities;
import dev.war.openfight.world.capabilities.item.CapabilityItem;
import dev.war.openfight.world.capabilities.item.WeaponCapability;
import dev.war.openfight.world.capabilities.item.ArmorCapability;
import dev.war.openfight.world.capabilities.item.WeaponTypeReloadListener;
import dev.war.openfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;
import dev.war.openfight.skill.SkillSlots;
import dev.war.openfight.registry.entries.EpicFightSkillDataKeys;

/** Opt-in development smoke: create a world through the UI, then run 600 ticks of combat. */
public final class PortClientSmoke {
    private static final Set<Screen> ACTIONED_SCREENS = java.util.Collections.newSetFromMap(new IdentityHashMap<>());
    private static int ticks;
    private static boolean attackQueued;
    private static Minecraft smokeClient;
    private static java.util.UUID combatPlayerId;
    private static java.util.UUID combatTargetId;
    private static int combatTicks;
    private static int attackCount;
    private static int weaponSwitchCount;
    private static int modeToggleCount;

    private PortClientSmoke() {}

    public static void register() {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment() || !Boolean.getBoolean("openfight.portSmoke")) return;

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            MixinEnvironment.getCurrentEnvironment().audit();
            EpicFightMod.LOGGER.info("PORT_SMOKE_MIXIN_AUDIT_COMPLETE");
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (++ticks > 2400) {
                fail(client, "Timed out waiting for world creation/combat smoke");
                return;
            }

            Screen screen = client.gui.screen();
            if (screen instanceof TitleScreen) {
                clickButton(screen, "singleplayer");
            } else if (screen instanceof SelectWorldScreen) {
                clickButton(screen, "create");
            } else if (screen instanceof CreateWorldScreen) {
                ((CreateWorldScreen) screen).getUiState().setName("ef-port-smoke-" + System.currentTimeMillis());
                clickButton(screen, "create");
            }

            if (!attackQueued && client.player != null && client.level != null) {
                MinecraftServer server = client.getSingleplayerServer();
                if (server != null) {
                    attackQueued = true;
                    java.util.UUID playerId = client.player.getUUID();
                    EpicFightMod.LOGGER.info("PORT_SMOKE_PLAYER_JOINED name={} tick={}", client.player.getGameProfile().name(), server.getTickCount());
                    server.execute(() -> beginCombatSmoke(client, server, playerId));
                }
            }
        });
        ServerTickEvents.END_SERVER_TICK.register(PortClientSmoke::tickCombatSmoke);
    }

    private static void clickButton(Screen screen, String keyFragment) {
        if (ACTIONED_SCREENS.contains(screen)) return;
        for (var child : screen.children()) {
            if (child instanceof Button button && button.getMessage().getString().toLowerCase().contains(keyFragment)) {
                ACTIONED_SCREENS.add(screen);
                button.onPress(null);
                EpicFightMod.LOGGER.info("PORT_SMOKE_CLICK screen={} button={}", screen.getClass().getSimpleName(), button.getMessage().getString());
                return;
            }
        }
    }

    private static void beginCombatSmoke(Minecraft client, MinecraftServer server, java.util.UUID playerId) {
        smokeClient = client;
        for (String type : new String[]{"axe", "sword", "greatsword", "longsword", "uchigatana", "dagger", "trident", "shield", "pickaxe", "shovel", "hoe", "bokken", "spear", "tachi", "fist", "bow", "crossbow"}) {
            if (WeaponTypeReloadListener.get(Identifier.fromNamespaceAndPath("epicfight", type)) == null) {
                fail(client, "Built-in weapon type missing: epicfight:" + type);
                return;
            }
        }

        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        if (player == null) {
            fail(client, "Integrated server player was not found");
            return;
        }
        var playerPatch = EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);
        if (player.connection == null || playerPatch == null ||
            !playerPatch.getSkill(SkillSlots.COMBO_ATTACKS).getDataManager().hasData(EpicFightSkillDataKeys.COMBO_COUNTER)) {
            fail(client, "Join bootstrap or combo skill data was not initialized");
            return;
        }
        EpicFightMod.LOGGER.info("PORT_SMOKE_SKILL_DATA_REGISTERED key=epicfight:combo_counter connection=ready");

        ItemStack sword = new ItemStack(Items.IRON_SWORD);
        player.getInventory().setItem(0, sword);
        player.getInventory().setSelectedSlot(0);
        var capability = EpicFightCapabilities.getItemStackCapability(sword);
        if (!(capability instanceof WeaponCapability)) {
            fail(client, "Iron sword capability missing: " + capability.getClass().getName());
            return;
        }
        CapabilityItem armorCapability = EpicFightCapabilities.getItemStackCapability(new ItemStack(Items.IRON_CHESTPLATE));
        if (!(armorCapability instanceof ArmorCapability)) {
            fail(client, "Iron chestplate capability missing: " + armorCapability.getClass().getName());
            return;
        }

        ServerLevel level = player.level();
        Zombie zombie = EntityTypes.ZOMBIE.spawn(level, BlockPos.containing(player.getX() + 2.0, player.getY(), player.getZ()), EntitySpawnReason.COMMAND);
        if (zombie == null) {
            fail(client, "Could not spawn test zombie");
            return;
        }
        zombie.setNoAi(true);
        combatPlayerId = playerId;
        combatTargetId = zombie.getUUID();
        combatTicks = 0;
        attackCount = 0;
        weaponSwitchCount = 0;
        modeToggleCount = 0;
        EpicFightMod.LOGGER.info("PORT_SMOKE_COMBAT_STARTED player={} ticks=600", player.getGameProfile().name());
    }

    private static void tickCombatSmoke(MinecraftServer server) {
        if (combatPlayerId == null || combatTicks >= 600) return;
        ServerPlayer player = server.getPlayerList().getPlayer(combatPlayerId);
        if (player == null) {
            EpicFightMod.LOGGER.error("PORT_SMOKE_FAILED player left before 600 combat ticks");
            smokeClient.execute(smokeClient::stop);
            combatPlayerId = null;
            return;
        }
        combatTicks++;

        if (combatTicks % 100 == 1) {
            ItemStack weapon = switch ((combatTicks / 100) % 4) {
                case 0 -> new ItemStack(Items.IRON_SWORD);
                case 1 -> new ItemStack(Items.IRON_AXE);
                case 2 -> new ItemStack(Items.TRIDENT);
                default -> new ItemStack(Items.DIAMOND_SWORD);
            };
            player.getInventory().setItem(0, weapon);
            player.getInventory().setSelectedSlot(0);
            if (!(EpicFightCapabilities.getItemStackCapability(weapon) instanceof WeaponCapability)) {
                EpicFightMod.LOGGER.error("PORT_SMOKE_FAILED switched weapon has no weapon capability: {}", weapon.getItem());
                smokeClient.execute(smokeClient::stop);
                combatPlayerId = null;
                return;
            }
            weaponSwitchCount++;
        }

        if (combatTicks == 1 || combatTicks == 201 || combatTicks == 401) {
            ServerPlayerPatch patch = EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);
            if (patch != null) {
                PlayerPatch.PlayerMode next = patch.getPlayerMode() == PlayerPatch.PlayerMode.EPICFIGHT
                    ? PlayerPatch.PlayerMode.VANILLA : PlayerPatch.PlayerMode.EPICFIGHT;
                patch.toMode(next, true);
                modeToggleCount++;
            }
        }

        if (combatTicks % 10 == 0) {
            ServerLevel level = player.level();
            Entity entity = level.getEntity(combatTargetId);
            Zombie zombie = entity instanceof Zombie found && found.isAlive() ? found : null;
            if (zombie == null) {
                zombie = EntityTypes.ZOMBIE.spawn(level, BlockPos.containing(player.getX() + 2.0, player.getY(), player.getZ()), EntitySpawnReason.COMMAND);
                if (zombie == null) {
                    EpicFightMod.LOGGER.error("PORT_SMOKE_FAILED could not respawn combat target at tick {}", combatTicks);
                    smokeClient.execute(smokeClient::stop);
                    combatPlayerId = null;
                    return;
                }
                zombie.setNoAi(true);
                combatTargetId = zombie.getUUID();
            }
            zombie.setHealth(zombie.getMaxHealth());
            float before = zombie.getHealth();
            player.attack(zombie);
            if (zombie.getHealth() < before) attackCount++;
        }

        if (combatTicks == 600) {
            if (attackCount < 20 || weaponSwitchCount < 5 || modeToggleCount < 3) {
                EpicFightMod.LOGGER.error("PORT_SMOKE_FAILED combat coverage ticks={} attacks={} weaponSwitches={} modeToggles={}", combatTicks, attackCount, weaponSwitchCount, modeToggleCount);
            } else {
                EpicFightMod.LOGGER.info("PORT_SMOKE_WORLD_COMBAT_COMPLETE ticks={} attacks={} weaponSwitches={} modeToggles={}", combatTicks, attackCount, weaponSwitchCount, modeToggleCount);
            }
            combatPlayerId = null;
            smokeClient.execute(smokeClient::stop);
        }
    }

    private static void fail(Minecraft client, String message) {
        EpicFightMod.LOGGER.error("PORT_SMOKE_FAILED {}", message);
        client.execute(client::stop);
    }
}
