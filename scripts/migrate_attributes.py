from pathlib import Path
import re
root=Path(__file__).resolve().parents[1]
for p in (root/'src/main/java').rglob('*.java'):
    old=s=p.read_text(encoding='utf-8')
    for cls in ['EntityAttributeCreationEvent','EntityAttributeModificationEvent']:
        s=s.replace('net.neoforged.neoforge.event.entity.'+cls,'dev.war.openfight.platform.fabric.event.'+cls)
    if p.name=='EpicFightAttributes.java':
        s=re.sub(r'import net\.neoforged\.[^;]+;\n','',s)
        s=re.sub(r'@EventBusSubscriber\([^\n]+\)\s*','',s)
        s=s.replace('@SubscribeEvent','')
    if p.name=='WitherGhostClone.java':
        s=s.replace('import net.minecraft.world.entity.FlyingMob;\n','')
        s=s.replace('extends FlyingMob','extends Mob').replace('EntityType<? extends FlyingMob>','EntityType<? extends Mob>')
        s=s.replace('public boolean hurt(DamageSource damagesource, float damage)', 'public boolean hurtServer(ServerLevel level, DamageSource damagesource, float damage)')
        s=s.replace('super.hurt(damagesource, damage)','super.hurtServer(level, damagesource, damage)')
        s=s.replace('public void customServerAiStep()', 'public void customServerAiStep(ServerLevel level)')
        pos=s.index('\n\tpublic static AttributeSupplier.Builder createAttributes()')
        s=s[:pos]+'''\n    @Override public void travel(Vec3 input) { this.travelFlying(input,0.02F); }
    @Override public boolean onClimbable() { return false; }
'''+s[pos:]
    if old!=s: p.write_text(s,encoding='utf-8')
print('Migrated custom/vanilla attribute supplier hooks and the flying ghost base/damage API')
