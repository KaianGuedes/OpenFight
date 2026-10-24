import re,sys
from pathlib import Path
sys.path.insert(0,str(Path(__file__).parent))
from jsrc import add_import
root=Path(__file__).resolve().parents[1]/'src/main/java/dev/war/openfight'
CMP='dev.war.openfight.platform.fabric.compat.'
# (file, [(pattern, repl, is_regex)], [imports])
E=[]
def rule(f,pairs,imports=()):
    E.append((f,pairs,imports))
L=lambda a,b:(a,b,False)
R=lambda a,b:(a,b,True)

rule('gameasset/Animations.java',[
 R(r'^import net\.neoforged\.[^\n]*\n',''),
 L('@EventBusSubscriber(modid = EpicFightMod.MODID)\n',''),
 L('    @SubscribeEvent\n',''),
 L('entitypatch.getOriginal().swingingArm == InteractionHand.OFF_HAND','entitypatch.getOriginal().getCurrentSwing() != null && entitypatch.getOriginal().getCurrentSwing().hand() == InteractionHand.OFF_HAND'),
 R(r'EventHooks\.canEntityGrief\(([\w.()]+), (\w+)\)',r'RuleCompat.mobGriefing(\1, \2)'),
 L('EntityTypes.LIGHTNING_BOLT.create(level)','EntityTypes.LIGHTNING_BOLT.create(level, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED)'),
 L('lightningbolt.setDamage(0.0F);\n',''),
 L('.lookupOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.LIGHTNING_BOLT)','.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.LIGHTNING_BOLT)'),
 L('e.hurt(damageSource, total);','e.hurtServer(level, damageSource, total);'),
 L('level.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)','level.getGameRules().get(GameRules.ADVANCE_WEATHER)'),
 L('level.setWeatherParameters(0, Mth.randomBetweenInclusive(level.random, 12000, 180000), true, true);','RuleCompat.setStorm(level, Mth.randomBetweenInclusive(level.random, 12000, 180000));'),
 L('NeoForgeConfig.SERVER.fullBoundingBoxLadders.get()','false'),
 L('bs.isLadder(level, bp, original)','bs.is(net.minecraft.tags.BlockTags.CLIMBABLE)'),
 L('bs.isLadder(level, tmp, original)','bs.is(net.minecraft.tags.BlockTags.CLIMBABLE)'),
 L('state.getSoundType(entitypatch.getLevel(), entitypatch.getOriginal().blockPosition(), entitypatch.getOriginal()).getHitSound()','state.getSoundType().getHitSound()'),
],[CMP+'RuleCompat'])
rule('skill/weaponinnate/BladeRushSkill.java',[L('Minecraft.getInstance().gui.setOverlayMessage','Minecraft.getInstance().gui.hud.setOverlayMessage')])
rule('server/commands/AnimatorCommand.java',[L('commandSourceStack.hasPermission(2)','commandSourceStack.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER)')])
rule('server/commands/PlayerModeCommand.java',[L('commandSourceStack.hasPermission(2)','commandSourceStack.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER)'),
 L('command.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)','command.getLevel().getGameRules().get(GameRules.SEND_COMMAND_FEEDBACK)')])
rule('server/commands/PlayerSkillCommand.java',[L('commandSourceStack.hasPermission(2)','commandSourceStack.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER)')])
rule('server/commands/PlayerStaminaCommand.java',[L('commandSourceStack.hasPermission(2)','commandSourceStack.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER)'),
 L('command.getSource().getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)','command.getSource().getLevel().getGameRules().get(GameRules.SEND_COMMAND_FEEDBACK)')])
rule('api/utils/LevelUtil.java',[
 L('level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)','RuleCompat.get(level, GameRules.MOB_GRIEFING)'),
 L('level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)','RuleCompat.get(level, GameRules.BLOCK_DROPS)'),
 R(r'\.setPos\(bp\.above\(\)\)',''),
 L('livingEntity.getType().is(EntityTypeTags.FALL_DAMAGE_IMMUNE)','livingEntity.is(EntityTypeTags.FALL_DAMAGE_IMMUNE)'),
],[CMP+'RuleCompat'])
rule('world/capabilities/entitypatch/boss/WitherPatch.java',[
 L('import net.neoforged.neoforge.event.EventHooks;\n',''),
 L('EventHooks.canEntityGrief(this.original.level(), this.original)','RuleCompat.mobGriefing(this.original.level(), this.original)'),
 L('blockstate.canEntityDestroy(this.original.level(), blockpos, this.original) && EventHooks.onEntityDestroyBlock(this.original, blockpos, blockstate)','RuleCompat.canEntityDestroy(this.original.level(), blockpos, this.original, blockstate)'),
 L('this.original.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)','RuleCompat.get(this.original.level(), GameRules.MOB_DROPS)'),
 L('WitherPatch.this.original.level().getNearbyEntities(LivingEntity.class,','((net.minecraft.server.level.ServerLevel)WitherPatch.this.original.level()).getNearbyEntities(LivingEntity.class,'),
],[CMP+'RuleCompat'])
rule('world/capabilities/projectile/WitherSkullPatch.java',[
 L('import net.minecraft.world.entity.MobSpawnType;','import net.minecraft.world.entity.EntitySpawnReason;'),
 L('MobSpawnType.REINFORCEMENT','EntitySpawnReason.REINFORCEMENT'),
])
rule('world/capabilities/projectile/ThrownTridentPatch.java',[
 L('entity.hurt(source, f)','entity.hurtOrSimulate(source, f)'),
])
rule('world/capabilities/entitypatch/player/PlayerPatch.java',[L('float fallDist = this.original.fallDistance;','float fallDist = (float)this.original.fallDistance;')])
rule('mixin/common/MixinEntity.java',[L('entitypatch.onFall(entitypatch.getOriginal().fallDistance, 1.0F)','entitypatch.onFall((float)entitypatch.getOriginal().fallDistance, 1.0F)')])
rule('world/capabilities/entitypatch/MobPatch.java',[L('this.original.doHurtTarget(target);','this.original.doHurtTarget((net.minecraft.server.level.ServerLevel)this.original.level(), target);')])
rule('world/capabilities/entitypatch/mob/EndermanPatch.java',[L('mob.randomTeleport(target.getX() + vec.x, target.getY(), target.getZ() + vec.z, false)','mob.randomTeleport(target.getX() + vec.x, target.getY(), target.getZ() + vec.z, false, state -> false)')])
rule('data/conditions/entity/HasCustomTag.java',[L('target.getTags().contains(tag)','target.entityTags().contains(tag)')])
rule('api/event/impl/VanillaEntityEventHooks.java',[L('entity.isInvulnerableTo(damageSource)','(entity.level() instanceof net.minecraft.server.level.ServerLevel sl$ && entity.isInvulnerableTo(sl$, damageSource))')])
rule('api/utils/HitEntityList.java',[L('targetGoal.targetConditions.test(mob, attacker.getOriginal())','targetGoal.targetConditions.test((net.minecraft.server.level.ServerLevel)mob.level(), mob, attacker.getOriginal())')])
rule('api/ex_cap/data/Moveset.java',[L('return I18n.exists(languageKey);','return net.minecraft.locale.Language.getInstance().has(languageKey);'),
 L('result.setPassiveSkill(EpicFightRegistries.SKILL.getValue(Identifier.parse(json.get("weapon_passive").getAsString())));','result.setPassiveSkill(EpicFightRegistries.SKILL.get(Identifier.parse(json.get("weapon_passive").getAsString())).orElseThrow());')])
