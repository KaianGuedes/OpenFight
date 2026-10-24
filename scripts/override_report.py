import re,sys,os
from collections import Counter
hdr=re.compile(r'^\s*(C:.*?\.java):(\d+): error: method does not override.*$')
c=Counter();files={}
seen=set()
for ln in open('latest-build.log',encoding='utf-8',errors='replace'):
    m=hdr.match(ln.rstrip('\n'))
    if m:
        p,l=m[1],int(m[2])
        if (p,l) in seen: continue
        seen.add((p,l))
        if p not in files: files[p]=open(p,encoding='utf-8').read().splitlines()
        src=files[p][l:l+3]
        s=next((x.strip() for x in src if not x.strip().startswith('@')),'')
        mm=re.search(r'(\w+)\s*\(',s)
        c[(mm[1] if mm else s)+' | '+s[:100]]+=1
for k,v in c.most_common(100):print(v,k)
