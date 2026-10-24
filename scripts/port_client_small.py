from pathlib import Path
import re
root=Path(__file__).resolve().parents[1]/'src/main/java/dev/war/openfight'
def edit(f,fn):
 p=root/f;s=p.read_text(encoding='utf-8');p.write_text(fn(s),encoding='utf-8')
def method(s,needle,new):
 a=s.index(needle);b=s.index('{',a);d=1;e=b+1
 while d:d+=(s[e]=='{')-(s[e]=='}');e+=1
 return s[:a]+new+s[e:]
edit('client/gui/datapack/widgets/ResizableEditBox.java',lambda s:s.replace('extends dev.war.openfight.client.gui.widgets.FilteredEditBox','extends FilteredEditBox'))
for f in ['client/input/InputUtils.java','api/client/input/InputManager.java','api/client/event/impl/VanillaGUIEventHooks.java']:
 edit(f,lambda s:re.sub(r'(\b\w+(?:\.\w+)*).getKey\(\)',r'InputConstants.getKey(\1.saveString())',s).replace('import org.lwjgl.glfw.GLFW;','').replace('import net.minecraft.client.player.Input;', '').replace('import net.neoforged.neoforge.client.ClientHooks;', '').replace('import net.neoforged.neoforge.client.event.InputEvent;', ''))
edit('api/client/input/InputManager.java',lambda s:method(s,'private static boolean isPhysicalKeyDown(','''private static boolean isPhysicalKeyDown(@NotNull KeyMapping keyMapping) {
        var key=InputConstants.getKey(keyMapping.saveString());
        if(keyMapping.isUnbound()) return false;
        if(key.getType()==InputConstants.Type.KEYBOARD) return InputConstants.isKeyDown(key.getValue());
        return (org.lwjgl.sdl.SDLMouse.SDL_GetMouseState(null,null) & (1 << (key.getValue()-1))) != 0;
    }'''))
edit('client/input/InputUtils.java',lambda s:method(s,'public static boolean checkInteractionKeyUsable(','''public static boolean checkInteractionKeyUsable(int mouseButton, KeyMapping keyMapping) { return true; }''').replace('player.input.tick(isSneaking, sneakingSpeedMultiplier)', 'player.input.tick()'))
edit('api/client/input/controller/EpicFightControllerModProvider.java',lambda s:s.replace('    @org.jetbrains.annotations.Nullable\n',''))
edit('api/client/event/impl/VanillaGUIEventHooks.java',lambda s:s.replace('import com.mojang.datafixers.util.Pair;','import com.mojang.blaze3d.platform.InputConstants;').replace('private static final Pair<Identifier, Identifier> OFFHAND_TEXTURE = Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);','private static final Identifier OFFHAND_TEXTURE = InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD;').replace('abstractContainerScreen.getSlotUnderMouse()', 'abstractContainerScreen.hoveredSlot').replace('keyCode >= 49 && keyCode <= 57','keyCode >= org.lwjgl.sdl.SDLScancode.SDL_SCANCODE_1 && keyCode <= org.lwjgl.sdl.SDLScancode.SDL_SCANCODE_9').replace('getItem(keyCode - 49)', 'getItem(keyCode - org.lwjgl.sdl.SDLScancode.SDL_SCANCODE_1)'))
edit('client/ClientEngine.java',lambda s:s.replace('import net.neoforged.neoforge.client.ClientHooks;','').replace('t1 == ParticleRenderType.CUSTOM || ', '').replace('t2 == ParticleRenderType.CUSTOM || ', ''))
edit('client/particle/ForceFieldEndParticle.java',lambda s:s.replace('extends Particle {', 'extends LegacyTextureSheetParticle {'))
edit('client/particle/AbstractTrailParticle.java',lambda s:s.replace('\t@Override\n\tpublic AABB getRenderBoundingBox(', '\tpublic AABB getRenderBoundingBox(').replace('AABB.INFINITE', 'new AABB(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE)').replace('protected int getLightColor(float', 'public int getLightColor(float').replace('state.emissiveRendering(level, pos)', 'state.emissiveRendering()').replace('state.getLightEmission(level, pos)', 'state.getLightEmission()'))
edit('client/particle/AnimationTrailParticle.java',lambda s:re.sub(r'(?m)^.*RenderSystem.(bindTexture|texParameter)\([^\n]*\);\n','',s))
edit('client/particle/GroundSlamParticle.java',lambda s:s.replace('smokeParticle.alpha = 0.33F;', '((net.minecraft.client.particle.SingleQuadParticle)smokeParticle).setAlpha(0.33F);').replace('double dx, double dy, double dz) {','double dx, double dy, double dz, net.minecraft.util.RandomSource randomSource) {').replace('particleOption.getPos()', 'net.minecraft.core.BlockPos.containing(x,y,z)'))
edit('client/particle/ProjectileTrailParticle.java',lambda s:s.replace('!this.owner.getOriginal().inGround', '!this.owner.getOriginal().isInGround()'))
edit('client/world/capabilites/entitypatch/player/AbstractClientPlayerPatch.java',lambda s:re.sub(r'this.original.([xyz])Cloak(O?)\b',lambda m:'this.original.avatarState().getInterpolatedCloak'+m[1].upper()+'(' + ('0.0F' if m[2] else '1.0F') +')',s).replace('this.original.getInventory().getSelected()', 'this.original.getMainHandItem()').replace('this.original.getInventory().offhand.getFirst()', 'this.original.getOffhandItem()').replace('this.original.getDeltaMovementLerped(partialTick)', 'this.original.avatarState().deltaMovementOnPreviousTick().lerp(this.original.getDeltaMovement(), partialTick)').replace('blockstate.isBed(this.original.level(), this.original.getSleepingPos().orElse(null), this.original)', 'blockstate.is(net.minecraft.tags.BlockTags.BEDS)'))
print('Small client API migration applied')
