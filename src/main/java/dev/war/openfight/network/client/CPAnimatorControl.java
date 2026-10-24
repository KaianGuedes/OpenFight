package dev.war.openfight.network.client;

import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import dev.war.openfight.api.animation.types.StaticAnimation;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.utils.ByteBufCodecsExtends;
import dev.war.openfight.network.common.AbstractAnimatorControl;
import dev.war.openfight.network.common.BiDirectionalAnimationVariable;

public class CPAnimatorControl extends AbstractAnimatorControl {
	public static final StreamCodec<RegistryFriendlyByteBuf, CPAnimatorControl> STREAM_CODEC =
		ByteBufCodecsExtends.composite7(
	        ByteBufCodecsExtends.enumCodec(AbstractAnimatorControl.Action.class),
	        CPAnimatorControl::action,
	        ByteBufCodecsExtends.ANIMATION,
	        CPAnimatorControl::animation,
	        ByteBufCodecs.FLOAT,
	        CPAnimatorControl::transitionTimeModifier,
	        ByteBufCodecs.BOOL,
	        CPAnimatorControl::pause,
	        ByteBufCodecs.BOOL,
	        CPAnimatorControl::isClientOnly,
	        ByteBufCodecs.BOOL,
	        CPAnimatorControl::responseToSender,
	        ByteBufCodecsExtends.listOf(BiDirectionalAnimationVariable.STREAM_CODEC),
	        AbstractAnimatorControl::animationVariables,
	        CPAnimatorControl::new
	    );
	
	private final boolean isClientOnly;
	private final boolean responseToSender;
	
	public CPAnimatorControl(
		  AbstractAnimatorControl.Action action
		, AssetAccessor<? extends StaticAnimation> animation
		, float transitionTimeModifier
		, boolean pause
		, boolean clientOnly
		, boolean resendToSender
	) {
		super(action, animation, transitionTimeModifier, pause);
		
		this.isClientOnly = clientOnly;
		this.responseToSender = resendToSender;
	}
	
	public CPAnimatorControl(
		  AbstractAnimatorControl.Action action
		, AssetAccessor<? extends StaticAnimation> animation
		, float transitionTimeModifier
		, boolean pause
		, boolean clientOnly
		, boolean resendToSender
		, List<BiDirectionalAnimationVariable> animationVariables
	) {
		super(action, animation, transitionTimeModifier, pause, animationVariables);
		
		this.isClientOnly = clientOnly;
		this.responseToSender = resendToSender;
	}
	
	public boolean isClientOnly() {
		return this.isClientOnly;
	}
	
	public boolean responseToSender() {
		return this.responseToSender;
	}
}