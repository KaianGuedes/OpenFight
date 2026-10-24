package dev.war.openfight.api.event.types.player;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import dev.war.openfight.api.event.LivingEntityPatchEvent;
import dev.war.openfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import dev.war.openfight.world.capabilities.item.CapabilityItem;

/// An event hook when weapon innate skill is changed to new item
/// You can use LivingEquipmentChangeEvent if the skill change is not your interest
public class ChangeInnateSkillEvent extends LivingEntityPatchEvent {
	private final ItemStack from;
	private final ItemStack to;
	private final CapabilityItem fromItemCapability;
	private final CapabilityItem toItemCapability;
	private final InteractionHand hand;
	
	public ChangeInnateSkillEvent(ServerPlayerPatch playerPatch, ItemStack from, CapabilityItem fromItemCapability, ItemStack to, CapabilityItem toItemCapability, InteractionHand hand) {
        super(playerPatch);
		this.from = from;
		this.to = to;
		this.fromItemCapability = fromItemCapability;
		this.toItemCapability = toItemCapability;
		this.hand = hand;
	}
	
	public ServerPlayerPatch getPlayerPatch() {
		return (ServerPlayerPatch)this.getEntityPatch();
	}
	
	public ItemStack getFrom() {
		return this.from;
	}
	
	public ItemStack getTo() {
		return this.to;
	}
	
	public CapabilityItem getFromItemCapability() {
		return this.fromItemCapability;
	}
	
	public CapabilityItem getToItemCapability() {
		return this.toItemCapability;
	}
	
	public InteractionHand getHand() {
		return this.hand;
	}
}
