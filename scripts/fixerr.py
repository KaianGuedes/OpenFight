"""Error-driven line fixer. Parses latest-build.log and applies per-line rewrites.
usage: fixerr.py [dry]
"""
import re,sys
from pathlib import Path
from collections import defaultdict
sys.path.insert(0,str(Path(__file__).parent))
from jsrc import *
root=Path(__file__).resolve().parents[1]
hdr=re.compile(r'^\s*(C:.*?\.java):(\d+): error: (.*)$')
recs=[];cur=None
for ln in open(root/'latest-build.log',encoding='utf-8',errors='replace').read().splitlines():
    m=hdr.match(ln)
    if m:
        cur={'f':m[1],'l':int(m[2]),'msg':m[3],'sym':'','loc':''};recs.append(cur);continue
    if cur:
        s=ln.strip()
        if s.startswith('symbol:'):cur['sym']=s[7:].strip()
        elif s.startswith('location:'):cur['loc']=s[9:].strip()

byfile=defaultdict(lambda: defaultdict(list))
for r in recs: byfile[r['f']][r['l']].append(r)


# (sym-regex, loc-regex or None, [(pattern, replacement)], imports)
RULES=[
 (r'^method capeTexture\(\)', 'PlayerSkin', [(None, lambda l: wrap_call(l,'.capeTexture()','SkinCompat.capeTexture(%s)'))], ['yesman.epicfight.platform.fabric.compat.SkinCompat']),
 (r'^method texture\(\)', 'PlayerSkin', [(None, lambda l: wrap_call(l,'.texture()','SkinCompat.texture(%s)'))], ['yesman.epicfight.platform.fabric.compat.SkinCompat']),
 (r'^method elytraTexture\(\)', 'PlayerSkin', [(None, lambda l: wrap_call(l,'.elytraTexture()','SkinCompat.elytraTexture(%s)'))], ['yesman.epicfight.platform.fabric.compat.SkinCompat']),
 (r'^method setInvulnerable\(', None, [(r'\.setInvulnerable\(', '.setPermanentlyInvulnerable(')], []),
 (r'^method serverLevel\(\)', None, [(r'\.serverLevel\(\)', '.level()')], []),
 (r'^method getLocation\(\)', 'SoundEvent', [(r'\.getLocation\(\)', '.location()')], []),
 (r'^variable swinging', None, [(r'\.swinging\b', '.isSwinging()')], []),
 (r'^method blocksMotion\(\)', None, [(r'\.blocksMotion\(\)', '.isSolid()')], []),
 (r'^variable hurtMarked', None, [(r'\.hurtMarked\b', '.syncVelocity')], []),
 (r'^variable hasImpulse', None, [(r'\.hasImpulse\b', '.needsSync')], []),
 (r'^method moveTo\(', None, [(r'(?<!getNavigation\(\))\.moveTo\(', '.snapTo(')], []),
 (r'^method getParts\(\)', None, [(r'\.getParts\(\)', '.getSubEntities()')], []),
 (r'^method getEntity\(\)', 'Camera', [(r'\.getEntity\(\)', '.entity()')], []),
 (r'^method getPosition\(\)', 'Camera', [(r'\.getPosition\(\)', '.position()')], []),
 (r'^method getLookVector\(\)', 'Camera', [(r'\.getLookVector\(\)', '.forwardVector()')], []),
 (r'^method getUpVector\(\)', 'Camera', [(r'\.getUpVector\(\)', '.upVector()')], []),
 (r'^method getLeftVector\(\)', 'Camera', [(r'\.getLeftVector\(\)', '.leftVector()')], []),
 (r'^method getXRot\(\)', 'Camera', [(r'\.getXRot\(\)', '.xRot()')], []),
 (r'^method getYRot\(\)', 'Camera', [(r'\.getYRot\(\)', '.yRot()')], []),
 (r'^method getAttributeModifiers\(\)', 'ItemStack', [(r'\.getAttributeModifiers\(\)\.forEach\(', '.forEachModifier(')], []),
]


def wrap_call(line, suffix, template, imports=None):
    while suffix in line:
        k=line.index(suffix)
        st=expr_before(line,k)
        line=line[:st]+template%line[st:k]+line[k+len(suffix):]
    return line

TAG_NAMES={'COMPOUND':'CompoundTag','LIST':'ListTag','STRING':'StringTag'}

