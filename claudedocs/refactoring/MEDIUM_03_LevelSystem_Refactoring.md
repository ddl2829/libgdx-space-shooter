# MEDIUM: LevelSystem Refactoring

**Priority**: MEDIUM
**Impact**: Medium (improves maintainability)
**Effort**: Medium (2-3 hours)
**SOLID Violations**: Single Responsibility Principle

## Problem Statement

`LevelSystem.java:24-320` handles 6 distinct responsibilities:

1. Level progression state management (lines 25-35, 58-162)
2. JSON level loading (lines 195-237)
3. Procedural level generation (lines 279-306)
4. Coordinate transformation/scaling (lines 199-250)
5. Entity spawning from data (lines 239-277)
6. Progress bar rendering (lines 308-319)

**Issues**:
- Mixing game logic with loading logic
- Rendering in a system class (should be in RenderSystem)
- Hard to test individual aspects
- Hard to swap level sources (JSON vs procedural)

## Proposed Solution

Split into 3 focused components:

### 1. LevelLoaderService
**Responsibility**: Load level data from various sources

```java
package com.dalesmithwebdev.galaxia.level;

import java.util.List;

public class LevelLoaderService {
    private final JsonLevelLoader jsonLoader;
    private final ProceduralLevelGenerator proceduralGenerator;

    public LevelLoaderService() {
        this.jsonLoader = new JsonLevelLoader();
        this.proceduralGenerator = new ProceduralLevelGenerator();
    }

    public LevelData loadLevel(String levelId) {
        // Try JSON first
        LevelData data = jsonLoader.load(levelId);
        if (data != null) {
            return data;
        }

        // Fallback to procedural
        return proceduralGenerator.generate(levelId);
    }

    public List<LevelLoader.LevelInfo> getAvailableLevels() {
        return jsonLoader.getAvailableLevels();
    }
}
```

### 2. JsonLevelLoader
**Responsibility**: Load JSON level files

```java
package com.dalesmithwebdev.galaxia.level;

import java.util.List;

public class JsonLevelLoader {
    public LevelData load(String levelId) {
        // Extract from LevelSystem:174-193
        return LevelLoader.loadLevel(levelId);
    }

    public List<LevelLoader.LevelInfo> getAvailableLevels() {
        return LevelLoader.getAvailableLevels();
    }
}
```

### 3. ProceduralLevelGenerator
**Responsibility**: Generate levels procedurally

```java
package com.dalesmithwebdev.galaxia.level;

import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.constants.LevelConstants;
import com.dalesmithwebdev.galaxia.utility.Rand;

public class ProceduralLevelGenerator {

    public LevelData generate(int levelNumber) {
        LevelData data = new LevelData();
        data.setName("Level " + levelNumber);

        int levelLength = calculateLevelLength(levelNumber);
        int startPosition = calculateStartPosition(levelNumber);

        // Generate meteors
        for (int y = startPosition; y < levelLength + 5000; y++) {
            addRandomMeteors(data, y, levelNumber);
            addRandomEnemies(data, y, levelNumber);
        }

        // Add boss at end
        addBoss(data, levelLength, levelNumber);

        return data;
    }

    private int calculateLevelLength(int levelNumber) {
        // Extract from LevelSystem:281
        return (int)ArcadeSpaceShooter.screenRect.height +
               LevelConstants.BASE_LEVEL_LENGTH +
               (LevelConstants.LEVEL_LENGTH_INCREMENT * levelNumber);
    }

    private int calculateStartPosition(int levelNumber) {
        // Extract from LevelSystem:282
        return levelNumber == 1 ?
               (int)ArcadeSpaceShooter.screenRect.height + LevelConstants.INITIAL_SPAWN_DELAY :
               (int)ArcadeSpaceShooter.screenRect.height;
    }

    private void addRandomMeteors(LevelData data, int y, int levelNumber) {
        // Extract spawn logic from LevelSystem:284-295
    }

    private void addRandomEnemies(LevelData data, int y, int levelNumber) {
        // Extract spawn logic from LevelSystem:297-301
    }

    private void addBoss(LevelData data, int levelLength, int levelNumber) {
        // Extract from LevelSystem:304-305
    }
}
```

### 4. CoordinateTransformService
**Responsibility**: Transform editor coordinates to game coordinates

