package yesman.epicfight.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/** Native simulation/extraction for billboards; custom geometry is recorded during level submission. */
public abstract class LegacyTextureSheetParticle extends SingleQuadParticle {
    protected LegacyTextureSheetParticle(ClientLevel level,double x,double y,double z) {
        super(level,x,y,z,null);
        ParticleBridge.track(this);
    }
    protected LegacyTextureSheetParticle(ClientLevel level,double x,double y,double z,double xd,double yd,double zd) {
        super(level,x,y,z,xd,yd,zd,null);
        ParticleBridge.track(this);
    }
    public ParticleRenderType getRenderType() { return EpicFightParticleRenderTypes.TRANSLUCENT; }
    @Override public final ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
    @Override protected Layer getLayer() { return this.getRenderType()==EpicFightParticleRenderTypes.OPAQUE ? Layer.OPAQUE : Layer.TRANSLUCENT; }
    public void pickSprite(SpriteSet sprites) { this.setSprite(sprites.get(this.random)); }
    public int getLightColor(float partialTick) { return this.level==null ? 15728880 : super.getLightCoords(partialTick); }
    @Override protected int getLightCoords(float partialTick) { return this.getLightColor(partialTick); }
    @Override public void extract(QuadParticleRenderState state,Camera camera,float partialTick) {
        if (!ParticleBridge.hasCustomGeometry(this)) super.extract(state,camera,partialTick);
    }
    public void render(VertexConsumer buffer,Camera camera,float partialTick) {
        Quaternionf rotation=new Quaternionf(camera.rotation());
        rotation.rotateZ(Mth.lerp(partialTick,this.oRoll,this.roll));
        var pos=camera.position();
        float x=(float)(Mth.lerp(partialTick,this.xo,this.x)-pos.x), y=(float)(Mth.lerp(partialTick,this.yo,this.y)-pos.y), z=(float)(Mth.lerp(partialTick,this.zo,this.z)-pos.z);
        float size=this.getQuadSize(partialTick);
        float[] xs={-1,-1,1,1}, ys={-1,1,1,-1};
        float[] us={this.getU1(),this.getU1(),this.getU0(),this.getU0()}, vs={this.getV1(),this.getV0(),this.getV0(),this.getV1()};
        for(int i=0;i<4;i++) {
            Vector3f v=new Vector3f(xs[i],ys[i],0).rotate(rotation).mul(size).add(x,y,z);
            buffer.addVertex(v.x,v.y,v.z).setUv(us[i],vs[i]).setColor(this.rCol,this.gCol,this.bCol,this.alpha).setLight(this.getLightColor(partialTick));
        }
    }
    final boolean belongsTo(ClientLevel level) { return this.level==level; }
}
