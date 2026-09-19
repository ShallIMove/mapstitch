package me.pajic.mapstitch.networking.payload;

import me.pajic.mapstitch.MapStitch;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.NotNull;

public record S2CPlaySound(Holder<SoundEvent> sound) implements CustomPacketPayload {

	public static final Type<S2CPlaySound> TYPE = new Type<>(MapStitch.id("s2c_play_sound"));
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CPlaySound> CODEC = StreamCodec.composite(
			SoundEvent.STREAM_CODEC, S2CPlaySound::sound,
			S2CPlaySound::new
	);

	@Override @NotNull
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
