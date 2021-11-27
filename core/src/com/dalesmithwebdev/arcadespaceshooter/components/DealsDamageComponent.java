package com.dalesmithwebdev.arcadespaceshooter.components;

import com.badlogic.ashley.core.Component;

public class DealsDamageComponent implements Component {
    public int strength;
    public int damageTypeMask;
    public DealsDamageComponent(int str, int mask)
    {
        strength = str;
        damageTypeMask = mask;
    }
}
