package yesman.epicfight.mixin.common;

import net.minecraft.server.ReloadableServerResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.data.reloader.ItemCapabilityReloadListener;

/** Item component initializers are applied immediately before this method returns. */
@Mixin(ReloadableServerResources.class)
public abstract class MixinReloadableServerResources {
    @Inject(method = "updateComponentsAndStaticRegistryTags", at = @At("TAIL"))
    private void epicfight$applyItemCapabilitiesAfterComponentsBind(CallbackInfo ci) {
        ItemCapabilityReloadListener.applyPendingCapabilities(true);
    }
}
