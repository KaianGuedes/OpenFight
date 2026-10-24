import re,sys
from collections import Counter
hdr=re.compile(r'^\s*C:.*?src[\\/]main[\\/]java[\\/](.*)\.java:(\d+): error: (.*)$')
c=Counter();seen=set()
depth=int(sys.argv[1]) if len(sys.argv)>1 else 6
for ln in open('latest-build.log',encoding='utf-8',errors='replace'):
    m=hdr.match(ln.rstrip('\n'))
    if m:
        k=(m[1],m[2],m[3])
        if k in seen: continue
        seen.add(k)
        p=re.split(r'[\\/]',m[1])
        c['/'.join(p[:depth])]+=1
print(sum(c.values()))
for k,v in c.most_common(500): print(v,k)
