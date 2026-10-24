"""One-time migration of supported seams; unsupported events remain visible as build errors."""
from pathlib import Path
import re
root=Path(__file__).resolve().parents[1]
sources=root/'src/main/java'
for p in sources.rglob('*.java'):
    old=s=p.read_text(encoding='utf-8')
    for cls in ('DeferredHolder','DeferredRegister'):
        s=s.replace('net.neoforged.neoforge.registries.'+cls,'dev.war.openfight.platform.fabric.registry.'+cls)
    for cls in ('IPayloadContext','DirectionalPayloadHandler'):
        s=s.replace('net.neoforged.neoforge.network.handling.'+cls,'dev.war.openfight.platform.fabric.network.'+cls)
    s=s.replace('net.neoforged.neoforge.network.registration.PayloadRegistrar','dev.war.openfight.platform.fabric.network.PayloadRegistrar')
    s=s.replace('net.neoforged.neoforge.network.PacketDistributor','dev.war.openfight.platform.fabric.network.PacketDistributor')
    if p.name=='EpicFightNetworkManager.java':
        s=re.sub(r'import net\.neoforged\.[^;]+;\n','',s)
        s=re.sub(r'@EventBusSubscriber\([^\n]+\)\n','',s)
        s=s.replace('@SubscribeEvent\n','')
        s=s.replace('register(final RegisterPayloadHandlersEvent event)', 'register()')
        s=s.replace('event.registrar(PROTOCOL_VERSION)', 'new PayloadRegistrar()')
    if p.name=='EpicFightSharedConstants.java':
        s=re.sub(r'import net\.neoforged\.[^;]+;\n','',s)
        s=s.replace('!FMLEnvironment.production','net.fabricmc.loader.api.FabricLoader.getInstance().isDevelopmentEnvironment()')
        s=s.replace('FMLEnvironment.dist == Dist.CLIENT','net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT')
    if old!=s: p.write_text(s,encoding='utf-8')

p=sources/'dev/war/openfight/world/capabilities/provider/AttachmentEntityPatchProvider.java'
s=p.read_text(encoding='utf-8').replace('import net.neoforged.neoforge.attachment.IAttachmentHolder;\n','').replace('IAttachmentHolder attachmentHolder','Entity entity')
s=re.sub(r'\s*if \(!\(attachmentHolder instanceof Entity entity\)\) \{.*?\n\s*\}', '',s,flags=re.S)
s=s.replace('attachmentHolder instanceof EnderDragonPart','entity instanceof EnderDragonPart')
p.write_text(s,encoding='utf-8')

p=sources/'dev/war/openfight/world/capabilities/EpicFightCapabilities.java'
s=p.read_text(encoding='utf-8')
s=re.sub(r'import net\.neoforged\.neoforge\.capabilities\.ItemCapability;\n','',s)
s=re.sub(r'\s*public static final ItemCapability<CapabilityItem, Void> CAPABILITY_ITEM =.*?\n\s*\);','',s,flags=re.S)
s=s.replace('stack.getCapability(CAPABILITY_ITEM)','ITEM_CAPABILITY_PROVIDER.getCapability(stack, null)')
s=re.sub(r'([\w.()]+)\.getData\(EpicFightAttachmentTypes.ENTITY_PATCH\)',r'EpicFightAttachmentTypes.get(\1)',s)
p.write_text(s,encoding='utf-8')

p=sources/'dev/war/openfight/world/capabilities/provider/CommonItemCapabilityProvider.java'
s=p.read_text(encoding='utf-8')
s=re.sub(r'import net\.neoforged\.neoforge\.capabilities\.[^;]+;\n','',s)
s=re.sub(r' implements ICapabilityProvider<[^\n]+>', '', s)
s=s.replace('\t@Override\n\tpublic @Nullable CapabilityItem getCapability','\tpublic @Nullable CapabilityItem getCapability')
p.write_text(s,encoding='utf-8')

p=sources/'dev/war/openfight/registry/entries/EpicFightAttachmentTypes.java'
p.write_text('''package dev.war.openfight.registry.entries;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import dev.war.openfight.world.capabilities.provider.AttachmentEntityPatchProvider;

public final class EpicFightAttachmentTypes {
    private EpicFightAttachmentTypes() {}
    public static final AttachmentType<AttachmentEntityPatchProvider> ENTITY_PATCH =
        AttachmentRegistry.create(Identifier.fromNamespaceAndPath("epicfight", "entitypatch"));
    public static AttachmentEntityPatchProvider get(Entity entity) {
        return ((AttachmentTarget)entity).getAttachedOrCreate(ENTITY_PATCH, () -> new AttachmentEntityPatchProvider(entity));
    }
}
''',encoding='utf-8')
print('Migrated registry references, payload handlers, environment checks and entity attachment reads')
