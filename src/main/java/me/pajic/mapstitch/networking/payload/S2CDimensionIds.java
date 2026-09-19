package me.pajic.mapstitch.networking.payload;

import me.pajic.mapstitch.MapStitch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record S2CDimensionIds(List<Identifier> dimensionIds) implements CustomPacketPayload {

	public static final Type<S2CDimensionIds> TYPE = new Type<>(MapStitch.id("dimension_ids"));
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CDimensionIds> CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), S2CDimensionIds::dimensionIds,
            S2CDimensionIds::new
    );

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
