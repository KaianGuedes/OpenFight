package dev.war.openfight.client.particle;

import java.util.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import dev.war.openfight.client.renderer.compat.SubmitBufferSource;

public final class ParticleBridge {
    private static final Set<LegacyTextureSheetParticle> CUSTOM=Collections.newSetFromMap(new WeakHashMap<>());
    private static SubmitBufferSource buffers;
    private static final ClassValue<Boolean> CUSTOM_RENDER=new ClassValue<>() {
        @Override protected Boolean computeValue(Class<?> type) {
            try { return type.getMethod("render",VertexConsumer.class,Camera.class,float.class).getDeclaringClass()!=LegacyTextureSheetParticle.class; }
            catch(ReflectiveOperationException ex) { throw new IllegalStateException(ex); }
        }
    };
    static boolean hasCustomGeometry(LegacyTextureSheetParticle particle) { return CUSTOM_RENDER.get(particle.getClass()); }
    static void track(LegacyTextureSheetParticle particle) { if(hasCustomGeometry(particle)) CUSTOM.add(particle); }
    public static SubmitBufferSource buffers() { return Objects.requireNonNull(buffers,"Particle geometry must be submitted during a level render"); }
    public static void submit(SubmitNodeCollector collector,Camera camera,float partialTick) {
        buffers=new SubmitBufferSource(collector,new PoseStack());
        try {
            for (var particle : List.copyOf(CUSTOM)) {
                if(!particle.isAlive() || !particle.belongsTo(Minecraft.getInstance().level)) { CUSTOM.remove(particle); continue; }
                RenderType type=EpicFightParticleRenderTypes.renderType(particle);
                particle.render(buffers.getBuffer(type),camera,partialTick);
            }
            buffers.endBatch();
        } finally { buffers=null; }
    }
}
