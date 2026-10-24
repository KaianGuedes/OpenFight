"""Preserve legacy NBT missing-key defaults in the new Optional-based API."""
from pathlib import Path
import re
root=Path(__file__).resolve().parents[1]
count=0
for p in (root/'src/main/java').rglob('*.java'):
    old=s=p.read_text(encoding='utf-8')
    nbt_names=set(re.findall(r'\b(?:CompoundTag|ListTag)\s+(\w+)\b',s))
    # Work only on receivers declared as NBT containers; buffers and other getInt/getString APIs stay intact.
    for name in nbt_names:
        receiver=r'\b'+re.escape(name)+r'\.'
        for method,default in [('Int','0'),('Long','0L'),('Float','0.0F'),('Double','0.0D'),('Short','(short)0'),('Byte','(byte)0'),('Boolean','false'),('String','""')]:
            s=re.sub(receiver+r'get'+method+r'\(([^()\n]*(?:\([^()\n]*\)[^()\n]*)?)\)',lambda m:m.group(0)[:m.group(0).index('get')]+f'get{method}Or('+m.group(1)+', '+default+')',s)
        s=re.sub(receiver+r'getCompound\(',name+'.getCompoundOrEmpty(',s)
        s=re.sub(receiver+r'getList\(([^()\n]*),\s*(?:Tag\.TAG_\w+|\d+)\)',name+r'.getListOrEmpty(\1)',s)
        # ListTag getList takes a numeric index, rather than a compound name/type pair.
        if re.search(r'\bListTag\s+'+re.escape(name)+r'\b',s):
            s=re.sub(receiver+r'getList\(',name+'.getListOrEmpty(',s)
    s=s.replace('.getAllKeys()', '.keySet()')
    if old!=s:
        p.write_text(s,encoding='utf-8'); count+=1
print(f'Ported NBT missing-key defaults in {count} files')
