package com.example;

//Mode1: Prioritize Autocast Over Manual Selection

public class Mode1 extends LEDMode {
    @Override
    public void activate() {
        System.out.println("Mode 1 activated: Prioritize Autocast.");
    }

    @Override
    public void deactivate() {
        System.out.println("Mode 1 deactivated.");
    }

    @Override
    public void onSpellSelected(String spellName) {
        System.out.println("Autocast spell selected: " + spellName);
    }

    @Override
    public void onSpellCasted(int eventId) {
        System.out.println("Spell casted in Mode 1: Event ID = " + eventId);
    }
}
