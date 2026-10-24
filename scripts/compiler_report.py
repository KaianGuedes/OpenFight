from pathlib import Path
from collections import Counter
import json
import re
import sys
root=Path(__file__).resolve().parents[1]
log=root/(sys.argv[1] if len(sys.argv)>1 else 'latest-build.log')
text=log.read_text(encoding='utf-8',errors='replace').split('> Task :compileJava FAILED')[0]
pattern=re.compile(r'([^\r\n]+\.java):(\d+): error: ([^\n]+)\n')
matches=list(pattern.finditer(text))
rows=[]
seen=set()
for i,match in enumerate(matches):
    key=(match[1],match[2],match[3])
    if key in seen: continue
    seen.add(key)
    path=Path(match[1])
    try: path=path.relative_to(root)
    except ValueError: pass
    block=text[match.end():matches[i+1].start() if i+1<len(matches) else len(text)]
    rows.append({'file':str(path).replace('\\','/'),'line':int(match[2]),'error':match[3],'detail':block.strip()[:1600]})
report=root/'build/reports'
report.mkdir(parents=True,exist_ok=True)
(report/'compile-errors.json').write_text(json.dumps(rows,indent=2),encoding='utf-8')
files=Counter(row['file'] for row in rows)
categories=Counter('/'.join(row['file'].split('/')[5:8]) for row in rows)
lines=[f'# Compile backlog: {len(rows)} diagnostics',f'\nLog: {log.name}\n','The full combat/client tree is compiled. Optional integrations, the NeoForge platform bridges and datagen are excluded explicitly.\n','## Highest-error source files\n']
lines+=['| Errors | Source |','| ---: | --- |']
lines+=[f'| {count} | {name} |' for name,count in files.most_common(30)]
lines+=['\n## API error families\n','| Count | Diagnostic |','| ---: | --- |']
lines+=[f'| {count} | {error} |' for error,count in Counter(row['error'] for row in rows).most_common(20)]
(report/'compile-backlog.md').write_text('\n'.join(lines)+'\n',encoding='utf-8')
(root/'COMPILE_BACKLOG.md').write_text('\n'.join(lines)+'\n',encoding='utf-8')
notes=root/'PORT_NOTES.md'
status=notes.read_text(encoding='utf-8')
status=re.sub(r'\*\*about [\d,]+ compiler errors \(deduplicated\)\*\*',f'**about {len(rows):,} compiler errors (deduplicated)**',status,count=1)
notes.write_text(status,encoding='utf-8')
print(f'{len(rows)} compiler errors across {len(files)} files; machine-readable and ranked reports written')
