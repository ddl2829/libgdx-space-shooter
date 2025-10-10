# MEDIUM: Reduce Global State

**Priority**: MEDIUM
**Impact**: High (enables testing, reduces coupling)
**Effort**: High (3-4 hours)
**SOLID Violations**: Dependency Inversion Principle

## Problem Statement

`ArcadeSpaceShooter.java` has 40+ public static fields accessed throughout codebase:

```java
public static Rectangle screenRect;
public static Engine engine;
public static SpriteBatch spriteBatch;
public static TextureAtlas textures;
public static Music backgroundMusic;
// ... 35+ more static fields
```

**Problems**:
- Impossible to unit test systems in isolation
- Tight coupling throughout codebase
- Can't run multiple game instances
- Global state makes reasoning about code difficult
- No dependency injection = hard to mock

## Proposed Solution

Create service classes with dependency injection:

### 1. AssetService

```java
package com.dalesmithwebdev.galaxia.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.List;

public class AssetService {
    private final TextureAtlas textures;
    private final SpriteBatch spriteBatch;
    private final BitmapFont bitmapFont;
    private final Skin uiSkin;

    // Texture references
    private final TextureRegion blank;
    private final TextureRegion playerShield;
    private final TextureRegion playerLivesGraphic;
    private final List<TextureRegion> backgroundElements;
    private final List<TextureRegion> shipTextures;
    private final List<TextureRegion> smallMeteors;
    private final List<TextureRegion> bigMeteors;
    private final TextureRegion missile;
    private final TextureRegion bomb;
    private final TextureRegion fireEffect;

    // Shaders
    private final ShaderProgram empShader;
    private final ShaderProgram outlineShader;
    private final ShaderProgram vignetteShader;

    // Audio
    private final Music backgroundMusic;

    public AssetService() {
        // Load all assets (extract from ArcadeSpaceShooter.create())
        this.textures = new TextureAtlas(Gdx.files.internal("ArcadeShooter.atlas"));
        this.spriteBatch = new SpriteBatch();
        this.bitmapFont = new BitmapFont();
        this.uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // Initialize texture references...
        this.blank = textures.findRegion("blank");
        // ... etc
    }

    // Getters
    public TextureAtlas getTextures() { return textures; }
    public SpriteBatch getSpriteBatch() { return spriteBatch; }
    public BitmapFont getBitmapFont() { return bitmapFont; }
    public TextureRegion getBlank() { return blank; }
    // ... all other getters

    public void dispose() {
        spriteBatch.dispose();
        bitmapFont.dispose();
        textures.dispose();
        backgroundMusic.dispose();
    }
}
```

### 2. GameStateService

```java
package com.dalesmithwebdev.galaxia.services;

public class GameStateService {
    private int kills = 0;
    private double playerScore = 0;
    private boolean paused = false;
    private boolean gameOverScheduled = false;
    private boolean empActive = false;
    private int empElapsedTime = 0;
    private int totalTime = 0;

    // Getters and setters
    public int getKills() { return kills; }
    public void setKills(int kills) { this.kills = kills; }
    public void incrementKills(int amount) { this.kills += amount; }

    public double getPlayerScore() { return playerScore; }
    public void setPlayerScore(double score) { this.playerScore = score; }
    public void addScore(double score) { this.playerScore += score; }

    public boolean isPaused() { return paused; }
    public void setPaused(boolean paused) { this.paused = paused; }

    public boolean isEmpActive() { return empActive; }
    public void setEmpActive(boolean active) { this.empActive = active; }

    public int getEmpElapsedTime() { return empElapsedTime; }
    public void updateEmpElapsedTime(float delta) { this.empElapsedTime += delta; }
    public void resetEmpElapsedTime() { this.empElapsedTime = 0; }

    public int getTotalTime() { return totalTime; }
    public void updateTotalTime(float delta) { this.totalTime += delta; }

    public boolean isGameOverScheduled() { return gameOverScheduled; }
    public void setGameOverScheduled(boolean scheduled) { this.gameOverScheduled = scheduled; }

    public void reset() {
        kills = 0;
        playerScore = 0;
        paused = false;
        gameOverScheduled = false;
        empActive = false;
        empElapsedTime = 0;
        totalTime = 0;
    }
}
```

### 3. ServiceLocator (Simple Pattern)

```java
package com.dalesmithwebdev.galaxia.services;

public class ServiceLocator {
    private static ServiceLocator instance;

    private final AssetService assetService;
    private final GameStateService gameStateService;

    private ServiceLocator() {
        this.assetService = new AssetService();
        this.gameStateService = new GameStateService();
    }

    public static ServiceLocator getInstance() {
        if (instance == null) {
            instance = new ServiceLocator();
        }
        return instance;
    }

    public static void reset() {
        if (instance != null) {
            instance.assetService.dispose();
        }
        instance = null;
    }

    public AssetService getAssets() {
        return assetService;
    }

    public GameStateService getGameState() {
        return gameStateService;
    }
}
```

