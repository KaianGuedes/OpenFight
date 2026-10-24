package dev.war.openfight.api.client.event.impl;

import net.minecraft.client.player.LocalPlayer;
import dev.war.openfight.api.event.EpicFightEventHooks;
import dev.war.openfight.client.events.engine.ControlEngine;

public final class EpicFightClientEventRegistrar {
    public static void registerEvents() {
        EpicFightEventHooks.Animation.START_ACTION.registerEvent(event -> {
            if (event.getEntityPatch().getOriginal() instanceof LocalPlayer) {
                ControlEngine.getInstance().unlockHotkeys();
            }
        });
    }

    private EpicFightClientEventRegistrar() {}
}
