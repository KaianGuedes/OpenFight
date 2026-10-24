package dev.war.openfight.network.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import dev.war.openfight.api.animation.LivingMotion;
import dev.war.openfight.api.animation.types.StaticAnimation;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.utils.ByteBufCodecsExtends;
import dev.war.openfight.network.ManagedCustomPacketPayload;

public record SPChangeLivingMotion(List<LivingMotion> livingMotions, List<AssetAccessor<? extends StaticAnimation>> animations, int entityId, boolean setChangesAsDefault) implements ManagedCustomPacketPayload {
	public static final StreamCodec<ByteBuf, SPChangeLivingMotion> STREAM_CODEC =
		StreamCodec.composite(
			ByteBufCodecsExtends.listOf(ByteBufCodecsExtends.extendableEnumCodec(LivingMotion.ENUM_MANAGER)),
			SPChangeLivingMotion::livingMotions,
			ByteBufCodecsExtends.listOf(ByteBufCodecsExtends.ANIMATION),
			SPChangeLivingMotion::animations,
			ByteBufCodecs.INT,
			SPChangeLivingMotion::entityId,
			ByteBufCodecs.BOOL,
			SPChangeLivingMotion::setChangesAsDefault,
			SPChangeLivingMotion::new
	    );
	
	public SPChangeLivingMotion(int entityId) {
		this(entityId, false);
	}
	
	public SPChangeLivingMotion(int entityId, boolean setChangesAsDefault) {
		this(new ArrayList<>(), new ArrayList<>(), entityId, setChangesAsDefault);
	}
	
	public SPChangeLivingMotion putPair(LivingMotion motion, AssetAccessor<? extends StaticAnimation> animation) {
		if (animation != null) {
			this.livingMotions.add(motion);
			this.animations.add(animation);
		}
		
		return this;
	}
	
	public void putEntries(Set<Map.Entry<LivingMotion, AssetAccessor<? extends StaticAnimation>>> motionSet) {
		motionSet.forEach((entry) -> {
			if (entry.getValue() != null) {
				this.livingMotions.add(entry.getKey());
				this.animations.add(entry.getValue());
			}
		});
	}
}