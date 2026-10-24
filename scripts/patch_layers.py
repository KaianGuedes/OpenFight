import re
from pathlib import Path
root=Path(__file__).resolve().parents[1]/'src/main/java/dev/war/openfight/client/renderer/patched/layer'
def edit(name,f):
    p=root/name
    raw=p.read_bytes().decode('utf-8');crlf='\r\n' in raw;s=raw.replace('\r\n','\n')
    s=f(s)
    if crlf:s=s.replace('\n','\r\n')
    p.write_bytes(s.encode('utf-8'))
def imp(s,*imps):
    for i in imps:
        if 'import '+i+';' not in s: s=s.replace('\nimport ','\nimport '+i+';\nimport ',1)
    return s

def patchedlayer(s):
    s=s.replace('''	@SuppressWarnings("unchecked")
	protected R castLayer(RenderLayer<?, ?> layer) {
		return (R)layer;
	}''','''	@SuppressWarnings("unchecked")
	protected R castLayer(RenderLayer<?, ?> layer) {
		return (R)layer;
	}

	/** Lets a vanilla layer submit its own geometry at the current pose (26.3 layers work from the render state). */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected static void submitVanillaLayer(@Nullable RenderLayer vanillaLayer, PoseStack poseStack, int packedLight, float yRot, float xRot) {
		EntityRenderState state = EpicRenderContext.state();
		SubmitNodeCollector collector = EpicRenderContext.collector();

		if (vanillaLayer != null && state != null && collector != null) {
			vanillaLayer.submit(poseStack, collector, packedLight, state, yRot, xRot);
		}
	}''')
    return imp(s,'net.minecraft.client.renderer.SubmitNodeCollector','net.minecraft.client.renderer.entity.state.EntityRenderState','dev.war.openfight.client.renderer.compat.EpicRenderContext')
edit('PatchedLayer.java',patchedlayer)

def original(s):
    s=s.replace('vanillaLayer.render(poseStack, buffer, packedLight, entityliving, entityliving.walkAnimation.position(), entityliving.walkAnimation.speed(), partialTicks, bob, yRot, xRot);','submitVanillaLayer(vanillaLayer, poseStack, packedLight, yRot, xRot);')
    return s
edit('RenderOriginalModelLayer.java',original)

def arrow(s):
    s=s.replace('ArrowLayer<?, ?>','ArrowLayer<?>')
    a=s.index('		float f = Mth.sqrt(f1 * f1 + f3 * f3);')
    b=s.index('	@Override\n	protected int numStuck')
    s=s[:a]+'''		float f = Mth.sqrt(f1 * f1 + f3 * f3);
		Arrow arrow = new Arrow(entity.level(), entity.getX(), entity.getY(), entity.getZ(), ItemStack.EMPTY, ItemStack.EMPTY);
		arrow.setYRot((float) (Math.atan2((double) f1, (double) f3) * (double) (180F / (float) Math.PI)));
		arrow.setXRot((float) (Math.atan2((double) f2, (double) f) * (double) (180F / (float) Math.PI)));
		arrow.yRotO = arrow.getYRot();
		arrow.xRotO = arrow.getXRot();

		SubmitNodeCollector collector = EpicRenderContext.collector();
		CameraRenderState camera = EpicRenderContext.camera();

		if (collector != null && camera != null) {
			EntityRenderState arrowState = this.dispatcher.extractEntity(arrow, partialTick);
			this.dispatcher.submit(arrowState, camera, 0.0D, 0.0D, 0.0D, poseStack, collector);
		}
	}

'''+s[b:]
    return imp(s,'net.minecraft.client.renderer.SubmitNodeCollector','net.minecraft.client.renderer.entity.state.EntityRenderState','net.minecraft.client.renderer.state.level.CameraRenderState','dev.war.openfight.client.renderer.compat.EpicRenderContext')
edit('PatchedArrowLayer.java',arrow)
edit('PatchedBeeStingerLayer.java',lambda s:s.replace('BeeStingerLayer<?, ?>','BeeStingerLayer<?>'))
edit('PatchedStuckInBodyLayer.java',lambda s:s)

def wings(s):
    a=s.index('		if (vanillaLayer.shouldRender(')
    b=s.index('	}\n}')
    s=s[:a]+'''		net.minecraft.world.item.equipment.Equippable equippable = livingentity.getItemBySlot(EquipmentSlot.CHEST).get(net.minecraft.core.component.DataComponents.EQUIPPABLE);

		if (equippable != null && equippable.assetId().isPresent()) {
			OpenMatrix4f modelMatrix = new OpenMatrix4f();
			modelMatrix.scale(new Vec3f(-0.9F, -0.9F, 0.9F)).translate(new Vec3f(0.0F, -0.5F, -0.1F)).mulFront(poses[8]);
			poseStack.pushPose();
			MathUtils.mulStack(poseStack, modelMatrix);
			submitVanillaLayer(vanillaLayer, poseStack, packedLight, yRot, xRot);
			poseStack.popPose();
		}
'''+s[b:]
    return s
edit('PatchedWingsLayer.java',wings)

def head(s):
    s=s.replace('ModelPart model = vanillaLayer.getParentModel().getHead();','ModelPart model = ((HeadedModel)vanillaLayer.getParentModel()).getHead();')
    s=s.replace('vanillaLayer.render(postStack, buffer, packedLightIn, entity, entity.walkAnimation.position(), entity.walkAnimation.speed(), packedLightIn, entity.tickCount, yRot, xRot);','submitVanillaLayer(vanillaLayer, postStack, packedLightIn, yRot, xRot);')
    return s
