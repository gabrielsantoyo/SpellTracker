package com.example;

/**
 * LEDMode defines the modes for the LED and their corresponding ESP8266 commands.
 */
public enum LEDMode {
    DIM("dim"),
    CAST("cast"),
    OFF("off");

    private final String command;

    LEDMode(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
