package dev.war.openfight.api.animation.types;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.resources.Identifier;
import dev.war.openfight.api.animation.AnimationManager.AnimationAccessor;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.model.Armature;

public class DirectStaticAnimation extends StaticAnimation implements AnimationAccessor<DirectStaticAnimation> {
	private Identifier registryName;
	
	public DirectStaticAnimation() {
		this.accessor = this;
	}
	
	public DirectStaticAnimation(float transitionTime, boolean isRepeat, Identifier registryName, AssetAccessor<? extends Armature> armature) {
		super(transitionTime, isRepeat, registryName.toString(), armature);
		
		this.registryName = registryName;
		this.accessor = this;
	}
	
	/* Multilayer, Pov animation Constructor */
	@ApiStatus.Internal
	public DirectStaticAnimation(Identifier baseAnimPath, float transitionTime, boolean repeatPlay, String registryName, AssetAccessor<? extends Armature> armature) {
		super(baseAnimPath, transitionTime, repeatPlay, registryName, armature);
		
		this.registryName = Identifier.parse(registryName);
	}
	
	@Override
	public DirectStaticAnimation get() {
		return this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <A extends DynamicAnimation> AnimationAccessor<A> getAccessor() {
		return (AnimationAccessor<A>)this;
	}
	
	@Override
	public Identifier registryName() {
		return this.registryName;
	}

	@Override
	public boolean isPresent() {
		return true;
	}

	@Override
	public int id() {
		return -1;
	}
	
	@Override
	public int getId() {
		return -1;
	}
	
	@Override
	public boolean inRegistry() {
		return false;
	}
}