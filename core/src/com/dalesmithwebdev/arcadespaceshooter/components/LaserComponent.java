package com.dalesmithwebdev.arcadespaceshooter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;

public class LaserComponent implements Component {
    public Texture explosionTexture;
    public LaserComponent(Texture explosion)
    {
        explosionTexture = explosion;
    }
}
