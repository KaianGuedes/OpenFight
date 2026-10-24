"""Preserve tracked files' original newline conventions before reviewing a migration chunk."""
from pathlib import Path
import subprocess
root=Path(__file__).resolve().parents[1]
for name in subprocess.check_output(['git','diff','--name-only'],cwd=root,text=True).splitlines():
 p=root/name
 if not p.exists() or p.suffix not in ['.java','.md','.gradle']:continue
 old=subprocess.check_output(['git','show','HEAD:'+name],cwd=root)
 data=p.read_bytes().replace(b'\r\n',b'\n')
 if b'\r\n' in old:data=data.replace(b'\n',b'\r\n')
 p.write_bytes(data)
