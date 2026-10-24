from pathlib import Path
import re
root=Path(__file__).resolve().parents[1]/'src/main/java/dev/war/openfight'
def edit(f,fn):
 p=root/f;s=p.read_text(encoding='utf-8');p.write_text(fn(s),encoding='utf-8')
def method(s,needle,new):
 a=s.index(needle);b=s.index('{',a);d=1;e=b+1
 while d:d+=(s[e]=='{')-(s[e]=='}');e+=1
 return s[:a]+new+s[e:]
edit('client/renderer/compat/SubmitBufferSource.java',lambda s:s.replace('\t@Override\n\tpublic VertexConsumer getBuffer', '\tpublic SubmitNodeCollector collector() { return this.collector; }\n\n\t@Override\n\tpublic VertexConsumer getBuffer'))
for f in ['client/renderer/patched/item/RenderItemBase.java','api/ex_cap/data/modifier/RenderModifier.java']:
 edit(f,lambda s:s.replace('import net.minecraft.client.renderer.ItemInHandRenderer;', '').replace('import net.minecraft.client.renderer.entity.ItemRenderer;', 'import dev.war.openfight.client.renderer.compat.ItemRenderer;').replace('ItemInHandRenderer', 'ItemRenderer').replace('minecraft.getItemRenderer()', 'new ItemRenderer()').replace('minecraft.gameRenderer.itemInHandRenderer', 'new ItemRenderer()'))
edit('client/renderer/patched/item/RenderTrident.java',lambda s:s.replace('mc.gameRenderer.itemInHandRenderer.renderItem', 'itemInHandRenderer.renderItem'))
edit('client/renderer/patched/item/RenderShield.java',lambda s:re.sub(r'(?m)^.*(?:import net.minecraft.client.resources.model.BakedModel;|BakedModel model =).*\n','',s).replace('itemRenderer.render(itemstack, transformType, !(hand == InteractionHand.MAIN_HAND), poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, model)', 'itemInHandRenderer.renderItem(entitypatch.getOriginal(), itemstack, transformType, hand == InteractionHand.OFF_HAND, poseStack, buffer, packedLight)'))
edit('client/renderer/FirstPersonRenderer.java',lambda s:s.replace('LivingEntityRenderer<LocalPlayer, PlayerModel>', 'LivingEntityRenderer<LocalPlayer, net.minecraft.client.renderer.entity.state.AvatarRenderState, PlayerModel>').replace('RenderLayer<LocalPlayer, PlayerModel>', 'RenderLayer<net.minecraft.client.renderer.entity.state.AvatarRenderState, PlayerModel>').replace('entity.getSkin().texture()', 'SkinCompat.texture(entity.getSkin())').replace('((MixinLivingEntityRenderer)renderer).invokeGetBob(entity, partialTicks)', '(entity.tickCount + partialTicks)').replace('@Override\n\tprotected void renderLayer(', 'protected void renderLayer('))
edit('client/renderer/patched/entity/PVindicatorRenderer.java',lambda s:s.replace('RenderLayer<AbstractIllager, IllagerModel<?>>', 'RenderLayer<net.minecraft.client.renderer.entity.state.IllagerRenderState, IllagerModel<net.minecraft.client.renderer.entity.state.IllagerRenderState>>'))
# BakedQuad now exposes positions and packed UVs directly; no Forge vertex-array offsets.
edit('api/client/model/Mesh.java',lambda s:method(s,'static void putBulkDataWithDrawingFunction(','''static void putBulkDataWithDrawingFunction(DrawingFunction drawingFunction, VertexConsumer builder, PoseStack.Pose pose, BakedQuad quad, float[] shade, float r, float g, float b, float alpha, int[] lights, int overlay, boolean mulColor) {
            var n=quad.direction().getUnitVec3i();
            Vector3f normal=pose.normal().transform(new Vector3f(n.getX(), n.getY(), n.getZ()));
            for(int i=0;i<4;i++) {
                Vector3f position=new Vector3f(quad.position(i)).mulPosition(pose.pose());
                long uv=quad.packedUV(i);
                drawingFunction.draw(builder,position.x,position.y,position.z,normal.x,normal.y,normal.z,lights[i],r*shade[i],g*shade[i],b*shade[i],alpha,Float.intBitsToFloat((int)uv),Float.intBitsToFloat((int)(uv>>>32)),overlay);
            }
        }''').replace('import net.neoforged.neoforge.client.model.IQuadTransformer;', '').replace('IQuadTransformer.UV2 * 4', '24'))
