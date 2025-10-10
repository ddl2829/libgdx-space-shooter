# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Galaxia is an arcade space shooter built with libGDX and Ashley ECS (Entity Component System). The game features:
- Multi-wave enemy combat with various enemy types (fighters, UFOs, bosses)
- Player upgrades (weapons, shields, special abilities)
- Dynamic difficulty scaling through level progression
- Custom shader effects (EMP, outline, vignette)
- Object pooling for performance optimization

## Build Commands

### Running the Game
```bash
./gradlew lwjgl3:run
```

### Building Runnable JAR
```bash
./gradlew lwjgl3:jar
# Output: lwjgl3/build/libs/Galaxia-*.jar
```

### Running Development Tools (Level Editor)
```bash
./gradlew tools:run
```
**macOS Note**: The tools module requires `-XstartOnFirstThread` JVM argument, which is automatically configured in Gradle.

### Building All Modules
```bash
./gradlew build
```

### Cleaning Build Artifacts
```bash
./gradlew clean              # Clean all modules
./gradlew core:clean         # Clean specific module
```

### Regenerating IDE Project Files
```bash
./gradlew idea               # IntelliJ IDEA
./gradlew eclipse            # Eclipse
```

### Running Tests
```bash
./gradlew test
```

## Project Structure

### Gradle Modules
- **core**: Main game logic shared across all platforms (Java 8)
- **lwjgl3**: Desktop launcher using LWJGL3 (Java 8)
- **tools**: JavaFX-based development tools including level editor (Java 17)

### Package Organization
```
com.dalesmithwebdev.galaxia/
├── ArcadeSpaceShooter.java     # Main game class, initializes systems and assets
├── components/                 # ECS components (data-only)
├── systems/                    # ECS systems (logic processors)
├── prefabs/                    # Entity factory classes
├── screens/                    # libGDX screen implementations
├── utility/                    # Helper classes
└── tests/                      # Test screens for debugging features
```

## ECS Architecture

Galaxia uses Ashley ECS pattern where:
- **Entities**: Game objects (player, enemies, projectiles, upgrades)
- **Components**: Pure data containers (no logic)
- **Systems**: Process entities with specific component combinations

### Core Systems (execution order matters)
1. **InputSystem**: Processes player input and controls
2. **MovementSystem**: Updates entity positions based on velocity/speed
3. **EnemyLogicSystem**: AI behavior for enemy entities
4. **DamageSystem**: Collision detection and damage application
5. **ExplosionSystem**: Particle effects and explosion animations
6. **RenderSystem**: Draws entities to screen (multiple render planes)
7. **LevelSystem**: Manages wave spawning and difficulty scaling

### Component Organization Patterns

**Entity Type Components** (marker components):
- `PlayerComponent`, `EnemyComponent`, `MeteorComponent`, `BossEnemyComponent`

**Capability Components** (entity abilities):
- `HasLasersComponent`, `HasMissilesComponent`, `HasBombsComponent`, `HasEmpComponent`, `HasShieldComponent`

**State Components** (temporary states):
- `ShieldedComponent`, `RecentlyDamagedComponent`, `BackgroundObjectComponent`

**Core Components** (spatial/rendering):
- `PositionComponent`: x, y coordinates
- `SpeedComponent`: velocity and movement direction
- `RenderComponent`: texture, dimensions, render plane (z-order)

**Combat Components**:
- `DealsDamageComponent`: Damage amount and damage type
- `TakesDamageComponent`: Health, max health
- `LaserComponent`, `MissileComponent`, `BombComponent`

### Working with ComponentMap
Use `ComponentMap` for efficient component access:
```java
ComponentMap.positionComponentComponentMapper.get(entity)
ComponentMap.playerComponentComponentMapper.get(entity)
```

### Adding New Entities

1. **Create prefab class** in `prefabs/` extending `Entity`
2. **Add required components** in constructor
3. **Use entity pools** for frequently created/destroyed entities (see `BackgroundElement` pattern)
4. **Add to engine** via `ArcadeSpaceShooter.engine.addEntity(entity)`

Example pattern:
```java
public class NewEnemy extends Entity {
    public NewEnemy() {
        add(new PositionComponent(x, y));
        add(new RenderComponent(texture, width, height, renderPlane));
        add(new EnemyComponent());
        add(new TakesDamageComponent(health));
        // ... other components
    }
}
```

## Screen Architecture

Galaxia uses libGDX's screen-based navigation:
- **StartScreen**: Main menu and game initialization
- **GameScreen**: Active gameplay, manages game loop and UI overlays
- **GameOverScreen**: End game state with score display
- **ShaderTestScreen**: Development testing screen (in tests/ directory)

Screen transitions handled via `ArcadeSpaceShooter.instance.setScreen(new ScreenName())`

