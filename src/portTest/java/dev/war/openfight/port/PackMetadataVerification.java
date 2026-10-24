package dev.war.openfight.port;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackFormat;

/** Exercises the actual Minecraft 26.3 pack-version codec without starting the game. */
public final class PackMetadataVerification {
    public static void main(String[] args) throws Exception {
        Path root=Path.of(args[0]);
        var pack=JsonParser.parseString(Files.readString(root.resolve("pack.mcmeta"))).getAsJsonObject().get("pack");
        var client=PackFormat.packCodec(PackType.CLIENT_RESOURCES).codec().parse(JsonOps.INSTANCE,pack).getOrThrow();
        var server=PackFormat.packCodec(PackType.SERVER_DATA).codec().parse(JsonOps.INSTANCE,pack).getOrThrow();
        if (!client.isValueInRange(PackFormat.of(97,1)) || !server.isValueInRange(PackFormat.of(121,0))) {
            throw new AssertionError("Root metadata does not support both resource and data versions");
        }
        int json=0;
        try (var files=Files.walk(root)) {
            for (var path:files.filter(path->path.toString().endsWith(".json")).toList()) {
                JsonParser.parseString(Files.readString(path));
                json++;
            }
        }
        System.out.println("PORT_PACK_METADATA_VERIFICATION_PASSED; JSON files parsed="+json);
    }
}
