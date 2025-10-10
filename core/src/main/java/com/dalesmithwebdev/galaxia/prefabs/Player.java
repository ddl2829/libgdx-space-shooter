package com.dalesmithwebdev.galaxia.prefabs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.constants.DamageTypeConstants;

public class Player extends Entity {
    public Player() {
        super();

        this.add(new PlayerComponent());
        this.add(new HasLasersComponent(150, HasLasersComponent.SINGLE));
        this.add(new SpeedComponent(0, 0));
        this.add(new TakesDamageComponent(50, DamageTypeConstants.ENEMY ^ DamageTypeConstants.ENEMY_LASER ^ DamageTypeConstants.METEOR));
        this.add(new DealsDamageComponent(5, DamageTypeConstants.PLAYER));

        // Add RenderComponent and PositionComponent on initial creation
        RenderComponent rc = new RenderComponent(ArcadeSpaceShooter.shipTextures.toArray(new com.badlogic.gdx.graphics.g2d.TextureRegion[ArcadeSpaceShooter.shipTextures.size()]), RenderComponent.PLANE_MAIN);
        this.add(rc);
        this.add(new PositionComponent(new Vector2(
                (ArcadeSpaceShooter.screenRect.width / 2),
                rc.height + 20
        )));

        // Debug upgrades
//        this.add(new HasMissilesComponent(1000));
//        this.add(new HasBombsComponent(500));
//        this.add(new HasShieldComponent());
//        this.add(new HasEmpComponent(10000));
    }
}