def line_rules(line, errs, ctx):
    new=line
    for e in errs:
        msg=e['msg'];sym=e['sym'];loc=e['loc']
        if msg.startswith('cannot assign a value to final variable') and re.search(r'^\s*this\.(minecraft|font)\s*=',new):
            return None  # delete line
        if msg.startswith('cannot find symbol'):
            if sym.startswith('variable itemHeight'):
                new=re.sub(r'\bitemHeight\b','defaultEntryHeight',new)
            if sym.startswith('method getScrollAmount'):
                new=new.replace('getScrollAmount()','scrollAmount()')
            if sym.startswith('method getMaxScroll'):
                new=new.replace('getMaxScroll()','maxScrollAmount()')
            if sym.startswith('method getScrollbarPosition'):
                new=new.replace('getScrollbarPosition()','scrollBarX()')
            if sym.startswith('method getAsString()') and ('Tag' in loc):
                while '.getAsString()' in new:
                    k=new.index('.getAsString()')
                    st=expr_before(new,k)
                    new=new[:st]+'TagCompat.getAsString('+new[st:k]+')'+new[k+len('.getAsString()'):]
                ctx['imports'].add('yesman.epicfight.platform.fabric.compat.TagCompat')
            if sym.startswith('method parseTag('):
                new=new.replace('TagParser.parseTag(','TagParser.parseCompoundFully(')
        if msg.startswith('no suitable method found for text(') :
            def tx(a,m):
                if len(a)>=5:
                    a=list(a);a[2]='(int)('+a[2]+')';a[3]='(int)('+a[3]+')'
                    return '.text('+', '.join(a)
            new=rewrite_calls(new,r'\.text\(',tx)
        if msg.startswith('no suitable method found for blitSprite(Identifier,int,int,int,int'):
            new=re.sub(r'\.blitSprite\(','.blitSprite(RenderPipelines.GUI_TEXTURED, ',new)
            ctx['imports'].add('net.minecraft.client.renderer.RenderPipelines')
        if msg.startswith('no suitable method found for blit(Identifier,int,int,int'):
            def bl(a,m):
                if len(a)==7:
                    return '.blit(RenderPipelines.GUI_TEXTURED, %s, %s, %s, (float)(%s), (float)(%s), %s, %s, 256, 256'%tuple(a)
                if len(a)==10:
                    return '.blit(RenderPipelines.GUI_TEXTURED, %s, %s, %s, (float)(%s), (float)(%s), %s, %s, %s, %s'%(a[0],a[1],a[2],a[4],a[5],a[6],a[7],a[8],a[9])
            new=rewrite_calls(new,r'\.blit\(',bl)
            ctx['imports'].add('net.minecraft.client.renderer.RenderPipelines')
        if msg.startswith('no suitable method found for init(Minecraft,int,int)'):
            new=re.sub(r'\.init\(\s*[\w.]+\s*,\s*',r'.init(',new)
        if 'method contains in class CompoundTag cannot be applied' in msg:
            new=re.sub(r'([\w.]+(?:\([^()]*\))?)\.contains\(([^,()]+(?:\([^()]*\))?),\s*(?:Tag\.)?(TAG_\w+|\d+)\)',
                       lambda m:'TagCompat.contains('+m[1]+', '+m[2]+', '+('Tag.'+m[3] if m[3].startswith('TAG_') else m[3])+')',new)
            ctx['imports'].add('yesman.epicfight.platform.fabric.compat.TagCompat')
            ctx['imports'].add('net.minecraft.nbt.Tag')
    for e in errs:
        if e['msg'].startswith('cannot find symbol'):
            for (sr,lr,pairs,imps) in RULES:
                if re.search(sr,e['sym']) and (lr is None or re.search(lr,e['loc'])):
                    for (pa,rp) in pairs:
                        if callable(rp): new=rp(new)
                        else: new=re.sub(pa,rp,new)
                    for im in imps: ctx['imports'].add(im)
    new=new.replace('Tag::getAsString','TagCompat::getAsString')
    if 'TagCompat::' in new: ctx['imports'].add('yesman.epicfight.platform.fabric.compat.TagCompat')
    return new

dry=len(sys.argv)>1 and sys.argv[1]=='dry'
changed=0
for f,lines in byfile.items():
    p=Path(f)
    if not p.exists(): continue
    raw=p.read_bytes().decode('utf-8')
    crlf='\r\n' in raw
    src=raw.replace('\r\n','\n').split('\n')
    ctx={'imports':set()}
    mod=False
    for ln,errs in lines.items():
        i=ln-1
        if i>=len(src): continue
        res=line_rules(src[i],errs,ctx)
        if res is None:
            src[i]='\0DELETE';mod=True
        elif res!=src[i]:
            src[i]=res;mod=True
    if not mod and not ctx['imports']: continue
    text='\n'.join(x for x in src if x!='\0DELETE')
    for imp in ctx['imports']: text=add_import(text,imp)
    if crlf: text=text.replace('\n','\r\n')
    if not dry: p.write_bytes(text.encode('utf-8'))
    changed+=1
print('files changed',changed)
