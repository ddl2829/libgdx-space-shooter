package com.dalesmithwebdev.galaxia.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Timer;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.BossEnemyComponent;
import com.dalesmithwebdev.galaxia.components.PositionComponent;
import com.dalesmithwebdev.galaxia.constants.LevelConstants;
import com.dalesmithwebdev.galaxia.level.LevelData;
import com.dalesmithwebdev.galaxia.level.LevelObject;
import com.dalesmithwebdev.galaxia.prefabs.SmallMeteor;
import com.dalesmithwebdev.galaxia.prefabs.LargeMeteor;
import com.dalesmithwebdev.galaxia.screens.LevelSelectScreen;
import com.dalesmithwebdev.galaxia.services.*;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;
import com.dalesmithwebdev.galaxia.utility.SoundManager;

/**
 * LevelSystem - Thin coordinator for level progression
 * Single Responsibility: Orchestrate level services and manage game loop
 */
public class LevelSystem extends EntitySystem {
    // Static fields for backwards compatibility
    public static int levelNumber = 0;
    public static int levelLength;

    // Services
    private final LevelProgressionService progressionService;
    private final EntitySpawnService spawnService;
    private final VirtualSpawnService virtualSpawnService;
    private final LevelLoaderService loaderService;
    private final ProceduralLevelService proceduralService;
    private final LevelNotificationService notificationService;
    private final GameStateService gameState;

    // Transform data stored for entity creation callback
    private LevelLoaderService.TransformData transformData = null;

    // Track if current level uses virtual spawning (JSON) or progressive spawning (procedural)
    private boolean usingVirtualSpawn = false;

    public LevelSystem() {
        // Get services from ServiceLocator
        ServiceLocator locator = ServiceLocator.getInstance();
        this.progressionService = locator.getLevelProgression();
        this.spawnService = locator.getEntitySpawn();
        this.virtualSpawnService = locator.getVirtualSpawn();
        this.loaderService = locator.getLevelLoader();
        this.proceduralService = locator.getProceduralLevel();
        this.notificationService = locator.getLevelNotification();
        this.gameState = locator.getGameState();
    }

    /**
     * Set current level ID (for backwards compatibility and external access)
     */
    public static void setCurrentLevelId(String levelId) {
        ServiceLocator.getInstance().getLevelProgression().setCurrentLevelId(levelId);
    }

    /**
     * Start initial level
     */
    public void startInitialLevel() {
        System.out.println(">>> LevelSystem.startInitialLevel: Triggering initial level build");
        BuildLevel();
    }

    @Override
    public void update(float gameTime) {
        final Engine engine = this.getEngine();

        if (gameState.isPaused() || progressionService.isPreppingLevel()) {
            return;
        }

        // Track level running time
        progressionService.updateLevelRunningTime(gameTime);

        // Spawn entities using appropriate method
        if (usingVirtualSpawn) {
            // Virtual just-in-time spawning for JSON levels (optimized)
            virtualSpawnService.processVirtualSpawns(engine, progressionService.getLevelRunningTime(),
                this::createEntityFromVirtual, ArcadeSpaceShooter.screenRect.height, gameTime);
        } else {
            // Progressive spawning for procedural levels (legacy)
            spawnService.processSpawnQueue(engine, this::createEntityFromPending);
        }

        // Only check for level completion after minimum time has passed
        if (!progressionService.hasMinimumTimeElapsed()) {
            return;
        }

        // Check if level is complete
        boolean spawnQueueEmpty = usingVirtualSpawn ?
            !virtualSpawnService.hasEnemiesRemaining() :
            spawnService.isSpawnQueueEmpty();

        if (progressionService.isLevelComplete(engine, spawnQueueEmpty)) {
            handleLevelComplete(engine);
        }
    }

