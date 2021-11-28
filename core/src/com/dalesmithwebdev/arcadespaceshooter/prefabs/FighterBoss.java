package com.dalesmithwebdev.arcadespaceshooter.prefabs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.*;
import com.dalesmithwebdev.arcadespaceshooter.systems.DamageSystem;

public class FighterBoss extends Entity {
    public FighterBoss(int levelNumber, int yPos) {
        this.add(new EnemyComponent(Math.max(1000 - (20 * levelNumber), 100), 0, 0.5 + (0.1 * levelNumber)));
        this.add(new RenderComponent(ArcadeSpaceShooter.bossTexture));
        this.add(new PositionComponent(new Vector2(ArcadeSpaceShooter.screenRect.width / 2, yPos)));
        this.add(new SpeedComponent(new Vector2(0, -1)));
        this.add(new TakesDamageComponent(50 + (10 * levelNumber), DamageSystem.LASER));
        this.add(new DealsDamageComponent(20, DamageSystem.ENEMY));
        this.add(new BossEnemyComponent(levelNumber < 2 ? 1 : levelNumber < 5 ? 2 : levelNumber < 8 ? 3 : 4));
    }
}
