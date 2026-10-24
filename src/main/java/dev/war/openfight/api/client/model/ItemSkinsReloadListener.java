package dev.war.openfight.api.client.model;

import java.util.Map;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.resources.FileToIdConverter;
import dev.war.openfight.platform.fabric.resource.JsonElementCodec;
import net.minecraft.util.profiling.ProfilerFiller;
import dev.war.openfight.client.events.engine.RenderEngine;

public class ItemSkinsReloadListener extends SimpleJsonResourceReloadListener<JsonElement> {
	public static final ItemSkinsReloadListener INSTANCE = new ItemSkinsReloadListener();
	
	public ItemSkinsReloadListener() {
		super(JsonElementCodec.CODEC, FileToIdConverter.json("item_skins"));
	}
	
	@Override
	protected void apply(Map<Identifier, JsonElement> objectIn, ResourceManager resourceManager, ProfilerFiller profileFiller) {
		RenderEngine.getInstance().reloadItemRenderers(objectIn);
	}
}