package yesman.epicfight.client.renderer.entity;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
public class NoopLivingEntityRenderer<T extends LivingEntity> extends LivingEntityRenderer<T,LivingEntityRenderState,EntityModel<LivingEntityRenderState>> {
 public NoopLivingEntityRenderer(EntityRendererProvider.Context context,float shadow) {super(context,new EntityModel<LivingEntityRenderState>(new ModelPart(java.util.List.of(),java.util.Map.of())){},shadow);}
 public LivingEntityRenderState createRenderState(){return new LivingEntityRenderState();}
 public Identifier getTextureLocation(LivingEntityRenderState state){return yesman.epicfight.client.renderer.patched.entity.PWitherRenderer.WITHER_INVULNERABLE_LOCATION;}
 // RenderEngine's dispatcher hook replaces this model with the original Epic Fight mesh.
}
