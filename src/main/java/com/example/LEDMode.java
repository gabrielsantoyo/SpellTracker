package com.example;

public abstract class LEDMode {
    public abstract void activate();

    public abstract void deactivate();

    public abstract void onSpellSelected(String spellName);

    public abstract void onSpellCasted(int eventId);
}
