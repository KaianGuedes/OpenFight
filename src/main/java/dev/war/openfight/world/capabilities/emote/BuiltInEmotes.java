package dev.war.openfight.world.capabilities.emote;

import net.minecraft.resources.ResourceKey;
import dev.war.openfight.client.cosmetics.Emote;
import dev.war.openfight.main.EpicFightMod;
import dev.war.openfight.registry.EpicFightRegistries;

public interface BuiltInEmotes {
    ResourceKey<Emote> FRUSTRATED = ResourceKey.create(EpicFightRegistries.Keys.EMOTE, EpicFightMod.identifier("frustrated"));
    ResourceKey<Emote> HOPAK = ResourceKey.create(EpicFightRegistries.Keys.EMOTE, EpicFightMod.identifier("hopak"));
    ResourceKey<Emote> LAUGH = ResourceKey.create(EpicFightRegistries.Keys.EMOTE, EpicFightMod.identifier("laugh"));
    ResourceKey<Emote> SALUTE = ResourceKey.create(EpicFightRegistries.Keys.EMOTE, EpicFightMod.identifier("salute"));
    ResourceKey<Emote> SLIT_THROAT = ResourceKey.create(EpicFightRegistries.Keys.EMOTE, EpicFightMod.identifier("slit_throat"));
    ResourceKey<Emote> WAVE_HAND = ResourceKey.create(EpicFightRegistries.Keys.EMOTE, EpicFightMod.identifier("wave_hand"));
}
