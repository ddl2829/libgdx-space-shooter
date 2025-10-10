package com.dalesmithwebdev.galaxia.prefabs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.constants.DamageTypeConstants;
import com.dalesmithwebdev.galaxia.utility.Rand;

public class LargeMeteor extends Entity {
    public LargeMeteor(int yPos) {
        this(Rand.nextInt((int)ArcadeSpaceShooter.screenRect.width), yPos);
    }

    public LargeMeteor(float xPos, float yPos) {
        int[] speeds = new int[] { 2, 2, 2, 3, 3, 3, 4, 4, 5, 6 };
        int selectedSpeed = -speeds[Rand.nextInt(speeds.length)];

        this.add(new MeteorComponent(true));
        this.add(new RenderComponent(ArcadeSpaceShooter.bigMeteors.get(Rand.nextInt(ArcadeSpaceShooter.bigMeteors.size())), RenderComponent.PLANE_MAIN));
        this.add(new TakesDamageComponent(8, DamageTypeConstants.LASER ^ DamageTypeConstants.MISSILE ^ DamageTypeConstants.BOMB));
        this.add(new DealsDamageComponent(20, DamageTypeConstants.METEOR));
        this.add(new SpeedComponent(0, selectedSpeed));
        this.add(new PositionComponent(new Vector2(xPos, yPos)));
    }

    public LargeMeteor(float xPos, float yPos, float ySpeed) {
        this.add(new MeteorComponent(true));
        this.add(new RenderComponent(ArcadeSpaceShooter.bigMeteors.get(Rand.nextInt(ArcadeSpaceShooter.bigMeteors.size())), RenderComponent.PLANE_MAIN));
        this.add(new TakesDamageComponent(8, DamageTypeConstants.LASER ^ DamageTypeConstants.MISSILE ^ DamageTypeConstants.BOMB));
        this.add(new DealsDamageComponent(20, DamageTypeConstants.METEOR));
        this.add(new SpeedComponent(0, (int)ySpeed));
        this.add(new PositionComponent(new Vector2(xPos, yPos)));
    }
}
