package me.pajic.mapstitch.networking.payload;

import me.pajic.mapstitch.MapStitch;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record C2SSharePin(UUID pinId, UUID target) implements CustomPacketPayload {

    public static final Type<C2SSharePin> TYPE = new Type<>(MapStitch.id("share_pin"));
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SSharePin> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, C2SSharePin::pinId,
            UUIDUtil.STREAM_CODEC, C2SSharePin::target,
            C2SSharePin::new
    );

    @Override @NotNull
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
