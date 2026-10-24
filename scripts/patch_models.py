import re,sys
from pathlib import Path
root=Path(__file__).resolve().parents[1]/'src/main/java/yesman/epicfight'
NONGENERIC=['CreeperModel','DrownedModel','HoglinModel','IronGolemModel','PlayerModel','SpiderModel','WitchModel','WitherBossModel']
STATEGENERIC=['EndermanModel','HumanoidModel','IllagerModel','ZombieVillagerModel']
dirs=['client/renderer','client/mesh','client/gui','api/client','client/world','world/capabilities','epicskins','compat/_none_']
changed=0
for d in dirs:
    base=root/d
    if not base.exists(): continue
    for p in base.rglob('*.java'):
        raw=p.read_bytes().decode('utf-8');crlf='\r\n' in raw;s=raw.replace('\r\n','\n');o=s
        for m in NONGENERIC:
            s=re.sub(r'\b'+m+r'<[^<>]*>',m,s)
        for m in STATEGENERIC:
            s=re.sub(r'\b'+m+r'<(?!\?>)(?!\w+RenderState>)[A-Z]\w*(?:<[^<>]*>)?>',m+'<?>',s)
        s=s.replace('ElytraLayer','WingsLayer')
        s=s.replace('context.getModelManager()','context')
        if s!=o:
            if crlf:s=s.replace('\n','\r\n')
            p.write_bytes(s.encode('utf-8'));changed+=1
print('changed',changed)
