package yesman.epicfight.world.capabilities;

import net.minecraft.resources.Identifier;

/** Descriptor of a global capability. NeoForge's BaseCapability registry does not exist on Fabric; this is only a typed key. */
public class GlobalCapability<T> {
	private final Identifier name;
	private final Class<T> type;
	
	public GlobalCapability(Identifier name, Class<T> type) {
		this.name = name;
		this.type = type;
	}
	
	public Identifier name() {
		return this.name;
	}
	
	public Class<T> type() {
		return this.type;
	}
}
