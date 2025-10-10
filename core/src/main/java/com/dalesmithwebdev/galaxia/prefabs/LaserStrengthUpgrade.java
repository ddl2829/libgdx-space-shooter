package com.dalesmithwebdev.galaxia.prefabs;

import com.badlogic.ashley.core.Entity;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.constants.DamageTypeConstants;

public class LaserStrengthUpgrade extends Entity {
    public LaserStrengthUpgrade(int level, float xPos, float yPos) {
        if(level == 1) {
            this.add(new RenderComponent(ArcadeSpaceShooter.textures.findRegion("powerupGreen_bolt"), RenderComponent.PLANE_ABOVE));
        }
        if(level == 2) {
            this.add(new RenderComponent(ArcadeSpaceShooter.textures.findRegion("powerupBlue_bolt"), RenderComponent.PLANE_ABOVE));
        }
        this.add(new PositionComponent(xPos, yPos));
        this.add(new SpeedComponent(0, -3));
        this.add(new TakesDamageComponent(1, DamageTypeConstants.PLAYER));
        this.add(new LaserStrengthUpgradeComponent());
    }
}
