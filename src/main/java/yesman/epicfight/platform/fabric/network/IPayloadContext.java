package yesman.epicfight.platform.fabric.network;

import net.minecraft.world.entity.player.Player;

/** Fabric invokes play handlers on the appropriate game thread. */
@FunctionalInterface
public interface IPayloadContext {
    Player player();
}
