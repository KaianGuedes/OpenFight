package dev.war.openfight.world.capabilities.item;

import com.google.common.collect.ImmutableList;
import net.minecraft.network.chat.Component;
import dev.war.openfight.EpicFight;
import dev.war.openfight.api.utils.ExtensibleEnum;
import dev.war.openfight.api.utils.ExtensibleEnumManager;
import dev.war.openfight.api.utils.ParseUtil;

import java.util.*;

public interface WeaponCategory extends ExtensibleEnum {
	ExtensibleEnumManager<WeaponCategory> ENUM_MANAGER = new ExtensibleEnumManager<> ("weapon_category");

    /// Returns a translation key
    /// For backward compatibility, this method is implemented as default
    default Component getTranslatable() {
        return Component.translatable(String.format("weapon_category.%s.%s", EpicFight.NAMESPACE, ParseUtil.toLowerCase(this.toString())));
    }

    default List<WeaponCategory> getParents() {
        return ImmutableList.of();
    }
}