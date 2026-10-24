package dev.war.openfight.api.ex_cap.modules.core.events;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.resources.Identifier;
import dev.war.openfight.api.event.Event;
import dev.war.openfight.api.ex_cap.modules.core.data.BuilderEntry;
import dev.war.openfight.api.ex_cap.managers.ExCapManager;
import dev.war.openfight.world.capabilities.item.WeaponCapability;

import java.util.Map;

@Deprecated(forRemoval = true)
public class ExCapBuilderCreationEvent extends Event
{
    private final Map<Identifier, WeaponCapability.Builder> builders = Maps.newHashMap();

    public Map<Identifier, WeaponCapability.Builder> getBuilders() {
        return ImmutableMap.copyOf(builders);
    }

    public void addBuilder(Identifier rl, WeaponCapability.Builder builder) {
        ExCapManager.addAcceptor(rl);
        builders.put(rl, builder);
    }

    @Deprecated
    public void addBuilder(BuilderEntry... entries)
    {
        for (BuilderEntry entry : entries)
        {
            addBuilder(entry.id(), entry.template());
        }
    }
}