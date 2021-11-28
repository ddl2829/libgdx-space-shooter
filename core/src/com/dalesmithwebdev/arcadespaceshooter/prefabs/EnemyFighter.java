package com.dalesmithwebdev.arcadespaceshooter.prefabs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.*;
import com.dalesmithwebdev.arcadespaceshooter.systems.DamageSystem;
import com.dalesmithwebdev.arcadespaceshooter.utility.Rand;

public class EnemyFighter extends Entity {
    public EnemyFighter(int levelNumber, int yPosition) {
        double[] speeds = new double[] { 0.5, 0.5, 0.5, 0.5, 0.8, 0.8, 0.8, 0.9, 1, 1.1 };
        this.add(new EnemyComponent(Rand.nextInt(2000, 5000), Rand.nextInt(10000), speeds[Rand.nextInt(speeds.length)]));
        this.add(new RenderComponent(ArcadeSpaceShooter.enemyShip));
        this.add(new PositionComponent(new Vector2(Rand.nextInt(50, (int)ArcadeSpaceShooter.screenRect.width - 50), yPosition)));
        this.add(new SpeedComponent(new Vector2(0, -1)));
        this.add(new TakesDamageComponent(5 + (int)(0.5 * levelNumber), DamageSystem.LASER));
        this.add(new DealsDamageComponent(20, DamageSystem.ENEMY));
    }
}
