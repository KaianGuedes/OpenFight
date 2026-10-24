package dev.war.openfight.client.gui.datapack.widgets;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.input.MouseButtonEvent;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import dev.war.openfight.api.animation.*;
import dev.war.openfight.api.animation.types.*;
import dev.war.openfight.api.animation.types.AttackAnimation.Phase;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.client.animation.ClientAnimator;
import dev.war.openfight.api.client.animation.Layer;
import dev.war.openfight.api.client.animation.property.ClientAnimationProperties;
import dev.war.openfight.api.client.animation.property.TrailInfo;
import dev.war.openfight.api.client.model.Mesh;
import dev.war.openfight.api.client.model.SkinnedMesh;
import dev.war.openfight.api.client.model.SoftBodyTranslatable;
import dev.war.openfight.api.client.physics.cloth.ClothSimulatable;
import dev.war.openfight.api.client.physics.cloth.ClothSimulator;
import dev.war.openfight.api.collider.Collider;
import dev.war.openfight.api.model.Armature;
import dev.war.openfight.api.physics.PhysicsSimulator;
import dev.war.openfight.api.physics.SimulatableObject;
import dev.war.openfight.api.physics.SimulationTypes;
import dev.war.openfight.api.physics.bezier.CubicBezierCurve;
import dev.war.openfight.api.utils.math.*;
import dev.war.openfight.client.particle.AnimationTrailParticle;
import dev.war.openfight.client.particle.EpicFightParticleRenderTypes;
import dev.war.openfight.client.renderer.EpicFightShaders;
import dev.war.openfight.gameasset.Animations;
import dev.war.openfight.main.EpicFightSharedConstants;
import dev.war.openfight.world.capabilities.entitypatch.Faction;
import dev.war.openfight.world.capabilities.entitypatch.LivingEntityPatch;
import dev.war.openfight.world.damagesource.StunType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/// We're refactoring UI codes, use [dev.war.openfight.client.gui.widgets.ModelPreviewer] instead
@Deprecated
public class ModelPreviewer extends AbstractWidget implements ResizableComponent, TickableComponent {
	private final List<AssetAccessor<? extends StaticAnimation>> animationsToPlay = Lists.newArrayList();
	private final List<CustomTrailParticle> trailParticles = Lists.newArrayList();
	private final CheckBox showColliderCheckbox = new CheckBox(Minecraft.getInstance().font, 0, 60, 0, 10, null, null, true, Component.translatable("datapack_edit.model_player.collider"), null);
	private final CheckBox showItemCheckbox = new CheckBox(Minecraft.getInstance().font, 0, 40, 0, 10, null, null, true, Component.translatable("datapack_edit.model_player.item"), null);
	private final CheckBox showTrailCheckbox = new CheckBox(Minecraft.getInstance().font, 0, 40, 0, 10, null, null, true, Component.translatable("datapack_edit.model_player.trail"), null);
	
	private double zoom = -3.0D;
	private float xRot = 0.0F;
	private float yRotO = 180.0F;
	private float yRot = 180.0F;
	private float xMove = 0.0F;
	private float yMove = 0.0F;
	private int index;
	private float attackTimeBegin;
	private float attackTimeEnd;
	
	private FakeEntityPatch entitypatch;
	private NoEntityAnimator animator;
	private AssetAccessor<? extends SkinnedMesh> mesh;
	private AssetAccessor<? extends Armature> armature;
	private Identifier figureTexture;
	
	private Joint colliderJoint;
	private Collider collider;
	private List<TrailInfo> trailInfoList = Lists.newArrayList();
	private Item item;
	
	private Vec4f backgroundClearColor;
	private boolean cameraMoveEnabled = true;
	private boolean cameraRotationEnabled = true;
	private boolean zoomingCameraEnabled = true;
	
	private ClothSimulator clothSimulator;
	private SoftBodyTranslatable cloakMesh;
	private Identifier cloakTexture;
	private Vec3f cloakColor = new Vec3f(1.0F, 1.0F, 1.0F);
	
