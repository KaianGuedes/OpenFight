package yesman.epicfight.api.utils;

import yesman.epicfight.platform.fabric.compat.SkinCompat;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import yesman.epicfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;

import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;

import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Mesh;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.physics.cloth.ClothSimulator;
import yesman.epicfight.api.client.physics.cloth.ClothSimulator.ClothObject;
import yesman.epicfight.api.physics.SimulationTypes;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.client.renderer.patched.layer.PatchedCapeLayer;
import yesman.epicfight.client.renderer.patched.layer.WearableItemLayer;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;
import java.util.function.Function;

public class EntitySnapshot<T extends LivingEntityPatch<?>> {
	private static final InteractionHand[] HANDS = InteractionHand.values();
	
	public static EntitySnapshot<LivingEntityPatch<?>> captureLivingEntity(LivingEntityPatch<?> entitypatch) {
		if (RenderEngine.getInstance().hasRendererFor(entitypatch.getOriginal())) {
			return new EntitySnapshot<> (entitypatch);
		}
		
		return null;
	}
	
	public static PlayerSnapshot capturePlayer(AbstractClientPlayerPatch<?> playerpatch) {
		if (RenderEngine.getInstance().hasRendererFor(playerpatch.getOriginal())) {
			return new PlayerSnapshot(playerpatch);
		}
		
		return null;
	}
	
	protected final T entitypatch;
	protected final RenderableFigure entityFigure;
	protected final OpenMatrix4f[] poseMatrices;
	protected final OpenMatrix4f modelMatrix;
	protected final Vec3 position;
	protected final List<RenderableFigure> armorMeshes;
	protected final List<Pair<InteractionHand, ItemStack>> handItems;
	protected final float yRot;
	protected final float heightHalf;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public EntitySnapshot(T entitypatch) {
		LivingEntityRenderer vanillarenderer = (LivingEntityRenderer)Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entitypatch.getOriginal());
		PatchedEntityRenderer patchedrenderer = (PatchedEntityRenderer)RenderEngine.getInstance().getEntityRenderer(entitypatch.getOriginal());
		AssetAccessor<SkinnedMesh> meshAccessor = patchedrenderer.getMeshProvider(entitypatch);
		
		Identifier textureLocation = vanillarenderer.getTextureLocation((net.minecraft.client.renderer.entity.state.LivingEntityRenderState)vanillarenderer.createRenderState(entitypatch.getOriginal(),1.0F));
		
		if (textureLocation == null) {
			EpicFightMod.logAndStacktraceIfDevSide(Logger::warn, "No texture for " + entitypatch.getOriginal(), NullPointerException::new, "No texture is provided by vanilla renderer " + vanillarenderer.getClass().getSimpleName());
		}
		
		if (meshAccessor == null || meshAccessor.isEmpty()) {
			EpicFightMod.logAndStacktraceIfDevSide(Logger::warn, "No mesh for " + entitypatch.getOriginal(), NullPointerException::new, "No mesh is provided by patched renderer " + patchedrenderer.getClass().getSimpleName());
		}
		
		this.entityFigure = new RenderableFigure(meshAccessor.get(), textureLocation);
		
		Pose pose = entitypatch.getAnimator().getPose(1.0F);
		patchedrenderer.setJointTransforms(entitypatch, entitypatch.getArmature(), pose, 1.0F);
		this.poseMatrices = entitypatch.getArmature().getPoseAsTransformMatrix(pose, false);
		this.modelMatrix = entitypatch.getModelMatrix(1.0F);
		
		ImmutableList.Builder<RenderableFigure> builder = ImmutableList.builder();
		
		java.util.Arrays.stream(new EquipmentSlot[]{EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}).map(slot->entitypatch.getOriginal().getItemBySlot(slot)).forEach(itemstack -> {
			if (!(itemstack.has(net.minecraft.core.component.DataComponents.EQUIPPABLE))) {
				return;
			}
			
			EquipmentSlot armorSlot = itemstack.get(net.minecraft.core.component.DataComponents.EQUIPPABLE).slot();
			SkinnedMesh armor = WearableItemLayer.getCachedModel(itemstack.getItem());
			Identifier texture = WearableItemLayer.getArmorResource(entitypatch.getOriginal(), itemstack, armorSlot, null);
			
			if (armor != null) {
				builder.add(new RenderableFigure(armor, texture));
			}
		});
		
		this.armorMeshes = builder.build();
		
		ImmutableList.Builder<Pair<InteractionHand, ItemStack>> builder$2 = ImmutableList.builder();
		
		for (InteractionHand hand : HANDS) {
			ItemStack itemstack = entitypatch.getAdvancedHoldingItemStack(hand);
			
			if (!itemstack.isEmpty()) {
				builder$2.add(Pair.of(hand, itemstack));
			}
		}
		
