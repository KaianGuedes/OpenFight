from pathlib import Path
import json
root=Path(__file__).resolve().parents[1]
def ingredient(value):
    if isinstance(value,list): return [ingredient(v) for v in value]
    if isinstance(value,dict) and set(value)=={'item'}: return value['item']
    if isinstance(value,dict) and set(value)=={'tag'}: return '#'+value['tag']
    return value
count=0
for base in [root/'src/main/resources',root/'src/generated/resources']:
    for p in base.glob('data/*/recipe/*.json'):
        data=json.loads(p.read_text(encoding='utf-8'))
        old=json.dumps(data)
        if 'key' in data: data['key']={key:ingredient(value) for key,value in data['key'].items()}
        for key in ['ingredient','ingredients','base','addition','template']:
            if key in data: data[key]=ingredient(data[key])
        if old!=json.dumps(data):
            p.write_text(json.dumps(data,indent=2)+'\n',encoding='utf-8'); count+=1
resources=root/'src/main/resources'
pack=json.loads((resources/'pack.mcmeta').read_text(encoding='utf-8'))
pack['pack']['max_format']=[121,0] # One mod pack supplies both client resources 97.1 and server data 121.0.
(resources/'pack.mcmeta').write_text(json.dumps(pack,indent=2)+'\n',encoding='utf-8')
path=resources/'data/epicfight/tags/block/wither_skeleton_immune_to.json'
path.parent.mkdir(parents=True,exist_ok=True)
path.write_text(json.dumps({'values':['minecraft:wither_rose']},indent=2)+'\n',encoding='utf-8')
print(f'Migrated {count} recipe ingredients; root pack supports resource 97.1 and data 121.0')
