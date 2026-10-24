from pathlib import Path
root=Path(__file__).resolve().parents[1];j=root/'src/main/java/yesman/epicfight'
def edit(f,fn):
 p=j/f;s=p.read_text(encoding='utf-8');p.write_text(fn(s),encoding='utf-8')
edit('client/world/capabilites/entitypatch/player/LocalPlayerPatch.java',lambda s:s.replace('combatItem.getItem().canAttackBlock(bs,', 'combatItem.canDestroyBlock(bs,'))
edit('api/client/camera/EpicFightCameraAPI.java',lambda s:s.replace('cameraLocation.x(), cameraLocation.y(), cameraLocation.z())', 'cameraLocation.x(), cameraLocation.y(), cameraLocation.z(), 1.0F)'))
edit('client/renderer/patched/entity/PVindicatorRenderer.java',lambda s:s.replace('RenderLayer<net.minecraft.client.renderer.entity.state.IllagerRenderState, IllagerModel<net.minecraft.client.renderer.entity.state.IllagerRenderState>> originalRenderer', 'RenderLayer<?, ?> originalRenderer'))
edit('mixin/client/MixinMinecraft.java',lambda s:s.replace('Direction.getNearest(cameraPos.x, cameraPos.y, cameraPos.z)', 'Direction.getApproximateNearest(cameraPos.x, cameraPos.y, cameraPos.z)'))
edit('mixin/common/MixinLivingEntity.java',lambda s:s.replace('Operation<Void> originalOperation', 'Operation<Float> originalOperation'))
edit('client/renderer/patched/layer/WearableItemLayer.java',lambda s:s.replace('private final EquipmentAssetManager equipmentAssets;', '''private final EquipmentAssetManager equipmentAssets;
    private static EquipmentAssetManager snapshotAssets;
    public static Identifier getArmorResource(LivingEntity entity,ItemStack stack,EquipmentSlot slot,String suffix) {
        var event=EpicFightClientEventHooks.Render.ANIMATED_ARMOR_TEXTURE.post(new AnimatedArmorTextureEvent(entity,stack,slot,suffix));
        if(event.getResultLocation()!=null)return event.getResultLocation();
        var equippable=stack.get(net.minecraft.core.component.DataComponents.EQUIPPABLE);
        if(equippable==null || equippable.assetId().isEmpty() || snapshotAssets==null)return null;
        var type=slot==EquipmentSlot.LEGS?EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS:EquipmentClientInfo.LayerType.HUMANOID;
        var layers=snapshotAssets.get(equippable.assetId().get()).getLayers(type);
        return layers.isEmpty()?null:layers.getFirst().getTextureLocation(type);
    }''').replace('this.equipmentAssets = context.getEquipmentAssets();', 'this.equipmentAssets = context.getEquipmentAssets();\n        snapshotAssets=this.equipmentAssets;'))
p=root/'src/main/resources/epicfight.accesswidener';s=p.read_text(encoding='utf-8');line='accessible method net/minecraft/world/entity/projectile/arrow/AbstractArrow isInGround ()Z'
if line not in s:p.write_text(s+'\n'+line+'\n',encoding='utf-8')
print('Six remaining compiler diagnostics addressed')
