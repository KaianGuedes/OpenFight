from pathlib import Path
p=Path(__file__).resolve().parents[1]/'src/main/resources/epicfight.accesswidener'
lines=[
'accessible field net/minecraft/client/renderer/item/ItemStackRenderState activeLayerCount I',
'accessible field net/minecraft/client/renderer/item/ItemStackRenderState layers [Lnet/minecraft/client/renderer/item/ItemStackRenderState$LayerRenderState;',
'accessible field net/minecraft/client/renderer/item/ItemStackRenderState$LayerRenderState quads Lnet/minecraft/client/resources/model/geometry/ItemQuads;',
'accessible field net/minecraft/client/renderer/item/ItemStackRenderState$LayerRenderState tintLayers Lit/unimi/dsi/fastutil/ints/IntList;',
'accessible method net/minecraft/client/renderer/item/ItemStackRenderState$LayerRenderState applyTransform (Lcom/mojang/blaze3d/vertex/PoseStack$Pose;)V',
'accessible field net/minecraft/client/renderer/item/properties/numeric/RangeSelectItemModelProperties ID_MAPPER Lnet/minecraft/util/ExtraCodecs$LateBoundIdMapper;']
s=p.read_text(encoding='utf-8')
for line in lines:
 if line not in s:s+='\n'+line+'\n'
p.write_text(s,encoding='utf-8')
