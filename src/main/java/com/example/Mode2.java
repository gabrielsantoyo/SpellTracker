package com.example;

//Mode2: Blend Manual and Autocast Behaviors

public class Mode2 extends LEDMode {
    @Override
    public void activate() {
        System.out.println("Mode 2 activated: Blend Manual and Autocast.");
    }

    @Override
    public void deactivate() {
        System.out.println("Mode 2 deactivated.");
    }

    @Override
    public void onSpellSelected(String spellName) {
        System.out.println("Spell selected in blended mode: " + spellName);
    }

    @Override
    public void onSpellCasted(int eventId) {
        System.out.println("Spell casted in Mode 2: Event ID = " + eventId);
    }
}
