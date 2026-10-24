package dev.war.openfight.compat.kubejs;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.loading.FMLEnvironment;
import dev.war.openfight.platform.fabric.registry.DeferredHolder;
import dev.war.openfight.client.ClientEngine;
import dev.war.openfight.client.events.engine.ControlEngine;
import dev.war.openfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import dev.war.openfight.compat.kubejs.skill.CustomSkill;
import dev.war.openfight.registry.EpicFightRegistries;
import dev.war.openfight.registry.entries.EpicFightSkillDataKeys;
import dev.war.openfight.skill.Skill;
import dev.war.openfight.skill.SkillSlots;
import dev.war.openfight.world.capabilities.EpicFightCapabilities;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;
import dev.war.openfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class EpicFightKubeJSPlugin implements KubeJSPlugin {
    @Override
	public void registerBuilderTypes(BuilderTypeRegistry registry) {
    	registry.addDefault(EpicFightRegistries.Keys.SKILL, CustomSkill.CustomSkillBuilder.class, CustomSkill.CustomSkillBuilder::new);
    	
    	registry.of(EpicFightRegistries.Keys.SKILL, reg -> {
    		reg.add("basic", CustomSkill.CustomSkillBuilder.class, CustomSkill.CustomSkillBuilder::new);
    		reg.add("passive", CustomSkill.CustomSkillBuilder.class, CustomSkill.CustomSkillBuilder::new);
    		reg.add("chargeable", CustomSkill.CustomSkillBuilder.class, CustomSkill.CustomSkillBuilder::new);
    	});
    }
    
    @Override
    public void registerBindings(BindingRegistry event) {
        event.add("EpicFightCapabilities", EpicFightCapabilities.class);
        event.add("ServerPlayerPatch", ServerPlayerPatch.class);
        event.add("PlayerPatch", PlayerPatch.class);
        //event.add("EventType", PlayerEventListener.EventType.class);
        event.add("SkillSlots", SkillSlots.class);
        event.add("EpicFightSkillDataKeys", EpicFightSkillDataKeys.class);

        event.add("EFUtils", EFUtilsJS.class);
        
        if (event.type() == ScriptType.CLIENT && FMLEnvironment.dist.isClient()) {
            event.add("ClientEngine", ClientEngine.getInstance());
            event.add("ControlEngine", ControlEngine.getInstance());
            event.add("LocalPlayerPatch", LocalPlayerPatch.class);
        }
    }

    @Override
    public void registerTypeWrappers(TypeWrapperRegistry typeWrappers) {
        typeWrappers.register(Skill.class, o -> {
            if (o instanceof Skill skill) return skill;
            if (o instanceof String) {
                return EpicFightRegistries.SKILL.get(Identifier.parse((String)o));
            }
            if (o instanceof Identifier) {
                return EpicFightRegistries.SKILL.get((Identifier) o);
            }
            if (o instanceof DeferredHolder reg) {
                return (Skill) reg.get();
            }
            if (o instanceof BuilderBase builder) {
                return EpicFightRegistries.SKILL.get(builder.id);
            }
            throw new IllegalArgumentException("Object " + o + " of class " + o.getClass().getName() + " cannot be converted to type dev.war.openfight.skill.Skill");
        });
    }
}