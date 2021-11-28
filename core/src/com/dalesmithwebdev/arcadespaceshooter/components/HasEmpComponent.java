package com.dalesmithwebdev.arcadespaceshooter.components;

import com.badlogic.ashley.core.Component;

public class HasEmpComponent implements Component {
    public double shotInterval;
    public double timeSinceLastShot = 0;

    public HasEmpComponent(double shotInterval) {
        this.shotInterval = shotInterval;
    }
}
