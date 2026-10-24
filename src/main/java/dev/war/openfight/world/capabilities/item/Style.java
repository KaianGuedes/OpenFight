package dev.war.openfight.world.capabilities.item;

import dev.war.openfight.api.utils.ExtensibleEnum;
import dev.war.openfight.api.utils.ExtensibleEnumManager;

public interface Style extends ExtensibleEnum {
	ExtensibleEnumManager<Style> ENUM_MANAGER = new ExtensibleEnumManager<> ("style");
	
	boolean canUseOffhand();
}