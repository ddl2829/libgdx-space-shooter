package com.dalesmithwebdev.arcadespaceshooter.prefabs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.*;
import com.dalesmithwebdev.arcadespaceshooter.systems.DamageSystem;
import com.dalesmithwebdev.arcadespaceshooter.utility.Rand;

public class SmallMeteor extends Entity {
    public SmallMeteor(int yPos) {
        this.add(new MeteorComponent(false));
        this.add(new RenderComponent(ArcadeSpaceShooter.smallMeteors.get(Rand.nextInt(ArcadeSpaceShooter.smallMeteors.size())), RenderComponent.PLANE_MAIN));
        this.add(new TakesDamageComponent(2, DamageSystem.LASER ^ DamageSystem.MISSILE ^ DamageSystem.BOMB));
        this.add(new DealsDamageComponent(5, DamageSystem.METEOR));
        int[] speeds = new int[] { 3, 3, 3, 4, 4, 4, 5, 5, 6, 6 };
        this.add(new SpeedComponent(0, -speeds[Rand.nextInt(speeds.length)]));
        this.add(new PositionComponent(new Vector2(Rand.nextInt((int)ArcadeSpaceShooter.screenRect.width), yPos)));
    }
}
