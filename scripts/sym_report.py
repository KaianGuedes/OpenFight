import re,sys
from collections import Counter
lines=open('latest-build.log',encoding='utf-8',errors='replace').read().splitlines()
hdr=re.compile(r'^\s*C:.*\.java:(\d+): error: (.*)$')
c=Counter();cur=None
def flush():
    pass
recs=[]
for ln in lines:
    m=hdr.match(ln)
    if m: cur={'msg':m[2],'sym':None,'loc':None};recs.append(cur);continue
    if cur:
        s=ln.strip()
        if s.startswith('symbol:'): cur['sym']=s[7:].strip()
        elif s.startswith('location:'): cur['loc']=s[9:].strip()
for r in recs:
    if r['msg'].startswith('cannot find symbol'):
        c[(r['sym'] or '?')+'  @ '+(r['loc'] or '?')]+=1
    else: c[r['msg'][:110]]+=1
n=int(sys.argv[1]) if len(sys.argv)>1 else 80
print(len(recs))
for k,v in c.most_common(n): print(v,k)
