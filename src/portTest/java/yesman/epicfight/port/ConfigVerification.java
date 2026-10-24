package yesman.epicfight.port;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import yesman.epicfight.platform.fabric.config.ModConfigSpec;

/** Runs without Minecraft initialization and exercises config persistence/correction contracts. */
public final class ConfigVerification {
    enum Mode { VANILLA, COMBAT }
    public static void main(String[] args) throws Exception {
        Path dir=Path.of(args[0]);
        Files.createDirectories(dir);
        Path file=dir.resolve("config.json");
        Files.writeString(file,"{\"ticks\":99,\"zoom\":\"bad\",\"mode\":\"UNKNOWN\",\"enabled\":\"false\",\"items\":[\"invalid\"],\"future\":42}");
        var builder=new ModConfigSpec.Builder();
        var ticks=builder.defineInRange("ticks",2,1,10);
        var zoom=builder.defineInRange("zoom",0.25,0.0,1.0);
        var mode=builder.defineEnum("mode",Mode.VANILLA);
        var enabled=builder.define("enabled",true);
        var items=builder.<String>defineListAllowEmpty("items",List.of(),null,v->v instanceof String s && s.contains(":"));
        var spec=builder.build();
        spec.load(file);
        require(ticks.get()==2 && zoom.get()==0.25 && mode.get()==Mode.VANILLA && enabled.get() && items.get().isEmpty(),"Invalid input must return to declared defaults");
        var list=new ArrayList<String>(List.of("minecraft:stick"));
        items.set(list);
        list.add("minecraft:stone");
        require(items.get().size()==1,"Config must not retain caller's mutable list");
        ticks.set(7); mode.set(Mode.COMBAT); enabled.set(false); spec.save();
        ticks.set(1); mode.set(Mode.VANILLA);
        spec.load(file);
        require(ticks.get()==7 && mode.get()==Mode.COMBAT && !enabled.get(),"Save/load must retain typed values");
        require(Files.readString(file).contains("\"future\": 42"),"Unknown future keys must be preserved");
        try { ticks.set(11); throw new AssertionError("Range validation missing"); } catch (IllegalArgumentException expected) {}
        try { items.set(List.of("invalid")); throw new AssertionError("List validation missing"); } catch (IllegalArgumentException expected) {}
        try { zoom.set(Double.NaN); throw new AssertionError("Nonfinite validation missing"); } catch (IllegalArgumentException expected) {}
        Files.writeString(file,"{\"ticks\":1.5}");
        spec.load(file);
        require(ticks.get()==2 && mode.get()==Mode.VANILLA && enabled.get(),"Reload must reset missing keys and reject fractional integers");
        Files.writeString(file,"{bad");
        try { spec.load(file); throw new AssertionError("Malformed config accepted"); } catch (java.io.IOException expected) {}
        require(Files.readString(file).equals("{bad"),"Malformed input must not be overwritten");
        try (var files=Files.list(dir)) { require(files.noneMatch(path->path.toString().endsWith(".tmp")),"Atomic save leaked temp files"); }
        System.out.println("PORT_CONFIG_VERIFICATION_PASSED");
    }
    private static void require(boolean condition,String message) { if (!condition) throw new AssertionError(message); }
}
