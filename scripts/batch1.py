import re,sys
from pathlib import Path
sys.path.insert(0,str(Path(__file__).parent))
from jsrc import *
root=Path(__file__).resolve().parents[1]/'src/main/java'
CMP='dev.war.openfight.platform.fabric.compat.'
def instanceof_rewrite(text,cls,fn):
    pat=re.compile(r'\binstanceof\s+'+cls+r'\b(?!\s*\.class)')
    while True:
        m=pat.search(text)
        if not m: break
        # expression before 'instanceof'
        j=m.start()
        while j>0 and text[j-1]==' ': j-=1
        st=expr_before(text,j)
        expr=text[st:j]
        text=text[:st]+fn+'('+expr+')'+text[m.end():]
    return text
def process(t,p):
    o=t
    name=p.name
    if name=='package-info.java':
        t='\n'.join(l for l in t.split('\n') if 'NonnullByDefault' not in l and 'ParametersAreNonnull' not in l)
        return t
    if 'PartEntity' in t:
        t=t.replace('import net.neoforged.neoforge.entity.PartEntity;','import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;')
        t=re.sub(r'PartEntity<\?>','EnderDragonPart',t)
        t=re.sub(r'\bPartEntity\b','EnderDragonPart',t)
        t=re.sub(r'(partEntity|\(\(EnderDragonPart\)entity\))\.getParent\(\)',r'\1.parentMob',t)
    for cls,fn in [('SwordItem','ItemClassifier.isSword'),('DiggerItem','ItemClassifier.isDigger'),('TridentItem','ItemClassifier.isTrident')]:
        if re.search(r'instanceof\s+'+cls,t):
            t=instanceof_rewrite(t,cls,fn)
            t=add_import(t,CMP+'ItemClassifier')
    return t
for p in root.rglob('*.java'):
    s=str(p).replace('\\','/')
    if '/compat/' in s or '/platform/neoforge/' in s: continue
    raw=p.read_bytes().decode('utf-8')
    crlf='\r\n' in raw
    t=raw.replace('\r\n','\n')
    n=process(t,p)
    if n!=t:
        if crlf:n=n.replace('\n','\r\n')
        p.write_bytes(n.encode('utf-8'))
        print('changed',p.name)