```java
package com.dalesmithwebdev.galaxia.level;

import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.constants.LevelConstants;

public class CoordinateTransformService {
    private final float screenWidth;
    private final float screenHeight;

    public CoordinateTransformService(float screenWidth, float screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public TransformResult calculateTransform(LevelData levelData) {
        // Find min/max X and max Y (extract from LevelSystem:199-212)
        float minEditorX = Float.MAX_VALUE;
        float maxEditorX = Float.MIN_VALUE;
        float maxEditorY = 0;

        for (LevelObject obj : levelData.getObjects()) {
            if (obj.getX() < minEditorX) minEditorX = obj.getX();
            if (obj.getX() > maxEditorX) maxEditorX = obj.getX();
            if (obj.getY() > maxEditorY) maxEditorY = obj.getY();
        }

        // Calculate scaling
        float availableWidth = screenWidth - (2 * LevelConstants.HORIZONTAL_BUFFER);
        float editorWidth = maxEditorX - minEditorX;
        float xScale = editorWidth > 0 ? availableWidth / editorWidth : 1.0f;

        return new TransformResult(minEditorX, maxEditorX, maxEditorY, xScale);
    }

    public Vector2 transformPosition(float editorX, float editorY, TransformResult transform) {
        // Extract from LevelSystem:241-249
        float normalizedX = LevelConstants.HORIZONTAL_BUFFER +
                          (editorX - transform.minEditorX) * transform.xScale;

        float invertedY = transform.maxEditorY - editorY;
        float spawnY = screenHeight + invertedY;

        return new Vector2(normalizedX, spawnY);
    }

    public static class TransformResult {
        public final float minEditorX;
        public final float maxEditorX;
        public final float maxEditorY;
        public final float xScale;

        public TransformResult(float minEditorX, float maxEditorX, float maxEditorY, float xScale) {
            this.minEditorX = minEditorX;
            this.maxEditorX = maxEditorX;
            this.maxEditorY = maxEditorY;
            this.xScale = xScale;
        }
    }
}
```

### 5. LevelSpawnerService
**Responsibility**: Create entities from level objects

```java
package com.dalesmithwebdev.galaxia.level;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.galaxia.prefabs.*;

public class LevelSpawnerService {
    private final Engine engine;
    private final CoordinateTransformService coordinateService;

    public LevelSpawnerService(Engine engine, CoordinateTransformService coordinateService) {
        this.engine = engine;
        this.coordinateService = coordinateService;
    }

    public void spawnLevel(LevelData levelData) {
        CoordinateTransformService.TransformResult transform =
            coordinateService.calculateTransform(levelData);

        for (LevelObject obj : levelData.getObjects()) {
            Entity entity = createEntityFromType(obj, transform);
            if (entity != null) {
                engine.addEntity(entity);
            }
        }
    }

    private Entity createEntityFromType(LevelObject obj,
                                       CoordinateTransformService.TransformResult transform) {
        // Extract from LevelSystem:239-277
        Vector2 gamePos = coordinateService.transformPosition(obj.getX(), obj.getY(), transform);

        switch (obj.getType()) {
            case "METEOR_SMALL":
                return new SmallMeteor(gamePos.x, gamePos.y, -3);
            case "METEOR_LARGE":
                return new LargeMeteor(gamePos.x, gamePos.y, -3);
            case "ENEMY_FIGHTER":
                return new EnemyFighter(1, gamePos.x, gamePos.y, 0.8);
            case "POWERUP_LASER_STRENGTH":
                return new LaserStrengthUpgrade(1, gamePos.x, gamePos.y);
            // ... all other types
            default:
                System.err.println("Unknown object type: " + obj.getType());
                return null;
        }
    }
}
```

### 6. Refactored LevelProgressionSystem
**Responsibility**: ONLY manage level state and progression

