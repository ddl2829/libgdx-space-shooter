# MEDIUM_03: LevelSystem Refactoring Plan

**Status**: ✅ COMPLETED
**Priority**: MEDIUM
**Estimated Effort**: 4-6 hours
**Complexity**: HIGH
**Completion Date**: 2025-10-10

## Context

LevelSystem.java is currently a 409-line monolithic class with multiple responsibilities. This violates Single Responsibility Principle and makes the code difficult to test and maintain.

## Current Responsibilities (7 distinct concerns)

1. **Level Progression Management** (lines 30-31, 86-194)
   - Track current level number
   - Detect level completion
   - Transition between levels
   - Schedule next level loading

2. **Entity Spawning Queue** (lines 41-43, 52-62, 305-330)
   - Progressive entity spawning to avoid lag spikes
   - Queue management (FIFO)
   - Max entities per frame throttling (5 entities/frame)

3. **Level Loading** (lines 36-39, 196-225)
   - JSON level loading via LevelLoader
   - Fallback to procedural generation
   - Level sequence management

4. **JSON Level Parsing** (lines 227-279, 332-366)
   - Parse LevelData objects
   - Coordinate transformation (editor → game coordinates)
   - Scale X coordinates to fit screen
   - Invert Y coordinates for spawn timing
   - Entity type → prefab mapping

5. **Procedural Level Generation** (lines 368-395)
   - Legacy random level generation
   - Difficulty scaling based on level number
   - Spawn rate calculations

6. **Notification System** (lines 115-192)
   - Level completion messages
   - Tutorial messages (first level)
   - Next level announcements

7. **Boss Health Bar Rendering** (lines 397-408)
   - Draw boss HP bar during gameplay

## Proposed Architecture: 5 Services

### 1. **LevelProgressionService**
**Responsibility**: Manage level state and transitions

**Methods**:
```java
- void advanceLevel()
- boolean isLevelComplete(Engine engine)
- int getCurrentLevel()
- void resetLevel()
- String getCurrentLevelId()
- void setCurrentLevelId(String levelId)
```

**State**:
- Current level number
- Current level ID
- Level sequence information
- Level completion status

### 2. **EntitySpawnService**
**Responsibility**: Progressive entity spawning with performance management

**Methods**:
```java
- void queueEntity(Entity entity, float spawnY)
- void processSpawnQueue(Engine engine, int maxPerFrame)
- boolean isSpawnQueueEmpty()
- void clearQueue()
- int getQueueSize()
```

**State**:
- Spawn queue (List<PendingSpawn>)
- Max entities per frame setting
- Transform data for coordinate conversion

### 3. **LevelLoaderService**
**Responsibility**: Load and parse JSON level data

**Methods**:
```java
- LevelData loadLevel(String levelId)
- List<LevelInfo> getAvailableLevels()
- Entity createEntityFromLevelObject(LevelObject obj, TransformData transform)
- TransformData calculateTransform(LevelData data, float screenWidth)
```

**State**:
- Level sequence cache
- Current level index

### 4. **ProceduralLevelService**
**Responsibility**: Generate random levels for endless mode

**Methods**:
```java
- List<Entity> generateLevel(int levelNumber, int levelLength)
- int calculateLevelLength(int levelNumber)
- void addMeteors(List<Entity> entities, int levelNumber, int startPos, int endPos)
- void addEnemies(List<Entity> entities, int levelNumber, int startPos, int endPos)
- Entity createBoss(int levelNumber, int levelLength)
```

**State**:
- None (stateless generation)

### 5. **LevelNotificationService**
**Responsibility**: Display level-related UI messages

**Methods**:
```java
- void showLevelComplete(Engine engine)
- void showLevelStart(Engine engine, String levelName)
- void showTutorial(Engine engine)
- void showAllLevelsComplete(Engine engine)
- void scheduleNotification(Engine engine, String message, float delay)
```

**State**:
- None (stateless messaging)

## Refactored LevelSystem

After refactoring, LevelSystem becomes a **thin coordinator** that:
- Owns the 5 service instances
- Delegates work to appropriate services
- Orchestrates level progression flow
- Remains an EntitySystem for Ashley integration

