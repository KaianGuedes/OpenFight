"""Run the isolated development client through automatic world creation and a combat smoke."""
import argparse,os,subprocess,sys,time
from pathlib import Path
parser=argparse.ArgumentParser()
parser.add_argument('--timeout',type=int,default=360)
parser.add_argument('--log',default='gameplay-smoke.log')
parser.add_argument('--respawn',action='store_true',help='after combat, kill and respawn the player and assert movement input still moves them (-PsmokeRespawn)')
parser.add_argument('--shoulder-surfing',action='store_true',help='load Shoulder Surfing Reloaded in the dev client (-PwithShoulderSurfing) and assert the compat plugin registered')
args=parser.parse_args()
root=Path(__file__).resolve().parents[1]
env=dict(os.environ)
if not env.get('JAVA_HOME'):
    fallback = root.parent/'ef-port'/'tools'/'jdk-25.0.4.1+1'
    if fallback.is_dir():
        env['JAVA_HOME'] = str(fallback)
options_path = root/'run'/'options.txt'
options_path.parent.mkdir(parents=True, exist_ok=True)
if options_path.exists():
    lines = options_path.read_text(encoding='utf-8').splitlines()
else:
    lines = []
for key, value in {'onboardAccessibility': 'false', 'pauseOnLostFocus': 'false', 'fullscreen': 'false', 'startedCleanly': 'true'}.items():
    prefix = key + ':'
    for index, line in enumerate(lines):
        if line.startswith(prefix):
            lines[index] = prefix + value
            break
    else:
        lines.append(prefix + value)
options_path.write_text('\n'.join(lines) + '\n', encoding='utf-8')
log=root/args.log
started_at=time.time()
with log.open('wb') as output:
 process=subprocess.Popen([str(root/'gradlew.bat'),'--no-daemon','--console=plain','runClient','-PportSmoke']+(['-PsmokeRespawn'] if args.respawn else [])+(['-PwithShoulderSurfing'] if args.shoulder_surfing else []),cwd=root,env=env,stdout=output,stderr=subprocess.STDOUT)
 try:code=process.wait(timeout=args.timeout)
 except subprocess.TimeoutExpired:
  subprocess.run(['taskkill','/PID',str(process.pid),'/T','/F'],stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL)
  print(f'Dev client deadline reached after {args.timeout}s; stopped only launch PID {process.pid} and its children.')
  sys.exit(124)
text=log.read_text(encoding='utf-8',errors='replace')
markers=['PORT_SMOKE_MIXIN_AUDIT_COMPLETE','PORT_SMOKE_CLICK screen=TitleScreen','PORT_SMOKE_CLICK screen=CreateWorldScreen','PORT_SMOKE_PLAYER_JOINED','PORT_SMOKE_SKILL_DATA_REGISTERED key=epicfight:combo_counter connection=ready','PORT_SMOKE_COMBAT_STARTED','PORT_SMOKE_WORLD_COMBAT_COMPLETE ticks=600']
if args.respawn: markers+=['PORT_SMOKE_MOVE_BASELINE','PORT_SMOKE_PLAYER_KILLED','PORT_SMOKE_PLAYER_RESPAWNED','PORT_SMOKE_RESPAWN_PATCH_REFRESHED','PORT_SMOKE_RESPAWN_MOVEMENT_OK']
if args.shoulder_surfing: markers+=['Epic Fight TPS mode has been disabled due to a mod conflict with Shoulder Surfing']
missing=[marker for marker in markers if marker not in text]
runtime_log=root/'run/logs/latest.log'
runtime_text=runtime_log.read_text(encoding='utf-8',errors='replace') if runtime_log.exists() else ''
for forbidden in ("Can't find weapon type",'Components not bound yet','Error while deserializing datapack','PORT_SMOKE_FAILED','Caught error loading resourcepacks','Reloading failed','Exception in thread','crash report has been saved'):
 if forbidden.lower() in text.lower() or forbidden.lower() in runtime_text.lower(): missing.append(f'forbidden log text: {forbidden}')
known_environment_errors=(
 'HkeyPerformanceDataUtil',
 "Registry 'epicfight:weapon_data' was empty after loading",  # upstream exposes this optional extension registry with no built-ins
 'Failed to fetch user properties',                         # Loom's unauthenticated dev profile receives HTTP 401
 'Failed to fetch Realms feature flags',
 "Couldn't connect to realms",
)
error_lines=[line for line in (text+'\n'+runtime_text).splitlines() if (' ERROR ' in line or '/ERROR]' in line or ' ERROR]' in line) and not any(known in line for known in known_environment_errors)]
if error_lines: missing.append(f'ERROR log entries: {error_lines[:12]}')
crash_dir=root/'run/crash-reports'
if crash_dir.exists() and any(path.stat().st_mtime >= started_at for path in crash_dir.glob('crash-*.txt')): missing.append('new crash report exists in isolated run directory')
print(text[-3500:])
if runtime_text:
 print('Runtime latest.log tail:')
 print(runtime_text[-2500:])
print('Gameplay smoke: '+('PASS' if code==0 and not missing else 'FAIL')+'; missing markers/errors='+str(missing))
sys.exit(code if code else bool(missing))
