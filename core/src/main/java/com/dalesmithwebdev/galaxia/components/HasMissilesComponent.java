package com.dalesmithwebdev.galaxia.components;

import com.badlogic.ashley.core.Component;

public class HasMissilesComponent implements Component {
    public double shotInterval;
    public double timeSinceLastShot = 0;

    public HasMissilesComponent(int shotInterval) {
        this.shotInterval = shotInterval;
    }
}
