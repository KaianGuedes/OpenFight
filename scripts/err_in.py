import re,sys
from collections import Counter
# usage: err_in.py <path-substring> [N]
sub=sys.argv[1].replace('/','\\')
hdr=re.compile(r'^\s*C:.*?src[\\/]main[\\/]java[\\/](.*)\.java:(\d+): error: (.*)$')
lines=open('latest-build.log',encoding='utf-8',errors='replace').read().splitlines()
c=Counter();cur=None;recs=[]
for ln in lines:
    m=hdr.match(ln)
    if m:
        cur={'f':m[1],'l':m[2],'msg':m[3],'sym':None,'loc':None};recs.append(cur);continue
    if cur:
        s=ln.strip()
        if s.startswith('symbol:'):cur['sym']=s[7:].strip()
        elif s.startswith('location:'):cur['loc']=s[9:].strip()
seen=set()
for r in recs:
    if sub not in r['f'].replace('/','\\'):continue
    k=(r['f'],r['l'],r['msg'],r['sym'])
    if k in seen:continue
    seen.add(k)
    key=r['msg'][:120]
    if r['msg'].startswith('cannot find symbol'): key=(r['sym'] or '?')+' @ '+(r['loc'] or '?')
    c[key]+=1
n=int(sys.argv[2]) if len(sys.argv)>2 else 60
for k,v in c.most_common(n):print(v,k)
