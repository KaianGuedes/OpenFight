"""One-time GUI migration (1.21.1 GuiGraphics -> 26.3 GuiGraphicsExtractor)."""
import re,sys
from pathlib import Path
sys.path.insert(0,str(Path(__file__).parent))
from jsrc import *
root=Path(__file__).resolve().parents[1]/'src/main/java'
GG='GuiGraphicsExtractor'
changed=0
log=[]
def tint_of(a):
    r,g,b,al=a
    return f'ARGB.colorFromFloat({al}, {r}, {g}, {b})'

def fix_blit(args, tint):
    # old: blit(loc, x, y, u, v, w, h, tw, th)  (9) / blit(loc,x,y,w,h,u,v,uw,vh,tw,th) (11) / blit(loc,x,y,u,v,w,h,tw,th)
    pipe='RenderPipelines.GUI_TEXTURED'
    t=(', '+tint) if tint else ''
    if len(args)==9:
        loc,x,y,u,v,w,h,tw,th=args
        return f'blit({pipe}, {loc}, {x}, {y}, (float)({u}), (float)({v}), {w}, {h}, {tw}, {th}{t}'
    if len(args)==11:
        loc,x,y,w,h,u,v,uw,vh,tw,th=args
        return f'blit({pipe}, {loc}, {x}, {y}, (float)({u}), (float)({v}), {w}, {h}, {uw}, {vh}, {tw}, {th}{t}'
    return None

