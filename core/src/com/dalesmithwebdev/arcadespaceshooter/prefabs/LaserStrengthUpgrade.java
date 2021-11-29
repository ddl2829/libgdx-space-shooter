package com.dalesmithwebdev.arcadespaceshooter.prefabs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.dalesmithwebdev.arcadespaceshooter.components.*;
import com.dalesmithwebdev.arcadespaceshooter.systems.DamageSystem;

public class LaserStrengthUpgrade extends Entity {
    public LaserStrengthUpgrade(int level, float xPos, float yPos) {
        if(level == 1) {
            this.add(new RenderComponent(new Texture(Gdx.files.internal("power-ups/powerupGreen_bolt.png")), RenderComponent.PLANE_ABOVE));
        }
        if(level == 2) {
            this.add(new RenderComponent(new Texture(Gdx.files.internal("power-ups/powerupBlue_bolt.png")), RenderComponent.PLANE_ABOVE));
        }
        this.add(new PositionComponent(xPos, yPos));
        this.add(new SpeedComponent(0, -1));
        this.add(new TakesDamageComponent(1, DamageSystem.PLAYER));
        this.add(new LaserStrengthUpgradeComponent());
    }
}
