"""Produce a source-level target inventory without requiring the unfinished mod to compile."""
from pathlib import Path
import json
import re
root=Path(__file__).resolve().parents[1]
src=root/'src/main/java'
mc=root.parent/'mcsrc'
def class_path(fqn):
    parts=fqn.split('.')
    for end in range(len(parts),0,-1):
        base='/'.join(parts[:end])
        if (mc/(base+'.java')).exists():
            return base+('$'+'$'.join(parts[end:]) if end<len(parts) else '')
    return fqn.replace('.','/')
config=json.loads((root/'src/main/resources/epicfight.mixins.json').read_text(encoding='utf-8'))
def comments_out(s):
    return re.sub(r'"(?:\\.|[^"\\])*"|//[^\n]*|/\*.*?\*/',lambda m:' '*len(m[0]) if m[0].startswith(('/','/*')) else m[0],s,flags=re.S)
def annotation(s,start):
    i=s.index('(',start)
    depth=1; j=i+1; quoted=False
    while j<len(s) and depth:
        c=s[j]
        if c=='\\' and quoted: j+=2; continue
        if c=='"': quoted=not quoted
        if not quoted:
            if c=='(': depth+=1
            if c==')': depth-=1
        j+=1
    return s[i+1:j-1]
records=[]
for short in config.get('mixins',[])+config.get('client',[]):
    name=config['package']+'.'+short
    path=src/(name.replace('.','/')+'.java')
    if not path.exists(): records.append({'name':name,'targets':[],'error':'Missing mixin source'}); continue
    text=comments_out(path.read_text(encoding='utf-8'))
    imports={x.rsplit('.',1)[-1]:x for x in re.findall(r'import ([\w.]+);',text)}
    targets=[]
    match=re.search(r'@Mixin\s*\(',text)
    if match:
        args=annotation(text,match.start())
        for target in re.findall(r'([\w.]+)\.class',args):
            first=target.split('.')[0]
            if first in imports: target=imports[first]+target[len(first):]
            else:
                wildcards=re.findall(r'import ([\w.]+)\.\*;',text)
                candidates=[wildcard+'.'+target for wildcard in wildcards if (mc/((wildcard+'.'+target).replace('.','/')+'.java')).exists()]
                if len(candidates)==1: target=candidates[0]
            targets.append(class_path(target))
        if 'targets' in args:
            targets.extend(target.replace('.','/') for target in re.findall(r'"([\w.$]+)"',args))
    injections=[]
    for match in re.finditer(r'@(Inject|Redirect|ModifyArg|ModifyArgs|ModifyVariable|WrapOperation|ModifyExpressionValue|ModifyReturnValue)\s*\(',text):
        args=annotation(text,match.start())
        method=re.search(r'method\s*=\s*(\{[^}]*\}|"(?:\\.|[^"\\])*")',args)
        selectors=re.findall(r'"([^"\n]+)"',method[1]) if method else []
        invoke_targets=re.findall(r'target\s*=\s*"(L[^"\n]+)"',args)
        injections.append({'kind':match[1],'selectors':selectors,'sites':invoke_targets})
    records.append({'name':name,'targets':targets,'injections':injections})
(root/'mixin-targets.json').write_text(json.dumps(records,indent=2),encoding='utf-8')
print(f'Inventoried {len(records)} configured mixin sources')