for f in ['api/client/model/SkinnedMesh.java','client/online/texture/RemoteTexture.java']:
 edit(f,lambda s:s.replace('RenderSystem.recordRenderCall(', 'net.minecraft.client.Minecraft.getInstance().execute('))
edit('client/online/texture/RemoteTexture.java',lambda s:method(s,'private void upload(','''private void upload(NativeImage image) {
        this.apply(new net.minecraft.client.renderer.texture.TextureContents(image,null));
    }''').replace('public void load(ResourceManager pResourceManager) throws IOException {', 'public net.minecraft.client.renderer.texture.TextureContents loadContents(ResourceManager pResourceManager) throws IOException {').replace('\n\t}\n\t\n\t@Nullable\n\tprivate NativeImage load', '\n        return net.minecraft.client.renderer.texture.TextureContents.createMissing();\n\t}\n\t\n\t@Nullable\n\tprivate NativeImage load'))
edit('client/online/RemoteAssets.java',lambda s:s.replace('TEXTURE_MANAGER.getTexture(textureLocation, MissingTextureAtlasSprite.getTexture())', 'TEXTURE_MANAGER.getTexture(textureLocation)').replace('texture == MissingTextureAtlasSprite.getTexture()', '!(texture instanceof RemoteTexture)').replace('TEXTURE_MANAGER.register(textureLocation, httptexture)', 'TEXTURE_MANAGER.registerAndLoad(textureLocation, (RemoteTexture)httptexture)'))
for f in ['client/online/EpicSkins.java','epicskins/client/screen/AvatarEditScreen.java']:
 edit(f,lambda s:re.sub(r'([\w.]+(?:\(\))?(?:\.[\w]+\(\))*\.getSkin\(\)).capeTexture\(\)',r'dev.war.openfight.platform.fabric.compat.SkinCompat.capeTexture(\1)',s))
edit('epicskins/client/screen/AvatarEditScreen.java',lambda s:s.replace('this.updateScrollingState(pMouseX, pMouseY, pButton)', 'this.updateScrolling(event)').replace('b.mouseClicked(pMouseX, pMouseY, pButton)', 'b.mouseClicked(event, doubleClick)').replace('CosmeticsEntry oEntry = this.getFocused()', 'CosmeticsEntry oEntry = (CosmeticsEntry)this.getFocused()'))
edit('client/gui/compat/LegacySelectionList.java',lambda s:s.replace('protected final int defaultEntryHeight;', 'protected int defaultEntryHeight;'))
edit('client/EpicFightFabricClient.java',lambda s:s.replace('ControlEngine.registerFabricEvents()', 'ControlEngine.getInstance().registerFabricCallbacks()').replace('RenderEngine.registerFabricEvents()', 'RenderEngine.getInstance().registerFabricCallbacks()'))
edit('client/events/engine/RenderEngine.java',lambda s:s.replace('this.versionNotifier.render(graphics, true)', 'this.versionNotifier.extractRenderState(graphics, true)'))
edit('client/world/util/FakeLevel.java',lambda s:s.replace('.getHolderOrThrow(BuiltinDimensionTypes.OVERWORLD)', '.getOrThrow(BuiltinDimensionTypes.OVERWORLD)').replace('\t\t\tminecraft::getProfiler,\n', '').replace('minecraft.levelRenderer,', 'minecraft.levelExtractor,').replace('\t\t\t0\n', '\t\t\t0,\n            refLevel.getSeaLevel()\n').replace('    @Override\n    public void disconnect()', '    public void disconnect()'))
edit('api/client/physics/cloth/ClothSimulator.java',lambda s:s.replace('Minecraft.getInstance().getTimer()', 'Minecraft.getInstance().getDeltaTracker()').replace('Minecraft.getInstance().renderBuffers().bufferSource()', 'new dev.war.openfight.client.renderer.compat.SubmitBufferSource(dev.war.openfight.client.renderer.compat.EpicRenderContext.collector())'))
print('Render and skin API continuation applied')
