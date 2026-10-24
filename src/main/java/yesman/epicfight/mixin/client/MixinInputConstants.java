package yesman.epicfight.mixin.client;

import com.mojang.blaze3d.platform.InputConstants;
import java.nio.ByteBuffer;
import org.lwjgl.sdl.SDLKeyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** SDL exposes its keyboard state as a bounded scancode buffer; invalid/stale mappings must be harmless. */
@Mixin(InputConstants.class)
public abstract class MixinInputConstants {
    @Inject(method = "isKeyDown(I)Z", at = @At("HEAD"), cancellable = true)
    private static void epicfight$guardKeyboardStateBounds(int scancode, CallbackInfoReturnable<Boolean> cir) {
        ByteBuffer keyboardState = SDLKeyboard.SDL_GetKeyboardState();
        if (keyboardState == null || scancode < 0 || scancode >= keyboardState.limit()) {
            cir.setReturnValue(false);
        }
    }
}
