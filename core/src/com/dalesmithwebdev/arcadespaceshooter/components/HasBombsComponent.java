package com.dalesmithwebdev.arcadespaceshooter.components;

import com.badlogic.ashley.core.Component;

public class HasBombsComponent implements Component {
    public double shotInterval;
    public double timeSinceLastShot = 0;
}
