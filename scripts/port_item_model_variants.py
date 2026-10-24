"""Generate native display-context variants from the original NeoForge separate-transforms data."""
from pathlib import Path
import json
root=Path(__file__).resolve().parents[1]
assets=root/'src/main/resources/assets/epicfight'
count=0
for source in (assets/'models/item').glob('*.json'):
 original=json.loads(source.read_text(encoding='utf-8'))
 if original.get('loader')!='neoforge:separate_transforms':continue
 variants={'base':original['base'],**original.get('perspectives',{})}
 models={}
 for context,data in variants.items():
  target=assets/'models/item/fabric_port'/f'{source.stem}_{context}.json'
  target.parent.mkdir(parents=True,exist_ok=True)
  target.write_text(json.dumps(data,indent=2)+'\n',encoding='utf-8')
  models[context]={'type':'minecraft:model','model':f'epicfight:item/fabric_port/{source.stem}_{context}'}
 definition={'model':{'type':'minecraft:select','property':'minecraft:display_context','cases':[{'when':context,'model':value} for context,value in models.items() if context!='base'],'fallback':models['base']}}
 (assets/'items'/source.name).write_text(json.dumps(definition,indent=2)+'\n',encoding='utf-8')
 count+=1
print(f'Generated native context-specific models for {count} original items; source reference models preserved')