### 4. Update Systems to Use Services

```java
// BEFORE (DamageSystem.java)
Entity explosion = new Entity();
explosion.add(new RenderComponent(ArcadeSpaceShooter.textures.findRegion("laserRedShot"), RenderComponent.PLANE_ABOVE));
ArcadeSpaceShooter.playerScore += score;

// AFTER
AssetService assets = ServiceLocator.getInstance().getAssets();
GameStateService gameState = ServiceLocator.getInstance().getGameState();

Entity explosion = new Entity();
explosion.add(new RenderComponent(assets.getTextures().findRegion("laserRedShot"), RenderComponent.PLANE_ABOVE));
gameState.addScore(score);
```

### 5. Better: Constructor Injection for Systems

```java
package com.dalesmithwebdev.galaxia.systems;

public class DamageSystem extends EntitySystem {
    private final AssetService assets;
    private final GameStateService gameState;

    public DamageSystem(AssetService assets, GameStateService gameState) {
        this.assets = assets;
        this.gameState = gameState;
    }

    @Override
    public void update(float gameTime) {
        if(gameState.isPaused()) {
            return;
        }
        // Use assets and gameState instead of ArcadeSpaceShooter.*
    }
}
```

## Implementation Strategy

### Phase 1: Create Services (Low Risk)
1. Create `AssetService` with all texture/audio/shader access
2. Create `GameStateService` with all game state
3. Create `ServiceLocator` or use dependency injection framework
4. Keep existing static fields for backward compatibility

### Phase 2: Gradual Migration (Per System)
1. Update one system at a time to use services
2. Test after each system migration
3. Once all systems migrated, remove static fields

### Phase 3: Constructor Injection (Optional)
1. Pass services to system constructors
2. Enables true unit testing
3. Eliminates service locator pattern

## Refactoring Steps

1. Create `services` package
2. Create `AssetService.java`
3. Create `GameStateService.java`
4. Create `ServiceLocator.java`
5. Update `ArcadeSpaceShooter.create()` to initialize services
6. Migrate systems one-by-one:
   - `DamageSystem`
   - `InputSystem`
   - `LevelSystem`
   - `RenderSystem`
   - `MovementSystem`
   - `EnemyLogicSystem`
   - `ExplosionSystem`
7. Update screens to use services
8. Remove static fields from `ArcadeSpaceShooter`

## Files to Create

```
core/src/main/java/com/dalesmithwebdev/galaxia/services/
├── AssetService.java
├── GameStateService.java
└── ServiceLocator.java
```

## Files to Modify

- `ArcadeSpaceShooter.java` - Initialize services, deprecate statics
- All system files (7 files)
- All screen files (4 files)
- Prefab files that access static assets

## Benefits

- **Testability**: Can mock services for unit tests
- **Flexibility**: Can swap implementations (e.g., test assets)
- **Clarity**: Clear dependencies in constructor signatures
- **Maintainability**: Service boundaries make refactoring easier
- **Multiple Instances**: Could run multiple game instances
- **Dependency Inversion**: Depend on abstractions, not statics

## Testing Benefits

```java
// Before: IMPOSSIBLE to test
public class DamageSystemTest {
    @Test
    public void testDamageApplication() {
        // Can't test because ArcadeSpaceShooter.engine is null
        // Can't mock textures, sounds, etc.
    }
}

// After: EASY to test
public class DamageSystemTest {
    @Test
    public void testDamageApplication() {
        AssetService mockAssets = mock(AssetService.class);
        GameStateService mockState = mock(GameStateService.class);
        DamageSystem system = new DamageSystem(mockAssets, mockState);

        // Test in isolation!
    }
}
```

## Incremental Approach (Recommended)

Don't try to refactor everything at once. Migrate system-by-system:

**Week 1**: Create services, keep static fields, use both
**Week 2**: Migrate DamageSystem, InputSystem
**Week 3**: Migrate remaining systems
**Week 4**: Remove static fields, cleanup

## Alternative: Keep Static for Rapid Prototyping

If tight deadlines or rapid prototyping is priority:
- Keep static fields for now
- Add `// TODO: Refactor to service` comments
- Focus on gameplay features first
- Refactor when stability is needed

## Decision Point

**Refactor Now If**:
- Planning to add unit tests
- Need better code organization
- Multiple developers working on code
- Long-term maintenance expected

**Defer If**:
- Prototype/proof-of-concept stage
- Solo developer, short-term project
- Prioritizing features over architecture
