package me.pajic.mapstitch.networking;

import me.pajic.mapstitch.MapStitchClient;
import me.pajic.mapstitch.networking.payload.S2CDimensionIds;
import me.pajic.mapstitch.worldmap.WorldMapScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

public class ClientNetworkEvents {

    public static void setDimensionIds(S2CDimensionIds payload) {
        WorldMapScreen.dimensionIds = payload.dimensionIds();
    }

    public static void openWorldMapScreen(Player player, int scaleOverride) {
        player.playSound(SoundEvents.BOOK_PAGE_TURN);
        //~ if <26.1 'setScreenAndShow' -> 'setScreen'
        Minecraft.getInstance().setScreenAndShow(new WorldMapScreen(Math.max(scaleOverride, -1)));
    }

    public static void syncWorldMapScreen() {
        WorldMapScreen.clearMaps();
    }

    public static void playSound(Player player, Holder<SoundEvent> sound) {
        //noinspection deprecation
        if (sound.is(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT))) {
            if (MapStitchClient.CONFIG.sounds.atlasMapCreation.get()) player.playSound(sound.value());
        }
        else player.playSound(sound.value());
    }
}
