package dev.war.openfight.api.animation.types.datapack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.war.openfight.api.animation.LivingMotion;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.asset.JsonAssetLoader;
import dev.war.openfight.api.client.animation.property.ClientAnimationProperties;
import dev.war.openfight.api.client.animation.property.JointMask.JointMaskSet;
import dev.war.openfight.api.client.animation.property.JointMaskReloadListener;
import dev.war.openfight.api.model.Armature;

import java.util.Map;

public class DatapackMovementAnimation extends DatapackStaticAnimation {
	public DatapackMovementAnimation(float convertTime, boolean isRepeat, String path, AssetAccessor<? extends Armature> armature) {
		super(convertTime, isRepeat, path, armature);
	}
	
	@Override
	public EditorAnimation readAnimationFromJson(JsonAssetLoader.TransformFormat transformFormat, JsonArray rawAnimationJson) {
		EditorAnimation fakeAnimation = new EditorAnimation(this.registryName().toString(), this.armature, this.animationClip, transformFormat, rawAnimationJson);
		fakeAnimation.setAnimationClass(EditorAnimation.AnimationType.MOVEMENT);
		fakeAnimation.setParameter("convertTime", this.transitionTime);
		fakeAnimation.setParameter("isRepeat", this.isRepeat());
		fakeAnimation.setParameter("path", this.registryName().toString());
		fakeAnimation.setParameter("armature", this.armature);
		
		final JsonObject propertiesJson = fakeAnimation.getPropertiesJson();
		
		this.getProperty(ClientAnimationProperties.MULTILAYER_ANIMATION).ifPresentOrElse((multilayer) -> {
			JsonObject multilayerJson = new JsonObject();
			JsonObject baseJson = new JsonObject();
			
			baseJson.addProperty("priority", multilayer.getPriority().toString());
			
			final JsonArray baseMasks = new JsonArray();
			
			this.getProperty(ClientAnimationProperties.JOINT_MASK).ifPresent((jointMaskEntry) -> {
				for (Map.Entry<LivingMotion, JointMaskSet> entry : jointMaskEntry.getEntries()) {
					JsonObject maskObj = new JsonObject();
					maskObj.addProperty("livingmotion", entry.getKey().toString());
					maskObj.addProperty("type", JointMaskReloadListener.getKey(entry.getValue()).toString());
					baseMasks.add(maskObj);
				}
			});
			
			baseJson.add("masks", baseMasks);
			
			JsonObject compositeJson = new JsonObject();
			
			compositeJson.addProperty("priority", this.getPriority().toString());
			
			final JsonArray compositeMasks = new JsonArray();
			
			multilayer.getProperty(ClientAnimationProperties.JOINT_MASK).ifPresent((jointMaskEntry) -> {
				for (Map.Entry<LivingMotion, JointMaskSet> entry : jointMaskEntry.getEntries()) {
					JsonObject maskObj = new JsonObject();
					maskObj.addProperty("livingmotion", entry.getKey().toString());
					maskObj.addProperty("type", JointMaskReloadListener.getKey(entry.getValue()).toString());
					compositeMasks.add(maskObj);
				}
			});
			
			baseJson.add("masks", compositeMasks);
			
			multilayerJson.add("base", baseJson);
			multilayerJson.add("composite", compositeJson);
			propertiesJson.add("multilayer", multilayerJson);
		}, () -> {
			propertiesJson.add("layer", propertiesJson);
			propertiesJson.add("priority", propertiesJson);
			
			final JsonArray masks = new JsonArray();
			
			this.getProperty(ClientAnimationProperties.JOINT_MASK).ifPresent((jointMaskEntry) -> {
				for (Map.Entry<LivingMotion, JointMaskSet> entry : jointMaskEntry.getEntries()) {
					JsonObject maskObj = new JsonObject();
					maskObj.addProperty("livingmotion", entry.getKey().toString());
					maskObj.addProperty("type", JointMaskReloadListener.getKey(entry.getValue()).toString());
					masks.add(maskObj);
				}
			});
			
			propertiesJson.add("masks", masks);
		});
		
		this.fakeAnimation = fakeAnimation;
		
		return fakeAnimation;
	}
}
