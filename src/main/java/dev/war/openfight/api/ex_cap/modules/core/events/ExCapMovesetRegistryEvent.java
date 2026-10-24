package dev.war.openfight.api.ex_cap.modules.core.events;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.resources.Identifier;
import dev.war.openfight.api.event.Event;
import dev.war.openfight.api.ex_cap.modules.core.data.MoveSetEntry;
import dev.war.openfight.api.ex_cap.data.Moveset;

import java.util.Map;

/**
 * @deprecated Use the static register via {@link dev.war.openfight.registry.deferred.MovesetRegister} this exists primarily for legacy compatibility
 */
@Deprecated(forRemoval = true)
public class ExCapMovesetRegistryEvent extends Event
{
    private final Map<Identifier, Moveset.Builder> movesets;

    public ExCapMovesetRegistryEvent()
    {
        movesets = Maps.newHashMap();
    }

    public Map<Identifier, Moveset.Builder> getMovesets() {
        return ImmutableMap.copyOf(movesets);
    }

    public void addMoveset(Identifier id, Moveset.Builder moveSet) {
        movesets.put(id, moveSet);
    }

    @Deprecated
    public void addMoveSet(MoveSetEntry... entries)
    {
        for (MoveSetEntry entry : entries)
        {
            addMoveset(entry.id(), entry.builder());
        }
    }
}