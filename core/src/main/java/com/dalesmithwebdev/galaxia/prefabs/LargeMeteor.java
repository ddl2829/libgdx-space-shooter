package com.dalesmithwebdev.galaxia.prefabs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.systems.DamageSystem;
import com.dalesmithwebdev.galaxia.utility.Rand;

public class LargeMeteor extends Entity {
    public LargeMeteor(int yPos) {
        this.add(new MeteorComponent(true));
        this.add(new RenderComponent(ArcadeSpaceShooter.bigMeteors.get(Rand.nextInt(ArcadeSpaceShooter.bigMeteors.size())), RenderComponent.PLANE_MAIN));
        this.add(new TakesDamageComponent(8, DamageSystem.LASER ^ DamageSystem.MISSILE ^ DamageSystem.BOMB));
        this.add(new DealsDamageComponent(20, DamageSystem.METEOR));
        int[] speeds = new int[] { 2, 2, 2, 3, 3, 3, 4, 4, 5, 6 };
        this.add(new SpeedComponent(0, -speeds[Rand.nextInt(speeds.length)]));
        this.add(new PositionComponent(new Vector2(Rand.nextInt((int)ArcadeSpaceShooter.screenRect.width), yPos)));
    }
}
