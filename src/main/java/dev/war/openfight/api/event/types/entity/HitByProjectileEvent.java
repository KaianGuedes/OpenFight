package dev.war.openfight.api.event.types.entity;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import dev.war.openfight.api.event.CancelableEvent;
import dev.war.openfight.api.event.LivingEntityPatchEvent;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;

public class HitByProjectileEvent extends LivingEntityPatchEvent implements CancelableEvent {
    private final HitResult hitResult;
    private final Projectile projectile;

	public HitByProjectileEvent(LivingEntityPatch<?> entityPatch, HitResult hitResult, Projectile projectile) {
		super(entityPatch);

        this.hitResult = hitResult;
        this.projectile = projectile;
	}

    public final HitResult getHitResult() {
        return this.hitResult;
    }

    public final Projectile getProjectile() {
        return this.projectile;
    }
}