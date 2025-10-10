package com.dalesmithwebdev.galaxia.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class SpeedComponent implements Component {
    public Vector2 motion;
    public double movementSpeed = 0.5;

    public SpeedComponent(double movementSpeed)
    {
        this.motion = new Vector2(0, 0);
        this.movementSpeed = movementSpeed;
    }

    public SpeedComponent(int x, int y) {
        this.motion = new Vector2(x, y);
    }
}
