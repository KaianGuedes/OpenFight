package dev.war.openfight.client.input;

import org.jetbrains.annotations.ApiStatus;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import dev.war.openfight.generated.LangKeys;

@ApiStatus.Internal
public final class EpicFightInputCategories {
    private EpicFightInputCategories() {
    }

    public static final KeyMapping.Category COMBAT = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("epicfight", "combat"));
    public static final KeyMapping.Category GUI = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("epicfight", "gui"));
    public static final KeyMapping.Category SYSTEM = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("epicfight", "system"));
    public static final KeyMapping.Category CAMERA = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("epicfight", "camera"));
}