edit('PatchedHeadLayer.java',head)

def cape(s):
    s=s.replace('import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;','import net.minecraft.client.gui.screens.inventory.InventoryScreen;')
    s=s.replace('Minecraft.getInstance().screen instanceof EffectRenderingInventoryScreen','Minecraft.getInstance().gui.screen() instanceof InventoryScreen')
    s=s.replace('SkinCompat.capeTexture(entitypatch.getEpicSkinsInformation()).get()','entitypatch.getEpicSkinsInformation().capeTexture().get()')
    s=s.replace('vanillaLayer.render(poseStack, buffer, packedLight, entityliving, entityliving.walkAnimation.position(), entityliving.walkAnimation.speed(), partialTick, entityliving.tickCount, yRot, xRot);','submitVanillaLayer(vanillaLayer, poseStack, packedLight, yRot, xRot);')
    s=s.replace('entityliving.isModelPartShown(PlayerModelPart.CAPE)','entityliving.isModelPartShown(PlayerModelPart.CAPE)')
    s=s.replace('poseStack.mulPose(renderLocalMatrix);','poseStack.last().pose().mul(renderLocalMatrix);')
    s=s.replace('poseStack.last().normal().rotate(QuaternionUtils.YP.rotationDegrees(yBodyRot));','poseStack.last().normal().rotate(QuaternionUtils.YP.rotationDegrees(yBodyRot));')
    s=s.replace('renderSimulatingCape(poseStack, buffer, RenderTypes.entityCutout(capeTexture), Mesh.DrawingFunction.NEW_ENTITY, clothObj, entityX, entityY, entityZ, epicskinsInfo.r(), epicskinsInfo.g(), epicskinsInfo.b(), 1.0F, entitypatch, poses, packedLight, renderLocalPose, bodyYRot);','renderSimulatingCape(poseStack, buffer, RenderTypes.entityCutout(capeTexture), Mesh.DrawingFunction.NEW_ENTITY, clothObj, entityX, entityY, entityZ, epicskinsInfo.r(), epicskinsInfo.g(), epicskinsInfo.b(), 1.0F, entitypatch, poses, packedLight, renderLocalPose, bodyYRot);')
    return s
edit('PatchedCapeLayer.java',cape)

def villager(s):
    # The villager type/profession layer is driven by VillagerMetaDataSection hat data that no longer exists
    a=s.index('		if (!entityliving.isInvisible()) {')
    b=s.rindex('	}\n}')
    s=s[:a]+'''		if (!entityliving.isInvisible()) {
			VillagerData villagerdata = ((VillagerDataHolder)entitypatch.getOriginal()).getVillagerData();

			// 26.3 moved hat visibility into the villager model/state; the vanilla "hat" metadata is not consulted any more.
			if (!entityliving.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
				this.mesh.get().head.setHidden(true);
				this.mesh.get().hat.setHidden(true);
			}

			if (!entitypatch.getOriginal().getItemBySlot(EquipmentSlot.LEGS).isEmpty()) {
				this.mesh.get().jacket.setHidden(true);
			}

			Identifier typeKey = BuiltInRegistries.VILLAGER_TYPE.getKey(villagerdata.type().value());
			this.mesh.get().draw(postStack, buffer, RenderTypes.entityCutout(typeKey.withPath(path -> "textures/entity/zombie_villager/type/" + path + ".png")),
					packedLight, 1.0F, 1.0F, 1.0F, 1.0F, LivingEntityRenderer.getOverlayCoords(dev.war.openfight.client.renderer.compat.EpicRenderContext.state() instanceof net.minecraft.client.renderer.entity.state.LivingEntityRenderState lstate ? lstate : null, 0.0F), entitypatch.getArmature(), poses);

			if (!villagerdata.profession().is(VillagerProfession.NONE)) {
				Identifier professionKey = villagerdata.profession().unwrapKey().orElseThrow().identifier();
				this.mesh.get().draw(postStack, buffer, RenderTypes.entityCutout(professionKey.withPath(path -> "textures/entity/zombie_villager/profession/" + path + ".png")),
						packedLight, 1.0F, 1.0F, 1.0F, 1.0F, LivingEntityRenderer.getOverlayCoords(dev.war.openfight.client.renderer.compat.EpicRenderContext.state() instanceof net.minecraft.client.renderer.entity.state.LivingEntityRenderState lstate ? lstate : null, 0.0F), entitypatch.getArmature(), poses);
			}
		}
'''+s[b:]
    s=s.replace('import net.minecraft.client.resources.metadata.animation.VillagerMetaDataSection;\n','')
    return s
edit('PatchedVillagerProfessionLayer.java',villager)

def golem(s):
    s=s.replace('golementity.getCrackiness()','Crackiness.GOLEM.byFraction(golementity.getHealth() / golementity.getMaxHealth())')
    return s
edit('PatchedGolemCrackLayer.java',golem)

def outer(s):
    s=s.replace('LivingEntityRenderer.getOverlayCoords(entityliving, 0.0F)','OverlayTexture.pack(0.0F, entityliving.hurtTime > 0 || entityliving.deathTime > 0)')
    s=imp(s,'net.minecraft.client.renderer.texture.OverlayTexture')
    return s
edit('OuterLayerRenderer.java',outer)
print('ok')
