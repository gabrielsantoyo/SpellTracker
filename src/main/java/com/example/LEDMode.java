package com.example;

/**
 * LEDMode defines the modes for the LED and their corresponding ESP8266 commands.
 */
public enum LEDMode {
    DIM("dim"),     // Command to dim the LED
    CAST("cast");   // Command to brighten LED for a spell cast

    private final String command;

    LEDMode(String command) {
        this.command = command;
    }

    /**
     * Returns the command string associated with the LED mode.
     *
     * @return The command string.
     */
    public String getCommand() {
        return command;
    }
}
