package dev.war.openfight.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.phys.Vec3;

import dev.war.openfight.client.events.engine.ControlEngine;
import dev.war.openfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import dev.war.openfight.main.EpicFightMod;
import dev.war.openfight.world.capabilities.EpicFightCapabilities;

/**
 * Opt-in (-Dopenfight.smokeRespawn=true, gradle -PsmokeRespawn) death/respawn smoke, run after the combat smoke.
 * Holds the forward key, kills the player, respawns them and asserts that movement input still moves the player and
 * that the cached LocalPlayerPatch / ControlEngine follow the replacement LocalPlayer.
 */
final class PortRespawnSmoke {
    private enum Phase { BASELINE, KILL, WAIT_DEAD, WAIT_RESPAWN, SETTLE, MOVE, DONE }

    private static final double MIN_DISTANCE = 1.0D;
    private static final double MIN_ALIGNMENT = 0.7D;
    private static final int MOVE_TICKS = 40;

    private static boolean registered;
    private static Phase phase = Phase.DONE;
    private static int phaseTicks;
    private static Vec3 phaseStart = Vec3.ZERO;
    private static LocalPlayer deadPlayer;
    private static boolean forward;

    private PortRespawnSmoke() {}

    static boolean enabled() {
        return Boolean.getBoolean("openfight.smokeRespawn");
    }

    static void begin(Minecraft client, MinecraftServer server) {
        if (!registered) {
            registered = true;
            // Hold the key from the start of every tick: KeyMapping.setAll() on screen changes would otherwise release it.
            ClientTickEvents.START_CLIENT_TICK.register(c -> {
                if (c.player != null && c.options != null) c.options.keyUp.setDown(forward);
            });
            ClientTickEvents.END_CLIENT_TICK.register(PortRespawnSmoke::tick);
        }
        server.execute(() -> server.getAllLevels().forEach(level -> {
            java.util.List<Zombie> zombies = new java.util.ArrayList<>();
            level.getAllEntities().forEach(entity -> { if (entity instanceof Zombie zombie) zombies.add(zombie); });
            zombies.forEach(Zombie::discard);
        }));
        EpicFightMod.LOGGER.info("PORT_SMOKE_RESPAWN_STARTED");
        enter(Phase.BASELINE, client);
        forward = true;
    }

    private static void enter(Phase next, Minecraft client) {
        phase = next;
        phaseTicks = 0;
        if (client.player != null) phaseStart = client.player.position();
    }

    private static double horizontalDistance(Minecraft client) {
        Vec3 now = client.player.position();
        return Math.hypot(now.x - phaseStart.x, now.z - phaseStart.z);
    }

    /** Cosine between the horizontal displacement of the current phase and the camera's horizontal forward vector. */
    private static double cameraAlignment(Minecraft client) {
        Vec3 now = client.player.position();
        double dx = now.x - phaseStart.x;
        double dz = now.z - phaseStart.z;
        var forwardVector = client.gameRenderer.mainCamera().forwardVector();
        double fx = forwardVector.x();
        double fz = forwardVector.z();
        double length = Math.hypot(dx, dz) * Math.hypot(fx, fz);
        return length < 1.0E-6D ? 0.0D : (dx * fx + dz * fz) / length;
    }

