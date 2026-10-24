package yesman.epicfight.platform.fabric.resource;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;

/** Retains arbitrary JSON trees for the original Epic Fight reloader parsers. */
public final class JsonElementCodec {
    private JsonElementCodec() {}
    public static final Codec<JsonElement> CODEC=Codec.PASSTHROUGH.xmap(
        value->value.convert(JsonOps.INSTANCE).getValue(),value->new Dynamic<>(JsonOps.INSTANCE,value));
}
