import re
from pathlib import Path
root=Path(__file__).resolve().parents[1]/'src/main/java/yesman/epicfight'
def edit(rel,f):
    p=root/rel
    raw=p.read_bytes().decode('utf-8');crlf='\r\n' in raw;s=raw.replace('\r\n','\n')
    s=f(s)
    if crlf:s=s.replace('\n','\r\n')
    p.write_bytes(s.encode('utf-8'))

def pis(s):
    s=s.replace('import net.minecraft.client.player.Input;','import net.minecraft.client.player.ClientInput;\nimport net.minecraft.world.entity.player.Input;')
    s=s.replace('''    public static PlayerInputState fromVanillaInput(Input input) {
        return new PlayerInputState(
                input.leftImpulse, input.forwardImpulse,
                input.up, input.down,
                input.left, input.right,
                input.jumping, input.shiftKeyDown
        );
    }''','''    public static PlayerInputState fromVanillaInput(ClientInput input) {
        Vec2 move = input.getMoveVector();
        Input keys = input.keyPresses;

        return new PlayerInputState(
                move.x, move.y,
                keys.forward(), keys.backward(),
                keys.left(), keys.right(),
                keys.jump(), keys.shift()
        );
    }''')
    a=s.index('    public static Input applyToVanillaInput(')
    b=s.index('    public Vec2 getMoveVector()')
    s=s[:a]+'''    public static ClientInput applyToVanillaInput(@NotNull PlayerInputState updated, @NotNull ClientInput input) {
        Input keys = input.keyPresses;
        input.keyPresses = new Input(updated.up(), updated.down(), updated.left(), updated.right(), updated.jumping(), updated.sneaking(), keys.sprint());
        input.moveVector = new Vec2(updated.leftImpulse(), updated.forwardImpulse());
        return input;
    }

'''+s[b:]
    return s
edit('api/client/input/PlayerInputState.java',pis)

def im(s):
    s=s.replace('import net.minecraft.client.player.Input;','import net.minecraft.client.player.ClientInput;')
    s=s.replace('getInputState(@NotNull Input vanillaInput)','getInputState(@NotNull ClientInput vanillaInput)')
    s=s.replace('final Input input = player.input;','final ClientInput input = player.input;')
    return s
edit('api/client/input/InputManager.java',im)

def ev(s):
    s=s.replace('import net.minecraft.client.player.Input;','import net.minecraft.client.player.ClientInput;')
    s=re.sub(r'\bInput\b(?!State)','ClientInput',s)
    return s
edit('api/client/event/types/control/MappedMovementInputUpdateEvent.java',ev)

def ce(s):
    s=s.replace('import net.minecraft.client.player.Input;','import net.minecraft.client.player.ClientInput;')
    s=s.replace('private PlayerInputState inputTick(Input input) {','private PlayerInputState inputTick(ClientInput input) {')
    s=re.sub(r'InputConstants\.isKeyDown\(Minecraft\.getInstance\(\)\.getWindow\(\)\.getWindow\(\), ([^)]+)\)',r'InputConstants.isKeyDown(\1)',s)
    s=s.replace('key.isDown() || InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), key.getKey().getValue())','key.isDown() || InputConstants.isKeyDown(key.key.getValue())')
    s=s.replace('key.isDown() || InputConstants.isMouseButtonDown(Minecraft.getInstance().getWindow(), key.getKey().getValue())','key.isDown()')
    s=s.replace('key.getKey().getType()','key.key.getType()')
    s=s.replace('vanillaAttack.keyMapping().getKey() == epicFightAttack.keyMapping().getKey()','vanillaAttack.keyMapping().key == epicFightAttack.keyMapping().key')
    s=s.replace('this.minecraft.keyboardHandler.debugFeedbackTranslated(flag ? "debug.vanilla_model_debugging.on" : "debug.vanilla_model_debugging.off");','this.minecraft.gui.hud.getChat().addClientSystemMessage(Component.translatable(flag ? "debug.vanilla_model_debugging.on" : "debug.vanilla_model_debugging.off"));')
    s=s.replace('this.minecraft.gui.getChat().addMessage(','this.minecraft.gui.hud.getChat().addClientSystemMessage(')
    s=s.replace('combatItemForSwing.getItem().canAttackBlock(bs, this.player.level(), bp, this.player)','combatItemForSwing.canDestroyBlock(bs, this.player.level(), bp, this.player)')
    s=s.replace('.getEntity().interact(this.minecraft.player, event.getHand());','.getEntity().interact(this.minecraft.player, event.getHand(), ((EntityHitResult) this.minecraft.hitResult).getLocation());')
    s=s.replace('blockHitResult).result();','blockHitResult);')
    s=s.replace('InteractionResult useItemInteractionResult = blockstate.useItemOn(this.player.getItemInHand(event.getHand()), fakeLevelForSimulation, fakePlayerForSimulation, event.getHand(), blockHitResult).result();','InteractionResult useItemInteractionResult = blockstate.useItemOn(this.player.getItemInHand(event.getHand()), fakeLevelForSimulation, fakePlayerForSimulation, event.getHand(), blockHitResult);')
    return s
edit('client/events/engine/ControlEngine.java',ce)

def re_(s):
    s=s.replace('this.minecraft.options.hideGui','this.minecraft.gui.hud.isHidden()')
    s=s.replace('import yesman.epicfight.compat.MinecraftMod;\n','')
    return s
edit('client/events/engine/RenderEngine.java',re_)
print('ok')
