package dev.war.openfight.registry.entries;

import net.minecraft.nbt.CompoundTag;
import dev.war.openfight.EpicFight;
import dev.war.openfight.api.animation.AnimationManager;
import dev.war.openfight.api.animation.types.StaticAnimation;
import dev.war.openfight.registry.deferred.CustomDataRegister;
import dev.war.openfight.registry.deferred.holders.DeferredCustomData;
import dev.war.openfight.skill.SkillContainer;
import dev.war.openfight.world.capabilities.item.custom.CustomData;

import java.util.function.BiFunction;

public class EpicFightMovesetData
{
    public static final CustomDataRegister REGISTER = CustomDataRegister.createMoveset(EpicFight.NAMESPACE);

    public static final DeferredCustomData<CustomData<BiFunction<SkillContainer, CompoundTag, AnimationManager.AnimationAccessor<? extends StaticAnimation>>>> DODGE_ANIMATION = REGISTER.registerCustomData("dodge_animation", () -> CustomData.of((container, tag) -> null));
}