    /** Camera type, Shoulder Surfing state (reflective, optional mod) and Epic Fight's TPS mode, for the log. */
    private static String cameraState(Minecraft client) {
        String shoulderSurfing = "absent";
        if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("shouldersurfing")) {
            try {
                Object api = Class.forName("com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing").getMethod("getInstance").invoke(null);
                shoulderSurfing = String.valueOf(api.getClass().getMethod("isShoulderSurfing").invoke(api));
            } catch (ReflectiveOperationException exception) {
                shoulderSurfing = "error:" + exception;
            }
        }
        return "cameraType=" + client.options.getCameraType() + " shoulderSurfing=" + shoulderSurfing
            + " epicFightTps=" + dev.war.openfight.api.client.camera.EpicFightCameraAPI.getInstance().isTPSMode();
    }

    private static void fail(Minecraft client, String message) {
        forward = false;
        phase = Phase.DONE;
        EpicFightMod.LOGGER.error("PORT_SMOKE_FAILED respawn: {}", message);
        client.stop();
    }

    private static void tick(Minecraft client) {
        if (phase == Phase.DONE || client.player == null) return;
        phaseTicks++;
        MinecraftServer server = client.getSingleplayerServer();
        if (server == null) return;

        switch (phase) {
            case BASELINE -> {
                if (phaseTicks == 5) phaseStart = client.player.position(); // let any landing state settle
                if (phaseTicks >= 5 + MOVE_TICKS) {
                    double distance = horizontalDistance(client);
                    if (distance < MIN_DISTANCE) {
                        fail(client, "baseline movement before death did not move the player, distance=" + distance);
                        return;
                    }
                    double alignment = cameraAlignment(client);
                    if (alignment < MIN_ALIGNMENT) {
                        fail(client, "baseline forward input did not move along the camera forward vector, cos=" + alignment);
                        return;
                    }
                    EpicFightMod.LOGGER.info("PORT_SMOKE_MOVE_BASELINE distance={} cameraCos={} {}", String.format("%.2f", distance), String.format("%.2f", alignment), cameraState(client));
                    forward = false;
                    enter(Phase.KILL, client);
                }
            }
            case KILL -> {
                deadPlayer = client.player;
                java.util.UUID id = client.player.getUUID();
                server.execute(() -> {
                    ServerPlayer player = server.getPlayerList().getPlayer(id);
                    if (player != null) player.kill(player.level());
                });
                enter(Phase.WAIT_DEAD, client);
            }
            case WAIT_DEAD -> {
                if (client.player.isDeadOrDying()) {
                    EpicFightMod.LOGGER.info("PORT_SMOKE_PLAYER_KILLED");
                    enter(Phase.WAIT_RESPAWN, client);
                } else if (phaseTicks > 200) {
                    fail(client, "player did not die");
                }
            }
            case WAIT_RESPAWN -> {
                if (client.player != deadPlayer && !client.player.isDeadOrDying()) {
                    EpicFightMod.LOGGER.info("PORT_SMOKE_PLAYER_RESPAWNED");
                    LocalPlayerPatch cached = EpicFightCapabilities.getCachedLocalPlayerPatch();
                    LocalPlayerPatch engine = ControlEngine.getInstance().getPlayerPatch();
                    if (cached == null || cached.getOriginal() != client.player) {
                        fail(client, "cached LocalPlayerPatch does not belong to the respawned LocalPlayer");
                        return;
                    }
                    if (engine != cached || engine.getOriginal() != client.player) {
                        fail(client, "ControlEngine still references the previous LocalPlayerPatch");
                        return;
                    }
                    EpicFightMod.LOGGER.info("PORT_SMOKE_RESPAWN_PATCH_REFRESHED");
                    enter(Phase.SETTLE, client);
                } else if (phaseTicks % 20 == 1) {
                    client.player.respawn();
                } else if (phaseTicks > 400) {
                    fail(client, "player did not respawn");
                }
            }
            case SETTLE -> {
                if (phaseTicks >= 40 && client.gui.screen() == null && client.player.onGround()) {
                    forward = true;
                    enter(Phase.MOVE, client);
                } else if (phaseTicks > 600) {
                    fail(client, "respawned player never settled (screen=" + client.gui.screen() + ")");
                }
            }
            case MOVE -> {
                if (phaseTicks >= MOVE_TICKS) {
                    double distance = horizontalDistance(client);
                    forward = false;
                    if (distance < MIN_DISTANCE) {
                        fail(client, "movement input did not move the player after respawn, distance=" + distance);
                        return;
                    }
                    double alignment = cameraAlignment(client);
                    if (alignment < MIN_ALIGNMENT) {
                        fail(client, "forward input after respawn did not move along the camera forward vector, cos=" + alignment);
                        return;
                    }
                    EpicFightMod.LOGGER.info("PORT_SMOKE_RESPAWN_MOVEMENT_OK distance={} cameraCos={} {}", String.format("%.2f", distance), String.format("%.2f", alignment), cameraState(client));
                    phase = Phase.DONE;
                    client.stop();
                }
            }
            default -> { }
        }
    }
}
