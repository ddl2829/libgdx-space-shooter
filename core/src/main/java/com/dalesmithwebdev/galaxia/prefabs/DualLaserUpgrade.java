package com.dalesmithwebdev.galaxia.prefabs;

import com.badlogic.ashley.core.Entity;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.constants.DamageTypeConstants;

public class DualLaserUpgrade extends Entity {
    public DualLaserUpgrade(float xPos, float yPos) {
        this.add(new RenderComponent(ArcadeSpaceShooter.textures.findRegion("pill_red"), RenderComponent.PLANE_ABOVE));
        this.add(new PositionComponent(xPos, yPos));
        this.add(new SpeedComponent(0, -3));
        this.add(new TakesDamageComponent(1, DamageTypeConstants.PLAYER));
        this.add(new DualLaserUpgradeComponent());
    }
}
