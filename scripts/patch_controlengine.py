import re
from pathlib import Path
base=Path(__file__).resolve().parents[1]/'src/main/java/yesman/epicfight/client/events/engine'
p=base/'ControlEngine.java'
raw=p.read_bytes().decode('utf-8');crlf='\r\n' in raw;s=raw.replace('\r\n','\n')
s=re.sub(r'import net\.neoforged[^\n]*\n','',s)
s=s.replace('import org.lwjgl.glfw.GLFW;\n','')
s=s.replace('public class ControlEngine implements IEventBasedEngine {','public class ControlEngine {')
s=s.replace('''			InputEvent.InteractionKeyMappingTriggered inputEvent = ClientHooks.onClickInput(mouseButton, key, InteractionHand.MAIN_HAND);

	        if (inputEvent.isCanceled()) {
	        	return false;
	        }
''','')
s=s.replace('			int mouseButton = InputConstants.Type.MOUSE == key.getKey().getType() ? key.getKey().getValue() : -1;\n','')
# event handlers -> plain methods
s=s.replace('''	private void epicfight$mouseScrollEvent(InputEvent.MouseScrollingEvent event) {
        // Disables item switching for the vanilla mouse input
        if (isHotbarCyclingDisabled()) {
            event.setCanceled(true);
        }
	}''','''	/** @return true if the vanilla mouse scrolling (hotbar cycling) must be cancelled */
	public boolean shouldCancelMouseScroll() {
        // Disables item switching for the vanilla mouse input
        return isHotbarCyclingDisabled();
	}''')
s=s.replace('''	private void epicfight$moveInputEvent(MovementInputUpdateEvent event) {
		if (this.playerpatch == null) {
			return;
		}

		PlayerInputState playerinputstate = this.inputTick(event.getInput());''','''	/** Called right after the local player's movement input was ticked (was MovementInputUpdateEvent). */
	public void onMovementInputUpdate(net.minecraft.client.player.ClientInput clientInput) {
		if (this.playerpatch == null) {
			return;
		}

		PlayerInputState playerinputstate = this.inputTick(clientInput);''')
s=s.replace('''	private void epicfight$clientTickEndEvent(ClientTickEvent.Post event) {''','''	public void clientTickEnd() {''')
s=s.replace('''	private void epicfight$interactionKeyMappingTriggered(InputEvent.InteractionKeyMappingTriggered event) {
        if (this.minecraft.player == null || this.minecraft.hitResult == null) return;

        final InputAction triggeredAction = mapKeyMappingToAction(event.getKeyMapping());

        if (triggeredAction == null) {
            // The key mapping corresponds to a fixed vanilla action (attack, pick block, or use item).
            // These are predictable, so it's safe to map the key mapping to an input action.
            return;
        }''','''	/**
	 * Replacement for InputEvent.InteractionKeyMappingTriggered; called before vanilla starts an attack, use or pick action.
	 *
	 * @return true if the vanilla action must be cancelled (without swinging the hand)
	 */
	public boolean onInteractionKeyMappingTriggered(KeyMapping triggeredKeyMapping, InteractionHand triggeredHand) {
		InteractionKeyEvent event = new InteractionKeyEvent(triggeredKeyMapping, triggeredHand);
		this.processInteractionKeyMapping(event);
		return event.canceled;
	}

	private static final class InteractionKeyEvent {
		private final KeyMapping keyMapping;
		private final InteractionHand hand;
		private boolean canceled;

		private InteractionKeyEvent(KeyMapping keyMapping, InteractionHand hand) {
			this.keyMapping = keyMapping;
			this.hand = hand;
		}

		KeyMapping getKeyMapping() {
			return this.keyMapping;
		}

		InteractionHand getHand() {
			return this.hand;
		}

		void setCanceled(boolean canceled) {
			this.canceled = canceled;
		}

		void setSwingHand(boolean swing) {
		}
	}

	private void processInteractionKeyMapping(InteractionKeyEvent event) {
        if (this.minecraft.player == null || this.minecraft.hitResult == null) return;

        final InputAction triggeredAction = mapKeyMappingToAction(event.getKeyMapping());

        if (triggeredAction == null) {
            // The key mapping corresponds to a fixed vanilla action (attack, pick block, or use item).
            // These are predictable, so it's safe to map the key mapping to an input action.
            return;
        }''')
s=s.replace('''	private void epicfight$livingJumpEvent(LivingJumpEvent event) {
		if (event.getEntity() == this.player) {''','''	public void onLivingJump(LivingEntity jumpingEntity) {
		if (jumpingEntity == this.player) {''')
a=s.index('	@Override\n	public void gameEventBus(IEventBus gameEventBus) {')
b=s.index('	@Override\n	public void modEventBus(IEventBus modEventBus) {\n	}')+len('	@Override\n	public void modEventBus(IEventBus modEventBus) {\n	}')
s=s[:a]+'''	/** Registers the Fabric API callbacks of this engine. */
	public void registerFabricCallbacks() {
		net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> this.clientTickEnd());
	}'''+s[b:]
# key down checks
s=s.replace('key.isDown() || GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), key.getKey().getValue()) > 0','key.isDown() || InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), key.getKey().getValue())')
s=s.replace('key.isDown() || GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), key.getKey().getValue()) > 0','key.isDown() || InputConstants.isMouseButtonDown(Minecraft.getInstance().getWindow(), key.getKey().getValue())')
if crlf:s=s.replace('\n','\r\n')
p.write_bytes(s.encode('utf-8'))
(base/'IEventBasedEngine.java').unlink()
print('ok')