**Expected size**: ~150 lines (down from 409)

## Implementation Steps

### Step 1: Create Service Classes (4 new files)
1. Create `/services/LevelProgressionService.java` (~80 lines)
2. Create `/services/EntitySpawnService.java` (~120 lines)
3. Create `/services/LevelLoaderService.java` (~150 lines)
4. Create `/services/ProceduralLevelService.java` (~100 lines)
5. Create `/services/LevelNotificationService.java` (~80 lines)

### Step 2: Extract Static Inner Classes
- Move `TransformData` to LevelLoaderService
- Move `PendingSpawn` to EntitySpawnService

### Step 3: Refactor LevelSystem
1. Add service fields to LevelSystem constructor
2. Replace inline logic with service calls
3. Update update() method to use services
4. Update BuildLevel() to delegate to services
5. Remove BuildLevelFromJSON() and BuildProceduralLevel()
6. Simplify draw() method

### Step 4: Update Service Registration
- Update ServiceLocator to provide level services
- Wire services together in LevelSystem constructor

### Step 5: Update Dependent Code
- GameScreen.java (lines 45-46, 152-154)
- GameOverScreen.java (lines 49, 74, 79)
- LevelSelectScreen.java (likely uses LevelSystem.setCurrentLevelId)

### Step 6: Testing
- Run `./gradlew build`
- Verify level loading works
- Verify level progression works
- Verify procedural generation still works

## Benefits

### Single Responsibility
- Each service has one clear purpose
- Easier to understand and modify

### Testability
- Services can be unit tested independently
- Mock services for testing LevelSystem

### Reusability
- EntitySpawnService can spawn any entities progressively
- ProceduralLevelService can be used for endless mode
- LevelNotificationService can show any game messages

### Maintainability
- Smaller, focused classes
- Clear boundaries between concerns
- Easier to find and fix bugs

## Risks & Considerations

### Static State
- Current LevelSystem uses static fields (levelNumber, currentLevelId, levelSequence)
- Consider moving to service instances instead
- May need to reset services on game restart

### Performance
- Service method calls add minor overhead
- EntitySpawnService already optimized (5 entities/frame)
- Minimal impact expected

### Backwards Compatibility
- LevelSystem.levelNumber is public static (used externally)
- Provide getter/setter for transition period
- Update external references gradually

## Files to Modify

**New Files (5)**:
- `core/src/main/java/com/dalesmithwebdev/galaxia/services/LevelProgressionService.java`
- `core/src/main/java/com/dalesmithwebdev/galaxia/services/EntitySpawnService.java`
- `core/src/main/java/com/dalesmithwebdev/galaxia/services/LevelLoaderService.java`
- `core/src/main/java/com/dalesmithwebdev/galaxia/services/ProceduralLevelService.java`
- `core/src/main/java/com/dalesmithwebdev/galaxia/services/LevelNotificationService.java`

**Modified Files (4)**:
- `core/src/main/java/com/dalesmithwebdev/galaxia/systems/LevelSystem.java` (simplify to ~150 lines)
- `core/src/main/java/com/dalesmithwebdev/galaxia/services/ServiceLocator.java` (add level services)
- `core/src/main/java/com/dalesmithwebdev/galaxia/screens/GameOverScreen.java` (update level access)
- `core/src/main/java/com/dalesmithwebdev/galaxia/screens/GameScreen.java` (update level access)

## Success Criteria

✅ LevelSystem < 200 lines
✅ Each service has single clear purpose
✅ All tests pass
✅ `./gradlew build` succeeds
✅ Game loads levels correctly
✅ Level progression works
✅ No performance regression

## Related Refactorings

This refactoring builds on:
- ✅ MEDIUM_01: GameStateService (completed)
- ✅ CRITICAL_01: System splitting pattern (completed)
- ✅ MEDIUM_02: Service extraction pattern (completed)

## Notes

- LevelSystem also has a `draw()` method for boss HP bar - consider moving to RenderSystem
- Notification system might be generalized for other game messages
- EntitySpawnService could potentially be used by other systems (e.g., enemy waves)
