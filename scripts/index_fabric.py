"""Index all classes in Fabric API nested jars -> build/fabric-classes.txt, extract jars to build/fabric-jars."""
from pathlib import Path
import io,json,zipfile
root=Path(__file__).resolve().parents[1]
base=Path.home()/'.gradle/caches/modules-2/files-2.1/net.fabricmc.fabric-api/fabric-api/0.162.0+26.3'
jar=next(p for p in base.rglob('*.jar') if not p.name.endswith('-sources.jar'))
out=root/'build/fabric-jars';out.mkdir(parents=True,exist_ok=True)
lines=[]
with zipfile.ZipFile(jar) as z:
    for entry in json.loads(z.read('fabric.mod.json'))['jars']:
        name=Path(entry['file']).name
        data=z.read(entry['file'])
        (out/name).write_bytes(data)
        with zipfile.ZipFile(io.BytesIO(data)) as m:
            for n in m.namelist():
                if n.endswith('.class') and '/api/' in n and 'impl' not in n: lines.append(f'{name}\t{n[:-6].replace("/",".")}')
(root/'build/fabric-classes.txt').write_text('\n'.join(sorted(lines)),encoding='utf-8')
print(len(lines))
