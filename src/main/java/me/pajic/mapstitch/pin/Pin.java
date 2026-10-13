package me.pajic.mapstitch.pin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.UUID;

public record Pin(UUID id, String name, int color, Identifier dimension, int x, int z) {

    public static final int MAX_NAME_LENGTH = 32;

    public static final Codec<Pin> CODEC = RecordCodecBuilder.create(i -> i.group(
            UUIDUtil.CODEC.fieldOf("id").forGetter(Pin::id),
            Codec.STRING.fieldOf("name").forGetter(Pin::name),
            Codec.INT.fieldOf("color").forGetter(Pin::color),
            Identifier.CODEC.fieldOf("dimension").forGetter(Pin::dimension),
            Codec.INT.fieldOf("x").forGetter(Pin::x),
            Codec.INT.fieldOf("z").forGetter(Pin::z)
    ).apply(i, Pin::new));

    public static final StreamCodec<ByteBuf, Pin> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, Pin::id,
            ByteBufCodecs.stringUtf8(MAX_NAME_LENGTH), Pin::name,
            ByteBufCodecs.INT, Pin::color,
            Identifier.STREAM_CODEC, Pin::dimension,
            ByteBufCodecs.VAR_INT, Pin::x,
            ByteBufCodecs.VAR_INT, Pin::z,
            Pin::new
    );
}
