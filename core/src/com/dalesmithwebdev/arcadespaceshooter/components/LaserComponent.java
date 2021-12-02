package com.dalesmithwebdev.arcadespaceshooter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LaserComponent implements Component {
    public TextureRegion explosionTexture;
    public LaserComponent(TextureRegion explosion)
    {
        explosionTexture = explosion;
    }
}
