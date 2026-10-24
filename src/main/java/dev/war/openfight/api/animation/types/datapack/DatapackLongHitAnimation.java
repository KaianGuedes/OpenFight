package dev.war.openfight.api.animation.types.datapack;

import com.google.gson.JsonArray;

import net.minecraft.resources.Identifier;
import dev.war.openfight.api.animation.AnimationClip;
import dev.war.openfight.api.animation.types.LongHitAnimation;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.asset.JsonAssetLoader;
import dev.war.openfight.api.model.Armature;

public class DatapackLongHitAnimation extends LongHitAnimation implements DatapackAnimation<DatapackLongHitAnimation> {
	protected AnimationClip clip;
	protected EditorAnimation fakeAnimation;
	protected Identifier registryName;
	
	public DatapackLongHitAnimation(float convertTime, String path, AssetAccessor<? extends Armature> armature) {
		super(convertTime, path, armature);
		
		((DatapackAnimation<DatapackLongHitAnimation>)this).setRegistryName(Identifier.parse(path));
		this.accessor = this;
	}
	
	@Override
	public void setAnimationClip(AnimationClip clip) {
		this.clip = clip;
	}
	
	@Override
	public AnimationClip getAnimationClip() {
		return this.clip;
	}

	@Override
	public void setCreator(EditorAnimation fakeAnimation) {
		this.fakeAnimation = fakeAnimation;
	}

	@Override
	public EditorAnimation getCreator() {
		return this.fakeAnimation;
	}

	@Override
	public EditorAnimation readAnimationFromJson(JsonAssetLoader.TransformFormat transformFormat, JsonArray rawAnimationJson) {
		EditorAnimation fakeAnimation = new EditorAnimation(this.registryName().toString(), this.armature, this.clip, transformFormat, rawAnimationJson);
		fakeAnimation.setAnimationClass(EditorAnimation.AnimationType.LONG_HIT);
		fakeAnimation.setParameter("convertTime", this.transitionTime);
		fakeAnimation.setParameter("path", this.registryName().toString());
		fakeAnimation.setParameter("armature", this.armature);
		
		this.fakeAnimation = fakeAnimation;
		
		return fakeAnimation;
	}
	
	@Override
	public DatapackLongHitAnimation get() {
		return this;
	}
	
	@Override
	public void setRegistryName(Identifier registryName) {
		this.registryName = registryName;
	}
	
	@Override
	public Identifier registryName() {
		return this.registryName;
	}
	
	@Override
	public boolean isPresent() {
		return true;
	}
}
