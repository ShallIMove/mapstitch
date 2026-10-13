package me.pajic.mapstitch.networking.payload;

import me.pajic.mapstitch.MapStitch;
import me.pajic.mapstitch.pin.Pin;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record C2SPlacePin(String name, int color, Identifier dimension, int x, int z) implements CustomPacketPayload {

    public static final Type<C2SPlacePin> TYPE = new Type<>(MapStitch.id("place_pin"));
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SPlacePin> CODEC = StreamCodec.composite(
            ByteBufCodecs.stringUtf8(Pin.MAX_NAME_LENGTH), C2SPlacePin::name,
            ByteBufCodecs.INT, C2SPlacePin::color,
            Identifier.STREAM_CODEC, C2SPlacePin::dimension,
            ByteBufCodecs.VAR_INT, C2SPlacePin::x,
            ByteBufCodecs.VAR_INT, C2SPlacePin::z,
            C2SPlacePin::new
    );

    @Override @NotNull
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
