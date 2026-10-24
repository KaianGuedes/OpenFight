from pathlib import Path
import json
root=Path(__file__).resolve().parents[1];j=root/'src/main/java/dev/war/openfight'
p=j/'client/renderer/patched/layer/WearableItemLayer.java';s=p.read_text(encoding='utf-8').replace('new AnimatedArmorTextureEvent(entity,stack,slot,suffix)', 'new AnimatedArmorTextureEvent(entity,stack,slot,null)');p.write_text(s,encoding='utf-8')
p=j/'client/EpicFightFabricClient.java';s=p.read_text(encoding='utf-8').replace('        EpicFightClient.initialize(', '        PortClientSmoke.register();\n        EpicFightClient.initialize(');p.write_text(s,encoding='utf-8')
p=j/'client/platform/neoforge/event/ClientModBusEvent.java';s=p.read_text(encoding='utf-8').replace('  var particles=ParticleProviderRegistry.getInstance();','''  var hud=dev.war.openfight.client.events.engine.RenderEngine.getInstance().battleModeHUD;
  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry.addLast(dev.war.openfight.main.EpicFightMod.identifier("stamina_bar"),hud::renderStaminaBar);
  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry.addLast(dev.war.openfight.main.EpicFightMod.identifier("skills"),hud::renderNormalSkills);
  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry.addLast(dev.war.openfight.main.EpicFightMod.identifier("weapon_innate"),hud::renderWeaponInnateSkill);
  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry.addLast(dev.war.openfight.main.EpicFightMod.identifier("charging_bar"),hud::renderChargingBar);
  var particles=ParticleProviderRegistry.getInstance();''');p.write_text(s,encoding='utf-8')
p=root/'src/main/resources/epicfight.mixins.json';data=json.loads(p.read_text(encoding='utf-8'));data['client'].append('client.MixinEntityRendererReload');p.write_text(json.dumps(data,indent=2)+'\n',encoding='utf-8')
p=root/'build.gradle';s=p.read_text(encoding='utf-8');s+='''
// Explicit development-only title-screen smoke run, with runtime mixin audit and screenshot.
if (providers.gradleProperty('portSmoke').isPresent()) {
    loom.runs.client { vmArg '-Depicfight.portSmoke=true'; vmArg '-Dmixin.debug.verbose=true' }
}
''';p.write_text(s,encoding='utf-8')
run=root/'run';run.mkdir(exist_ok=True);p=run/'options.txt'
if not p.exists():p.write_text('onboardAccessibility:false\nstartedCleanly:true\nfullscreen:false\n',encoding='utf-8')
print('Opt-in dev smoke probe prepared')