	public ModelPreviewer(int x1, int x2, int y1, int y2, HorizontalSizing horizontal, VerticalSizing vertical, AssetAccessor<? extends Armature> armature, AssetAccessor<? extends SkinnedMesh> mesh) {
		super(x1, y1, x2, y2, Component.literal(""));
		
		if (armature != null) {
			this.entitypatch = new FakeEntityPatch(armature.get());
			this.animator = new NoEntityAnimator(this.entitypatch);
			this.entitypatch.initAnimator(this.animator);
		}
		
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.horizontalSizingOption = horizontal;
		this.verticalSizingOption = vertical;
		this.mesh = mesh;
		this.armature = armature;
		
	}
	
	public void setMesh(AssetAccessor<? extends SkinnedMesh> mesh) {
		this.mesh = mesh;
	}
	
	public void setFigureTexture(Identifier texture) {
		this.figureTexture = texture;
	}
	
	public void initCloakInfo(SoftBodyTranslatable cloakMesh, Identifier cloakTexture, ClothSimulator.ClothObjectBuilder builder) {
		this.cloakMesh = cloakMesh;
		this.cloakTexture = cloakTexture;
		
		if (builder != null) {
			this.clothSimulator = new ClothSimulator();
			this.clothSimulator.runWhen(ClothSimulator.MODELPREVIEWER_CLOAK, cloakMesh, builder, () -> true);
		}
	}
	
	public void removeCloak() {
		if (this.clothSimulator != null) {
			this.clothSimulator.stop(ClothSimulator.MODELPREVIEWER_CLOAK);
			this.clothSimulator = null;
		}
		
		this.cloakMesh = null;
		this.cloakTexture = null;
	}
	
	public void setCloakColor(int colorCode) {
		float b = (colorCode & 255) / 255.0F;
		float g = ((colorCode & 65280) >> 8) / 255.0F;
		float r = ((colorCode & 16711680) >> 16) / 255.0F;
		
		this.cloakColor = new Vec3f(r, g, b);
	}
	
	public void setCloakColor(float r, float g, float b) {
		this.cloakColor = new Vec3f(r, g, b);
	}
	
	public Vec3f getCloakColor() {
		return this.cloakColor;
	}
	
	public SoftBodyTranslatable getCloakMesh() {
		return this.cloakMesh;
	}
	
	public void setCloakTexture(Identifier cloakTexture) {
		this.cloakTexture = cloakTexture;
	}
	
	public Identifier getCloakTexture() {
		return this.cloakTexture;
	}
	
	public AssetAccessor<? extends Armature> getArmature() {
		return this.armature;
	}
	
	public AssetAccessor<? extends SkinnedMesh> getMesh() {
		return this.mesh;
	}
	
	public Animator getAnimator() {
		return this.animator;
	}
	
	public void setCameraTransform(double zoom, float xRot, float yRot, float xMove, float yMove) {
		this.zoom = zoom;
		this.xRot = xRot;
		this.yRotO = yRot;
		this.yRot = yRot;
		this.xMove = xMove;
		this.yMove = yMove;
	}
	
	public void setCollider(Collider collider) {
		this.collider = collider;
	}
	
	public void setCollider(Collider collider, Joint joint) {
		this.collider = collider;
		this.colliderJoint = joint;
	}
	
	public void setColliderJoint(Joint joint) {
		this.colliderJoint = joint;
	}
	
	public void setTrailInfo(TrailInfo... trailInfos) {
		this.trailInfoList.clear();
		
		for (TrailInfo trailInfo : trailInfos) {
			this.trailInfoList.add(trailInfo);
		}
	}
	
	public void setItemToRender(Item item) {
		this.item = item;
	}
	
	public void setAttackTimeBegin(float attackTimeBegin) {
		this.attackTimeBegin = attackTimeBegin;
	}
	
	public void setAttackTimeEnd(float attackTimeEnd) {
		this.attackTimeEnd = attackTimeEnd;
	}
	
	public void setBackgroundClearColor(Vec4f clearColor) {
		this.backgroundClearColor = clearColor;
	}
	
	public void enableCameraControll(boolean enable) {
		this.cameraMoveEnabled = enable;
		this.cameraRotationEnabled = enable;
		this.zoomingCameraEnabled = enable;
	}
	
	public void enableCameraMove(boolean enable) {
		this.cameraMoveEnabled = enable;
	}
	
	public void enableCameraRotation(boolean enable) {
		this.cameraRotationEnabled = enable;
	}
	
