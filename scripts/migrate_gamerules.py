from pathlib import Path
root=Path(__file__).resolve().parents[1]
src=root/'src/main/java/dev/war/openfight'
p=src/'world/gamerule/EpicFightGameRules.java'
s=p.read_text(encoding='utf-8')
s=s[:s.index('\tpublic static <Type, Config extends')]
s=s.replace('GameRules.BooleanValue','Boolean').replace('GameRules.IntegerValue','Integer').replace('GameRules.Category','GameRuleCategory')
s=s.replace('import net.minecraft.world.level.gamerules.GameRules;', '''import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleEvents;''')
s+='''    public static <T,C extends ModConfigSpec.ConfigValue<T>,V> ConfigurableGameRule<T,C,V> create(
        String name,GameRuleCategory category,Function<ModConfigSpec.Builder,C> definition,RuleType<T,V> type,boolean sync) {
        return new ConfigurableGameRule<>(name,category,definition,type,sync);
    }
    public static class ConfigurableGameRule<T,C extends ModConfigSpec.ConfigValue<T>,V> {
        private final String ruleName;
        private final GameRuleCategory category;
        private final Function<ModConfigSpec.Builder,C> definition;
        private final RuleType<T,V> ruleType;
        private final boolean synchronize;
        private final Map<Level,T> clientValues=new java.util.WeakHashMap<>();
        private C config;
        private GameRule<T> key;
        private ConfigurableGameRule(String name,GameRuleCategory category,Function<ModConfigSpec.Builder,C> definition,RuleType<T,V> type,boolean sync) {
            this.ruleName=name; this.category=category; this.definition=definition; this.ruleType=type; this.synchronize=sync;
        }
        @SuppressWarnings("unchecked") public void registerGameRule() {
            String path=ruleName.replaceAll("([a-z0-9])([A-Z])","$1_$2").toLowerCase(java.util.Locale.ROOT);
            key=ruleType.factory().apply(config.get()).category(category).buildAndRegister(Identifier.fromNamespaceAndPath("epicfight",path));
            if (synchronize) GameRuleEvents.changeCallback(key).register((value,server)-> {
                if (server!=null) {
                    var payload=new SPChangeGamerule(new KeyValuePair((ConfigurableGameRule<Object,?,?>)(Object)this,value));
                    server.getPlayerList().getPlayers().forEach(player->EpicFightNetworkManager.sendToPlayer(payload,player));
                }
            });
        }
        public boolean shouldSync() { return synchronize; }
        @SuppressWarnings("unchecked") public SPChangeGamerule getSyncPacket(ServerPlayer player) {
            return new SPChangeGamerule(new KeyValuePair((ConfigurableGameRule<Object,?,?>)(Object)this,getRuleValue(player.level())));
        }
        public void defineConfig(ModConfigSpec.Builder builder) { config=definition.apply(builder); }
        public String getRuleName() { return ruleName; }
        public RuleType<T,V> getRuleType() { return ruleType; }
        public GameRule<T> getRuleKey() { return key; }
        public C getConfigHolder() { return config; }
        public T getRuleValue(Level level) {
            if (level instanceof ServerLevel serverLevel) return serverLevel.getGameRules().get(key);
            return clientValues.getOrDefault(level,config.get());
        }
        public void setRuleValue(Level level,T value) {
            if (level instanceof ServerLevel serverLevel) serverLevel.getGameRules().set(key,value,serverLevel.getServer());
            else clientValues.put(level,value);
        }
    }
    public record RuleType<T,V>(Function<T,GameRuleBuilder<T>> factory,StreamCodec<ByteBuf,T> codec) {
        private static final RuleType<Boolean,Boolean> BOOLEAN=new RuleType<>(GameRuleBuilder::forBoolean,ByteBufCodecs.BOOL);
        private static final RuleType<Integer,Integer> INTEGER=new RuleType<>(GameRuleBuilder::forInteger,ByteBufCodecs.INT);
    }
    public record KeyValuePair(ConfigurableGameRule<Object,?,?> gamerule,Object value) {}
}
'''
p.write_text(s,encoding='utf-8')
p=src/'network/EpicFightClientBoundPayloadHandler.java'
s=p.read_text(encoding='utf-8')
start=s.index('\t\tGameRules.Value<?> ruleValue =')
end=s.index('\n\t}',start)
s=s[:start]+'        data.keyValuePair().gamerule().setRuleValue(context.player().level(),data.keyValuePair().value());'+s[end:]
p.write_text(s,encoding='utf-8')
print('Ported typed game rules, server change callbacks and client synchronization values')
