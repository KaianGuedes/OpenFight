package dev.war.openfight.client.platform.neoforge.event;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.renderer.entity.NoopRenderer;
import dev.war.openfight.client.particle.*;
import dev.war.openfight.client.renderer.entity.*;
import dev.war.openfight.client.renderer.blockentity.FractureBlockRenderer;
import dev.war.openfight.registry.entries.*;
import dev.war.openfight.client.ClientEngine;
public final class ClientModBusEvent {
 private ClientModBusEvent() {}
 public static void register() {
  var hud=dev.war.openfight.client.events.engine.RenderEngine.getInstance().battleModeHUD;
  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry.addLast(dev.war.openfight.main.EpicFightMod.identifier("stamina_bar"),hud::renderStaminaBar);
  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry.addLast(dev.war.openfight.main.EpicFightMod.identifier("skills"),hud::renderNormalSkills);
  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry.addLast(dev.war.openfight.main.EpicFightMod.identifier("weapon_innate"),hud::renderWeaponInnateSkill);
  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry.addLast(dev.war.openfight.main.EpicFightMod.identifier("charging_bar"),hud::renderChargingBar);
  var particles=ParticleProviderRegistry.getInstance();
    	particles.register(EpicFightParticles.ENDERMAN_DEATH_EMIT.get(), EnderParticle.EndermanDeathEmitProvider::new);
    	particles.register(EpicFightParticles.HIT_BLUNT.get(), HitBluntParticle.Provider::new);
    	particles.register(EpicFightParticles.HIT_BLADE.get(), new HitCutParticle.Provider());
    	particles.register(EpicFightParticles.CUT.get(), CutParticle.Provider::new);
    	particles.register(EpicFightParticles.NORMAL_DUST.get(), DustParticle.NormalDustProvider::new);
    	particles.register(EpicFightParticles.DUST_EXPANSIVE.get(), DustParticle.ExpansiveDustProvider::new);
    	particles.register(EpicFightParticles.DUST_CONTRACTIVE.get(), DustParticle.ContractiveDustProvider::new);
    	particles.register(EpicFightParticles.EVISCERATE.get(), new EviscerateParticle.Provider());
    	particles.register(EpicFightParticles.BLOOD.get(), BloodParticle.Provider::new);
    	particles.register(EpicFightParticles.BLADE_RUSH_SKILL.get(), BladeRushParticle.Provider::new);
    	particles.register(EpicFightParticles.GROUND_SLAM.get(), new GroundSlamParticle.Provider());
        particles.register(EpicFightParticles.GROUND_FRACTURE.get(), new GroundSlamParticle.BlockParticleProvider());
    	particles.register(EpicFightParticles.BREATH_FLAME.get(), EnderParticle.BreathFlameProvider::new);
    	particles.register(EpicFightParticles.FORCE_FIELD.get(), new ForceFieldParticle.Provider());
    	particles.register(EpicFightParticles.FORCE_FIELD_END.get(), new ForceFieldEndParticle.Provider());
    	particles.register(EpicFightParticles.ADRENALINE_PLAYER_BEATING.get(), new EntityAfterimageParticle.AdrenalineParticleProvider());
    	particles.register(EpicFightParticles.WHITE_AFTERIMAGE.get(), new EntityAfterimageParticle.WhiteAfterimageProvider());
    	particles.register(EpicFightParticles.LASER.get(), new LaserParticle.Provider());
    	particles.register(EpicFightParticles.NEUTRALIZE.get(), new DustParticle.ExpansiveMetaParticle.Provider());
    	particles.register(EpicFightParticles.BOSS_CASTING.get(), new DustParticle.ContractiveMetaParticle.Provider());
    	particles.register(EpicFightParticles.TSUNAMI_SPLASH.get(), TsunamiSplashParticle.Provider::new);
    	particles.register(EpicFightParticles.SWING_TRAIL.get(), new AnimationTrailParticle.Provider());
    	particles.register(EpicFightParticles.PROJECTILE_TRAIL.get(), new ProjectileTrailParticle.Provider());
    	particles.register(EpicFightParticles.FEATHER.get(), FeatherParticle.Provider::new);
    	particles.register(EpicFightParticles.AIR_BURST.get(), new AirBurstParticle.Provider());
    	particles.register(EpicFightParticles.ASH_DIRECTIONAL.get(), AshDirectionalParticle.Provider::new);
    	particles.register(EpicFightParticles.CATHARSIS.get(), CatharsisParticle.Provider::new);
		EntityRendererRegistry.register(EpicFightEntityTypes.AREA_EFFECT_BREATH.get(), NoopRenderer::new);
		EntityRendererRegistry.register(EpicFightEntityTypes.DROPPED_NETHER_STAR.get(), DroppedNetherStarRenderer::new);
		EntityRendererRegistry.register(EpicFightEntityTypes.DEATH_HARVEST_ORB.get(), NoopRenderer::new);
		EntityRendererRegistry.register(EpicFightEntityTypes.DODGE_LOCATION_INDICATOR.get(), NoopRenderer::new);
		EntityRendererRegistry.register(EpicFightEntityTypes.WITHER_GHOST_CLONE.get(), WitherGhostRenderer::new);
		EntityRendererRegistry.register(EpicFightEntityTypes.WITHER_SKELETON_MINION.get(), WitherSkeletonMinionRenderer::new);
		BlockEntityRendererRegistry.register(EpicFightBlockEntities.FRACTURE.get(), FractureBlockRenderer::new);
  ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
   dev.war.openfight.client.renderer.patched.item.RenderItemBase.initItemRenderers(client);
  });
 }
 public static void reloadLayers() {
  dev.war.openfight.client.renderer.patched.layer.WearableItemLayer.clearModels();
  var subjects=dev.war.openfight.api.client.model.SoftBodyTranslatable.TRACKING_SIMULATION_SUBJECTS;
  subjects.removeIf(dev.war.openfight.api.client.physics.cloth.ClothSimulatable::invalid);
  for(var owner:subjects) owner.getClothSimulator().getAllRunningObjects().forEach(entry->owner.getClothSimulator().restart(entry.getKey()));
 }
}
