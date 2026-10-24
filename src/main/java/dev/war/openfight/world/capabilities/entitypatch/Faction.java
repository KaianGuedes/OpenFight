package dev.war.openfight.world.capabilities.entitypatch;

import net.minecraft.resources.Identifier;
import dev.war.openfight.api.utils.ExtensibleEnum;
import dev.war.openfight.api.utils.ExtensibleEnumManager;

public interface Faction extends ExtensibleEnum {
	ExtensibleEnumManager<Faction> ENUM_MANAGER = new ExtensibleEnumManager<> ("faction");
	
	public Identifier healthBarTexture();
	
	public int healthBarIndex();
	
	public int damageColor();
}