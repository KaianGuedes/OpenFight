import re
from pathlib import Path
root=Path(__file__).resolve().parents[1]/'src/main/java/dev/war/openfight'
for p in (root/'client/particle').glob('*.java'):
 if p.name in ['LegacyTextureSheetParticle.java','ParticleBridge.java','EpicFightParticleRenderTypes.java']:continue
 s=p.read_text(encoding='utf-8')
 s=s.replace('import net.minecraft.client.particle.TextureSheetParticle;','').replace('extends TextureSheetParticle','extends LegacyTextureSheetParticle')
 s=s.replace('class CustomModelParticle<M extends Mesh> extends Particle', 'class CustomModelParticle<M extends Mesh> extends LegacyTextureSheetParticle')
 s=s.replace('ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT', 'EpicFightParticleRenderTypes.TRANSLUCENT').replace('ParticleRenderType.PARTICLE_SHEET_OPAQUE', 'EpicFightParticleRenderTypes.OPAQUE')
 s=re.sub(r'(public Particle createParticle\([^\n]*)(\) \{)',r'\1, net.minecraft.util.RandomSource randomSource\2',s)
 s=s.replace('MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();', 'var buffers = ParticleBridge.buffers();').replace('buffers.endLastBatch()', 'buffers.endBatch()')
 s=s.replace('RenderSystem.setShaderTexture(0, this.texture);','// Texture binding belongs to the extracted RenderType (EpicFightParticleRenderTypes).')
 p.write_text(s,encoding='utf-8')
(root/'client/particle/EpicFightParticleRenderTypes.java').write_text('''package dev.war.openfight.client.particle;
import java.util.function.Function;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import dev.war.openfight.client.renderer.EpicFightRenderTypes;
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
  RenderType type=RenderTypes.entityTranslucent(texture);
  if(particle.getRenderType()==TRANSLUCENT_GLOWING) type=RenderTypes.lightning();
  return particle instanceof CustomModelParticle<?> ? EpicFightRenderTypes.getTriangulated(type) : type;
 }
}
''',encoding='utf-8')
p=root/'client/events/engine/RenderEngine.java';s=p.read_text(encoding='utf-8').replace('public void renderAfterLevel(LevelRenderContext context) {','public void renderAfterLevel(LevelRenderContext context) {\n        dev.war.openfight.client.particle.ParticleBridge.submit(context.submitNodeCollector(), this.minecraft.gameRenderer.mainCamera(), this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false));');p.write_text(s,encoding='utf-8')
print('Particle simulation and extraction bridge applied')
