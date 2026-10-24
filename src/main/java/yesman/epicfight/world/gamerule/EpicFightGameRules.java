package yesman.epicfight.world.gamerule;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleEvents;
import net.minecraft.world.level.Level;
import yesman.epicfight.platform.fabric.config.ModConfigSpec;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPChangeGamerule;
import yesman.epicfight.skill.SkillSlots;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class EpicFightGameRules {
	public static final ConfigurableGameRule<Boolean, ModConfigSpec.BooleanValue, Boolean> GLOBAL_STUN = create(
			  "globalStun"
			, GameRuleCategory.MOBS
			, configBuilder -> configBuilder.define("default_gamerule.globalStun", true)
			, RuleType.BOOLEAN
			, false
	);
	
	public static final ConfigurableGameRule<Boolean, ModConfigSpec.BooleanValue, Boolean> KEEP_SKILLS = create(
			  "keepSkills"
			, GameRuleCategory.PLAYER
			, configBuilder -> configBuilder.define("default_gamerule.keepSkills", true)
			, RuleType.BOOLEAN
			, false
	);
	
	public static final ConfigurableGameRule<Boolean, ModConfigSpec.BooleanValue, Boolean> HAS_FALL_ANIMATION = create(
			  "hasFallAnimation"
			, GameRuleCategory.PLAYER
			, configBuilder -> configBuilder.define("default_gamerule.hasFallAnimation", true)
			, RuleType.BOOLEAN
			, true
	);
	
	public static final ConfigurableGameRule<Boolean, ModConfigSpec.BooleanValue, Boolean> DISABLE_ENTITY_UI = create(
			  "disableEntityUI"
			, GameRuleCategory.MISC
			, configBuilder -> configBuilder.define("default_gamerule.disapleEntityUI", false)
			, RuleType.BOOLEAN
			, true
	);
	
	public static final ConfigurableGameRule<Boolean, ModConfigSpec.BooleanValue, Boolean> CAN_SWITCH_PLAYER_MODE = create(
			  "canSwitchPlayerMode"
			, GameRuleCategory.PLAYER
			, configBuilder -> configBuilder.define("default_gamerule.canSwitchPlayerMode", true)
			, RuleType.BOOLEAN
			, true
	);
	
	public static final ConfigurableGameRule<Boolean, ModConfigSpec.BooleanValue, Boolean> STIFF_COMBO_ATTACKS = create(
			  "stiffComboAttacks"
			, GameRuleCategory.PLAYER
			, configBuilder -> configBuilder.define("default_gamerule.stiffComboAttacks", true)
			, RuleType.BOOLEAN
			, true
	);
	
	public static final ConfigurableGameRule<Boolean, ModConfigSpec.BooleanValue, Boolean> NO_MOBS_IN_BOSSFIGHT = create(
			  "noMobsInBossfight"
			, GameRuleCategory.SPAWNING
			, configBuilder -> configBuilder.define("default_gamerule.noMobsInBossfight", true)
			, RuleType.BOOLEAN
			, true
	);
	
	public static final ConfigurableGameRule<Integer, ModConfigSpec.IntValue, Integer> INITIAL_PLAYER_MODE = create(
			  "initialMode"
			, GameRuleCategory.PLAYER
			, configBuilder -> configBuilder.comment("0 = vanilla, 1 = epicfight").defineInRange("default_gamerule.initialMode", 1, 0, 1)
			, RuleType.INTEGER
			, true
	);
	
	public static final ConfigurableGameRule<Integer, ModConfigSpec.IntValue, Integer> WEIGHT_PENALTY = EpicFightGameRules.create(
			  "weightPenalty"
			, GameRuleCategory.PLAYER
			, configBuilder -> configBuilder.defineInRange("default_gamerule.weightPenalty", 100, 0, 100)
			, RuleType.INTEGER
			, true
	);
	
	public static final ConfigurableGameRule<Boolean, ModConfigSpec.BooleanValue, Boolean> EPIC_DROP = EpicFightGameRules.create(
			  "epicDrop"
			, GameRuleCategory.DROPS
			, configBuilder -> configBuilder.define("default_gamerule.epicDrop", false)
			, RuleType.BOOLEAN
			, true
	);
	
	public static final ConfigurableGameRule<Integer, ModConfigSpec.IntValue, Integer> SKILL_REPLACE_COOLDOWN = EpicFightGameRules.create(
			  "skillReplaceCooldown"
			, GameRuleCategory.PLAYER
			, configBuilder -> configBuilder.defineInRange("default_gamerule.skillReplaceCooldown", 6000, 0, Integer.MAX_VALUE)
			, RuleType.INTEGER
			, true
	);

    public static final ConfigurableGameRule<Boolean, ModConfigSpec.BooleanValue, Boolean> ALLOW_VANILLA_MELEE = EpicFightGameRules.create(
        "allowVanillaMelee"
        , GameRuleCategory.PLAYER
        , configBuilder -> configBuilder.define("default_gamerule.allow_vanilla_melee", true)
        , RuleType.BOOLEAN
        , true
    );

    /// Maximum number of passive skills a player may have equipped at once. Default 3 preserves
    /// the legacy hard cap. Upper bound is [SkillSlots#CORE_PASSIVE_SLOT_COUNT] because that's how
    /// many passive `SkillSlots` enum constants this mod actually declares; raising past that has
    /// no effect.
    public static final ConfigurableGameRule<Integer, ModConfigSpec.IntValue, Integer> MAX_PASSIVE_SKILLS = EpicFightGameRules.create(
          "maxPassiveSkills"
        , GameRuleCategory.PLAYER
        , configBuilder -> configBuilder.defineInRange("default_gamerule.maxPassiveSkills", 3, 0, SkillSlots.CORE_PASSIVE_SLOT_COUNT)
        , RuleType.INTEGER
        , true
    );

	public static final Map<String, ConfigurableGameRule<?, ?, ?>> GAME_RULES = ImmutableMap.<String, ConfigurableGameRule<?, ?, ?>>builder()
			.put("globalStun", GLOBAL_STUN)
			.put("keepSkills", KEEP_SKILLS)
			.put("hasFallAnimation", HAS_FALL_ANIMATION)
			.put("disableEntityUI", DISABLE_ENTITY_UI)
			.put("canSwitchPlayerMode", CAN_SWITCH_PLAYER_MODE)
			.put("stiffComboAttacks", STIFF_COMBO_ATTACKS)
			.put("noMobsInBossfight", NO_MOBS_IN_BOSSFIGHT)
			.put("initialMode", INITIAL_PLAYER_MODE)
			.put("weightPenalty", WEIGHT_PENALTY)
			.put("epicDrop", EPIC_DROP)
			.put("skillReplaceCooldown", SKILL_REPLACE_COOLDOWN)
            .put("allowVanillaMelee", ALLOW_VANILLA_MELEE)
            .put("maxPassiveSkills", MAX_PASSIVE_SKILLS)
			.build();
	
	public static void registerGameRules() {
		GAME_RULES.values().forEach(gamerule -> gamerule.registerGameRule());
	}
	
    public static <T,C extends ModConfigSpec.ConfigValue<T>,V> ConfigurableGameRule<T,C,V> create(
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
