package dev.war.openfight.platform.fabric.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

/** Small JSON configuration store retaining Epic Fight's typed value declarations. */
public final class ModConfigSpec {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Map<String,ConfigValue<?>> values;
    private JsonObject document = new JsonObject();
    private Path path;
    private ModConfigSpec(Map<String,ConfigValue<?>> values) {
        this.values = new LinkedHashMap<>(values);
        this.values.values().forEach(value -> value.owner = this);
    }
    public synchronized void load(Path path) throws IOException {
        this.path = path.toAbsolutePath();
        JsonObject loaded = new JsonObject();
        if (Files.exists(path)) {
            try (var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                JsonElement parsed = GSON.fromJson(reader, JsonElement.class);
                if (parsed == null || !parsed.isJsonObject()) throw new IOException("Configuration must be an object: " + path);
                loaded = parsed.getAsJsonObject();
            } catch (com.google.gson.JsonParseException e) {
                throw new IOException("Malformed configuration: " + path, e);
            }
        }
        document = loaded;
        for (var entry : values.entrySet()) {
            entry.getValue().reset();
            if (document.has(entry.getKey())) entry.getValue().read(document.get(entry.getKey()));
        }
        save();
    }
    public synchronized void save() throws IOException {
        if (path == null) throw new IllegalStateException("Configuration has not been loaded");
        values.forEach((key,value) -> document.add(key,GSON.toJsonTree(value.get())));
        Files.createDirectories(path.getParent());
        Path temp = Files.createTempFile(path.getParent(),path.getFileName().toString(),".tmp");
        try {
            Files.writeString(temp,GSON.toJson(document),StandardCharsets.UTF_8);
            try { Files.move(temp,path,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING); }
            catch (AtomicMoveNotSupportedException e) { Files.move(temp,path,StandardCopyOption.REPLACE_EXISTING); }
        } finally { Files.deleteIfExists(temp); }
    }
    public static class ConfigValue<T> implements Supplier<T> {
        private final T defaultValue;
        private final Predicate<Object> validator;
        private volatile T value;
        private ModConfigSpec owner;
        ConfigValue(T value, Predicate<Object> validator) {
            this.defaultValue=value; this.value=value; this.validator=validator;
            if (!validator.test(value)) throw new IllegalArgumentException("Invalid configuration default: " + value);
        }
        @Override public T get() { return value; }
        public T getDefault() { return defaultValue; }
        private void reset() { value = defaultValue; }
        @SuppressWarnings("unchecked")
        public void set(T value) {
            if (!validator.test(value)) throw new IllegalArgumentException("Invalid configuration value: " + value);
            this.value = value instanceof List<?> list ? (T)List.copyOf(list) : value;
        }
        @SuppressWarnings({"unchecked","rawtypes"}) void read(JsonElement element) {
            try {
                Object candidate;
                if (defaultValue instanceof Enum<?> e) candidate=Enum.valueOf((Class)e.getDeclaringClass(),element.getAsString());
                else if (defaultValue instanceof Integer) candidate=element.getAsBigDecimal().intValueExact();
                else if (defaultValue instanceof Double) candidate=element.getAsDouble();
                else if (defaultValue instanceof Boolean) {
                    if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isBoolean()) return;
                    candidate=element.getAsBoolean();
                } else if (defaultValue instanceof List<?>) {
                    candidate=GSON.fromJson(element,List.class);
                } else candidate=element.getAsString();
                if (validator.test(candidate)) set((T)candidate);
            } catch (RuntimeException e) { value=defaultValue; }
        }
        public void save() {
            if (owner == null) throw new IllegalStateException("Configuration specification has not been built");
            try { owner.save(); } catch (IOException e) { throw new java.io.UncheckedIOException(e); }
        }
    }
    public static final class BooleanValue extends ConfigValue<Boolean> {
        BooleanValue(boolean value) { super(value,v -> v instanceof Boolean); }
    }
    public static final class IntValue extends ConfigValue<Integer> {
        IntValue(int value,int min,int max) { super(value,v -> v instanceof Integer n && n>=min && n<=max); }
    }
    public static final class DoubleValue extends ConfigValue<Double> {
        DoubleValue(double value,double min,double max) { super(value,v -> v instanceof Double n && Double.isFinite(n) && n>=min && n<=max); }
    }
    public static final class EnumValue<E extends Enum<E>> extends ConfigValue<E> {
        EnumValue(E value) { super(value,v -> value.getDeclaringClass().isInstance(v)); }
    }
    public static final class Builder {
        private final Map<String,ConfigValue<?>> values=new LinkedHashMap<>();
        private <T,V extends ConfigValue<T>> V add(String name,V value) {
            if (values.putIfAbsent(name,value)!=null) throw new IllegalArgumentException("Duplicate config key: " + name);
            return value;
        }
        public Builder comment(String... comment) { return this; }
        public BooleanValue define(String name,boolean value) { return add(name,new BooleanValue(value)); }
        public BooleanValue define(String name,Supplier<Boolean> value) { return define(name,value.get().booleanValue()); }
        public <T> ConfigValue<T> define(String name,T value) {
            return add(name,new ConfigValue<>(value,v -> v!=null && value.getClass().isInstance(v)));
        }
        public IntValue defineInRange(String name,int value,int min,int max) { return add(name,new IntValue(value,min,max)); }
        public DoubleValue defineInRange(String name,double value,double min,double max) { return add(name,new DoubleValue(value,min,max)); }
        public <E extends Enum<E>> EnumValue<E> defineEnum(String name,E value) { return add(name,new EnumValue<>(value)); }
        public <T> ConfigValue<List<? extends T>> defineList(String name,List<? extends T> value,Supplier<T> element,Predicate<Object> validator) {
            return add(name,new ConfigValue<>(List.copyOf(value),v -> v instanceof List<?> list && list.stream().allMatch(validator)));
        }
        public <T> ConfigValue<List<? extends T>> defineListAllowEmpty(String name,List<? extends T> value,Supplier<T> element,Predicate<Object> validator) {
            return defineList(name,value,element,validator);
        }
        public ModConfigSpec build() { return new ModConfigSpec(values); }
    }
}
