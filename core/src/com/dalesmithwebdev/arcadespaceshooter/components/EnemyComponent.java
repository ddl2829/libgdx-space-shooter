package com.dalesmithwebdev.arcadespaceshooter.components;

import com.badlogic.ashley.core.Component;

public class EnemyComponent implements Component {
    public double shotInterval;
    public double timeSinceLastShot;
    public EnemyComponent(double interval, double randomOffset)
    {
        shotInterval = interval;
        timeSinceLastShot = randomOffset;
    }
}
