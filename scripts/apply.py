"""Apply a spec of exact-text edits.
Format:
### relative/path/under/dev/war/openfight.java   (or absolute-from-src/main/java if starts with yesman/)
--- old
text (may be multi-line)
+++ new
text
(blank '###'/'---' markers delimit blocks)
Use '--- old*' to replace all occurrences. 'old' must occur exactly once otherwise.
"""
import sys,re
from pathlib import Path
root=Path(__file__).resolve().parents[1]/'src/main/java'
spec=Path(sys.argv[1]).read_text(encoding='utf-8').split('\n')
edits=[];cur=None;mode=None;allflag=False
def flush():
    global cur
    if cur and cur.get('old') is not None: edits.append(cur)
for ln in spec:
    if ln.startswith('### '):
        flush();cur={'file':ln[4:].strip(),'old':None,'new':[]};mode=None;fpath=ln[4:].strip();continue
    if ln.startswith('--- old'):
        if cur and cur.get('old') is not None:
            f=cur['file'];flush();cur={'file':f,'old':None,'new':[]}
        cur['old']=[];cur['all']=ln.startswith('--- old*');mode='old';continue
    if ln.startswith('+++ new'):
        mode='new';continue
    if cur is None: continue
    if mode=='old': cur['old'].append(ln)
    elif mode=='new': cur['new'].append(ln)
flush()
byfile={}
for e in edits: byfile.setdefault(e['file'],[]).append(e)
bad=0
for f,es in byfile.items():
    p=root/f if f.startswith('yesman/') else root/'dev/war/openfight'/f
    if not p.exists():
        print('MISSING',f);bad+=1;continue
    raw=p.read_bytes().decode('utf-8')
    crlf='\r\n' in raw
    t=raw.replace('\r\n','\n')
    for e in es:
        old='\n'.join(e['old']).strip('\n');new='\n'.join(e['new']).strip('\n')
        # tolerate trailing whitespace differences per line
        c=t.count(old)
        if c==0:
            # try whitespace-insensitive line match
            ol=[l.strip() for l in old.split('\n')]
            tl=t.split('\n');found=-1
            for i in range(len(tl)-len(ol)+1):
                if all(tl[i+k].strip()==ol[k] for k in range(len(ol))): found=i;break
            if found<0:
                print('NOT FOUND in',f,':',old[:80].replace('\n','\\n'));bad+=1;continue
            ind=re.match(r'\s*',tl[found])[0]
            nl=new.split('\n')
            tl[found:found+len(ol)]=[ (ind+l if i==0 else l) for i,l in enumerate(nl)]
            t='\n'.join(tl);continue
        if c>1 and not e.get('all'):
            print('AMBIGUOUS',c,'in',f,':',old[:80].replace('\n','\\n'));bad+=1;continue
        t=t.replace(old,new)
    if crlf:t=t.replace('\n','\r\n')
    p.write_bytes(t.encode('utf-8'))
print('done, problems:',bad)
