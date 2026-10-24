package dev.war.openfight.compat.kubejs;

import dev.latvian.mods.kubejs.typings.Info;
import dev.war.openfight.api.event.types.player.SkillCastEvent;
import dev.war.openfight.api.utils.side.ClientOnly;
import dev.war.openfight.client.events.engine.ControlEngine;
import dev.war.openfight.skill.Skill;
import dev.war.openfight.world.capabilities.EpicFightCapabilities;

public class EFUtilsJS {
    @ClientOnly
    @Info("""
            Requests the server to execute a skill. Called from the client.
            """)
    public static SkillCastEvent requestExecuteSkill(Skill skill) {
        return EpicFightCapabilities.getCachedLocalPlayerPatch().getSkill(skill).sendCastRequest(EpicFightCapabilities.getCachedLocalPlayerPatch(), ControlEngine.getInstance());
    }
}
