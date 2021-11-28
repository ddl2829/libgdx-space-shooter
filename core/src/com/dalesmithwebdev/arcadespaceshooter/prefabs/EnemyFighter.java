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
        this.add(new EnemyComponent());

        int laserMask = HasLasersComponent.SINGLE;
        if(levelNumber > 4) {
            laserMask = HasLasersComponent.DUAL;
        }
        if(levelNumber > 6) {
            if(Rand.nextInt(100) < 20) {
                laserMask = HasLasersComponent.SINGLE ^ HasLasersComponent.UPGRADED;
            } else {
                laserMask = HasLasersComponent.DUAL;
            }
        }
        if(levelNumber > 8) {
            if(Rand.nextInt(100) < 20) {
                laserMask = HasLasersComponent.DUAL ^ HasLasersComponent.UPGRADED;
            } else {
                laserMask = HasLasersComponent.SINGLE ^ HasLasersComponent.UPGRADED;
            }
        }
        if(levelNumber > 10) {
            if(Rand.nextInt(100) < 20) {
                laserMask = HasLasersComponent.DUAL ^ HasLasersComponent.UPGRADED ^ HasLasersComponent.DIAGONAL;
            } else {
                laserMask = HasLasersComponent.DUAL ^ HasLasersComponent.UPGRADED;
            }
        }

        if(Rand.nextInt(100) < 3) {
            laserMask = laserMask | HasLasersComponent.DIAGONAL;
        }

        // gunless enemies on level 1
        if(levelNumber > 1) {
            // small chance of gunless enemies on later levels
            if(Rand.nextInt(100) > 2) {
                this.add(new HasLasersComponent(Rand.nextInt(2000, 5000), laserMask));
            }
        }
        this.add(new RenderComponent(ArcadeSpaceShooter.enemyShip));
        this.add(new PositionComponent(new Vector2(Rand.nextInt(50, (int)ArcadeSpaceShooter.screenRect.width - 50), yPosition)));
        this.add(new SpeedComponent(speeds[Rand.nextInt(speeds.length)]));
        this.add(new TakesDamageComponent(5 + (int)(0.5 * levelNumber), DamageSystem.LASER ^ DamageSystem.MISSILE));
        this.add(new DealsDamageComponent(20, DamageSystem.ENEMY));
    }
}
