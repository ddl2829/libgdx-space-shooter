package com.dalesmithwebdev.arcadespaceshooter.prefabs;

import com.badlogic.ashley.core.Entity;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.*;
import com.dalesmithwebdev.arcadespaceshooter.systems.DamageSystem;

public class BombUpgrade extends Entity {
    public BombUpgrade(float xPos, float yPos) {
        this.add(new RenderComponent(ArcadeSpaceShooter.bombUpgrade, RenderComponent.PLANE_ABOVE));
        this.add(new PositionComponent(xPos, yPos));
        this.add(new SpeedComponent(0, -1));
        this.add(new TakesDamageComponent(1, DamageSystem.PLAYER));
        this.add(new BombUpgradeComponent());
    }
}
