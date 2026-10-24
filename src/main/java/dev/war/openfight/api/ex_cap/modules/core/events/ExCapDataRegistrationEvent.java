package dev.war.openfight.api.ex_cap.modules.core.events;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.resources.Identifier;
import dev.war.openfight.api.event.Event;
import dev.war.openfight.api.ex_cap.modules.core.data.ExCapData;
import dev.war.openfight.api.ex_cap.modules.core.data.ExCapDataEntry;

import java.util.Map;

@Deprecated(forRemoval = true)
public class ExCapDataRegistrationEvent extends Event
{
    private final Map<Identifier, ExCapData.Builder> dataMap = Maps.newHashMap();

    public Map<Identifier, ExCapData.Builder> getDataMap() {
        return ImmutableMap.copyOf(dataMap);
    }

    @Deprecated
    public void addData(Identifier key, ExCapData.Builder data)
    {
        dataMap.put(key, data);
    }

    @Deprecated
    public void addData(ExCapDataEntry... entries)
    {
        for (ExCapDataEntry entry : entries)
        {
            dataMap.put(entry.id(), entry.data());
        }
    }
}