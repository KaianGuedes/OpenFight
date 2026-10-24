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
import org.jetbrains.annotations.NotNull;
import dev.war.openfight.api.event.EpicFightEventHooks;
import dev.war.openfight.api.ex_cap.modules.core.events.ExCapDataRegistrationEvent;
import dev.war.openfight.api.ex_cap.managers.DatasetManager;
import dev.war.openfight.network.server.SPDatapackSync;

import java.util.Map;

@Deprecated
public class ExCapDataCreationReloadListener extends SimpleJsonResourceReloadListener<JsonElement>
{

    public static final String DIRECTORY = "capabilities/weapons/ex_cap_data/definitions";

    private static final Gson GSON = (new GsonBuilder()).create();

    public ExCapDataCreationReloadListener()
    {
        super(JsonElementCodec.CODEC, FileToIdConverter.json(DIRECTORY));
    }
    @Override
    protected void apply(@NotNull Map<Identifier, JsonElement> elementMap, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller)
    {
        ExCapDataRegistrationEvent exCapDataRegistrationEvent = new ExCapDataRegistrationEvent();
        EpicFightEventHooks.Registry.EX_CAP_DATA_CREATION.post(exCapDataRegistrationEvent);
        DatasetManager.acceptEvent(exCapDataRegistrationEvent);


    }

    public static void processServerPacket(SPDatapackSync packet) {
        if (packet.packetType() == SPDatapackSync.PacketType.EX_CAP_DATA){
            DatasetManager.acceptEvent(EpicFightEventHooks.Registry.EX_CAP_DATA_CREATION.post(new ExCapDataRegistrationEvent()));
        }
    }
}