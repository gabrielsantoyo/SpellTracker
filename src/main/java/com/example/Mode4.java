package com.example;

import java.awt.Color;

// Mode4: Player-selected Color Glow
public class Mode4 {
    private final Color selectedColor;

    public Mode4(Color selectedColor) {
        this.selectedColor = selectedColor;
    }

    public void activate() {
        // Logic to activate the glow effect with the selected color
        System.out.println("Mode 4 activated with color: " + selectedColor);
        // Implement your actual LED or hardware control logic here
    }

    public void deactivate() {
        // Logic to deactivate the glow effect
        System.out.println("Mode 4 deactivated.");
        // Implement your actual LED or hardware control logic here
    }

    public void onSpellSelected(String spellName) {
        // When a spell is selected, show the glow color
        System.out.println("Glow color remains fixed in Mode 4: " + selectedColor);
        // You can also adjust behavior depending on the spell if needed
    }

    public void onSpellCasted(int eventId) {
        // When a spell is cast, trigger the glow effect with the selected color
        System.out.println("Glow effect triggered for Event ID: " + eventId + " with color: " + selectedColor);
        // Implement LED control logic for casting a spell and triggering the glow effect
    }

    // You can also use LEDMode for triggering specific LED modes or behaviors based on game states.
    public void triggerLEDMode(LEDMode mode) {
        LEDController controller = new LEDController();
        controller.setLEDMode(mode);
    }
}
