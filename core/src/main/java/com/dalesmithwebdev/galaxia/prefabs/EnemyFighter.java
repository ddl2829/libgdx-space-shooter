package com.dalesmithwebdev.galaxia.prefabs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.constants.DamageTypeConstants;
import com.dalesmithwebdev.galaxia.utility.Rand;

public class EnemyFighter extends Entity {
    public EnemyFighter(int levelNumber, int yPosition) {
        this(levelNumber, Rand.nextInt(50, (int)ArcadeSpaceShooter.screenRect.width - 50), yPosition);
    }

    public EnemyFighter(int levelNumber, float xPosition, float yPosition) {
        double[] speeds = new double[] { 0.5, 0.5, 0.5, 0.5, 0.8, 0.8, 0.8, 0.9, 1, 1.1 };
        double selectedSpeed = speeds[Rand.nextInt(speeds.length)];

        initializeEnemy(levelNumber, xPosition, yPosition, selectedSpeed);
    }

    public EnemyFighter(int levelNumber, float xPosition, float yPosition, double xSpeed) {
        initializeEnemy(levelNumber, xPosition, yPosition, xSpeed);
    }

    private void initializeEnemy(int levelNumber, float xPosition, float yPosition, double xSpeed) {
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
        this.add(new RenderComponent(ArcadeSpaceShooter.textures.findRegion("enemyShip"), RenderComponent.PLANE_MAIN));
        this.add(new PositionComponent(new Vector2(xPosition, yPosition)));
        this.add(new SpeedComponent(xSpeed));
        this.add(new TakesDamageComponent(5 + (int)(0.5 * levelNumber), DamageTypeConstants.LASER ^ DamageTypeConstants.MISSILE ^ DamageTypeConstants.BOMB));
        this.add(new DealsDamageComponent(20, DamageTypeConstants.ENEMY));
    }
}
