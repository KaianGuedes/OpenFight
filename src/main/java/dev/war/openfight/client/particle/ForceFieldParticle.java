package dev.war.openfight.client.particle;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.client.model.ClassicMesh;
import dev.war.openfight.api.client.model.Meshes;
import dev.war.openfight.registry.entries.EpicFightParticles;
import dev.war.openfight.world.capabilities.EpicFightCapabilities;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;
import dev.war.openfight.world.capabilities.entitypatch.boss.enderdragon.DragonCrystalLinkPhase;

public class ForceFieldParticle extends TexturedCustomModelParticle {
	private LivingEntityPatch<?> caster;
	
	public ForceFieldParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, AssetAccessor<ClassicMesh> particleMesh, Identifier texture) {
		super(level, x, y, z, xd, yd, zd, particleMesh, texture);
		this.lifetime = DragonCrystalLinkPhase.CHARGING_TICK;
		this.hasPhysics = false;
		this.roll = (float)xd;
		this.pitch = (float)zd;
		
		Entity entity = level.getEntity((int)Double.doubleToLongBits(yd));
		
		if (entity != null) {
			this.caster = EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
		}
	}
	
	@Override
	public ParticleRenderType getRenderType() {
		return EpicFightParticleRenderTypes.PARTICLE_MODEL_NO_NORMAL;
	}
	
	@Override
	public void tick() {
		super.tick();
		
		this.yaw += 36.0F;
		this.scale += (float)(Math.max(30 - this.age, 0)) / 140.0F;
		
		if (this.caster != null && this.caster.getStunShield() <= 0.0F) {
			this.remove();
		}
		
		for (int x = -1; x <= 1; x+=2) {
			for (int z = -1; z <= 1; z += 2) {
				Vec3 rand = new Vec3(Math.random() * x, Math.random(), Math.random() * z).normalize().scale(10.0D);
				this.level.addParticle(EpicFightParticles.DUST_CONTRACTIVE.get(), this.x + rand.x, this.y + rand.y - 1.0D, this.z + rand.z, -rand.x, -rand.y, -rand.z);
			}
		}
	}
	
	@Override
	public int getLightColor(float p_107086_) {
		int i = super.getLightColor(p_107086_);
		int k = i >> 16 & 255;
		return 240 | k << 16;
	}
	
	public static class Provider implements ParticleProvider<SimpleParticleType> {
		@Override
		public Particle createParticle(SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, net.minecraft.util.RandomSource randomSource) {
			return new ForceFieldParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, Meshes.FORCE_FIELD, EnderDragonRenderer.CRYSTAL_BEAM_LOCATION);
		}
	}
}