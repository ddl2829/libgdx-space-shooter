package com.dalesmithwebdev.arcadespaceshooter.prefabs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.*;
import com.dalesmithwebdev.arcadespaceshooter.systems.DamageSystem;
import com.dalesmithwebdev.arcadespaceshooter.utility.Rand;

public class LargeMeteor extends Entity {
    public LargeMeteor(int yPos) {
        this.add(new MeteorComponent(true));
        this.add(new RenderComponent(ArcadeSpaceShooter.meteorBig));
        this.add(new TakesDamageComponent(8, DamageSystem.LASER));
        this.add(new DealsDamageComponent(10, DamageSystem.METEOR));
        int[] speeds = new int[] { 1, 1, 1, 2, 2, 2, 3, 3, 4, 5 };
        this.add(new SpeedComponent(0, -speeds[Rand.nextInt(speeds.length)]));
        this.add(new PositionComponent(new Vector2(Rand.nextInt((int)ArcadeSpaceShooter.screenRect.width), yPos)));
    }
}
