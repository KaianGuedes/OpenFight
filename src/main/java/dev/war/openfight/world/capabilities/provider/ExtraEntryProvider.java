package dev.war.openfight.world.capabilities.provider;

import net.minecraft.world.item.Item;
import dev.war.openfight.api.animation.AnimationManager.AnimationAccessor;
import dev.war.openfight.api.animation.types.StaticAnimation;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.client.model.SkinnedMesh;
import dev.war.openfight.api.model.Armature;
import dev.war.openfight.world.capabilities.item.CapabilityItem;

import java.util.function.Function;

/**
 * Used to get extra entries created in a runtime via datapack editor
 *
 * Might be implementation more future-proofing method in the next version.
 * Just remaining for backward compatibility
 */
@Deprecated(forRemoval = true, since = "26.1")
public interface ExtraEntryProvider {
    <T extends StaticAnimation> AnimationAccessor<T> getExtraOrBuiltInAnimation(String path);

    AssetAccessor<? extends SkinnedMesh> getExtraOrBuiltInMesh(String path);

    AssetAccessor<? extends Armature> getExtraOrBuiltInArmature(String path);

    Function<Item, ? extends CapabilityItem.Builder<?>> getExtraOrBuiltInWeaponType(String typeName);
}