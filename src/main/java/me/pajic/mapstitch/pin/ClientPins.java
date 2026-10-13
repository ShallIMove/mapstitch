package me.pajic.mapstitch.pin;

import java.util.List;

public class ClientPins {

    private static List<Pin> pins = List.of();

    public static List<Pin> get() {
        return pins;
    }

    public static void set(List<Pin> newPins) {
        pins = List.copyOf(newPins);
    }
}