```java
package com.dalesmithwebdev.galaxia.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Timer;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.BossEnemyComponent;
import com.dalesmithwebdev.galaxia.components.EnemyComponent;
import com.dalesmithwebdev.galaxia.components.NotificationComponent;
import com.dalesmithwebdev.galaxia.constants.LevelConstants;
import com.dalesmithwebdev.galaxia.level.*;
import com.dalesmithwebdev.galaxia.utility.SoundManager;

public class LevelProgressionSystem extends EntitySystem {
    public static int levelNumber = 0;
    public static int levelLength;

    private final LevelLoaderService levelLoader;
    private final LevelSpawnerService levelSpawner;

    private boolean preppingLevel = false;
    private float levelRunningTime = 0;

    private String currentLevelId = null;
    private java.util.List<LevelLoader.LevelInfo> levelSequence = null;
    private int currentLevelIndex = 0;

    public LevelProgressionSystem(LevelLoaderService levelLoader,
                                  LevelSpawnerService levelSpawner) {
        this.levelLoader = levelLoader;
        this.levelSpawner = levelSpawner;
    }

    public void setCurrentLevelId(String levelId) {
        this.currentLevelId = levelId;
        this.levelSequence = levelLoader.getAvailableLevels();

        for (int i = 0; i < levelSequence.size(); i++) {
            if (levelSequence.get(i).getId().equals(levelId)) {
                currentLevelIndex = i;
                break;
            }
        }
    }

    public void startInitialLevel() {
        buildLevel();
    }

    @Override
    public void update(float gameTime) {
        if (ArcadeSpaceShooter.paused || preppingLevel) {
            return;
        }

        levelRunningTime += gameTime;

        if (levelRunningTime < LevelConstants.MIN_LEVEL_TIME_MS) {
            return;
        }

        checkLevelCompletion();
    }

    private void checkLevelCompletion() {
        // Extract from LevelSystem:76-161
    }

    private void buildLevel() {
        levelRunningTime = 0;
        SoundManager.playWarpIn();

        // Load level data
        LevelData levelData;
        if (currentLevelId != null) {
            levelData = levelLoader.loadLevel(currentLevelId);
        } else {
            levelData = levelLoader.loadLevel(String.valueOf(levelNumber));
        }

        // Spawn entities
        levelSpawner.spawnLevel(levelData);
        levelLength = calculateLevelLength(levelData);

        preppingLevel = false;
    }

    private int calculateLevelLength(LevelData levelData) {
        // Calculate from level data max Y
        return (int)(ArcadeSpaceShooter.screenRect.height + levelData.getMaxY() + 1000);
    }
}
```

### 7. Move Progress Bar to RenderSystem

```java
// In RenderSystem.java
public void drawBossProgressBar() {
    // Extract from LevelSystem:308-319
    ImmutableArray<Entity> bossEntities = engine.getEntitiesFor(
        Family.all(BossEnemyComponent.class).get()
    );

    if (bossEntities.size() > 0) {
        Entity boss = bossEntities.get(0);
        PositionComponent bossPos = ComponentMap.positionMapper.get(boss);
        double pct = (LevelSystem.levelLength - bossPos.position.y) / LevelSystem.levelLength;

        // Draw progress bar
        spriteBatch.setColor(Color.BLACK);
        spriteBatch.draw(blank, screenRect.width - 158, 25, 150, 12);
        spriteBatch.setColor(Color.WHITE);
        spriteBatch.draw(blank, screenRect.width - 159, 26, (int)(pct * 148), 10);
    }
}
```

## Refactoring Steps

1. Create `LevelLoaderService.java`
2. Create `JsonLevelLoader.java`
3. Create `ProceduralLevelGenerator.java`
4. Create `CoordinateTransformService.java`
5. Create `LevelSpawnerService.java`
6. Rename `LevelSystem` → `LevelProgressionSystem`
7. Refactor `LevelProgressionSystem` to use services
8. Move progress bar rendering to `RenderSystem`
9. Update `ArcadeSpaceShooter` to wire services
10. Test level loading and progression

## Files to Create

```
core/src/main/java/com/dalesmithwebdev/galaxia/level/
├── LevelLoaderService.java
├── JsonLevelLoader.java
├── ProceduralLevelGenerator.java
├── CoordinateTransformService.java
└── LevelSpawnerService.java
```

## Files to Modify

- `core/src/main/java/com/dalesmithwebdev/galaxia/systems/LevelSystem.java` → Rename and refactor
- `core/src/main/java/com/dalesmithwebdev/galaxia/systems/RenderSystem.java` → Add boss progress bar
- `core/src/main/java/com/dalesmithwebdev/galaxia/ArcadeSpaceShooter.java` → Wire services

## Benefits

- **Testability**: Can test loading, generation, and spawning separately
- **Flexibility**: Easy to add new level sources (XML, database, etc.)
- **Clarity**: Each class has single, clear purpose
- **Maintainability**: Changes to coordinate math don't affect progression
- **Separation**: Rendering moved to appropriate system

## Metrics

- **LevelSystem**: 320 lines → ~150 lines (LevelProgressionSystem)
- **New Services**: ~350 lines (5 focused classes)
- **Net Change**: +180 lines but far better organized
- **Boss Progress Bar**: Moved to RenderSystem (~12 lines)
