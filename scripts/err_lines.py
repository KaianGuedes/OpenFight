import re,sys
from collections import Counter
# usage: err_lines.py <msg-substring> [path-substring]  -> prints source line of each unique error + next line(s)
msgsub=sys.argv[1]
sub=(sys.argv[2] if len(sys.argv)>2 else '').replace('/','\\')
hdr=re.compile(r'^\s*(C:.*?src[\\/]main[\\/]java[\\/](.*)\.java):(\d+): error: (.*)$')
lines=open('latest-build.log',encoding='utf-8',errors='replace').read().splitlines()
seen=set();c=Counter()
out=[]
for i,ln in enumerate(lines):
    m=hdr.match(ln)
    if not m:continue
    if msgsub not in m[4]:continue
    if sub not in m[2].replace('/','\\'):continue
    k=(m[2],m[3])
    if k in seen:continue
    seen.add(k)
    src=lines[i+1].strip() if i+1<len(lines) else ''
    out.append((m[2].split('/')[-1].split('\\')[-1],m[3],m[4][:60],src))
    c[src]+=1
mode=sys.argv[3] if len(sys.argv)>3 else 'count'
if mode=='count':
    for k,v in c.most_common(100):print(v,k)
else:
    for o in out:print(*o,sep=' | ')