	public void enableZoomingCamera(boolean enable) {
		this.zoomingCameraEnabled = enable;
	}
	
	public void addAnimationToPlay(AssetAccessor<? extends StaticAnimation> animation) {
        // Blocks crashes for exceptional cases
        if (animation == null) {
            return;
        }

		if (this.index == -1) {
			this.index = 0;
		}
		
		this.animationsToPlay.add(animation);
		this.animator.playAnimation(animation, 0.0F);
	}
	
	public void removeAnimationPlayingAnimation(AssetAccessor<? extends StaticAnimation> animation) {
		this.animationsToPlay.remove(animation);
	}
	
	public void clearAnimations() {
		this.index = -1;
		this.animationsToPlay.clear();
		this.animator.playAnimation(Animations.EMPTY_ANIMATION, 0.0F);
		
		((ClientAnimator)this.animator).iterAllLayers((layer) -> {
			layer.off(this.animator.getEntityPatch());
		});
		
		this.animator.playAnimation(Animations.OFF_ANIMATION_HIGHEST, 0.0F);
		this.animator.playAnimation(Animations.OFF_ANIMATION_MIDDLE, 0.0F);
		this.animator.playAnimation(Animations.OFF_ANIMATION_LOWEST, 0.0F);
	}
	
	public void restartAnimations() {
		this.index = 0;
		
		if (this.animationsToPlay.size() == 0) {
			this.index = -1;
			return;
		}
		
		this.animator.playAnimation(this.animationsToPlay.get(0), 0.0F);
	}
	
