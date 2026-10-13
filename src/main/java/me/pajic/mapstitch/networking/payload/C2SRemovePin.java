package me.pajic.mapstitch.networking.payload;

import me.pajic.mapstitch.MapStitch;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record C2SRemovePin(UUID pinId) implements CustomPacketPayload {

    public static final Type<C2SRemovePin> TYPE = new Type<>(MapStitch.id("remove_pin"));
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SRemovePin> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, C2SRemovePin::pinId,
            C2SRemovePin::new
    );

    @Override @NotNull
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
