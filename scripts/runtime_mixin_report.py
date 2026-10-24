"""Verify every configured Epic Fight mixin applied in the verbose development-client log."""
from pathlib import Path
import json,re,sys
root=Path(__file__).resolve().parents[1]
log=root/(sys.argv[1] if len(sys.argv)>1 else 'client-smoke.log')
text=log.read_text(encoding='utf-8',errors='replace')
config=json.loads((root/'src/main/resources/epicfight.mixins.json').read_text(encoding='utf-8'))
configured=config['mixins']+config['client']
applied=dict(re.findall(r'Mixing ([\w.$]+) from epicfight\.mixins\.json into ([\w.$]+)',text))
missing=[name for name in configured if name not in applied]
audit='PORT_SMOKE_MIXIN_AUDIT_COMPLETE' in text
stable='PORT_SMOKE_TITLE_SCREEN_STABLE ticks=100' in text
recovery='Caught error loading resourcepacks' in text
lines=[f'Runtime mixin audit: {log.name}',f'Configured: {len(configured)}',f'Applied: {sum(name in applied for name in configured)}',f'Missing: {len(missing)}',f'MixinEnvironment.audit() completed: {audit}',f'Title screen stable for 100 client ticks: {stable}',f'Resource-pack recovery: {recovery}','','This validates loading/transformation, not execution of every gameplay injection.','']
lines += [f'{name}: {applied.get(name,"NOT APPLIED")}' for name in configured]
(root/'MIXIN_RUNTIME_AUDIT.txt').write_text('\n'.join(lines)+'\n',encoding='utf-8')
print('\n'.join(lines[:8]))
if missing or not audit or not stable or recovery:raise SystemExit(1)