rule('api/ex_cap/managers/ItemPresetManager.java',[L('EpicFightRegistries.WEAPON_DATA.holders()','EpicFightRegistries.WEAPON_DATA.listElements()')])
rule('api/ex_cap/managers/MovesetManager.java',[L('EpicFightRegistries.MOVESET_DATA.holders()','EpicFightRegistries.MOVESET_DATA.listElements()')])
rule('registry/callbacks/SkillCallbacks.java',[L('registry.holders()','registry.listElements()')])
rule('api/data/reloader/ItemCapabilityReloadListener.java',[
 L('BuiltInRegistries.SOUND_EVENT.getValue(soundLocation).get()','BuiltInRegistries.SOUND_EVENT.get(soundLocation).orElseThrow()'),
 L('BuiltInRegistries.PARTICLE_TYPE.getValue(hitParticleLocation).get()','(Holder<ParticleType<?>>)(Holder)BuiltInRegistries.PARTICLE_TYPE.get(hitParticleLocation).orElseThrow()'),
])
rule('world/capabilities/item/WeaponTypeReloadListener.java',[
 L('BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.parse(tag.getStringOr("hit_particle", "")))','BuiltInRegistries.PARTICLE_TYPE.get(Identifier.parse(tag.getStringOr("hit_particle", "")))'),
 L('BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse(tag.getStringOr("swing_sound", ""))).get()','BuiltInRegistries.SOUND_EVENT.get(Identifier.parse(tag.getStringOr("swing_sound", ""))).orElseThrow()'),
 L('BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse(tag.getStringOr("hit_sound", ""))).get()','BuiltInRegistries.SOUND_EVENT.get(Identifier.parse(tag.getStringOr("hit_sound", ""))).orElseThrow()'),
])
rule('api/data/reloader/MobPatchReloadListener.java',[
 L('(float)tag.getCompoundOrEmpty("attributes").getDouble("scale")','(float)tag.getCompoundOrEmpty("attributes").getDoubleOr("scale", 1.0D)'),
 L('tag.getCompoundOrEmpty("attributes").getDouble("chasing_speed")','tag.getCompoundOrEmpty("attributes").getDoubleOr("chasing_speed", 0.0D)'),
 L('tag.getString(stunType.name().toLowerCase(Locale.ROOT))','tag.getStringOr(stunType.name().toLowerCase(Locale.ROOT), "")'),
])
rule('api/utils/ParseUtil.java',[L('byteTag.getAsByte()','byteTag.byteValue()')])
rule('world/capabilities/entitypatch/EntityDecorations.java',[])
rule('skill/identity/RevelationSkill.java',[L('EntityType.byString(registryName).orElse(null)','EntityType.byString(registryName).orElse(null)')])
rule('registry/entries/EpicFightConditions.java',[L('EpicFightRegistries.CONDITION.get(key)','EpicFightRegistries.CONDITION.getValue(key)')])
rule('world/entity/DroppedNetherStar.java',[L('this.lifespan = 6000;','')])
rule('network/EpicFightClientBoundPayloadHandler.java',[
 L('livingentity.lerpX = data.position().x;','livingentity.lerpPos.x = data.position().x;'),
])
rule('api/animation/property/MoveCoordFunctions.java',[L('entitypatch.getOriginal().attackAnim = 2;','')])

done=0
for f,pairs,imports in E:
    p=root/f
    if not p.exists():
        print('MISSING',f);continue
    raw=p.read_bytes().decode('utf-8');crlf='\r\n' in raw
    t=raw.replace('\r\n','\n');o=t
    for a,b,isre in pairs:
        if isre: t=re.sub(a,b,t,flags=re.M)
        else:
            if a not in t: print('  no match in',f,':',a[:60])
            t=t.replace(a,b)
    for imp in imports: t=add_import(t,imp)
    if t!=o:
        if crlf:t=t.replace('\n','\r\n')
        p.write_bytes(t.encode('utf-8'));done+=1
print('changed',done)
