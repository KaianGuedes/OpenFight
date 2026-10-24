package dev.war.openfight.api.data.reloader;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.resources.FileToIdConverter;
import dev.war.openfight.platform.fabric.resource.JsonElementCodec;
import net.minecraft.util.profiling.ProfilerFiller;
import dev.war.openfight.api.utils.side.ClientOnly;
import dev.war.openfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import dev.war.openfight.main.EpicFightMod;
import dev.war.openfight.network.server.SPDatapackSync;
import dev.war.openfight.registry.EpicFightRegistries;
import dev.war.openfight.registry.entries.EpicFightSkills;
import dev.war.openfight.skill.Skill;
import dev.war.openfight.skill.SkillCategories;
import dev.war.openfight.skill.SkillContainer;
import dev.war.openfight.world.capabilities.EpicFightCapabilities;
import dev.war.openfight.world.capabilities.skill.PlayerSkills;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SkillReloadListener extends SimpleJsonResourceReloadListener<JsonElement> {
	private static final List<CompoundTag> SKILL_PARAMS = Lists.newArrayList();
	private static final Gson GSON = (new GsonBuilder()).create();
	
	public static List<CompoundTag> getSkillParams() {
		return Collections.unmodifiableList(SKILL_PARAMS);
	}
	
	public static Skill getSkill(String name) {
		Identifier rl;
		
		if (name.indexOf(':') >= 0) {
			rl = Identifier.parse(name);
		} else {
            rl = EpicFightMod.identifier(name);
		}
		
		if (EpicFightRegistries.SKILL.containsKey(rl)) {
			return EpicFightRegistries.SKILL.getValue(rl);
		} else {
			return null;
		}
	}
	
	public static Collection<Skill> getSkills(Predicate<Skill> predicate) {
		return EpicFightRegistries.SKILL.listElements().map(Holder::value).filter(skill -> predicate.test(skill)).toList();
	}
	
	public static Stream<Identifier> getSkillNames(Predicate<Skill> predicate) {
		return EpicFightRegistries.SKILL.listElements().map(Holder::value).filter(skill -> predicate.test(skill)).map(skill -> skill.getRegistryName());
	}
	
	public static void reloadAllSkillsAnimations() {
		EpicFightRegistries.SKILL.listElements().map(Holder::value).forEach((skill) -> skill.registerPropertiesToAnimation());
	}
	
    @ClientOnly
	public static void processServerPacket(SPDatapackSync packet) {
		for (CompoundTag tag : packet.tags()) {
			if (!EpicFightRegistries.SKILL.containsKey(Identifier.parse(tag.getStringOr("id", "")))) {
				EpicFightMod.LOGGER.warn("Failed to syncronize Datapack for skill: " + tag.getStringOr("id", ""));
				continue;
			}
			
			EpicFightRegistries.SKILL.getValue(Identifier.parse(tag.getStringOr("id", ""))).loadDatapackParameters(tag);
		}
		
		LocalPlayerPatch localplayerpatch = EpicFightCapabilities.getCachedLocalPlayerPatch();
		
		if (localplayerpatch != null) {
			PlayerSkills skillCapability = localplayerpatch.getPlayerSkills();
			
			for (SkillContainer skill : skillCapability.skillContainers) {
				if (skill.getSkill() != null) {
					// Reload skill
					skill.setSkill(getSkill(skill.getSkill().toString()), true);
				}
			}
			
			skillCapability.skillContainers[SkillCategories.BASIC_ATTACK.universalOrdinal()].setSkill(EpicFightSkills.COMBO_ATTACKS.get());
			skillCapability.skillContainers[SkillCategories.KNOCKDOWN_WAKEUP.universalOrdinal()].setSkill(EpicFightSkills.KNOCKDOWN_WAKEUP.get());
		}
	}
	
	private static Pair<Identifier, CompoundTag> parseParameters(Map.Entry<Identifier, JsonElement> entry) {
		try {
			CompoundTag tag = TagParser.parseCompoundFully(entry.getValue().toString());
			tag.putString("id", entry.getKey().toString());
			SKILL_PARAMS.add(tag);
			
			return Pair.of(entry.getKey(), tag);
		} catch (CommandSyntaxException e) {
			EpicFightMod.LOGGER.warn("Can't parse skill parameter for " + entry.getKey() + " because of " + e.getMessage());
			e.printStackTrace();
			
			return Pair.of(entry.getKey(), new CompoundTag());
		}
	}
	
	private static final SkillReloadListener INSTANCE = new SkillReloadListener();
	
	public static SkillReloadListener getInstance() {
		return INSTANCE;
	}
	
	public SkillReloadListener() {
		super(JsonElementCodec.CODEC, FileToIdConverter.json("skill_parameters"));
	}
	
	@Override
	protected void apply(Map<Identifier, JsonElement> objectIn, ResourceManager resourceManager, ProfilerFiller profileFiller) {
		SKILL_PARAMS.clear();
		
		objectIn.entrySet().stream().filter((entry) -> {
			if (!EpicFightRegistries.SKILL.containsKey(entry.getKey())) {
				EpicFightMod.LOGGER.warn("Skill " + entry.getKey() + " doesn't exist in the registry.");
				return false;
			}
			
			return true;
		}).map(SkillReloadListener::parseParameters).forEach((pair) -> EpicFightRegistries.SKILL.getValue(pair.getFirst()).loadDatapackParameters(pair.getSecond()));
	}
}