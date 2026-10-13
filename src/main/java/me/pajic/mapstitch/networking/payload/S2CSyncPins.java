package me.pajic.mapstitch.networking.payload;

import me.pajic.mapstitch.MapStitch;
import me.pajic.mapstitch.pin.Pin;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record S2CSyncPins(List<Pin> pins) implements CustomPacketPayload {

    public static final Type<S2CSyncPins> TYPE = new Type<>(MapStitch.id("sync_pins"));
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSyncPins> CODEC = StreamCodec.composite(
            Pin.STREAM_CODEC.apply(ByteBufCodecs.list()), S2CSyncPins::pins,
            S2CSyncPins::new
    );

    @Override @NotNull
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