## Asset Management

### Texture Atlas
All sprites packed into `ArcadeShooter.atlas`, accessed via:
```java
ArcadeSpaceShooter.textures.findRegion("regionName")
```

Common texture regions:
- Player: `player`, `playerLeft`, `playerRight`
- Enemies: various fighter and UFO sprites
- Meteors: `meteorBig`, `meteorBrown_*`, `meteorGrey_*`
- Weapons: `spaceMissiles` (indexed)
- Effects: `fire03`, `shield`, `blank`

### Asset List Generation
Asset manifest auto-generated at build time:
- **Generated file**: `assets/assets.txt`
- **Triggered by**: `processResources` Gradle task
- **Source**: `build.gradle` `generateAssetList` task

### Shaders
Custom shaders in `assets/shaders/`:
- **emp/**: EMP effect shader (screen distortion)
- **outline/**: Entity outline shader
- **vignette/**: Screen edge darkening

Each shader has `vertex.glsl` and `fragment.glsl` files.

## UI and Scene2D

UI uses Scene2D with skin file:
- **Skin**: `assets/ui/uiskin.json`
- **Access**: `ArcadeSpaceShooter.uiSkin`

Game uses Scene2D tables for UI layout (HUD, menus, overlays).

## Performance Considerations

### Object Pooling
Frequently created/destroyed entities use Ashley pools:
```java
private final Pool<Entity> pool = new Pool<Entity>() {
    @Override
    protected Entity newObject() {
        return new EntityType();
    }
};
```

See `ArcadeSpaceShooter.backgroundPool` for reference implementation.

### Memory Monitoring
Game logs memory usage every 60 seconds to console (see `ArcadeSpaceShooter.render()`).

### Render Planes (Z-ordering)
`RenderComponent` defines render plane constants:
- Lower values render first (background)
- Higher values render last (foreground)

## Development Tools Module

### Tools Architecture
- **Framework**: JavaFX for UI, libGDX embedded via `Lwjgl3AWTCanvas` wrapped in `SwingNode`
- **Purpose**: Visual level editor, asset management, debugging utilities
- **Java Version**: 17 (different from core/lwjgl3 which use Java 8)

### JavaFX + libGDX Integration
The tools module demonstrates integrating JavaFX UI with libGDX rendering:
1. `Lwjgl3AWTCanvas` provides AWT-compatible libGDX rendering
2. `SwingNode` wraps AWT components for JavaFX
3. Thread coordination between JavaFX, Swing, and libGDX render threads

### Platform Notes
- **macOS**: Requires `-XstartOnFirstThread` JVM argument (auto-configured in Gradle)
- **Windows/Linux**: No special configuration needed

## Testing and Debugging

### Test Cases
`GameTestCase` enum defines special test scenarios:
- **SHADER**: Launches `ShaderTestScreen` for shader debugging

Launch with test case:
```java
new ArcadeSpaceShooter(GameTestCase.SHADER)
```

### Test Screens
Development test screens located in `screens/tests/` for isolated feature testing.

## Key Dependencies

- **libGDX** (`gdxVersion`): Core game framework
- **Ashley** (`ashleyVersion`): Entity Component System
- **gdx-vfx** (`gdxVfxCoreVersion`, `gdxVfxEffectsVersion`): Post-processing effects
- **jbump** (`jbumpVersion`): Collision detection library
- **gdx-controllers**: Gamepad/controller support
- **gdx-freetype**: Dynamic font rendering
- **JavaFX 21**: Tools module UI framework

Version variables defined in `gradle.properties`.

## Common Patterns

### Entity Lifecycle
1. Create entity (via prefab or new Entity())
2. Add components
3. Add to engine: `ArcadeSpaceShooter.engine.addEntity(entity)`
4. Systems automatically process entity based on component composition
5. Remove from engine when done: `ArcadeSpaceShooter.engine.removeEntity(entity)`
6. Return to pool if using object pooling

### Accessing Game State
Static references in `ArcadeSpaceShooter`:
- `ArcadeSpaceShooter.engine`: Ashley engine instance
- `ArcadeSpaceShooter.spriteBatch`: Shared sprite batch
- `ArcadeSpaceShooter.textures`: Texture atlas
- `ArcadeSpaceShooter.screenRect`: Screen dimensions
- `ArcadeSpaceShooter.instance`: Game instance reference

### Pause/Resume
Game pause state: `ArcadeSpaceShooter.paused`
- Systems should check this flag before processing
- Used for pause menus and game over transitions

## Code Style Notes

- Java 8 source compatibility for core/lwjgl3 modules
- Java 17 for tools module
- Component classes are simple data containers (no logic)
- System classes contain all game logic
- Prefab classes are entity factories
- Use ComponentMap for component access efficiency
