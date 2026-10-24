package yesman.epicfight.client.particle;
import java.util.function.Function;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
public final class EpicFightParticleRenderTypes {
 public static final ParticleRenderType OPAQUE=new ParticleRenderType("epicfight:opaque","EFO");
 public static final ParticleRenderType TRANSLUCENT=new ParticleRenderType("epicfight:translucent","EFT");
 public static final ParticleRenderType PARTICLE_MODEL_NO_NORMAL=new ParticleRenderType("epicfight:model","EFM");
 public static final ParticleRenderType LIGHTNING=new ParticleRenderType("epicfight:lightning","EFL");
 public static final ParticleRenderType TRANSLUCENT_GLOWING=new ParticleRenderType("epicfight:glowing","EFG");
 public static final ParticleRenderType ENTITY_PARTICLE=new ParticleRenderType("epicfight:entity","EFE");
 public static final Function<Identifier,ParticleRenderType> TRAIL_EFFECT=net.minecraft.util.Util.memoize(id->new ParticleRenderType(id.toString(),"EFTR"));
 static RenderType renderType(LegacyTextureSheetParticle particle) {
  if(particle instanceof AbstractTrailParticle<?> trail) return RenderTypes.entityTranslucent(trail.trailInfo.texturePath());
  Identifier texture=particle instanceof TexturedCustomModelParticle model ? model.texture : TextureAtlas.LOCATION_PARTICLES;
  ParticleRenderType semanticType=particle.getRenderType();
  RenderType type;
  if (semanticType == LIGHTNING || semanticType == TRANSLUCENT_GLOWING) {
   // Upstream LIGHTNING used additive blending and no depth writes; the 26.3 lightning pipeline
   // preserves that blend. TRANSLUCENT_GLOWING used ordinary alpha blending in upstream.
   type = semanticType == LIGHTNING ? RenderTypes.lightning() : RenderTypes.entityTranslucent(texture);
  } else if (semanticType == OPAQUE) {
   type = RenderTypes.entityCutout(texture);
  } else if (semanticType == ENTITY_PARTICLE) {
   type = RenderTypes.entityTranslucent(texture);
  } else {
   type = RenderTypes.entityTranslucent(texture);
  }
  return particle instanceof CustomModelParticle<?> ? EpicFightRenderTypes.getTriangulated(type) : type;
 }
}
