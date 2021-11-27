package com.dalesmithwebdev.arcadespaceshooter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class SpeedComponent implements Component {
    public Vector2 motion;

    public SpeedComponent(Vector2 p)
    {
        motion = p;
    }

    public SpeedComponent(int x, int y)
    {
        motion = new Vector2(x, y);
    }
}
