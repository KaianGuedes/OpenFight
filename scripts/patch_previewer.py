import re,sys
from pathlib import Path
root=Path(__file__).resolve().parents[1]/'src/main/java/dev/war/openfight/client/gui'

def block_end(s,start):
    i=s.index('{',start);d=0
    while True:
        c=s[i]
        if c=='{':d+=1
        elif c=='}':
            d-=1
            if d==0:return i+1
        i+=1

def replace_block(s,sig_regex,new):
    m=re.search(sig_regex,s)
    if not m:
        print('MISSING',sig_regex);return s
    e=block_end(s,m.start())
    return s[:m.start()]+new+s[e:]

def patch(rel):
    p=root/rel
    raw=p.read_bytes().decode('utf-8');crlf='\r\n' in raw;s=raw.replace('\r\n','\n')
    # render-to-texture is not portable (RenderTarget/RenderSystem state are gone): stub
    s=replace_block(s,r'\tprotected void renderFigure\(','''\t// STUB(26.3 port): the 3D preview used an offscreen RenderTarget with direct RenderSystem/BufferUploader draws,
\t// which no longer exist. The preview is reduced to a flat background; animation/camera state is still simulated.
\tprotected void renderFigure(GuiGraphicsExtractor guiGraphics) {
\t}''')
    s=replace_block(s,r'\t@Override\n\tpublic void extractWidgetRenderState\(','''\t@Override
\tpublic void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
\t\tVec4f clear = this.backgroundClearColor;
\t\tint argb = clear == null ? 0xFF282828 : ((int)(clear.w * 255.0F) << 24 | (int)(clear.x * 255.0F) << 16 | (int)(clear.y * 255.0F) << 8 | (int)(clear.z * 255.0F));
\t\tguiGraphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), argb);
\t\tthis.renderFigure(guiGraphics);

\t\tif (this.animator != null) {
\t\t\t// Controls visibilities of each widget
\t\t\tint top = this.getY() + 6;
\t\t\tint right = this.getX() + this.getWidth() - 2;

\t\t\tif (!this.trailInfoList.isEmpty()) {
\t\t\t\tright -= this.showTrailCheckbox.getWidth();
\t\t\t\tthis.showTrailCheckbox.setX(right);
\t\t\t\tthis.showTrailCheckbox.setY(top);
\t\t\t\tthis.showTrailCheckbox.extractWidgetRenderState(guiGraphics, mouseX, mouseY, partialTicks);
\t\t\t}

\t\t\tif (this.item != null) {
\t\t\t\tright -= this.showItemCheckbox.getWidth();
\t\t\t\tthis.showItemCheckbox.setX(right);
\t\t\t\tthis.showItemCheckbox.setY(top);
\t\t\t\tthis.showItemCheckbox.extractWidgetRenderState(guiGraphics, mouseX, mouseY, partialTicks);
\t\t\t}

\t\t\tif (this.collider != null) {
\t\t\t\tright -= this.showColliderCheckbox.getWidth();
\t\t\t\tthis.showColliderCheckbox.setX(right);
\t\t\t\tthis.showColliderCheckbox.setY(top);
\t\t\t\tthis.showColliderCheckbox.extractWidgetRenderState(guiGraphics, mouseX, mouseY, partialTicks);
\t\t\t}
\t\t}
\t}''')
    s=replace_block(s,r'\tclass ModelRenderTarget extends RenderTarget',"")
    s=replace_block(s,r'\tclass CustomTrailParticle extends AnimationTrailParticle','''\t/** STUB(26.3 port): the preview trail particle depended on the removed particle render API. */
\tclass CustomTrailParticle {
\t\tprotected CustomTrailParticle(Joint joint, AssetAccessor<? extends StaticAnimation> animation, TrailInfo trailInfo) {
\t\t}

\t\tvoid tick() {
\t\t}

\t\tboolean isAlive() {
\t\t\treturn false;
\t\t}
\t}''')
    s=s.replace('\tprivate final ModelRenderTarget modelRenderTarget;\n','')
    s=re.sub(r'\t\tthis\.modelRenderTarget = new ModelRenderTarget\(\);\n.*?this\.modelRenderTarget\.clear\(Minecraft\.ON_OSX\);\n','',s,flags=re.S)
    s=re.sub(r'\n[ \t]*this\.modelRenderTarget\.setClearColor\(clearColor[^\n]*\n','\n',s)
    s=re.sub(r'\n[ \t]*this\.modelRenderTarget\.resize\([^\n]*\n','\n',s)
    s=s.replace('\t\tthis.modelRenderTarget.destroyBuffers();\n','')
    # mouse events
    s=s.replace('this.isValidClickButton(button)','event.button() == 0 || event.button() == 1 || event.button() == 2')
    s=s.replace('this.clicked(x, y)','this.isMouseOver(x, y)')
    s=re.sub(r'(show\w+Checkbox)\.mouseClicked\(x, y, button\)',r'\1.mouseClicked(event, doubleClick)',s)
    s=s.replace('this.playDownSound(Minecraft.getInstance().getSoundManager());','')
    # unused imports that no longer exist
    for imp in ['com.mojang.blaze3d.pipeline.RenderTarget','net.minecraft.client.renderer.ShaderInstance','net.minecraft.client.renderer.entity.ItemRenderer','net.minecraft.client.resources.model.BakedModel','net.minecraft.client.renderer.MultiBufferSource.BufferSource','com.mojang.blaze3d.platform.Window','com.mojang.blaze3d.systems.RenderSystem']:
        s=s.replace('import '+imp+';\n','')
    if crlf:s=s.replace('\n','\r\n')
    p.write_bytes(s.encode('utf-8'))
for r in sys.argv[1:]:patch(r)
print('ok')
