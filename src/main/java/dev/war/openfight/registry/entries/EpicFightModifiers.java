package dev.war.openfight.registry.entries;

import dev.war.openfight.EpicFight;
import dev.war.openfight.api.animation.LivingMotions;
import dev.war.openfight.api.ex_cap.data.Moveset;
import dev.war.openfight.api.ex_cap.data.modifier.WeaponModifier;
import dev.war.openfight.api.ex_cap.provider.ProviderConditional;
import dev.war.openfight.gameasset.Animations;
import dev.war.openfight.registry.deferred.ModifierRegister;
import dev.war.openfight.registry.deferred.holders.DeferredModifier;
import dev.war.openfight.skill.SkillSlots;
import dev.war.openfight.world.capabilities.item.CapabilityItem;

public final class EpicFightModifiers {
    private EpicFightModifiers() {}

    public static final ModifierRegister REGISTRY = ModifierRegister.create(EpicFight.NAMESPACE);

    public static final DeferredModifier GREATSWORD = REGISTRY.registerModifier("greatsword", () -> WeaponModifier.builder()
            .target(EpicFightItemCapabilityPresets.GREATSWORD).modifyBuilder(builder -> builder.canBePlacedOffhand(true)));

    public static final DeferredModifier BOKKEN = REGISTRY.registerModifier("bokken", () -> WeaponModifier.builder()
            .target(EpicFightItemCapabilityPresets.BOKKEN)
            .addConditionalModifier(ProviderConditional.createSkillCondition(CapabilityItem.Styles.OCHS, EpicFightSkills.BERSERKER, SkillSlots.PASSIVE1, false, false))
            .addMovesetModifier(CapabilityItem.Styles.OCHS, Moveset.builder()
                    .addLivingMotionsRecursive(Animations.BIPED_HOLD_GREATSWORD,
                            LivingMotions.IDLE, LivingMotions.JUMP, LivingMotions.KNEEL, LivingMotions.SNEAK,
                            LivingMotions.SWIM, LivingMotions.FLY, LivingMotions.CREATIVE_FLY, LivingMotions.CREATIVE_IDLE)
                    .addLivingMotionsRecursive(Animations.BIPED_WALK_GREATSWORD,
                            LivingMotions.WALK, LivingMotions.CHASE)
                    .addLivingMotionModifier(LivingMotions.RUN, Animations.BIPED_RUN_GREATSWORD)
                    .addLivingMotionModifier(LivingMotions.BLOCK, Animations.GREATSWORD_GUARD)
                    .addComboAttacks(
                            Animations.GREATSWORD_AUTO1, Animations.GREATSWORD_AUTO2,
                            Animations.GREATSWORD_DASH, Animations.GREATSWORD_AIR_SLASH
                    )
                    .addInnateSkill((itemStack, playerPatch) -> EpicFightSkills.STEEL_WHIRLWIND.get())));
}
