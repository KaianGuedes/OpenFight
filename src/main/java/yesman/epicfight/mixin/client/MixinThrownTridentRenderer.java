package yesman.epicfight.mixin.client;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.entity.state.ThrownTridentRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import yesman.epicfight.api.utils.math.*;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.projectile.ThrownTridentPatch;
import yesman.epicfight.client.renderer.compat.StateEntityAccess;
@Mixin(ThrownTridentRenderer.class)
public abstract class MixinThrownTridentRenderer {
 @Unique private float epicfight$oldX,epicfight$oldY;
 @Inject(method="submit(Lnet/minecraft/client/renderer/entity/state/ThrownTridentRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",at=@At(value="INVOKE",target="Lcom/mojang/blaze3d/vertex/PoseStack;rotateDegrees(Lcom/mojang/math/Axis;F)V",ordinal=0))
 private void epicfight$render(ThrownTridentRenderState state,PoseStack poseStack,SubmitNodeCollector collector,CameraRenderState camera,CallbackInfo info) {
  this.epicfight$oldX=state.xRot;this.epicfight$oldY=state.yRot;
  ThrownTrident tridentEntity=(ThrownTrident)((StateEntityAccess)state).epicfight$getEntity();
  float partialTicks=((StateEntityAccess)state).epicfight$getPartialTick();
		ThrownTridentPatch tridentPatch = EpicFightCapabilities.getEntityPatch(tridentEntity, ThrownTridentPatch.class);
		
		if (tridentPatch != null) {
			if (tridentPatch.isInnateActivated() && tridentEntity.getOwner() != null) {
				Entity owner = tridentEntity.getOwner();
				Vec3 toOwner = owner.position().subtract(tridentEntity.position());
				Vec3 toOwnerHorizontalNorm = owner.position().subtract(tridentEntity.position()).subtract(0, toOwner.y, 0).normalize();
				Vec3 toOwnerNorm = toOwner.normalize();
				Vec3 rotAxis = toOwnerHorizontalNorm.cross(toOwnerNorm).normalize();
				float deg = (float) MathUtils.getAngleBetween(toOwnerNorm, toOwnerHorizontalNorm);
				
				poseStack.rotate(QuaternionUtils.rotationDegrees(new Vector3f((float)rotAxis.x, (float)rotAxis.y, (float)rotAxis.z), deg));
				poseStack.rotate(QuaternionUtils.XP.rotationDegrees(90.0F));
				poseStack.rotate(QuaternionUtils.ZP.rotationDegrees(Mth.lerp(partialTicks, tridentEntity.xRotO, tridentEntity.getXRot()) + 90.0F));
				poseStack.translate(0.0D, -0.8D, -0.0D);
				
                state.xRot=-90F;state.yRot=90F;
            }
        }
 }
 @Inject(method="submit(Lnet/minecraft/client/renderer/entity/state/ThrownTridentRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",at=@At("TAIL"))
 private void epicfight$restore(ThrownTridentRenderState state,PoseStack pose,SubmitNodeCollector collector,CameraRenderState camera,CallbackInfo info){state.xRot=this.epicfight$oldX;state.yRot=this.epicfight$oldY;}
}
