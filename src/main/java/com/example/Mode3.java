package com.example;

//Mode3: LED Split or Animation Effect

public class Mode3 extends LEDMode {
    @Override
    public void activate() {
        System.out.println("Mode 3 activated: LED Split Animation.");
    }

    @Override
    public void deactivate() {
        System.out.println("Mode 3 deactivated.");
    }

    @Override
    public void onSpellSelected(String spellName) {
        System.out.println("Spell selected for split animation: " + spellName);
    }

    @Override
    public void onSpellCasted(int eventId) {
        System.out.println("Split animation triggered for Event ID: " + eventId);
    }
}
