package dev.war.openfight.api.ex_cap.listeners;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.resources.FileToIdConverter;
import dev.war.openfight.platform.fabric.resource.JsonElementCodec;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import dev.war.openfight.api.ex_cap.managers.ModifierManager;
import dev.war.openfight.network.server.SPDatapackSync;

import java.util.Map;

@ApiStatus.Experimental
public class WeaponModifierReloadListener extends SimpleJsonResourceReloadListener<JsonElement>
{
    public static final String DIRECTORY = "capabilities/weapons/modifiers";

    private static final Gson GSON = (new GsonBuilder()).create();

    public WeaponModifierReloadListener() {
        super(JsonElementCodec.CODEC, FileToIdConverter.json(DIRECTORY));
    }

    @Override
    protected @NotNull Map<Identifier, JsonElement> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        ModifierManager.acceptEvent();
        return super.prepare(resourceManager, profiler);

    }

    @Override
    protected void apply(@NotNull Map<Identifier, JsonElement> resourceLocationJsonElementMap, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        ModifierManager.modify();
    }

    public static void processServerPacket(SPDatapackSync packet) {
        if (packet.packetType() == SPDatapackSync.PacketType.MODIFIER) {
            ModifierManager.acceptEvent();
            ModifierManager.modify();

        }
    }
}