def process(text, path):
    orig=text
    if 'GuiGraphics' not in text and 'getMinecraft()' not in text and '.setScreen(' not in text \
       and 'mouseClicked' not in text and 'keyPressed' not in text and 'mouseDragged' not in text \
       and 'mouseReleased' not in text and 'charTyped' not in text and 'getAsString' not in text:
        return text
    text=re.sub(r'\bGuiGraphics\b',GG,text)
    text=text.replace('net.minecraft.client.gui.GuiGraphicsExtractor','net.minecraft.client.gui.GuiGraphicsExtractor')
    # --- method renames on definitions that carry GuiGraphicsExtractor
    for old,new in [('render','extractRenderState'),('renderWidget','extractWidgetRenderState'),
                    ('renderBackground','extractBackground'),('renderListBackground','extractListBackground'),
                    ('renderListSeparators','extractListSeparators'),('renderListItems','extractListItems'),
                    ('renderHeader','extractHeader')]:
        text=re.sub(r'(\b(?:void|boolean)\s+)'+old+r'(\s*\(\s*(?:@NotNull\s+)?'+GG+r')',r'\1'+new+r'\2',text)
    # calls: x.render(guiGraphics, ...) / super.render(guiGraphics...)
    text=re.sub(r'\.render\(((?:guiGraphics|graphics|pGuiGraphics|gui|guiGraphicsIn)\s*,)',r'.extractRenderState(\1',text)
    text=re.sub(r'\.renderWidget\(((?:guiGraphics|graphics|pGuiGraphics)\s*,)',r'.extractWidgetRenderState(\1',text)
    text=re.sub(r'\brenderBackground\(((?:guiGraphics|graphics|pGuiGraphics)\s*,)',r'extractBackground(\1',text)
    text=re.sub(r'\brenderListBackground\(',r'extractListBackground(',text)
    text=re.sub(r'\brenderListSeparators\(',r'extractListSeparators(',text)
    text=text.replace('.drawString(','.text(').replace('.drawCenteredString(','.centeredText(')
    text=text.replace('.renderItemDecorations(','.itemDecorations(')
    text=re.sub(r'(guiGraphics|graphics)\.renderItem\(',r'\1.item(',text)
    text=re.sub(r'\.pose\(\)\.pushPose\(\)',r'.pose().pushMatrix()',text)
    text=re.sub(r'\.pose\(\)\.popPose\(\)',r'.pose().popMatrix()',text)
    text=re.sub(r'fill\(RenderType\.guiOverlay\(\),\s*',r'fill(',text)
    # fillGradient(RenderType, x0,y0,x1,y1,c1,c2,z)
    def fg(args,m):
        if len(args)>=8 and args[0].startswith('RenderType'): return 'fillGradient('+', '.join(args[1:7])
        if len(args)==7: return 'fillGradient('+', '.join(args[:6])
        return None
    text=rewrite_calls(text,r'fillGradient\(',fg)
    # fill(x0,y0,x1,y1,z,color) 6 args -> drop z? (RenderType removed above gives 6 args w/ z)
    def fl(args,m):
        if len(args)==6 and not args[0].startswith('RenderPipelines'): return 'fill('+', '.join(args[:4]+[args[5]])
        return None
    text=rewrite_calls(text,r'(?<=graphics)\.fill\(|(?<=Graphics)\.fill\(',lambda a,m: ('.fill('+', '.join(a[:4]+[a[5]])) if len(a)==6 else None)
    # pose translate/scale 3 args
    def tr(args,m):
        if len(args)==3: return m.group(0)[:-1]+'translate('+', '.join('(float)('+a+')' for a in args[:2])
        return None
    text=rewrite_calls(text,r'\.pose\(\)\.translate\(',lambda a,m: ('.pose().translate('+', '.join('(float)('+x+')' for x in a[:2])) if len(a)==3 else None)
    text=rewrite_calls(text,r'\.pose\(\)\.scale\(',lambda a,m: ('.pose().scale('+', '.join('(float)('+x+')' for x in a[:2])) if len(a)==3 else None)
    text=rewrite_calls(text,r'\.pose\(\)\.mulPose\(',lambda a,m: ('.pose().rotate((float)Math.toRadians('+re.sub(r'^Axis\.[XYZ]P\.rotationDegrees\((.*)\)$',r'\1',a[0])+')') if re.match(r'^Axis\.ZP\.rotationDegrees\(.*\)$',a[0]) else None)
    # --- setColor tint + blit
    lines=text.split('\n');outl=[];tint=None
    for ln in lines:
        m=re.match(r'^(\s*)(?:\w+)\.setColor\(([^,]+),([^,]+),([^,]+),([^)]+)\);\s*$',ln)
        if m and 'guiGraphics' in ln or (m and 'graphics' in ln):
            vals=[x.strip() for x in m.groups()[1:]]
            if all(re.fullmatch(r'1(\.0+)?F?',v) for v in vals): tint=None
            else: tint=tint_of(vals)
            continue
        if 'blit(' in ln and (tint or True):
            # single-line blit statements
            def bl(args,mm,tint=tint):
                return fix_blit(args,tint)
            ln=rewrite_calls(ln,r'(?<=[gG]raphics)\.blit\(',lambda a,mm,tint=tint: ('.'+r) if (r:=fix_blit(a,tint)) else None)
        outl.append(ln)
    text='\n'.join(outl)
    if 'RenderPipelines.GUI_TEXTURED' in text: text=add_import(text,'net.minecraft.client.renderer.RenderPipelines')
    if 'ARGB.' in text: text=add_import(text,'net.minecraft.util.ARGB')
    # --- screen / minecraft access
    text=re.sub(r'\b(?!Minecraft\b)(\w+)\.getMinecraft\(\)',lambda m:'Minecraft.getInstance()',text)
    text=re.sub(r'\.setScreen\(',r'.gui.setScreen(',text)
    text=text.replace('.gui.gui.setScreen(','.gui.setScreen(')
    text=re.sub(r'\b(minecraft|mc|Minecraft\.getInstance\(\)|this\.minecraft|getInstance\(\))\.screen\b(?!\s*\()',r'\1.gui.screen()',text)
    if 'Minecraft.getInstance()' in text: text=add_import(text,'net.minecraft.client.Minecraft')
    text=re.sub(r'\bthis\.minecraft\s*=\s*[^;]+;\n','',text) if re.search(r'void init\(Minecraft',text) else text
    # --- tags
    # --- event signatures
    def has(sig): return sig in text
    # mouseClicked(double a,double b,int c)
    def sigfix(text,name,pat,newsig,locals_fn):
        rx=re.compile(r'(public|protected)\s+boolean\s+'+name+pat+r'\s*\{')
        out=[];pos=0
        for m in rx.finditer(text):
            g=m.groups()
            out.append(text[pos:m.start()])
            decl=newsig(g)
            out.append(decl+'\n'+locals_fn(g))
            pos=m.end()
        out.append(text[pos:])
        return ''.join(out)
    num=r'\(\s*(?:final\s+)?double\s+(\w+)\s*,\s*(?:final\s+)?double\s+(\w+)\s*,\s*(?:final\s+)?int\s+(\w+)\s*\)'
    text=sigfix(text,'mouseClicked',num,lambda g:f'{g[0]} boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {{',
        lambda g:f'double {g[1]} = event.x(); double {g[2]} = event.y(); int {g[3]} = event.button();')
    text=sigfix(text,'mouseReleased',num,lambda g:f'{g[0]} boolean mouseReleased(MouseButtonEvent event) {{',
        lambda g:f'double {g[1]} = event.x(); double {g[2]} = event.y(); int {g[3]} = event.button();')
    num5=r'\(\s*(?:final\s+)?double\s+(\w+)\s*,\s*(?:final\s+)?double\s+(\w+)\s*,\s*(?:final\s+)?int\s+(\w+)\s*,\s*(?:final\s+)?double\s+(\w+)\s*,\s*(?:final\s+)?double\s+(\w+)\s*\)'
    text=sigfix(text,'mouseDragged',num5,lambda g:f'{g[0]} boolean mouseDragged(MouseButtonEvent event, double {g[4]}, double {g[5]}) {{',
        lambda g:f'double {g[1]} = event.x(); double {g[2]} = event.y(); int {g[3]} = event.button();')
    kp=r'\(\s*(?:final\s+)?int\s+(\w+)\s*,\s*(?:final\s+)?int\s+(\w+)\s*,\s*(?:final\s+)?int\s+(\w+)\s*\)'
    text=sigfix(text,'keyPressed',kp,lambda g:f'{g[0]} boolean keyPressed(KeyEvent event) {{',
        lambda g:f'int {g[1]} = event.key(); int {g[2]} = event.keycode(); int {g[3]} = event.modifiers();')
    text=sigfix(text,'keyReleased',kp,lambda g:f'{g[0]} boolean keyReleased(KeyEvent event) {{',
        lambda g:f'int {g[1]} = event.key(); int {g[2]} = event.keycode(); int {g[3]} = event.modifiers();')
    ct=r'\(\s*(?:final\s+)?char\s+(\w+)\s*,\s*(?:final\s+)?int\s+(\w+)\s*\)'
    text=sigfix(text,'charTyped',ct,lambda g:f'{g[0]} boolean charTyped(CharacterEvent event) {{',
        lambda g:f'char {g[1]} = (char)event.codepoint(); int {g[2]} = 0;')
    # void onClick(double,double) / onRelease / onDrag
    def sigvoid(text,name,pat,newsig,loc):
        rx=re.compile(r'(public|protected)\s+void\s+'+name+pat+r'\s*\{')
        out=[];pos=0
        for m in rx.finditer(text):
            g=m.groups();out.append(text[pos:m.start()]);out.append(newsig(g)+'\n'+loc(g));pos=m.end()
        out.append(text[pos:]);return ''.join(out)
    d2=r'\(\s*(?:final\s+)?double\s+(\w+)\s*,\s*(?:final\s+)?double\s+(\w+)\s*\)'
    text=sigvoid(text,'onClick',d2,lambda g:f'{g[0]} void onClick(MouseButtonEvent event, boolean doubleClick) {{',lambda g:f'double {g[1]} = event.x(); double {g[2]} = event.y();')
    text=sigvoid(text,'onRelease',d2,lambda g:f'{g[0]} void onRelease(MouseButtonEvent event) {{',lambda g:f'double {g[1]} = event.x(); double {g[2]} = event.y();')
    d4=r'\(\s*(?:final\s+)?double\s+(\w+)\s*,\s*(?:final\s+)?double\s+(\w+)\s*,\s*(?:final\s+)?double\s+(\w+)\s*,\s*(?:final\s+)?double\s+(\w+)\s*\)'
    text=sigvoid(text,'onDrag',d4,lambda g:f'{g[0]} void onDrag(MouseButtonEvent event, double {g[3]}, double {g[4]}) {{',lambda g:f'double {g[1]} = event.x(); double {g[2]} = event.y();')
    # super.mouseX(a,b,c) calls -> pass through event
    text=re.sub(r'super\.mouseClicked\(\s*\w+\s*,\s*\w+\s*,\s*\w+\s*\)','super.mouseClicked(event, doubleClick)',text)
    text=re.sub(r'super\.mouseReleased\(\s*\w+\s*,\s*\w+\s*,\s*\w+\s*\)','super.mouseReleased(event)',text)
    text=re.sub(r'super\.mouseDragged\(\s*\w+\s*,\s*\w+\s*,\s*\w+\s*,\s*(\w+)\s*,\s*(\w+)\s*\)',r'super.mouseDragged(event, \1, \2)',text)
    text=re.sub(r'super\.keyPressed\(\s*\w+\s*,\s*\w+\s*,\s*\w+\s*\)','super.keyPressed(event)',text)
    text=re.sub(r'super\.keyReleased\(\s*\w+\s*,\s*\w+\s*,\s*\w+\s*\)','super.keyReleased(event)',text)
    text=re.sub(r'super\.charTyped\(\s*\w+\s*,\s*\w+\s*\)','super.charTyped(event)',text)
    text=re.sub(r'super\.onClick\(\s*\w+\s*,\s*\w+\s*\)','super.onClick(event, doubleClick)',text)
    text=re.sub(r'super\.onRelease\(\s*\w+\s*,\s*\w+\s*\)','super.onRelease(event)',text)
    text=re.sub(r'super\.onDrag\(\s*\w+\s*,\s*\w+\s*,\s*(\w+)\s*,\s*(\w+)\s*\)',r'super.onDrag(event, \1, \2)',text)
    for cls,imp in [('MouseButtonEvent','net.minecraft.client.input.MouseButtonEvent'),('KeyEvent','net.minecraft.client.input.KeyEvent'),('CharacterEvent','net.minecraft.client.input.CharacterEvent')]:
        if re.search(r'\b'+cls+r'\b',text): text=add_import(text,imp)
    return text

for p in root.rglob('*.java'):
    s=str(p).replace('\\','/')
    if '/compat/' in s or '/platform/neoforge/' in s: continue
    raw=p.read_bytes().decode('utf-8')
    crlf='\r\n' in raw
    t=raw.replace('\r\n','\n')
    n=process(t,p)
    if n!=t:
        if crlf: n=n.replace('\n','\r\n')
        p.write_bytes(n.encode('utf-8'));changed+=1
print('changed',changed)
