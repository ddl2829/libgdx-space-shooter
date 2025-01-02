package com.dalesmithwebdev.galaxia.prefabs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.systems.DamageSystem;

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
        this.add(new RenderComponent(ArcadeSpaceShooter.textures.findRegion("bossEnemy"), RenderComponent.PLANE_MAIN));
        this.add(new PositionComponent(new Vector2(ArcadeSpaceShooter.screenRect.width / 2, yPos)));
        this.add(new SpeedComponent(0.5 + (0.1 * levelNumber)));
        this.add(new TakesDamageComponent(50 + (10 * levelNumber), DamageSystem.LASER ^ DamageSystem.MISSILE ^ DamageSystem.BOMB));
        this.add(new DealsDamageComponent(50, DamageSystem.ENEMY));
        this.add(new BossEnemyComponent());
    }
}
