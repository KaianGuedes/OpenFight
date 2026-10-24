import re,sys
# usage: ctx.py <path-substring> [radius] [msg-substring]
sub=sys.argv[1].replace('/','\\')
rad=int(sys.argv[2]) if len(sys.argv)>2 else 2
msgsub=sys.argv[3] if len(sys.argv)>3 else ''
hdr=re.compile(r'^\s*(C:.*?\.java):(\d+): error: (.*)$')
lines=open('latest-build.log',encoding='utf-8',errors='replace').read().splitlines()
seen={}
for i,ln in enumerate(lines):
    m=hdr.match(ln)
    if not m: continue
    if sub not in m[1].replace('/','\\'): continue
    if msgsub not in m[3]: continue
    sym=''
    for k in range(i+1,min(i+8,len(lines))):
        s=lines[k].strip()
        if s.startswith('symbol:'): sym=s[7:].strip()
        if s.startswith('location:'): sym+=' @ '+s[9:].strip(); break
    key=(m[1],int(m[2]))
    if key in seen:
        seen[key].append((m[3],sym)); continue
    seen[key]=[(m[3],sym)]
cache={}
cur=None
for (f,l),errs in sorted(seen.items()):
    if f not in cache: cache[f]=open(f,encoding='utf-8',errors='replace').read().splitlines()
    src=cache[f]
    name=f.split('\\')[-1]
    print('==',name,l,'|',' ; '.join((e[0][:60]+((' ['+e[1]+']') if e[1] else '')) for e in errs[:2])[:240])
    for k in range(max(0,l-1-rad),min(len(src),l+rad)):
        print(('>' if k==l-1 else ' '),src[k][:220])
