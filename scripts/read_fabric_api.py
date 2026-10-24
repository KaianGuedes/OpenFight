"""Read the exact API bundled in this project's Fabric dependency, not a remembered older API."""
from pathlib import Path
import io
import json
import subprocess
import sys
import zipfile
root=Path(__file__).resolve().parents[1]
base=Path.home()/'.gradle/caches/modules-2/files-2.1/net.fabricmc.fabric-api/fabric-api/0.162.0+26.3'
jar=next(p for p in base.rglob('*.jar') if not p.name.endswith('-sources.jar'))
classes=sys.argv[1:]
with zipfile.ZipFile(jar) as z:
    entries=[entry['file'] for entry in json.loads(z.read('fabric.mod.json'))['jars']]
    for entry in entries:
        data=z.read(entry)
        with zipfile.ZipFile(io.BytesIO(data)) as module:
            names=[cls for cls in classes if cls.replace('.','/')+'.class' in module.namelist()]
        if names:
            dest=root/'build/api-reference'/Path(entry).name
            dest.parent.mkdir(parents=True,exist_ok=True)
            dest.write_bytes(data)
            output=subprocess.check_output([str(root.parent/'tools/jdk-25.0.4.1+1/bin/javap.exe'),'-classpath',str(dest),*names])
            print(output.decode(encoding='utf-8',errors='replace'))