    /**
     * Handle level completion and transition
     */
    private void handleLevelComplete(final Engine engine) {
        progressionService.advanceLevel();

        // Sync static field for backwards compatibility
        levelNumber = progressionService.getLevelNumber();

        if (progressionService.getLevelNumber() > 1) {
            // Show completion and prep next level
            notificationService.showLevelComplete(engine);
            progressionService.setPreppingLevel(true);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (progressionService.isUsingLevelSequence()) {
                        handleLevelSequenceProgression(engine);
                    } else {
                        handleProceduralProgression(engine);
                    }
                }
            }, 5);
        } else {
            // First level - show tutorial
            notificationService.showTutorial(engine);
            BuildLevel();
        }
    }

    /**
     * Handle progression through JSON level sequence
     */
    private void handleLevelSequenceProgression(final Engine engine) {
        if (progressionService.advanceToNextLevelInSequence()) {
            // Load next level in sequence
            String levelName = progressionService.getCurrentLevelName();
            notificationService.showLevelStart(engine, levelName);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    BuildLevel();
                }
            }, 3);
        } else {
            // All levels complete!
            notificationService.showAllLevelsComplete(engine);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    ArcadeSpaceShooter.instance.setScreen(new LevelSelectScreen());
                }
            }, 5);
        }
    }

    /**
     * Handle progression in procedural mode
     */
    private void handleProceduralProgression(final Engine engine) {
        notificationService.showLevelStart(engine, progressionService.getLevelNumber());

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                BuildLevel();
            }
        }, 3);
    }

    /**
     * Build and load a level
     */
    private void BuildLevel() {
        System.out.println(">>> LevelSystem.BuildLevel: Called!");

        // Reset level timer
        progressionService.resetLevelRunningTime();

        // Play warp-in sound
        SoundManager.playWarpIn();

        // Try to load JSON level first
        String currentLevelId = progressionService.getCurrentLevelId();
        System.out.println(">>> LevelSystem.BuildLevel: currentLevelId = " + currentLevelId);

        if (currentLevelId != null) {
            LevelData levelData = loaderService.loadLevel(currentLevelId);
            if (levelData != null) {
                System.out.println(">>> LevelSystem.BuildLevel: Building from JSON");
                BuildLevelFromJSON(levelData);
                progressionService.setPreppingLevel(false);
                return;
            }
        }

        // Fallback: procedural generation
        System.out.println(">>> LevelSystem.BuildLevel: Building procedural level");
        BuildProceduralLevel();
        progressionService.setPreppingLevel(false);
    }

    /**
     * Build level from JSON data
     */
    private void BuildLevelFromJSON(LevelData levelData) {
        // Use virtual spawning for JSON levels (optimized)
        usingVirtualSpawn = true;

        // Clear virtual spawn service for new level
        virtualSpawnService.clear();

        // Calculate coordinate transform
        transformData = loaderService.calculateTransform(
            levelData,
            ArcadeSpaceShooter.screenRect.width,
            ArcadeSpaceShooter.screenRect.height
        );

        // First pass: Calculate all spawn positions and timings
        float minSpawnTiming = Float.MAX_VALUE;
        java.util.List<SpawnData> spawnDataList = new java.util.ArrayList<>();

        for (LevelObject obj : levelData.getObjects()) {
            // Calculate spawn X coordinate (with screen scaling)
            float spawnX = transformData.getHorizontalBuffer() +
                          (obj.getX() - transformData.getMinEditorX()) * transformData.getXScale();

            // Calculate spawn timing from editor Y position
            // Invert: editor high Y = early spawn, editor low Y = late spawn
            // Apply Y-scaling to spread spawns over time
            float invertedY = (transformData.getMaxEditorY() - obj.getY()) * transformData.getYScale();
            float spawnTiming = invertedY; // Use invertedY directly for timing

            // All entities spawn at same screen Y (just above screen)
            // They will scroll down from this position
            float spawnScreenY = ArcadeSpaceShooter.screenRect.height;

            spawnDataList.add(new SpawnData(obj, spawnX, spawnScreenY, spawnTiming));

            if (spawnTiming < minSpawnTiming) {
                minSpawnTiming = spawnTiming;
            }
        }

        System.out.println("Spawn timing range: " + minSpawnTiming + " to " +
                         (spawnDataList.isEmpty() ? "N/A" : spawnDataList.get(spawnDataList.size()-1).spawnTiming));

        // Second pass: Normalize spawn timings to start from 0
        for (SpawnData data : spawnDataList) {
            float normalizedTiming = data.spawnTiming - minSpawnTiming;
            virtualSpawnService.scheduleEntity(data.obj, data.spawnX, data.spawnScreenY, normalizedTiming);
        }

        // Sort virtual entities by normalized spawn order
        virtualSpawnService.sortBySpawnOrder();

        System.out.println("Scheduled " + virtualSpawnService.getVirtualEntityCount() + " entities for virtual just-in-time spawning");

        // Set level length
        int calculatedLength = loaderService.calculateLevelLength(levelData, ArcadeSpaceShooter.screenRect.height);
        progressionService.setLevelLength(calculatedLength);

        // Sync static field for backwards compatibility
        levelLength = calculatedLength;

        System.out.println("=== Level loading complete (virtual spawning enabled) ===");
    }

    /**
     * Helper class to hold spawn data during normalization
     */
    private static class SpawnData {
        final LevelObject obj;
        final float spawnX;
        final float spawnScreenY;
        final float spawnTiming;

        SpawnData(LevelObject obj, float spawnX, float spawnScreenY, float spawnTiming) {
            this.obj = obj;
            this.spawnX = spawnX;
            this.spawnScreenY = spawnScreenY;
            this.spawnTiming = spawnTiming;
        }
    }

    /**
     * Build procedural level
     */
    private void BuildProceduralLevel() {
        // Use progressive spawning for procedural levels (legacy)
        usingVirtualSpawn = false;

        int calculatedLength = proceduralService.calculateLevelLength(
            progressionService.getLevelNumber(),
            ArcadeSpaceShooter.screenRect.height
        );

        progressionService.setLevelLength(calculatedLength);
        levelLength = calculatedLength; // Sync static field

        proceduralService.generateLevel(
            this.getEngine(),
            progressionService.getLevelNumber(),
            calculatedLength
        );
    }

    /**
     * Create entity from pending spawn (callback for spawn service)
     */
    private Entity createEntityFromPending(EntitySpawnService.PendingSpawn pending) {
        if (transformData == null) {
            return null;
        }
        return loaderService.createEntityFromLevelObject(pending.getLevelObject(), transformData);
    }

    /**
     * Create entity from virtual spawn (callback for virtual spawn service)
     * Uses object pooling for meteors to eliminate GC pressure
     */
    private Entity createEntityFromVirtual(VirtualSpawnService.VirtualEntity virtualEntity) {
        String type = virtualEntity.getLevelObject().getType();
        float x = virtualEntity.getSpawnX();
        float y = virtualEntity.getSpawnScreenY(); // Use screen Y position for placement

        // Use object pools for meteors (most frequent entity type)
        if (type.equals("METEOR_SMALL")) {
            Entity meteor = ArcadeSpaceShooter.smallMeteorPool.obtain();
            if (meteor instanceof SmallMeteor) {
                ((SmallMeteor) meteor).reset(x, y, LevelConstants.ENTITY_FIXED_DOWNWARD_SPEED);
            }
            return meteor;
        } else if (type.equals("METEOR_LARGE")) {
            Entity meteor = ArcadeSpaceShooter.largeMeteorPool.obtain();
            if (meteor instanceof LargeMeteor) {
                ((LargeMeteor) meteor).reset(x, y, LevelConstants.ENTITY_FIXED_DOWNWARD_SPEED);
            }
            return meteor;
        }

        // For non-meteor entities, use standard creation (no pooling yet)
        return loaderService.createEntityFromLevelObject(virtualEntity.getLevelObject(), transformData);
    }

    /**
     * Draw boss health bar
     */
    public void draw() {
        ArcadeSpaceShooter.spriteBatch.setColor(Color.BLACK);
        ArcadeSpaceShooter.spriteBatch.draw(
            ArcadeSpaceShooter.blank,
            ArcadeSpaceShooter.screenRect.width - 158,
            25,
            150,
            12
        );

        ImmutableArray<Entity> bossEntities = this.getEngine().getEntitiesFor(Family.all(BossEnemyComponent.class).get());
        if (bossEntities.size() > 0) {
            Entity boss = bossEntities.get(0);
            PositionComponent bossPosition = ComponentMap.positionMapper.get(boss);
            double pct = (progressionService.getLevelLength() - bossPosition.position.y) / progressionService.getLevelLength();
            ArcadeSpaceShooter.spriteBatch.setColor(Color.WHITE);
            ArcadeSpaceShooter.spriteBatch.draw(
                ArcadeSpaceShooter.blank,
                ArcadeSpaceShooter.screenRect.width - 159,
                26,
                (int)(pct * 148),
                10
            );
        }
    }
}
