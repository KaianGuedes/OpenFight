package dev.war.openfight.world.capabilities.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;
import dev.war.openfight.api.animation.AnimationManager;
import dev.war.openfight.api.animation.LivingMotion;
import dev.war.openfight.api.animation.LivingMotions;
import dev.war.openfight.api.animation.types.AttackAnimation;
import dev.war.openfight.gameasset.Animations;
import dev.war.openfight.particle.HitParticleType;
import dev.war.openfight.registry.entries.EpicFightParticles;
import dev.war.openfight.registry.entries.EpicFightSkills;
import dev.war.openfight.registry.entries.EpicFightSounds;
import dev.war.openfight.skill.Skill;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;

import org.jetbrains.annotations.Nullable;
import java.util.List;

public class TridentCapability extends RangedWeaponCapability {
    private List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> attackMotion;
    private List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> mountAttackMotion;

    public TridentCapability(RangedWeaponCapability.Builder builder) {
        super(builder);

        this.attackMotion = List.of(Animations.TRIDENT_AUTO1, Animations.TRIDENT_AUTO2, Animations.TRIDENT_AUTO3, Animations.SPEAR_DASH, Animations.SPEAR_ONEHAND_AIR_SLASH);
        this.mountAttackMotion = List.of(Animations.SPEAR_MOUNT_ATTACK);
    }

    @Override @NotNull
    public Style getStyle(LivingEntityPatch<?> entitypatch) {
        return Styles.ONE_HAND;
    }

    @Override
    public SoundEvent getHitSound() {
        return EpicFightSounds.BLADE_HIT.get();
    }

    @Override
    public HitParticleType getHitParticle() {
        return EpicFightParticles.HIT_BLADE.get();
    }

    @Override
    public List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> getAutoAttackMotion(PlayerPatch<?> playerpatch) {
        return this.attackMotion;
    }

    @Override
    public List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> getMountAttackMotion(PlayerPatch<?>  livingentitypatch) {
        return this.mountAttackMotion;
    }

    @Override
    public LivingMotion getLivingMotion(LivingEntityPatch<?> entitypatch, InteractionHand hand) {
        return entitypatch.getOriginal().isUsingItem() && entitypatch.getOriginal().getUseItem().getUseAnimation() == ItemUseAnimation.SPEAR ? LivingMotions.AIM : null;
    }

    @Nullable
    @Override
    public Skill getInnateSkill(PlayerPatch<?> playerpatch, ItemStack itemstack) {
        if (net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(dev.war.openfight.platform.fabric.registry.RegistryHolders.of(playerpatch.getLevel(), Enchantments.RIPTIDE), itemstack) > 0) {
            return EpicFightSkills.TSUNAMI.get();
        } else if (net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(dev.war.openfight.platform.fabric.registry.RegistryHolders.of(playerpatch.getLevel(), Enchantments.CHANNELING), itemstack) > 0) {
            return EpicFightSkills.WRATHFUL_LIGHTING.get();
        } else if (net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(dev.war.openfight.platform.fabric.registry.RegistryHolders.of(playerpatch.getLevel(), Enchantments.LOYALTY), itemstack) > 0) {
            return EpicFightSkills.EVERLASTING_ALLEGIANCE.get();
        } else {
            return null;
        }
    }
}