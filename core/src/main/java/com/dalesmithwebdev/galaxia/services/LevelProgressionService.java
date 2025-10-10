package com.dalesmithwebdev.galaxia.services;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.dalesmithwebdev.galaxia.components.BossEnemyComponent;
import com.dalesmithwebdev.galaxia.components.EnemyComponent;
import com.dalesmithwebdev.galaxia.level.LevelLoader;

import java.util.List;

/**
 * LevelProgressionService - Manages level state and transitions
 * Single Responsibility: Level progression tracking and completion detection
 */
public class LevelProgressionService {
    private int levelNumber = 0;
    private int levelLength;
    private boolean preppingLevel = false;
    private float levelRunningTime = 0;
    private static final float MIN_LEVEL_TIME = 3000; // 3 seconds minimum before checking completion

    // JSON level support
    private String currentLevelId = null;
    private List<LevelLoader.LevelInfo> levelSequence = null;
    private int currentLevelIndex = 0;

    /**
     * Get current level number
     */
    public int getLevelNumber() {
        return levelNumber;
    }

    /**
     * Set current level number
     */
    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    /**
     * Advance to next level
     */
    public void advanceLevel() {
        this.levelNumber++;
    }

    /**
     * Get current level length
     */
    public int getLevelLength() {
        return levelLength;
    }

    /**
     * Set current level length
     */
    public void setLevelLength(int levelLength) {
        this.levelLength = levelLength;
    }

    /**
     * Check if currently preparing a level
     */
    public boolean isPreppingLevel() {
        return preppingLevel;
    }

    /**
     * Set level preparation state
     */
    public void setPreppingLevel(boolean preppingLevel) {
        this.preppingLevel = preppingLevel;
    }

    /**
     * Get level running time
     */
    public float getLevelRunningTime() {
        return levelRunningTime;
    }

    /**
     * Reset level running time
     */
    public void resetLevelRunningTime() {
        this.levelRunningTime = 0;
    }

    /**
     * Update level running time
     */
    public void updateLevelRunningTime(float deltaTime) {
        this.levelRunningTime += deltaTime;
    }

    /**
     * Check if level has been running for minimum time
     */
    public boolean hasMinimumTimeElapsed() {
        return levelRunningTime >= MIN_LEVEL_TIME;
    }

    /**
     * Check if level is complete (no enemies/bosses left)
     */
    public boolean isLevelComplete(Engine engine, boolean spawnQueueEmpty) {
        ImmutableArray<com.badlogic.ashley.core.Entity> boss = engine.getEntitiesFor(Family.all(BossEnemyComponent.class).get());
        ImmutableArray<com.badlogic.ashley.core.Entity> enemies = engine.getEntitiesFor(Family.all(EnemyComponent.class).get());

        // Level complete when all enemies are gone AND spawn queue is empty
        return boss.size() == 0 && enemies.size() == 0 && spawnQueueEmpty;
    }

    /**
     * Get current level ID
     */
    public String getCurrentLevelId() {
        return currentLevelId;
    }

    /**
     * Set current level ID and load level sequence
     */
    public void setCurrentLevelId(String levelId) {
        System.out.println(">>> LevelProgressionService.setCurrentLevelId: Setting level ID to: " + levelId);
        this.currentLevelId = levelId;

        // Load the level sequence
        System.out.println(">>> LevelProgressionService.setCurrentLevelId: Loading available levels");
        this.levelSequence = LevelLoader.getAvailableLevels();
        System.out.println(">>> LevelProgressionService.setCurrentLevelId: Found " + levelSequence.size() + " levels");

        // Find index of current level
        for (int i = 0; i < levelSequence.size(); i++) {
            if (levelSequence.get(i).getId().equals(levelId)) {
                currentLevelIndex = i;
                System.out.println(">>> LevelProgressionService.setCurrentLevelId: Found level at index " + i);
                break;
            }
        }
    }

    /**
     * Get level sequence
     */
    public List<LevelLoader.LevelInfo> getLevelSequence() {
        return levelSequence;
    }

    /**
     * Get current level index in sequence
     */
    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    /**
     * Advance to next level in sequence
     * @return true if there is a next level, false if sequence is complete
     */
    public boolean advanceToNextLevelInSequence() {
        if (levelSequence == null) {
            return false;
        }

        currentLevelIndex++;
        if (currentLevelIndex < levelSequence.size()) {
            currentLevelId = levelSequence.get(currentLevelIndex).getId();
            return true;
        }
        return false;
    }

    /**
     * Get name of current level in sequence
     */
    public String getCurrentLevelName() {
        if (levelSequence != null && currentLevelIndex < levelSequence.size()) {
            return levelSequence.get(currentLevelIndex).getName();
        }
        return null;
    }

    /**
     * Check if using JSON level sequence
     */
    public boolean isUsingLevelSequence() {
        return currentLevelId != null && levelSequence != null;
    }

    /**
     * Reset level progression state
     */
    public void reset() {
        levelNumber = 0;
        levelLength = 0;
        preppingLevel = false;
        levelRunningTime = 0;
        currentLevelId = null;
        levelSequence = null;
        currentLevelIndex = 0;
    }
}
