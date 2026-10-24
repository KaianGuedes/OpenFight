from pathlib import Path
from collections import defaultdict
import json
import re
root=Path(__file__).resolve().parents[1]
mc=root.parent/'mcsrc'
names=defaultdict(list)
for p in mc.rglob('*.java'): names[p.stem].append('.'.join(p.relative_to(mc).with_suffix('').parts))
result=[]
s=(root/'platform-build.log').read_text(encoding='utf-8').split('> Task :compileJava FAILED')[0]
for member,cls in re.findall(r'error: (\w+) has (?:private|protected) access in ([\w.]+)',s):
    base=cls.split('.')[0]
    candidates=names.get(base,[])
    if len(candidates)==1:
        owner=candidates[0]+cls[len(base):].replace('.','$')
        entry={'owner':owner,'member':member}
        if entry not in result: result.append(entry)
(root/'access-candidates.json').write_text(json.dumps(result,indent=2),encoding='utf-8')
print(f'{len(result)} distinct field/method access candidates')
