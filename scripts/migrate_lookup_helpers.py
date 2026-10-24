from pathlib import Path
import re
root=Path(__file__).resolve().parents[1]
def rewrite(s,method,factory):
    cursor=0
    marker='.'+method+'('
    while True:
        pos=s.find(marker,cursor)
        if pos<0: break
        start=pos-1; depth=0
        while start>=0:
            c=s[start]
            if c==')': depth+=1
            elif c=='(':
                if depth==0: break
                depth-=1
            elif depth==0 and not (c.isalnum() or c in '._$'): break
            start-=1
        start+=1
        receiver=s[start:pos]
        if not receiver or receiver.endswith('EnchantmentHelper'):
            cursor=pos+len(marker); continue
        end=pos+len(marker); argstart=end; depth=1; quoted=False
        while end<len(s) and depth:
            c=s[end]
            if c=='\\' and quoted: end+=2; continue
            if c=='"': quoted=not quoted
            if not quoted:
                if c=='(': depth+=1
                if c==')': depth-=1
            end+=1
        if depth: break
        value=factory(receiver,s[argstart:end-1])
        s=s[:start]+value+s[end:]
        cursor=start+len(value)
    return s
count=0
for p in (root/'src/main/java').rglob('*.java'):
    old=s=p.read_text(encoding='utf-8')
    s=rewrite(s,'holderOrThrow',lambda receiver,args:'dev.war.openfight.platform.fabric.registry.RegistryHolders.of('+receiver+', '+args+')')
    s=rewrite(s,'getEnchantmentLevel',lambda receiver,args:'net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel('+args+', '+receiver+')')
    aliases={'MOVEMENT_SPEED':'SPEED','MOVEMENT_SLOWDOWN':'SLOWNESS','DIG_SPEED':'HASTE','DIG_SLOWDOWN':'MINING_FATIGUE','DAMAGE_BOOST':'STRENGTH','HEAL':'INSTANT_HEALTH','HARM':'INSTANT_DAMAGE','JUMP':'JUMP_BOOST','CONFUSION':'NAUSEA','DAMAGE_RESISTANCE':'RESISTANCE'}
    for a,b in aliases.items(): s=re.sub(r'\bMobEffects\.'+a+r'\b','MobEffects.'+b,s)
    if p.name=='EpicFightBlocks.java':
        s=s.replace('() -> new FractureBlock(BlockBehaviour.Properties.of())','id -> new FractureBlock(BlockBehaviour.Properties.of().setId(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,id)))')
    if p.name=='EpicFightEntityTypes.java':
        s=re.sub(r'\.build\("([^"]+)"\)',r'.build(net.minecraft.resources.ResourceKey.create(Registries.ENTITY_TYPE,EpicFightMod.identifier("\1")))',s)
        s=s.replace('.immuneTo(Blocks.WITHER_ROSE)', '.immuneTo(net.minecraft.tags.TagKey.create(Registries.BLOCK,EpicFightMod.identifier("wither_skeleton_immune_to")))')
    if p.name=='UniversalBlockEntityType.java': s=s.replace('super(p_155259_, p_155260_, p_155261_);','super(p_155259_, p_155260_);')
    if old!=s: p.write_text(s,encoding='utf-8'); count+=1
print(f'Migrated lookup extensions, effect names and registry properties in {count} files')
