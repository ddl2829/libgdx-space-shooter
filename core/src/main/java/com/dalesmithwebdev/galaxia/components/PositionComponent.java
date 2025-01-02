package com.dalesmithwebdev.galaxia.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class PositionComponent implements Component {
    public Vector2 position;

    public PositionComponent(Vector2 p)
    {
        position = p;
    }

    public PositionComponent(float x, float y)
    {
        position = new Vector2(x, y);
    }
}
