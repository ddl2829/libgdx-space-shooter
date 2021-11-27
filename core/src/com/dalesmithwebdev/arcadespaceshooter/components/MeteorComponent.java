package com.dalesmithwebdev.arcadespaceshooter.components;

import com.badlogic.ashley.core.Component;

public class MeteorComponent implements Component {
    public boolean isBig;
    public MeteorComponent(boolean big)
    {
        isBig = big;
    }
}
