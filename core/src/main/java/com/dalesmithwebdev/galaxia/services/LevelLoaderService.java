package com.dalesmithwebdev.galaxia.services;

import com.badlogic.ashley.core.Entity;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.constants.LevelConstants;
import com.dalesmithwebdev.galaxia.level.LevelData;
import com.dalesmithwebdev.galaxia.level.LevelLoader;
import com.dalesmithwebdev.galaxia.level.LevelObject;
import com.dalesmithwebdev.galaxia.prefabs.*;

import java.util.List;

/**
 * LevelLoaderService - Load and parse JSON level data
 * Single Responsibility: JSON level loading and entity creation from level objects
 */
public class LevelLoaderService {
    /**
     * Coordinate transform data for converting editor coordinates to game coordinates
     */
    public static class TransformData {
        private final float minEditorX;
        private final float maxEditorX;
        private final float maxEditorY;
        private final float xScale;
        private final float horizontalBuffer;

        public TransformData(float minEditorX, float maxEditorX, float maxEditorY, float xScale, float horizontalBuffer) {
            this.minEditorX = minEditorX;
            this.maxEditorX = maxEditorX;
            this.maxEditorY = maxEditorY;
            this.xScale = xScale;
            this.horizontalBuffer = horizontalBuffer;
        }

        public float getMinEditorX() {
            return minEditorX;
        }

        public float getMaxEditorX() {
            return maxEditorX;
        }

        public float getMaxEditorY() {
            return maxEditorY;
        }

        public float getXScale() {
            return xScale;
        }

        public float getHorizontalBuffer() {
            return horizontalBuffer;
        }
    }

    /**
     * Load level data from JSON file
     */
    public LevelData loadLevel(String levelId) {
        System.out.println(">>> LevelLoaderService.loadLevel: Loading level data for: " + levelId);
        LevelData levelData = LevelLoader.loadLevel(levelId);
        if (levelData != null) {
            System.out.println(">>> LevelLoaderService.loadLevel: Level data loaded successfully");
        } else {
            System.out.println(">>> LevelLoaderService.loadLevel: Level data was NULL!");
        }
        return levelData;
    }

    /**
     * Get list of available levels
     */
    public List<LevelLoader.LevelInfo> getAvailableLevels() {
        return LevelLoader.getAvailableLevels();
    }

    /**
     * Calculate transform data for coordinate conversion
     */
    public TransformData calculateTransform(LevelData levelData, float screenWidth, float screenHeight) {
        System.out.println("=== Loading level: " + levelData.getName() + " ===");
        System.out.println("Total objects in level: " + levelData.getObjects().size());

        // Find min/max X and max Y values for scaling
        float minEditorX = Float.MAX_VALUE;
        float maxEditorX = Float.MIN_VALUE;
        float maxEditorY = 0;

        for (LevelObject obj : levelData.getObjects()) {
            if (obj.getX() < minEditorX) minEditorX = obj.getX();
            if (obj.getX() > maxEditorX) maxEditorX = obj.getX();
            if (obj.getY() > maxEditorY) maxEditorY = obj.getY();
        }

        System.out.println("Editor X range: " + minEditorX + " to " + maxEditorX);
        System.out.println("Max editor Y value: " + maxEditorY);
        System.out.println("Screen dimensions: " + screenWidth + " x " + screenHeight);

        // Add buffer/padding on each side (typical entity width / 2)
        float horizontalBuffer = LevelConstants.HORIZONTAL_BUFFER;
        float availableWidth = screenWidth - (2 * horizontalBuffer);

        // Calculate X scaling factor to fit within available width
        float editorWidth = maxEditorX - minEditorX;
        float xScale = editorWidth > 0 ? availableWidth / editorWidth : 1.0f;
        System.out.println("X scaling factor: " + xScale + " (editor width: " + editorWidth + ", buffer: " + horizontalBuffer + ")");

        return new TransformData(minEditorX, maxEditorX, maxEditorY, xScale, horizontalBuffer);
    }

    /**
     * Create an entity from a level object using the provided transform data
     */
    public Entity createEntityFromLevelObject(LevelObject obj, TransformData transform) {
        // Scale X coordinate to fit screen width with buffer
        float normalizedX = transform.horizontalBuffer + (obj.getX() - transform.minEditorX) * transform.xScale;

        // Invert Y coordinate: editor high Y = start, Y=0 = end
        // Game high Y = later encounter, low Y = early encounter
        float invertedY = transform.maxEditorY - obj.getY();
        float spawnY = ArcadeSpaceShooter.screenRect.height + invertedY;

        switch (obj.getType()) {
            case "METEOR_SMALL":
                return new SmallMeteor(normalizedX, spawnY, LevelConstants.ENTITY_FIXED_DOWNWARD_SPEED);
            case "METEOR_LARGE":
                return new LargeMeteor(normalizedX, spawnY, LevelConstants.ENTITY_FIXED_DOWNWARD_SPEED);
            case "ENEMY_FIGHTER":
                return new EnemyFighter(1, normalizedX, spawnY, LevelConstants.ENEMY_HORIZONTAL_SPEED);
            case "POWERUP_LASER_STRENGTH":
                return new LaserStrengthUpgrade(1, normalizedX, spawnY);
            case "POWERUP_DUAL_LASER":
                return new DualLaserUpgrade(normalizedX, spawnY);
            case "POWERUP_DIAGONAL_LASER":
                return new DiagonalLaserUpgrade(normalizedX, spawnY);
            case "POWERUP_MISSILE":
                return new MissileUpgrade(normalizedX, spawnY);
            case "POWERUP_BOMB":
                return new BombUpgrade(normalizedX, spawnY);
            case "POWERUP_EMP":
                return new EmpUpgrade(normalizedX, spawnY);
            case "POWERUP_SHIELD":
                return new ShieldUpgrade(normalizedX, spawnY);
            default:
                System.err.println("Unknown object type: " + obj.getType());
                return null;
        }
    }

    /**
     * Calculate level length from level data
     */
    public int calculateLevelLength(LevelData levelData, float screenHeight) {
        TransformData transform = calculateTransform(levelData, ArcadeSpaceShooter.screenRect.width, screenHeight);
        int levelLength = (int)(screenHeight + transform.maxEditorY + 1000);
        System.out.println("Level length set to: " + levelLength);
        return levelLength;
    }
}