		this.handItems = builder$2.build();
		this.position = entitypatch.getOriginal().position();
		this.yRot = Mth.wrapDegrees(entitypatch.getYRot());
		this.heightHalf = entitypatch.getOriginal().getBbHeight() * 0.5F;
		this.entitypatch = entitypatch;
	}
	
	public void render(PoseStack poseStack, MultiBufferSource buffers, RenderType rendertype, Mesh.DrawingFunction drawingFunction, int packedLight, float r, float g, float b, float a) {
		if (this.entityFigure.mesh == null || this.entityFigure.texture == null) {
			return;
		}
		
		this.entityFigure.mesh.initialize();
		this.entityFigure.mesh.draw(poseStack, buffers, rendertype, drawingFunction, packedLight, r, g, b, a, OverlayTexture.NO_OVERLAY, this.entitypatch.getArmature(), this.poseMatrices);
		
		for (RenderableFigure armorFigures : this.armorMeshes) {
			armorFigures.mesh.initialize();
			armorFigures.mesh.draw(poseStack, buffers, rendertype, drawingFunction, packedLight, r, g, b, a, OverlayTexture.NO_OVERLAY, this.entitypatch.getArmature(), this.poseMatrices);
		}
	}
	
	public void renderTextured(PoseStack poseStack, MultiBufferSource buffers, Function<Identifier, RenderType> rendertypeFunction, Mesh.DrawingFunction drawingFunction, int packedLight, float r, float g, float b, float a) {
		if (this.entityFigure.mesh == null || this.entityFigure.texture == null) {
			return;
		}
		
		this.entityFigure.mesh.initialize();
		this.entityFigure.mesh.draw(poseStack, buffers, rendertypeFunction.apply(this.entityFigure.texture), drawingFunction, packedLight, r, g, b, a, OverlayTexture.NO_OVERLAY, this.entitypatch.getArmature(), this.poseMatrices);
		
		for (RenderableFigure armorFigures : this.armorMeshes) {
			armorFigures.mesh.initialize();
			armorFigures.mesh.draw(poseStack, buffers, rendertypeFunction.apply(armorFigures.texture), drawingFunction, packedLight, r, g, b, a, OverlayTexture.NO_OVERLAY, this.entitypatch.getArmature(), this.poseMatrices);
		}
	}
	
	public void renderItems(PoseStack poseStack, MultiBufferSource buffers, RenderType rendertype, Mesh.DrawingFunction drawingFunction, int packedLight, float alpha) {
        for(var item:this.handItems) {
            ItemStack stack=item.getSecond();
            if(!RenderEngine.getInstance().getItemRenderer(stack).appearedInAfterimage()) continue;
            poseStack.pushPose();
            MathUtils.mulStack(poseStack,RenderEngine.getInstance().getItemRenderer(stack).getCorrectionMatrix(this.entitypatch,item.getFirst(),this.poseMatrices));
            var state=new net.minecraft.client.renderer.item.ItemStackRenderState();
            Minecraft.getInstance().getItemModelResolver().updateForLiving(state,stack,item.getFirst()==InteractionHand.MAIN_HAND?ItemDisplayContext.THIRD_PERSON_RIGHT_HAND:ItemDisplayContext.THIRD_PERSON_LEFT_HAND,this.entitypatch.getOriginal());
            for(int i=0;i<state.activeLayerCount;i++) {
                var layer=state.layers[i];
                poseStack.pushPose(); layer.applyTransform(poseStack.last());
                for(var quad:layer.quads.all()) {
                    int tint=quad.materialInfo().tintIndex();
                    int color=layer.tintLayers!=null && tint>=0 && tint<layer.tintLayers.size()?layer.tintLayers.getInt(tint):0xFFFFFFFF;
                    drawingFunction.putBulkData(poseStack.last(),quad,buffers.getBuffer(rendertype),(color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,alpha,packedLight,OverlayTexture.NO_OVERLAY,false);
                }
                poseStack.popPose();
            }
            poseStack.popPose();
        }
    }
	
	public OpenMatrix4f[] poseMatrices() {
		return this.poseMatrices;
	}
	
	public OpenMatrix4f getModelMatrix() {
		return this.modelMatrix;
	}
	
	public float getYRot() {
		return this.yRot;
	}
	
	public float getHeightHalf() {
		return this.heightHalf;
	}
	
	public Vec3 getPosition() {
		return this.position;
	}
	
	/**
	 * Code copy from {@link ItemRenderer#renderModelLists} but replaces putting bulk data to follow DrawingFunction
	 */
	@SuppressWarnings("deprecation")
	public static void renderModelLists(net.minecraft.client.resources.model.geometry.ItemQuads model, ItemStack stack, int light, int overlay, float alpha, PoseStack pose, VertexConsumer buffer, Mesh.DrawingFunction function) {
        renderQuadList(pose,buffer,model.all(),stack,light,overlay,alpha,function);
    }
	
	public static void renderQuadList(PoseStack pose, VertexConsumer buffer, List<BakedQuad> quads, ItemStack stack, int light, int overlay, float alpha, Mesh.DrawingFunction function) {
        for(var quad:quads) function.putBulkData(pose.last(),quad,buffer,1F,1F,1F,alpha,light,overlay,false);
    }
	
	public record RenderableFigure(Mesh mesh, Identifier texture) {
	}
	
	public static class PlayerSnapshot extends EntitySnapshot<AbstractClientPlayerPatch<?>> {
		protected final Matrix4f localMatrix;
		protected final OpenMatrix4f[] unboundPoseMatrices;
		protected RenderableFigure capeFigure;
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public PlayerSnapshot(AbstractClientPlayerPatch<?> entitypatch) {
			super(entitypatch);
			
			PatchedLivingEntityRenderer patchedrenderer = (PatchedLivingEntityRenderer)RenderEngine.getInstance().getEntityRenderer(entitypatch.getOriginal());
			PoseStack poseStack = new PoseStack();
			patchedrenderer.mulPoseStack(poseStack, entitypatch.getArmature(), entitypatch.getOriginal(), entitypatch, 1.0F);
			this.localMatrix = poseStack.last().pose();
			this.unboundPoseMatrices = new OpenMatrix4f[entitypatch.getArmature().getPoseMatrices().length];
			
			for (int i = 0; i < entitypatch.getArmature().getPoseMatrices().length; i++) {
				this.unboundPoseMatrices[i] = new OpenMatrix4f(entitypatch.getArmature().getPoseMatrices()[i]);
			}
			
			if (entitypatch.getOriginal().isModelPartShown(PlayerModelPart.CAPE) && SkinCompat.capeTexture(entitypatch.getOriginal().getSkin()) != null) {
				entitypatch.getSimulator(SimulationTypes.CLOTH).ifPresent(clohtSimulator -> {
					clohtSimulator.getRunningObject(ClothSimulator.PLAYER_CLOAK).ifPresent(clothObj -> {
						ClothObject capturedClothObj = clothObj.captureMyself();
						Function<Float, OpenMatrix4f> partialColliderTransformProvider = (partialFrame) -> {
							Vec3 pos = entitypatch.getOriginal().getPosition(partialFrame);
							float yRotLerp = Mth.rotLerp(partialFrame, entitypatch.getYRotO(), entitypatch.getYRot());
							
							return OpenMatrix4f.createTranslation((float)pos.x, (float)pos.y, (float)pos.z).rotateDeg(180.0F - yRotLerp, Vec3f.Y_AXIS);
				        };
						
						capturedClothObj.tick(entitypatch, partialColliderTransformProvider, 1.0F, entitypatch.getArmature(), this.unboundPoseMatrices);
						this.capeFigure = new RenderableFigure(capturedClothObj, SkinCompat.capeTexture(entitypatch.getOriginal().getSkin()));
					});
				});
			}
		}
		
		@Override
		public void render(PoseStack poseStack, MultiBufferSource buffers, RenderType rendertype, Mesh.DrawingFunction drawingFunction, int packedLight, float r, float g, float b, float a) {
			if (this.capeFigure != null) {
				PatchedCapeLayer.renderSimulatingCape(poseStack, buffers, rendertype, drawingFunction, (ClothObject)this.capeFigure.mesh, this.position.x, this.position.y, this.position.z, r, g, b, a, this.entitypatch, this.unboundPoseMatrices, packedLight, this.localMatrix, this.yRot);
			}
			
			super.render(poseStack, buffers, rendertype, drawingFunction, packedLight, r, g, b, a);
		}
		
		@Override
		public void renderTextured(PoseStack poseStack, MultiBufferSource buffers, Function<Identifier, RenderType> rendertypeFunction, Mesh.DrawingFunction drawingFunction, int packedLight, float r, float g, float b, float a) {
			super.renderTextured(poseStack, buffers, rendertypeFunction, drawingFunction, packedLight, r, g, b, a);
			
			if (this.capeFigure != null) {
				PatchedCapeLayer.renderSimulatingCape(poseStack, buffers, rendertypeFunction.apply(this.capeFigure.texture), drawingFunction, (ClothObject)this.capeFigure.mesh, this.position.x, this.position.y, this.position.z, r, g, b, a, this.entitypatch, this.unboundPoseMatrices, packedLight, this.localMatrix, this.yRot);
			}
		}
	}
}
