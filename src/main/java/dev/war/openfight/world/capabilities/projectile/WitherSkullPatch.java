package dev.war.openfight.world.capabilities.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.hurtingprojectile.WitherSkull;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import dev.war.openfight.gameasset.Animations;
import dev.war.openfight.registry.entries.EpicFightEntityTypes;
import dev.war.openfight.world.capabilities.EpicFightCapabilities;
import dev.war.openfight.world.capabilities.entitypatch.mob.WitherSkeletonPatch;
import dev.war.openfight.world.damagesource.EpicFightDamageSource;
import dev.war.openfight.world.damagesource.EpicFightDamageSources;
import dev.war.openfight.world.damagesource.StunType;
import dev.war.openfight.world.entity.WitherSkeletonMinion;
import dev.war.openfight.world.gamerule.EpicFightGameRules;

public class WitherSkullPatch extends ProjectilePatch<WitherSkull> {
    public WitherSkullPatch(WitherSkull original) {
        super(original);
    }

    @Override
    public void onJoinWorld(WitherSkull entity, Level level, boolean worldgenSpawn) {
        super.onJoinWorld(entity, level, worldgenSpawn);
        this.impact = 1.0F;
    }

    @Override
    protected void setMaxStrikes(WitherSkull projectileEntity, int maxStrikes) {
    }

    @Override
    public boolean onProjectileImpact(HitResult hitResult) {
        if (this.isLogicalClient()) {
            return false;
        }

        if (!(hitResult instanceof EntityHitResult entityHitResult)) {
            if (this.getLevel() instanceof ServerLevel serverLevel && Math.random() < 0.2D) {
                Vec3 location = hitResult.getLocation();
                BlockPos blockpos = new BlockPos.MutableBlockPos(location.x, location.y, location.z);
                Projectile projectile = this.original;
                EntityType<?> entityType = EpicFightEntityTypes.WITHER_SKELETON_MINION.get();

                if (
                    SpawnPlacements.isSpawnPositionOk(entityType, serverLevel, blockpos) &&
                    SpawnPlacements.checkSpawnRules(entityType, serverLevel, EntitySpawnReason.REINFORCEMENT, blockpos, serverLevel.random) &&
                    !EpicFightGameRules.NO_MOBS_IN_BOSSFIGHT.getRuleValue(serverLevel)
                ) {
                    WitherBoss summoner = (projectile.getOwner() instanceof WitherBoss) ? ((WitherBoss)projectile.getOwner()) : null;
                    WitherSkeletonMinion witherskeletonminion = new WitherSkeletonMinion(serverLevel, summoner, projectile.getX(), projectile.getY() + 0.1D, projectile.getZ());
                    witherskeletonminion.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(blockpos), EntitySpawnReason.REINFORCEMENT, null);
                    witherskeletonminion.setYRot(projectile.getYRot() - 180.0F);
                    serverLevel.addFreshEntity(witherskeletonminion);

                    EpicFightCapabilities.<WitherSkeletonMinion, WitherSkeletonPatch<WitherSkeletonMinion>>getParameterizedEntityPatch(witherskeletonminion, WitherSkeletonMinion.class, WitherSkeletonPatch.class)
                        .ifPresent(witherskeletonpatch -> witherskeletonpatch.playAnimationInstantly(Animations.WITHER_SKELETON_SPECIAL_SPAWN));
                }
            }
        } else {
            return entityHitResult.getEntity() instanceof WitherSkeletonMinion;
        }

        return false;
    }

    @Override
    public EpicFightDamageSource createEpicFightDamageSource() {
        return EpicFightDamageSources.witherSkull(this.original, this.original.getOwner())
                .setStunType(StunType.SHORT)
                .addRuntimeTag(DamageTypeTags.IS_PROJECTILE)
                .setBaseArmorNegation(this.armorNegation)
                .setBaseImpact(this.impact)
                .setInitialPosition(this.initialFirePosition);
    }
}