package me.pajic.mapstitch.pin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import me.pajic.mapstitch.MapStitch;
import me.pajic.mapstitch.networking.payload.S2CSyncPins;
import me.pajic.mapstitch.platform.MultiLoaderUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PinStorage {

    public static final int MAX_PINS = 512;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<Path, List<Pin>> CACHE = new HashMap<>();

    private static Path file(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        return server.getWorldPath(LevelResource.ROOT)
                .resolve("data").resolve(MapStitch.MOD_ID).resolve("pins")
                .resolve(player.getUUID() + ".json")
                .toAbsolutePath().normalize();
    }

    public static List<Pin> get(ServerPlayer player) {
        return CACHE.computeIfAbsent(file(player), PinStorage::read);
    }

    public static boolean add(ServerPlayer player, Pin pin) {
        List<Pin> pins = get(player);
        if (pins.size() >= MAX_PINS) return false;
        pins.add(pin);
        write(file(player), pins);
        return true;
    }

    public static @Nullable Pin remove(ServerPlayer player, UUID id) {
        List<Pin> pins = get(player);
        Iterator<Pin> it = pins.iterator();
        while (it.hasNext()) {
            Pin pin = it.next();
            if (pin.id().equals(id)) {
                it.remove();
                write(file(player), pins);
                return pin;
            }
        }
        return null;
    }

    public static void sync(ServerPlayer player) {
        MultiLoaderUtil.INSTANCE.s2c(player, new S2CSyncPins(List.copyOf(get(player))));
    }

    public static void unload(ServerPlayer player) {
        CACHE.remove(file(player));
    }

    private static List<Pin> read(Path path) {
        List<Pin> pins = new ArrayList<>();
        if (Files.exists(path)) try (Reader reader = Files.newBufferedReader(path)) {
            JsonElement json = JsonParser.parseReader(reader);
            Pin.CODEC.listOf().parse(JsonOps.INSTANCE, json)
                    .resultOrPartial(MapStitch.LOGGER::warn)
                    .ifPresent(pins::addAll);
        } catch (Exception e) {
            MapStitch.LOGGER.warn("Couldn't read pins from {}", path, e);
        }
        return pins;
    }

    private static void write(Path path, List<Pin> pins) {
        try {
            Files.createDirectories(path.getParent());
            JsonElement json = Pin.CODEC.listOf().encodeStart(JsonOps.INSTANCE, pins).getOrThrow();
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(json, writer);
            }
        } catch (Exception e) {
            MapStitch.LOGGER.warn("Couldn't save pins to {}", path, e);
        }
    }
}
