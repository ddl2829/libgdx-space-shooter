package com.dalesmithwebdev.galaxia.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.constants.LevelConstants;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.level.LevelData;
import com.dalesmithwebdev.galaxia.level.LevelLoader;
import com.dalesmithwebdev.galaxia.level.LevelObject;
import com.dalesmithwebdev.galaxia.prefabs.*;
import com.dalesmithwebdev.galaxia.screens.LevelSelectScreen;
import com.dalesmithwebdev.galaxia.constants.DamageTypeConstants;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;
import com.dalesmithwebdev.galaxia.utility.SoundManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LevelSystem extends EntitySystem {
    public static int levelNumber = 0;
    public static int levelLength;
    private boolean preppingLevel = false;
    private float levelRunningTime = 0;
    private static final float MIN_LEVEL_TIME = 3000; // 3 seconds minimum before checking completion

    // JSON level support
    private static String currentLevelId = null;
    private static List<LevelLoader.LevelInfo> levelSequence = null;
    private static int currentLevelIndex = 0;

    // Lazy spawning queue for performance (to avoid single-frame spawn spike)
    private List<PendingSpawn> spawnQueue = new ArrayList<>();
    private int maxEntitiesPerFrame = 5; // Spawn at most this many entities per frame to avoid lag

    // Coordinate transform data (stored for lazy spawning)
    private static class TransformData {
        float minEditorX, maxEditorX, maxEditorY, xScale, horizontalBuffer;
    }
    private TransformData transformData = null;

    /**
     * Pending entity spawn - created from level data but not yet added to engine
     */
    private static class PendingSpawn {
        LevelObject levelObject;
        float spawnY;

        PendingSpawn(LevelObject obj, float spawnY) {
            this.levelObject = obj;
            this.spawnY = spawnY;
        }
    }

    public static void setCurrentLevelId(String levelId) {
        System.out.println(">>> LevelSystem.setCurrentLevelId: Setting level ID to: " + levelId);
        currentLevelId = levelId;
        // Load the level sequence
        System.out.println(">>> LevelSystem.setCurrentLevelId: Loading available levels");
        levelSequence = LevelLoader.getAvailableLevels();
        System.out.println(">>> LevelSystem.setCurrentLevelId: Found " + levelSequence.size() + " levels");
        // Find index of current level
        for (int i = 0; i < levelSequence.size(); i++) {
            if (levelSequence.get(i).getId().equals(levelId)) {
                currentLevelIndex = i;
                System.out.println(">>> LevelSystem.setCurrentLevelId: Found level at index " + i);
                break;
            }
        }
    }

    public void startInitialLevel() {
        System.out.println(">>> LevelSystem.startInitialLevel: Triggering initial level build");
        BuildLevel();
    }

    public void update(float gameTime) {
        final Engine engine = this.getEngine();
        if(ArcadeSpaceShooter.paused) {
            return;
        }
        if (preppingLevel) {
            return;
        }

        // Progressive entity spawning for performance (spread initial spawn over multiple frames)
        processSpawnQueue();

        // Track level running time
        levelRunningTime += gameTime;

        // Only check for level completion after minimum time has passed
        if (levelRunningTime < MIN_LEVEL_TIME) {
            return;
        }

        // Check if level is complete (no boss entities left)
        ImmutableArray<Entity> boss = this.getEngine().getEntitiesFor(Family.all(BossEnemyComponent.class).get());
        ImmutableArray<Entity> enemies = this.getEngine().getEntitiesFor(Family.all(EnemyComponent.class).get());

        // Level complete when all enemies are gone AND spawn queue is empty
        if (boss.size() == 0 && enemies.size() == 0 && isSpawnQueueEmpty()) {
            levelNumber++;

            if (levelNumber > 1) {
                Entity e = new Entity();
                e.add(new NotificationComponent("Level Complete!", 3000, true));
                this.getEngine().addEntity(e);
                preppingLevel = true;

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        // Try to load next level in sequence
                        if (currentLevelId != null && levelSequence != null) {
                            currentLevelIndex++;
                            if (currentLevelIndex < levelSequence.size()) {
                                // Load next level
                                currentLevelId = levelSequence.get(currentLevelIndex).getId();
                                Entity e = new Entity();
                                e.add(new NotificationComponent("Begin Level: " + levelSequence.get(currentLevelIndex).getName(), 3000, true));
                                engine.addEntity(e);

                                Timer.schedule(new Timer.Task() {
                                    @Override
                                    public void run() {
                                        BuildLevel();
                                    }
                                }, 3);
                            } else {
                                // All levels complete!
                                Entity e = new Entity();
                                e.add(new NotificationComponent("All Levels Complete!", 3000, true));
                                engine.addEntity(e);

                                Timer.schedule(new Timer.Task() {
                                    @Override
                                    public void run() {
                                        ArcadeSpaceShooter.instance.setScreen(new LevelSelectScreen());
                                    }
                                }, 5);
                            }
                        } else {
                            // Fallback to old system
                            Entity e = new Entity();
                            e.add(new NotificationComponent("Begin Level " + levelNumber, 3000, true));
                            engine.addEntity(e);

                            Timer.schedule(new Timer.Task() {
                                @Override
                                public void run() {
                                    BuildLevel();
                                }
                            }, 3);
                        }
                    }
                }, 5);
            } else {
                // First level load
                Entity e = new Entity();
                e.add(new NotificationComponent("Use the Arrow Keys to move", 3000, true));
                this.getEngine().addEntity(e);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        Entity e = new Entity();
                        e.add(new NotificationComponent("Hold the space bar to shoot", 3000, true));
                        engine.addEntity(e);

                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                Entity e = new Entity();
                                e.add(new NotificationComponent("Good Luck!", 3000, true));
                                engine.addEntity(e);
                            }
                        }, 3);
                    }
                }, 3);

                BuildLevel();
            }
        }
    }

    private void BuildLevel() {
        System.out.println(">>> LevelSystem.BuildLevel: Called!");
        // Reset level timer
        levelRunningTime = 0;

        // Play warp-in sound at the start of each level
        SoundManager.playWarpIn();

        System.out.println(">>> LevelSystem.BuildLevel: currentLevelId = " + currentLevelId);
        // Try to load from JSON first
        if (currentLevelId != null) {
            System.out.println(">>> LevelSystem.BuildLevel: Loading level data for: " + currentLevelId);
            LevelData levelData = LevelLoader.loadLevel(currentLevelId);
            if (levelData != null) {
                System.out.println(">>> LevelSystem.BuildLevel: Level data loaded successfully, building from JSON");
                BuildLevelFromJSON(levelData);
                preppingLevel = false;
                return;
            } else {
                System.out.println(">>> LevelSystem.BuildLevel: Level data was NULL!");
            }
        } else {
            System.out.println(">>> LevelSystem.BuildLevel: currentLevelId is NULL, using procedural generation");
        }

        // Fallback: procedural generation (old system)
        System.out.println(">>> LevelSystem.BuildLevel: Building procedural level");
        BuildProceduralLevel();
        preppingLevel = false;
    }

    private void BuildLevelFromJSON(LevelData levelData) {
        System.out.println("=== Loading level: " + levelData.getName() + " ===");
        System.out.println("Total objects in level: " + levelData.getObjects().size());

        // Clear spawn queue for new level
        spawnQueue.clear();

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
        System.out.println("Screen dimensions: " + ArcadeSpaceShooter.screenRect.width + " x " + ArcadeSpaceShooter.screenRect.height);

        // Add buffer/padding on each side (typical entity width / 2)
        float horizontalBuffer = LevelConstants.HORIZONTAL_BUFFER;
        float availableWidth = ArcadeSpaceShooter.screenRect.width - (2 * horizontalBuffer);

        // Calculate X scaling factor to fit within available width
        float editorWidth = maxEditorX - minEditorX;
        float xScale = editorWidth > 0 ? availableWidth / editorWidth : 1.0f;
        System.out.println("X scaling factor: " + xScale + " (editor width: " + editorWidth + ", buffer: " + horizontalBuffer + ")");

        // Store transform data for lazy spawning
        transformData = new TransformData();
        transformData.minEditorX = minEditorX;
        transformData.maxEditorX = maxEditorX;
        transformData.maxEditorY = maxEditorY;
        transformData.xScale = xScale;
        transformData.horizontalBuffer = horizontalBuffer;

        // Queue all objects for progressive spawning (spread over multiple frames to avoid lag spike)
        for (LevelObject obj : levelData.getObjects()) {
            float invertedY = maxEditorY - obj.getY();
            float spawnY = ArcadeSpaceShooter.screenRect.height + invertedY;
            spawnQueue.add(new PendingSpawn(obj, spawnY));
        }

        System.out.println("Queued " + spawnQueue.size() + " entities for progressive spawning (avoiding lag spike)");

        // Set level length based on max Y (which represents level distance)
        levelLength = (int)(ArcadeSpaceShooter.screenRect.height + maxEditorY + 1000);
        System.out.println("Level length set to: " + levelLength);
        System.out.println("=== Level loading complete (instant!) ===");
    }

    /**
     * Check if spawn queue has any enemies or bosses left
     */
    private boolean isSpawnQueueEmpty() {
        if (spawnQueue.isEmpty()) {
            return true;
        }

        // Check if any remaining entities in queue are enemies or bosses
        for (PendingSpawn pending : spawnQueue) {
            String type = pending.levelObject.getType();
            if (type.equals("ENEMY_FIGHTER") || type.startsWith("BOSS_")) {
                return false; // Still have enemies waiting to spawn
            }
        }

        return true; // Only powerups/meteors left, doesn't count
    }

    /**
     * Process spawn queue - spawn entities progressively to avoid single-frame lag spike.
     * All entities spawn at their designed Y positions (respecting level design),
     * but spread over multiple frames for performance.
     */
    private void processSpawnQueue() {
        if (spawnQueue.isEmpty() || transformData == null) {
            return;
        }

        // Spawn a limited number of entities per frame to avoid lag
        int spawnedThisFrame = 0;
        while (!spawnQueue.isEmpty() && spawnedThisFrame < maxEntitiesPerFrame) {
            // Take from front of queue (FIFO)
            PendingSpawn pending = spawnQueue.remove(0);
            spawnedThisFrame++;

            Entity entity = createEntityFromType(
                pending.levelObject,
                transformData.minEditorX,
                transformData.maxEditorX,
                transformData.maxEditorY,
                transformData.xScale,
                transformData.horizontalBuffer
            );

            if (entity != null) {
                this.getEngine().addEntity(entity);
            }
        }
    }

    private Entity createEntityFromType(LevelObject obj, float minEditorX, float maxEditorX, float maxEditorY, float xScale, float horizontalBuffer) {
        // Scale X coordinate to fit screen width with buffer
        float normalizedX = horizontalBuffer + (obj.getX() - minEditorX) * xScale;

        // Invert Y coordinate: editor high Y = start, Y=0 = end
        // Game high Y = later encounter, low Y = early encounter
        float invertedY = maxEditorY - obj.getY();
        float spawnY = ArcadeSpaceShooter.screenRect.height + invertedY;

        switch (obj.getType()) {
            case "METEOR_SMALL":
                return new SmallMeteor(normalizedX, spawnY, LevelConstants.ENTITY_FIXED_DOWNWARD_SPEED); // Fixed downward speed
            case "METEOR_LARGE":
                return new LargeMeteor(normalizedX, spawnY, LevelConstants.ENTITY_FIXED_DOWNWARD_SPEED); // Fixed downward speed
            case "ENEMY_FIGHTER":
                return new EnemyFighter(1, normalizedX, spawnY, LevelConstants.ENEMY_HORIZONTAL_SPEED); // Fixed horizontal speed
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

    private void BuildProceduralLevel() {
        // Old procedural generation system
        levelLength = (int)ArcadeSpaceShooter.screenRect.height + LevelConstants.BASE_LEVEL_LENGTH + (LevelConstants.LEVEL_LENGTH_INCREMENT * levelNumber);
        int startPosition = levelNumber == 1 ? (int)ArcadeSpaceShooter.screenRect.height + 1000 : (int)ArcadeSpaceShooter.screenRect.height;

        for (int l = startPosition; l < levelLength + 5000; l++) {
            double randomAmt = LevelConstants.SMALL_METEOR_BASE_RATE + (LevelConstants.SMALL_METEOR_SCALE_RATE * levelNumber);
            if(com.dalesmithwebdev.galaxia.utility.Rand.nextFloat() * 100 < randomAmt) {
                Entity newMeteor = new SmallMeteor(l);
                this.getEngine().addEntity(newMeteor);
            }

            randomAmt = LevelConstants.LARGE_METEOR_BASE_RATE + (LevelConstants.LARGE_METEOR_SCALE_RATE * levelNumber);
            if(com.dalesmithwebdev.galaxia.utility.Rand.nextFloat() * 100 < randomAmt) {
                Entity newMeteor = new LargeMeteor(l);
                this.getEngine().addEntity(newMeteor);
            }

            double enemyAmount = LevelConstants.ENEMY_BASE_RATE + (LevelConstants.ENEMY_SCALE_RATE * levelNumber);
            if(com.dalesmithwebdev.galaxia.utility.Rand.nextFloat() * 100 < enemyAmount) {
                Entity enemy = new EnemyFighter(levelNumber, l);
                this.getEngine().addEntity(enemy);
            }
        }

        Entity boss = new FighterBoss(levelNumber, levelLength);
        this.getEngine().addEntity(boss);
    }

    public void draw() {
        ArcadeSpaceShooter.spriteBatch.setColor(Color.BLACK);
        ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, ArcadeSpaceShooter.screenRect.width - 158, 25, 150, 12);
        ImmutableArray<Entity> bossEntities = this.getEngine().getEntitiesFor(Family.all(BossEnemyComponent.class).get());
        if (bossEntities.size() > 0) {
            Entity boss = bossEntities.get(0);
            PositionComponent bossPosition = ComponentMap.positionMapper.get(boss);
            double pct = (levelLength - bossPosition.position.y) / levelLength;
            ArcadeSpaceShooter.spriteBatch.setColor(Color.WHITE);
            ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, ArcadeSpaceShooter.screenRect.width - 159, 26, (int)(pct * 148), 10);
        }
    }
}
