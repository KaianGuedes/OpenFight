package yesman.epicfight.platform.fabric.network;

import java.util.function.BiConsumer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record DirectionalPayloadHandler<T extends CustomPacketPayload>(
    BiConsumer<T, IPayloadContext> client, BiConsumer<T, IPayloadContext> server
) {}
