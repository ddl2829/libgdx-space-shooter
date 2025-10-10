package com.dalesmithwebdev.galaxia.prefabs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.constants.DamageTypeConstants;
import com.dalesmithwebdev.galaxia.utility.Rand;

public class SmallMeteor extends Entity {
    public SmallMeteor(int yPos) {
        this(Rand.nextInt((int)ArcadeSpaceShooter.screenRect.width), yPos);
    }

    public SmallMeteor(float xPos, float yPos) {
        int[] speeds = new int[] { 3, 3, 3, 4, 4, 4, 5, 5, 6, 6 };
        int selectedSpeed = -speeds[Rand.nextInt(speeds.length)];

        this.add(new MeteorComponent(false));
        this.add(new RenderComponent(ArcadeSpaceShooter.smallMeteors.get(Rand.nextInt(ArcadeSpaceShooter.smallMeteors.size())), RenderComponent.PLANE_MAIN));
        this.add(new TakesDamageComponent(2, DamageTypeConstants.LASER ^ DamageTypeConstants.MISSILE ^ DamageTypeConstants.BOMB));
        this.add(new DealsDamageComponent(5, DamageTypeConstants.METEOR));
        this.add(new SpeedComponent(0, selectedSpeed));
        this.add(new PositionComponent(new Vector2(xPos, yPos)));
    }

    public SmallMeteor(float xPos, float yPos, float ySpeed) {
        this.add(new MeteorComponent(false));
        this.add(new RenderComponent(ArcadeSpaceShooter.smallMeteors.get(Rand.nextInt(ArcadeSpaceShooter.smallMeteors.size())), RenderComponent.PLANE_MAIN));
        this.add(new TakesDamageComponent(2, DamageTypeConstants.LASER ^ DamageTypeConstants.MISSILE ^ DamageTypeConstants.BOMB));
        this.add(new DealsDamageComponent(5, DamageTypeConstants.METEOR));
        this.add(new SpeedComponent(0, (int)ySpeed));
        this.add(new PositionComponent(new Vector2(xPos, yPos)));
    }
}
