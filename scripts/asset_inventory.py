from pathlib import Path
import hashlib
import json
root=Path(__file__).resolve().parents[1]
original=root.parent/'upstream/src/main/resources/assets/epicfight'
copy=root/'src/main/resources/assets/epicfight'
output=root/'build/resources/main/assets/epicfight'
preserved=[]; missing=[]; changed=[]
for p in original.rglob('*'):
    if not p.is_file(): continue
    relative=p.relative_to(original)
    # Language formatting and added category aliases are intentional. All other original assets must stay intact.
    if relative.parts[0]=='lang': continue
    sourcehash=hashlib.sha256(p.read_bytes()).hexdigest()
    dest=copy/relative
    compiled=output/relative
    if not dest.is_file() or not compiled.is_file(): missing.append(str(relative)); continue
    if sourcehash!=hashlib.sha256(dest.read_bytes()).hexdigest() or sourcehash!=hashlib.sha256(compiled.read_bytes()).hexdigest():
        changed.append(str(relative))
    preserved.append({'path':str(relative).replace('\\','/'),'sha256':sourcehash})
report={'upstream_commit':'a78aa24b72e90a9d09f5fd61925e4369d117abaf','preserved_assets':len(preserved),'missing':missing,'unexpected_changes':changed,'assets':preserved}
(root/'ASSET_INVENTORY.json').write_text(json.dumps(report,indent=2),encoding='utf-8')
print(f'{len(preserved)} original non-language assets verified in source and processed output; missing={len(missing)}, changed={len(changed)}')
if missing or changed: raise SystemExit(1)
