package com.dalesmithwebdev.galaxia.prefabs;

import com.badlogic.ashley.core.Entity;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.systems.DamageSystem;

public class ShieldUpgrade extends Entity {
    public ShieldUpgrade(float xPos, float yPos) {
        this.add(new RenderComponent(ArcadeSpaceShooter.textures.findRegion("powerupBlue_shield"), RenderComponent.PLANE_ABOVE));
        this.add(new PositionComponent(xPos, yPos));
        this.add(new SpeedComponent(0, -1));
        this.add(new TakesDamageComponent(1, DamageSystem.PLAYER));
        this.add(new ShieldUpgradeComponent());
    }
}
