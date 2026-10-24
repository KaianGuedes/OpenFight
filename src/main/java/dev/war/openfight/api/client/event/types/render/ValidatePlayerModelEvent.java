package dev.war.openfight.api.client.event.types.render;

import dev.war.openfight.api.event.Event;
import dev.war.openfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;

public class ValidatePlayerModelEvent extends Event {
    private final AbstractClientPlayerPatch<?> playerPatch;
    private final boolean shouldRenderOriginal;
    private boolean shouldRender;

    public ValidatePlayerModelEvent(AbstractClientPlayerPatch<?> playerPatch, boolean shouldRenderOriginal) {
        this.playerPatch = playerPatch;
        this.shouldRenderOriginal = shouldRenderOriginal;
        this.shouldRender = shouldRenderOriginal;
    }

    public boolean getShouldRenderOriginal() {
        return this.shouldRenderOriginal;
    }

    public boolean getShouldRender() {
        return this.shouldRender;
    }

    public void setShouldRender(boolean shouldRender) {
        this.shouldRender = shouldRender;
    }

    public AbstractClientPlayerPatch<?> getPlayerPatch() {
        return this.playerPatch;
    }
}