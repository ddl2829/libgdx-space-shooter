package com.dalesmithwebdev.galaxia.components;

import com.badlogic.ashley.core.Component;

public class TakesDamageComponent implements Component {
    public int health;
    public int takesDamageFromMask;
    public int maxHealth;
    public TakesDamageComponent(int hp, int mask)
    {
        health = hp;
        maxHealth = hp;
        takesDamageFromMask = mask;
    }
}
