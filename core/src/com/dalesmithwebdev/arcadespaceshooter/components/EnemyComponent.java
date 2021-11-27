package com.dalesmithwebdev.arcadespaceshooter.components;

import com.badlogic.ashley.core.Component;

public class EnemyComponent implements Component {
    public double shotInterval;
    public double timeSinceLastShot;
    public double movementSpeed = 0.5;

    public EnemyComponent(double interval, double randomOffset, double movementSpeed)
    {
        shotInterval = interval;
        timeSinceLastShot = randomOffset;
        this.movementSpeed = movementSpeed;
    }
}
