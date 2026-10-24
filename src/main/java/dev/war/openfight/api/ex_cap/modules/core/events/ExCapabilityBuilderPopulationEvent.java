package dev.war.openfight.api.ex_cap.modules.core.events;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.resources.Identifier;
import dev.war.openfight.api.event.Event;
import dev.war.openfight.api.ex_cap.modules.core.data.ExCapData;
import dev.war.openfight.api.ex_cap.managers.DatasetManager;

import java.util.Map;
import java.util.List;

@Deprecated(forRemoval = true)
public class ExCapabilityBuilderPopulationEvent extends Event {
    private final Map<Identifier, List<ExCapData.Builder>> builders;

    public ExCapabilityBuilderPopulationEvent() {
        this.builders = Maps.newHashMap();
    }

    public Map<Identifier, List<ExCapData.Builder>> getBuilders() {
        return ImmutableMap.copyOf(builders);
    }

    @Deprecated
    public void registerData(Identifier target, Identifier... dataSet) {
        List<ExCapData.Builder> list = Lists.newArrayList();
        for (Identifier id : dataSet) {
            list.add(DatasetManager.get(id));
        }
        builders.computeIfAbsent(target, k -> Lists.newArrayList()).addAll(list);
    }

    @Deprecated
    public void registerData(Identifier target, List<ExCapData.Builder> dataSet)
    {
        builders.computeIfAbsent(target, k -> Lists.newArrayList()).addAll(dataSet);
    }
}