	@Override
	public void _tick() {
		if (this.animator != null) {
			this.animator.tick();
		}
		
		this.yRotO = this.yRot;
		this.trailParticles.forEach((trail) -> trail.tick());
		this.trailParticles.removeIf((trail) -> !trail.isAlive());
		
		if (this.entitypatch != null && this.clothSimulator != null) {
			this.clothSimulator.tick(this.entitypatch);
		}
	}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
double x = event.x(); double y = event.y(); int button = event.button();
		if (this.active && this.visible) {
			if (event.button() == 0 || event.button() == 1 || event.button() == 2) {
				boolean flag = this.isMouseOver(x, y);
				
				if (flag) {
					
					
					if (this.item != null) {
						if (this.showItemCheckbox.mouseClicked(event, doubleClick)) {
							return true;
						}
					}
					
					if (!this.trailInfoList.isEmpty()) {
						if (this.showTrailCheckbox.mouseClicked(event, doubleClick)) {
							return true;
						}
					}
					
					if (this.collider != null) {
						if (this.showColliderCheckbox.mouseClicked(event, doubleClick)) {
							return true;
						}
					}
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
double mouseX = event.x(); double mouseY = event.y(); int button = event.button();
		if (this.isMouseOver(mouseX, mouseY)) {
			if (button == 0) {
				if (this.cameraRotationEnabled) {
					this.xRot = (float)Mth.clamp(this.xRot + dy * 2.5D, -30.0D, 45.0D);
					this.yRot += dx * 2.5D;
				}
			} else if (button == 2) {
				if (this.cameraMoveEnabled) {
					this.xMove += (float)dx * 0.015F * -this.zoom;
					this.yMove += -(float)dy * 0.015F * -this.zoom;
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean mouseScrolled(double x, double y, double xScroll, double yScroll) {
		if (this.zoomingCameraEnabled) {
			this.zoom = Mth.clamp(this.zoom + yScroll * 0.5D, -20.0D, -0.5D);
			return true;
		}
		
		return false;
	}
	
	// STUB(26.3 port): the 3D preview used an offscreen RenderTarget with direct RenderSystem/BufferUploader draws,
	// which no longer exist. The preview is reduced to a flat background; animation/camera state is still simulated.
	protected void renderFigure(GuiGraphicsExtractor guiGraphics) {
	}
	
	@Override
	public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
		Vec4f clear = this.backgroundClearColor;
		int argb = clear == null ? 0xFF282828 : ((int)(clear.w * 255.0F) << 24 | (int)(clear.x * 255.0F) << 16 | (int)(clear.y * 255.0F) << 8 | (int)(clear.z * 255.0F));
		guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), argb);
		this.renderFigure(guiGraphics);

		if (this.animator != null) {
			// Controls visibilities of each widget
			int top = this.getY() + 6;
			int right = this.getX() + this.getWidth() - 2;

			if (!this.trailInfoList.isEmpty()) {
				right -= this.showTrailCheckbox.getWidth();
				this.showTrailCheckbox.setX(right);
				this.showTrailCheckbox.setY(top);
				this.showTrailCheckbox.extractWidgetRenderState(guiGraphics, mouseX, mouseY, partialTicks);
			}

			if (this.item != null) {
				right -= this.showItemCheckbox.getWidth();
				this.showItemCheckbox.setX(right);
				this.showItemCheckbox.setY(top);
				this.showItemCheckbox.extractWidgetRenderState(guiGraphics, mouseX, mouseY, partialTicks);
			}

			if (this.collider != null) {
				right -= this.showColliderCheckbox.getWidth();
				this.showColliderCheckbox.setX(right);
				this.showColliderCheckbox.setY(top);
				this.showColliderCheckbox.extractWidgetRenderState(guiGraphics, mouseX, mouseY, partialTicks);
			}
		}
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementInput) {
		narrationElementInput.add(NarratedElementType.TITLE, this.createNarrationMessage());
	}
	
	@Override
	public void resize(ScreenRectangle screenRectangle) {
		if (this.getHorizontalSizingOption() != null) {
			this.getHorizontalSizingOption().resizeFunction.resize(this, screenRectangle, this.getX1(), this.getX2());
		}
		
		if (this.getVerticalSizingOption() != null) {
			this.getVerticalSizingOption().resizeFunction.resize(this, screenRectangle, this.getY1(), this.getY2());
		}
		
		double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
	}
	
	public void onDestroy() {
	}
	
	public class FakeEntityPatch extends LivingEntityPatch<LivingEntity> implements SimulatableObject, ClothSimulatable {
		public FakeEntityPatch(Armature armature) {
			super(null);
			this.armature = armature.deepCopy();
		}
		
		public void setAnimator() {
			this.animator = ModelPreviewer.this.animator;
		}
		
		@Override
		public void initAnimator(Animator animator) {
			this.animator = animator;
		}
		
		@Override
		public void updateMotion(boolean considerInaction) {
			
		}
		
		@Override
		public AssetAccessor<? extends StaticAnimation> getHitAnimation(StunType stunType) {
			return null;
		}
		
		@Override
		public boolean isLogicalClient() {
			return true;
		}
		
		@Override
		public void cancelItemUse() {
		}
		
		@Override
		public float getAttackDirectionPitch(float partialTick) {
			return 0.0F;
		}
		
		@Override
		public OpenMatrix4f getModelMatrix(float partialTicks) {
			return MathUtils.getModelMatrixIntegral(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, partialTicks, 1.0F, 1.0F, 1.0F);
		}
		
		@Override
		public void poseTick(DynamicAnimation animation, Pose pose, float time, float partialTicks) {
		}
		
		@Override
		public void updateEntityState() {
		}
		
		@Override
		public boolean invalid() {
			return false;
		}
		
		@Override
		public Vec3 getObjectVelocity() {
			return Vec3.ZERO;
		}
		
		@Override
		public Vec3 getAccurateCloakLocation(float partialFrame) {
			return Vec3.ZERO;
		}
		
		@Override
		public Vec3 getAccuratePartialLocation(float partialFrame) {
			return Vec3.ZERO;
		}
		
		@Override
		public float getAccurateYRot(float partialFrame) {
			return ModelPreviewer.this.yRot;
		}
		
		@Override
		public float getYRotDelta(float partialFrame) {
			return ModelPreviewer.this.yRot - ModelPreviewer.this.yRotO;
		}
		
		@Override
		public float getYRot() {
			return ModelPreviewer.this.yRot;
		}
		
		@Override
		public float getYRotO() {
			return ModelPreviewer.this.yRotO;
		}
		
		@Override
		public <SIM extends PhysicsSimulator<?, ?, ?, ?, ?>> Optional<SIM> getSimulator(SimulationTypes<?, ?, ?, ?, ?, SIM> simulationType) {
			return Optional.empty();
		}

		@Override
		public float getScale() {
			return 1.0F;
		}

		@Override
		public Animator getSimulatableAnimator() {
			return this.animator;
		}

		@Override
		public float getGravity() {
			return 9.8F;
		}

		@Override
		public ClothSimulator getClothSimulator() {
			return null;
		}
		
		@Override
		public Faction getFaction() {
			return null;
		}
		
		@Override
		public boolean isFakeEntity() {
			return true;
		}
	}
	
	public class NoEntityAnimator extends ClientAnimator {
		public NoEntityAnimator(LivingEntityPatch<?> entitypatch) {
			super(entitypatch, NoEntityBaseLayer::new);
		}
		
		@Override
		public void tick() {
			this.baseLayer.update(this.entitypatch);
			
			if (this.baseLayer.animationPlayer.isEnd() && this.baseLayer.getNextAnimation() == null) {
				AssetAccessor<? extends StaticAnimation> toPlay = ModelPreviewer.this.index > -1 && ModelPreviewer.this.index < ModelPreviewer.this.animationsToPlay.size() ? ModelPreviewer.this.animationsToPlay.get(ModelPreviewer.this.index) : Animations.EMPTY_ANIMATION;
				this.baseLayer.playAnimation(toPlay, this.entitypatch, 0.0F);
				
				if (!ModelPreviewer.this.trailInfoList.isEmpty()) {
					for (TrailInfo trailInfo : ModelPreviewer.this.trailInfoList) {
						if (trailInfo.playable()) {
							CustomTrailParticle trail = new CustomTrailParticle(ModelPreviewer.this.entitypatch.getArmature().searchJointByName(trailInfo.joint()), toPlay, trailInfo);
							ModelPreviewer.this.trailParticles.add(trail);
						} else {
							toPlay.get().getProperty(ClientAnimationProperties.TRAIL_EFFECT).ifPresent(trailInfos -> {
								for (TrailInfo info : trailInfos) {
									if (info.hand() != InteractionHand.MAIN_HAND) {
										continue;
									}
									
									TrailInfo combinedTrailInfo = trailInfo.overwrite(info);
									
									if (combinedTrailInfo.playable()) {
										CustomTrailParticle trail = new CustomTrailParticle(ModelPreviewer.this.entitypatch.getArmature().searchJointByName(combinedTrailInfo.joint()), toPlay, combinedTrailInfo);
										ModelPreviewer.this.trailParticles.add(trail);
									}
								}
							});
						}
					}
				}
				
				ModelPreviewer.this.index = (ModelPreviewer.this.index + 1) % ModelPreviewer.this.animationsToPlay.size();
			}
		}
		
		public LivingEntityPatch<?> getEntityPatch() {
			return this.entitypatch;
		}
		
		static class NoEntityAnimationPlayer extends AnimationPlayer {
			@Override
			public void tick(LivingEntityPatch<?> entitypatch) {
				DynamicAnimation nowPlay = this.getAnimation().get();
				
				this.prevElapsedTime = this.elapsedTime;
				this.elapsedTime += EpicFightSharedConstants.A_TICK * (this.isReversed() && nowPlay.canBePlayedReverse() ? -1.0F : 1.0F);
				
				if (this.elapsedTime >= nowPlay.getTotalTime()) {
					if (nowPlay.isRepeat()) {
						this.prevElapsedTime = 0;
						this.elapsedTime %= nowPlay.getTotalTime();
					} else {
						this.elapsedTime = nowPlay.getTotalTime();
						this.isEnd = true;
					}
				} else if (this.elapsedTime < 0) {
					if (nowPlay.isRepeat()) {
						this.prevElapsedTime = nowPlay.getTotalTime();
						this.elapsedTime = nowPlay.getTotalTime() + this.elapsedTime;
					} else {
						this.elapsedTime = 0.0F;
						this.isEnd = true;
					}
				}
			}
			
			@Override
			public void begin(AssetAccessor<? extends DynamicAnimation> animation, LivingEntityPatch<?> entitypatch) {
			}
			
			@Override
			public Pose getCurrentPose(LivingEntityPatch<?> entitypatch, float partialTicks) {
				return this.play.get().getRawPose(this.prevElapsedTime + (this.elapsedTime - this.prevElapsedTime) * partialTicks);
			}
		}
		
		static class NoEntityLayer extends Layer {
			public NoEntityLayer(Priority priority) {
				super(priority, NoEntityAnimationPlayer::new);
			}
			
			public void playAnimation(AssetAccessor<? extends StaticAnimation> nextAnimation, LivingEntityPatch<?> entitypatch, float convertTimeModifier) {
				Pose lastPose = entitypatch.getAnimator().getPose(1.0F);
				this.resume();
				
				if (!nextAnimation.get().isMetaAnimation()) {
					this.setLinkAnimation(nextAnimation, entitypatch, lastPose, convertTimeModifier);
					this.linkAnimation.putOnPlayer(this.animationPlayer, entitypatch);
					this.nextAnimation = nextAnimation;
				}
			}
			
			@Override
			public void playAnimationInstantly(AssetAccessor<? extends DynamicAnimation> nextAnimation, LivingEntityPatch<?> entitypatch) {
				this.resume();
				nextAnimation.get().putOnPlayer(this.animationPlayer, entitypatch);
				this.nextAnimation = null;
			}
			
			@Override
			protected void setLinkAnimation(AssetAccessor<? extends StaticAnimation> nextAnimation, LivingEntityPatch<?> entitypatch, Pose lastPose, float convertTimeModifier) {
				Pose currentPose = this.animationPlayer.getAnimation().get().getRawPose(this.animationPlayer.getElapsedTime());
				Pose nextAnimationPose = nextAnimation.get().getRawPose(0.0F);
				float totalTime = nextAnimation.get().getTransitionTime();
				
				AssetAccessor<? extends DynamicAnimation> fromAnimation = this.animationPlayer.isEmpty() ? entitypatch.getClientAnimator().baseLayer.animationPlayer.getAnimation() : this.animationPlayer.getAnimation();
				
				if (fromAnimation instanceof LinkAnimation linkAnimation) {
					fromAnimation = linkAnimation.getFromAnimation();
				}
				
				this.linkAnimation.getAnimationClip().reset();
				this.linkAnimation.setTotalTime(totalTime);
				this.linkAnimation.setConnectedAnimations(fromAnimation, nextAnimation);
				
				Map<String, JointTransform> data1 = currentPose.getJointTransformData();
				Map<String, JointTransform> data2 = nextAnimationPose.getJointTransformData();
				
				for (String jointName : data1.keySet()) {
					if (data1.containsKey(jointName) && data2.containsKey(jointName)) {
						Keyframe[] keyframes = new Keyframe[2];
						keyframes[0] = new Keyframe(0.0F, data1.get(jointName));
						keyframes[1] = new Keyframe(totalTime, data2.get(jointName));
						TransformSheet sheet = new TransformSheet(keyframes);
						this.linkAnimation.getAnimationClip().addJointTransform(jointName, sheet);
					}
				}
				
				this.animationPlayer.setPlayAnimation(this.linkAnimation);
			}
			
			public void update(LivingEntityPatch<?> entitypatch) {
				if (this.paused) {
					this.animationPlayer.setElapsedTime(this.animationPlayer.getElapsedTime());
				} else {
					this.animationPlayer.tick(entitypatch);
				}
				
				if (!this.paused && this.animationPlayer.isEnd()) {
					if (this.nextAnimation != null) {
						this.nextAnimation.get().putOnPlayer(this.animationPlayer, entitypatch);
						this.nextAnimation = null;
					} else {
						if (this.animationPlayer.getAnimation() instanceof LayerOffAnimation) {
							this.animationPlayer.getAnimation().get().end(entitypatch, Animations.EMPTY_ANIMATION, true);
						} else {
							this.off(entitypatch);
						}
					}
				}
			}
			
			public Pose getEnabledPose(LivingEntityPatch<?> entitypatch, float partialTick) {
				DynamicAnimation animation = this.animationPlayer.getAnimation().get();
				Pose pose = animation.getRawPose(this.animationPlayer.getPrevElapsedTime() + (this.animationPlayer.getElapsedTime() - this.animationPlayer.getPrevElapsedTime()) * partialTick);
				pose.disableJoint((entry) -> !animation.hasTransformFor(entry.getKey()));
				
				return pose;
			}
			
			public void off(LivingEntityPatch<?> entitypatch) {
				if (!this.isDisabled() && !(this.animationPlayer.getAnimation() instanceof LayerOffAnimation)) {
					float convertTime = entitypatch.getClientAnimator().baseLayer.animationPlayer.getAnimation().get().getTransitionTime();
					setLayerOffAnimation(this.animationPlayer.getAnimation(), this.getEnabledPose(entitypatch, 1.0F), this.layerOffAnimation, convertTime);
					this.playAnimationInstantly(this.layerOffAnimation, entitypatch);
				}
			}
		}
		
		static class NoEntityBaseLayer extends Layer.BaseLayer {
			public NoEntityBaseLayer() {
				super(NoEntityAnimationPlayer::new);
				
				this.compositeLayers.clear();
				
				for (Priority priority : Priority.values()) {
					this.compositeLayers.computeIfAbsent(priority, NoEntityLayer::new);
				}
				
				this.baseLayerPriority = Priority.LOWEST;
			}
			
			@Override
			public void playAnimation(AssetAccessor<? extends StaticAnimation> nextAnimation, LivingEntityPatch<?> entitypatch, float convertTimeModifier) {
				Priority priority = nextAnimation.get().getPriority();
				this.baseLayerPriority = priority;
				this.offCompositeLayersLowerThan(entitypatch, nextAnimation);
				
				Pose lastPose = entitypatch.getAnimator().getPose(1.0F);
				this.resume();
				
				if (!nextAnimation.get().isMetaAnimation()) {
					this.setLinkAnimation(nextAnimation, entitypatch, lastPose, convertTimeModifier);
					this.linkAnimation.putOnPlayer(this.animationPlayer, entitypatch);
					entitypatch.updateEntityState();
					this.nextAnimation = nextAnimation;
				}
			}
			
			@Override
			public void playAnimationInstantly(AssetAccessor<? extends DynamicAnimation> nextAnimation, LivingEntityPatch<?> entitypatch) {
				this.resume();
				nextAnimation.get().putOnPlayer(this.animationPlayer, entitypatch);
				this.nextAnimation = null;
			}
			
			@Override
			protected void playLivingAnimation(AssetAccessor<? extends StaticAnimation> nextAnimation, LivingEntityPatch<?> entitypatch) {
				this.resume();
				
				if (!nextAnimation.get().isMetaAnimation()) {
					this.concurrentLinkAnimation.acceptFrom(this.animationPlayer.getAnimation().get().getRealAnimation(), nextAnimation, this.animationPlayer.getElapsedTime());
					this.concurrentLinkAnimation.putOnPlayer(this.animationPlayer, entitypatch);
					this.nextAnimation = nextAnimation;
				}
			}
			
			@Override
			public void update(LivingEntityPatch<?> entitypatch) {
				if (this.paused) {
					this.animationPlayer.setElapsedTime(this.animationPlayer.getElapsedTime());
				} else {
					this.animationPlayer.tick(entitypatch);
				}
				
				if (!this.paused && this.animationPlayer.isEnd()) {
					if (this.nextAnimation != null) {
						this.nextAnimation.get().putOnPlayer(this.animationPlayer, entitypatch);
						this.nextAnimation = null;
					} else {
						if (this.animationPlayer.getAnimation() instanceof LayerOffAnimation) {
							this.animationPlayer.getAnimation().get().end(entitypatch, Animations.EMPTY_ANIMATION, true);
						} else {
							this.off(entitypatch);
						}
					}
				}
				
				for (Layer layer : this.compositeLayers.values()) {
					layer.update(entitypatch);
				}
			}
			
			@Override
			protected void setLinkAnimation(AssetAccessor<? extends StaticAnimation> nextAnimation, LivingEntityPatch<?> entitypatch, Pose lastPose, float convertTimeModifier) {
				Pose currentPose = this.animationPlayer.getAnimation().get().getRawPose(this.animationPlayer.getElapsedTime());
				Pose nextAnimationPose = nextAnimation.get().getRawPose(0.0F);
				float totalTime = nextAnimation.get().getTransitionTime();
				
				AssetAccessor<? extends DynamicAnimation> fromAnimation = this.animationPlayer.isEmpty() ? entitypatch.getClientAnimator().baseLayer.animationPlayer.getAnimation() : this.animationPlayer.getAnimation();
				
				if (fromAnimation instanceof LinkAnimation linkAnimation) {
					fromAnimation = linkAnimation.getFromAnimation();
				}
				
				this.linkAnimation.getAnimationClip().reset();
				this.linkAnimation.setTotalTime(totalTime);
				this.linkAnimation.setConnectedAnimations(fromAnimation, nextAnimation);
				
				Map<String, JointTransform> data1 = currentPose.getJointTransformData();
				Map<String, JointTransform> data2 = nextAnimationPose.getJointTransformData();
				
				for (String jointName : data1.keySet()) {
					if (data1.containsKey(jointName) && data2.containsKey(jointName)) {
						Keyframe[] keyframes = new Keyframe[2];
						keyframes[0] = new Keyframe(0.0F, data1.get(jointName));
						keyframes[1] = new Keyframe(totalTime, data2.get(jointName));
						TransformSheet sheet = new TransformSheet(keyframes);
						this.linkAnimation.getAnimationClip().addJointTransform(jointName, sheet);
					}
				}
				
				this.animationPlayer.setPlayAnimation(this.linkAnimation);
			}
			
			public void offCompositeLayerLowerThan(LivingEntityPatch<?> entitypatch, StaticAnimation nextAnimation) {
				for (Priority p : nextAnimation.getPriority().lowersAndEqual()) {
					if (p == Priority.LOWEST && !nextAnimation.isMainFrameAnimation()) {
						continue;
					}
					
					this.compositeLayers.get(p).off(entitypatch);
				}
			}
			
			public Layer getLayer(Priority priority) {
				return this.compositeLayers.get(priority);
			}
			
			@Override
			public void off(LivingEntityPatch<?> entitypatch) {
			}
			
			@Override
			protected boolean isDisabled() {
				return false;
			}
			
			@Override
			protected boolean isBaseLayer() {
				return true;
			}
		}
	}
	

	
	/** STUB(26.3 port): the preview trail particle depended on the removed particle render API. */
	class CustomTrailParticle {
		protected CustomTrailParticle(Joint joint, AssetAccessor<? extends StaticAnimation> animation, TrailInfo trailInfo) {
		}

		void tick() {
		}

		boolean isAlive() {
			return false;
		}
	}
	
	/*******************************************************************
	 * @ResizableComponent variables                                   *
	 *******************************************************************/
	private int x1;
	private int x2;
	private int y1;
	private int y2;
	private final HorizontalSizing horizontalSizingOption;
	private final VerticalSizing verticalSizingOption;
	
	@Override
	public void setX1(int x1) {
		this.x1 = x1;
	}

	@Override
	public void setX2(int x2) {
		this.x2 = x2;
	}

	@Override
	public void setY1(int y1) {
		this.y1 = y1;
	}

	@Override
	public void setY2(int y2) {
		this.y2 = y2;
	}
	
	@Override
	public int getX1() {
		return this.x1;
	}

	@Override
	public int getX2() {
		return this.x2;
	}

	@Override
	public int getY1() {
		return this.y1;
	}

	@Override
	public int getY2() {
		return this.y2;
	}

	@Override
	public HorizontalSizing getHorizontalSizingOption() {
		return this.horizontalSizingOption;
	}

	@Override
	public VerticalSizing getVerticalSizingOption() {
		return this.verticalSizingOption;
	}

	@Override
	public void _setActive(boolean active) {
	}

	@Override
	public int _getX() {
		return this.getX();
	}

	@Override
	public int _getY() {
		return this.getY();
	}

	@Override
	public int _getWidth() {
		return this.getWidth();
	}

	@Override
	public int _getHeight() {
		return this.getHeight();
	}

	@Override
	public void _setX(int x) {
		this.setX(x);
	}

	@Override
	public void _setY(int y) {
		this.setY(y);
	}

	@Override
	public void _setWidth(int width) {
		this.setWidth(width);
	}

	@Override
	public void _setHeight(int height) {
		this.setHeight(height);
	}

	@Override
	public Component _getMessage() {
		return this.getMessage();
	}

	@Override
	public void _renderWidget(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.extractWidgetRenderState(guiGraphics, mouseX, mouseY, partialTicks);
	}
	
	public void sysoutTranslationInfo() {
		System.out.println("x move: " + this.xMove +" y move: "+ this.yMove +" x rot: "+ this.xRot +" y rot: "+ this.yRot +" zoom: "+ this.zoom);
	}
}
