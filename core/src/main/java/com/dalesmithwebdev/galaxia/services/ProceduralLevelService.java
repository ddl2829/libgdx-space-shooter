package com.dalesmithwebdev.galaxia.services;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.constants.LevelConstants;
import com.dalesmithwebdev.galaxia.prefabs.EnemyFighter;
import com.dalesmithwebdev.galaxia.prefabs.FighterBoss;
import com.dalesmithwebdev.galaxia.prefabs.LargeMeteor;
import com.dalesmithwebdev.galaxia.prefabs.SmallMeteor;

/**
 * ProceduralLevelService - Generate random levels for endless mode
 * Single Responsibility: Procedural level generation with difficulty scaling
 */
public class ProceduralLevelService {

    /**
     * Generate and spawn a procedural level directly into the engine
     */
    public void generateLevel(Engine engine, int levelNumber, int levelLength) {
        int startPosition = levelNumber == 1
            ? (int)ArcadeSpaceShooter.screenRect.height + 1000
            : (int)ArcadeSpaceShooter.screenRect.height;

        // Generate meteors and enemies
        for (int l = startPosition; l < levelLength + 5000; l++) {
            // Small meteors
            double smallMeteorRate = LevelConstants.SMALL_METEOR_BASE_RATE
                + (LevelConstants.SMALL_METEOR_SCALE_RATE * levelNumber);
            if (com.dalesmithwebdev.galaxia.utility.Rand.nextFloat() * 100 < smallMeteorRate) {
                Entity newMeteor = new SmallMeteor(l);
                engine.addEntity(newMeteor);
            }

            // Large meteors
            double largeMeteorRate = LevelConstants.LARGE_METEOR_BASE_RATE
                + (LevelConstants.LARGE_METEOR_SCALE_RATE * levelNumber);
            if (com.dalesmithwebdev.galaxia.utility.Rand.nextFloat() * 100 < largeMeteorRate) {
                Entity newMeteor = new LargeMeteor(l);
                engine.addEntity(newMeteor);
            }

            // Enemies
            double enemyRate = LevelConstants.ENEMY_BASE_RATE
                + (LevelConstants.ENEMY_SCALE_RATE * levelNumber);
            if (com.dalesmithwebdev.galaxia.utility.Rand.nextFloat() * 100 < enemyRate) {
                Entity enemy = new EnemyFighter(levelNumber, l);
                engine.addEntity(enemy);
            }
        }

        // Add boss at the end
        Entity boss = new FighterBoss(levelNumber, levelLength);
        engine.addEntity(boss);
    }

    /**
     * Calculate level length based on level number
     */
    public int calculateLevelLength(int levelNumber, float screenHeight) {
        return (int)screenHeight
            + LevelConstants.BASE_LEVEL_LENGTH
            + (LevelConstants.LEVEL_LENGTH_INCREMENT * levelNumber);
    }
}
