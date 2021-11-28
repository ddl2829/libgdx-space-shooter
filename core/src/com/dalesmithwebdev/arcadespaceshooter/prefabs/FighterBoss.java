package com.dalesmithwebdev.arcadespaceshooter.prefabs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.*;
import com.dalesmithwebdev.arcadespaceshooter.systems.DamageSystem;

public class FighterBoss extends Entity {
    public FighterBoss(int levelNumber, int yPos) {

        int laserMask = HasLasersComponent.SINGLE;
        if(levelNumber > 1) {
            laserMask = HasLasersComponent.DUAL;
        }
        if(levelNumber > 3) {
            laserMask = HasLasersComponent.SINGLE ^ HasLasersComponent.UPGRADED;
        }
        if(levelNumber > 5) {
            laserMask = HasLasersComponent.DUAL ^ HasLasersComponent.UPGRADED;
        }
        if(levelNumber > 7) {
            laserMask = HasLasersComponent.DUAL ^ HasLasersComponent.UPGRADED ^ HasLasersComponent.DIAGONAL;
        }

        this.add(new EnemyComponent());
        this.add(new HasLasersComponent(Math.max(1000 - (20 * levelNumber), 100), laserMask));
        this.add(new RenderComponent(ArcadeSpaceShooter.bossTexture));
        this.add(new PositionComponent(new Vector2(ArcadeSpaceShooter.screenRect.width / 2, yPos)));
        this.add(new SpeedComponent(0.5 + (0.1 * levelNumber)));
        this.add(new TakesDamageComponent(50 + (10 * levelNumber), DamageSystem.LASER ^ DamageSystem.MISSILE));
        this.add(new DealsDamageComponent(20, DamageSystem.ENEMY));
        this.add(new BossEnemyComponent());
    }
}
