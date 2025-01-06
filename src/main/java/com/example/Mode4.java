package com.example;

import java.awt.Color;

public class Mode4 {
    private final Color selectedColor; // The primary glow color
    private static final long CAST_DURATION_MS = 500; // Duration for the "cast" effect (0.5 seconds)
    private final LEDController ledController; // Instance of LEDController to handle actual LED commands

    // Constructor takes the selected color and the LEDController instance
    public Mode4(Color selectedColor, LEDController ledController) {
        this.selectedColor = selectedColor;
        this.ledController = ledController;
    }

    public void activate() {
        // Logic to activate the dim glow effect with the selected color
        setDimGlow();
        System.out.println("Mode 4 activated with color: " + selectedColor);
    }

    public void deactivate() {
        // Logic to deactivate the glow effect
        System.out.println("Mode 4 deactivated.");
        turnOffGlow();
    }

    public void onSpellSelected(String spellName) {
        // Set the glow to dim when a spell is selected
        System.out.println("Spell selected: " + spellName + ". Setting glow to dim with color: " + selectedColor);
        setDimGlow();
    }

    public void onSpellCasted(int eventId) {
        // Brighten the glow briefly when a spell is cast
        System.out.println("Spell cast (Event ID: " + eventId + "). Triggering glow cast effect.");
        triggerCastGlow();
    }

    private void setDimGlow() {
        // Implement logic to set a dim glow effect
        System.out.println("Setting dim glow with color: " + selectedColor);
        ledController.setLEDMode(LEDMode.DIM);  // Send the command to set the glow to dim
    }

    private void triggerCastGlow() {
        // Brighten glow for the cast effect
        System.out.println("Triggering bright glow for cast with color: " + selectedColor);
        ledController.setLEDMode(LEDMode.CAST);  // Send the command to brighten the glow

        // Schedule a return to dim glow after 0.5 seconds
        try {
            Thread.sleep(CAST_DURATION_MS); // Pause for 0.5 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted during cast glow effect: " + e.getMessage());
        }

        // Return to dim glow
        setDimGlow();
    }

    private void turnOffGlow() {
        // Implement logic to turn off the glow effect
        System.out.println("Turning off glow.");
        ledController.setLEDMode(LEDMode.OFF);  // Send the command to turn off the glow
    }

    public void triggerLEDMode(LEDMode mode) {
        // Example of triggering specific LED behavior
        ledController.setLEDMode(mode);
        System.out.println("Triggered LED mode: " + mode);
    }
}
