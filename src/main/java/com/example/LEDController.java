package com.example;

public class LEDController {
    private LEDMode currentMode;

    public void setMode(LEDMode mode) {
        if (currentMode != null) {
            currentMode.deactivate();
        }
        currentMode = mode;
        currentMode.activate();
    }

    public void handleSpellSelection(String spellName) {
        if (currentMode != null) {
            currentMode.onSpellSelected(spellName);
        }
    }

    public void handleSpellCasted(int eventId) {
        if (currentMode != null) {
            currentMode.onSpellCasted(eventId);
        }
    }
}
