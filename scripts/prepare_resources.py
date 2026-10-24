from pathlib import Path
import json
import shutil
root=Path(__file__).resolve().parents[1]
resources=root/'src/main/resources'
assets=resources/'assets/epicfight'
def write(path,obj):
    path.parent.mkdir(parents=True,exist_ok=True)
    path.write_text(json.dumps(obj,indent=2,ensure_ascii=False)+'\n',encoding='utf-8')

models=[]
for base in [resources,root/'src/generated/resources']:
    folder=base/'assets/epicfight/models/item'
    for p in folder.glob('*.json'):
        path=assets/'items'/p.name
        if not path.exists():
            write(path,{'model':{'type':'minecraft:model','model':'epicfight:item/'+p.stem}})
        models.append(p.stem)
for base in [resources,root/'src/generated/resources']:
    data=base/'data'
    for ns in data.iterdir() if data.exists() else []:
        if not ns.is_dir(): continue
        for old,new in [('recipes','recipe'),('loot_tables','loot_table'),('advancements','advancement')]:
            source=ns/old
            if source.exists(): shutil.copytree(source,ns/new,dirs_exist_ok=True)
        for old,new in [('items','item'),('blocks','block'),('entity_types','entity_type'),('damage_types','damage_type')]:
            source=ns/'tags'/old
            if source.exists(): shutil.copytree(source,ns/'tags'/new,dirs_exist_ok=True)
write(resources/'data/epicfight/tags/item/stray_cloth_repair_materials.json',{'values':['minecraft:string']})
for category,layer in [('humanoid',1),('humanoid_leggings',2)]:
    dest=assets/f'textures/entity/equipment/{category}/stray_cloth.png'
    dest.parent.mkdir(parents=True,exist_ok=True)
    shutil.copyfile(assets/f'textures/models/armor/stray_cloth_layer_{layer}.png',dest)
write(assets/'equipment/stray_cloth.json',{'layers':{'humanoid':[{'texture':'epicfight:stray_cloth'}],'humanoid_leggings':[{'texture':'epicfight:stray_cloth'}]}})
for lang in (assets/'lang').glob('*.json'):
    data=json.loads(lang.read_text(encoding='utf-8'))
    for category in ['combat','gui','system','camera']:
        old='key.epicfight.'+category
        if old in data: data['key.category.epicfight.'+category]=data[old]
    write(lang,data)
for p in resources.rglob('pack.mcmeta'):
    data=json.loads(p.read_text(encoding='utf-8'))
    data['pack']={'description':data['pack'].get('description','Epic Fight personal resources'),'min_format':[97,1],'max_format':[97,1]}
    write(p,data)
write(resources/'pack.mcmeta',{'pack':{'description':'Epic Fight personal-use Fabric 26.3 port','min_format':[97,1],'max_format':[97,1]}})
mixins=resources/'epicfight.mixins.json'
data=json.loads(mixins.read_text(encoding='utf-8'))
data['required']=True
data['compatibilityLevel']='JAVA_25'
data.pop('refmap',None)
for name in ['common.fabric.EntityConstructionMixin','common.fabric.EntityTickMixin']:
    if name not in data['mixins']: data['mixins'].append(name)
write(mixins,data)
print(f'Prepared {len(set(models))} modern item model declarations, singular data paths, armor equipment and pack metadata')
