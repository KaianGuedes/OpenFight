"""Relocate class descriptors using the actual, unique 26.3 source class names."""
from pathlib import Path
from collections import defaultdict
import re
root=Path(__file__).resolve().parents[1]
mc=root.parent/'mcsrc'
names=defaultdict(list)
for p in mc.rglob('*.java'): names[p.stem].append('/'.join(p.relative_to(mc).with_suffix('').parts))
changed=0
for p in (root/'src/main/java').rglob('*.java'):
    old=s=p.read_text(encoding='utf-8')
    def relocate(match):
        cls=match[1]
        if (mc/(cls+'.java')).exists(): return match[0]
        candidates=names.get(cls.rsplit('/',1)[-1],[])
        return 'L'+candidates[0]+';' if len(candidates)==1 else match[0]
    s=re.sub(r'L((?:net/minecraft|com/mojang)/[\w/$]+);',relocate,s)
    s=s.replace('.writeResourceLocation(','.writeIdentifier(').replace('.readResourceLocation(','.readIdentifier(')
    if p.name=='EpicFightClientBoundPayloadHandler.java': s=s.replace('data.identifier(), data.radius()', 'data.location(), data.radius()')
    if p.name=='ByteBufCodecsExtends.java': s=s.replace('tagKey.identifier()', 'tagKey.location()')
    if p.name=='AdaptiveSkinSkill.java': s=s.replace('tag.identifier()', 'tag.location()')
    if old!=s: p.write_text(s,encoding='utf-8'); changed+=1
print(f'Relocated exact descriptors and corrected keyed identifier access in {changed} files')
