package dev.war.openfight.api.animation.types.datapack;

import com.google.gson.JsonArray;

import net.minecraft.resources.Identifier;
import dev.war.openfight.api.animation.AnimationClip;
import dev.war.openfight.api.animation.AnimationManager.AnimationAccessor;
import dev.war.openfight.api.animation.types.StaticAnimation;
import dev.war.openfight.api.asset.JsonAssetLoader;

public interface DatapackAnimation<A extends StaticAnimation> extends AnimationAccessor<A> {
	void setAnimationClip(AnimationClip clip);
	void setRegistryName(Identifier registryName);
	void setCreator(EditorAnimation fakeAnimation);
	EditorAnimation getCreator();
	EditorAnimation readAnimationFromJson(JsonAssetLoader.TransformFormat transformFormat, JsonArray rawAnimationJson);
	
	@Override
	default int id() {
		return -1;
	}
	
	@Override
	default boolean inRegistry() {
		return false;
	}
}