package dev.war.openfight.api.animation.types.datapack;

import com.google.gson.JsonArray;

import net.minecraft.resources.Identifier;
import dev.war.openfight.api.animation.AnimationClip;
import dev.war.openfight.api.animation.types.KnockdownAnimation;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.asset.JsonAssetLoader;
import dev.war.openfight.api.model.Armature;

public class DatapackKnockdownAnimation extends KnockdownAnimation implements DatapackAnimation<DatapackKnockdownAnimation> {
	protected AnimationClip clip;
	protected EditorAnimation fakeAnimation;
	protected Identifier registryName;
	
	public DatapackKnockdownAnimation(float transitionTime, String path, AssetAccessor<? extends Armature> armature) {
		super(transitionTime, path, armature);
		
		((DatapackAnimation<DatapackKnockdownAnimation>)this).setRegistryName(Identifier.parse(path));
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
		fakeAnimation.setAnimationClass(EditorAnimation.AnimationType.KNOCK_DOWN);
		fakeAnimation.setParameter("convertTime", this.transitionTime);
		fakeAnimation.setParameter("path", this.registryName().toString());
		fakeAnimation.setParameter("armature", this.armature);
		
		this.fakeAnimation = fakeAnimation;
		
		return fakeAnimation;
	}

	@Override
	public DatapackKnockdownAnimation get() {
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
