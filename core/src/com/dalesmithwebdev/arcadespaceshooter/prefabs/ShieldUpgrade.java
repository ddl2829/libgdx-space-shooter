package com.dalesmithwebdev.arcadespaceshooter.prefabs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.dalesmithwebdev.arcadespaceshooter.components.*;
import com.dalesmithwebdev.arcadespaceshooter.systems.DamageSystem;

public class ShieldUpgrade extends Entity {
    public ShieldUpgrade(float xPos, float yPos) {
        this.add(new RenderComponent(new Texture(Gdx.files.internal("power-ups/powerupBlue_shield.png")), RenderComponent.PLANE_ABOVE));
        this.add(new PositionComponent(xPos, yPos));
        this.add(new SpeedComponent(0, -1));
        this.add(new TakesDamageComponent(1, DamageSystem.PLAYER));
        this.add(new ShieldUpgradeComponent());
    }
}
