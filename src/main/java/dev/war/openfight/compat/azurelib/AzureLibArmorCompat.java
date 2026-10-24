package dev.war.openfight.compat.azurelib;

import net.neoforged.bus.api.IEventBus;
import dev.war.openfight.api.client.event.EpicFightClientEventHooks;
import dev.war.openfight.api.client.model.transformer.HumanoidModelBaker;
import dev.war.openfight.compat.ICompatModule;
import dev.war.openfight.compat.azurelib.client.AzureArmorTransformer;

public class AzureLibArmorCompat implements ICompatModule {
	@Override
	public void onModEventBusClient(IEventBus eventBus) {
		HumanoidModelBaker.registerNewTransformer(new AzureArmorTransformer());
	}
	
	@Override
	public void onGameEventBusClient(IEventBus eventBus) {
        EpicFightClientEventHooks.Render.ANIMATED_ARMOR_TEXTURE.registerEvent(AzureArmorTransformer::getGeoArmorTexturePath);
	}
	
	@Override
	public void onModEventBus(IEventBus eventBus) {
	}
	
	@Override
	public void onGameEventBus(IEventBus eventBus) {
	}
}