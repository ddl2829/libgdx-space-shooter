# Galaxia Level Editor

A comprehensive JavaFX-based level editor for the Galaxia arcade space shooter game.

## Overview

The Galaxia Level Editor provides visual tools for designing game levels with meteors, enemies, power-ups, and boss configurations. It features real-time preview with libGDX rendering, drag-and-drop object placement, and automated difficulty calculation.

## Features

### Level List View
- Table display of all levels with sortable columns
- Statistics: enemy counts, meteors, power-ups, difficulty rating
- CRUD operations: Create, Edit, Duplicate, Delete levels
- Double-click to open level editor

### Level Editor Window
- **Object Palette**: Browse and select placeable objects
  - Meteors (large, small)
  - Enemies (fighters, UFOs, bosses)
  - Power-ups (lasers, missiles, bombs, shields, EMP)
- **Level Preview**: Visual editing canvas with libGDX rendering
  - Grid overlay (toggleable)
  - Snap-to-grid placement
  - Object selection and highlighting
  - Vertical scrolling for long levels
- **Configuration Panels**:
  - Object properties (position, scale, movement, combat stats)
  - Boss configuration (health, abilities, phases)
  - Level metadata (name, length, difficulty)

### Object Configuration
- **Position and Scale**: Precise placement and sizing
- **Movement**: Patterns (straight, diagonal, zigzag, circular), speed, direction
- **Enemy Properties**: Health, fire rate, weapons (lasers, missiles, shields)
- **Timing**: Spawn delays for choreographed encounters

### Boss Configuration
- Health and fire rate
- Movement patterns
- Abilities: Lasers (single/dual/diagonal/upgraded), missiles, shields, EMP
- Multiple phases with health thresholds

### Auto-Calculated Metrics
- Difficulty rating (0-10 scale)
- Estimated level length (time in seconds)
- Object counts by type

## Architecture

### Module Structure
```
tools/
├── src/main/java/com/cosmicdan/galaxia/tools/
│   ├── ToolsApplication.java         # Main entry point
│   ├── model/                        # Data models
│   │   ├── Level.java               # Level definition
│   │   ├── PlacedObject.java        # Object in level
│   │   ├── BossConfiguration.java   # Boss settings
│   │   ├── ObjectType.java          # Enum of placeable types
│   │   └── MovementPattern.java     # Movement patterns
│   ├── service/                     # Business logic
│   │   └── LevelService.java        # Level persistence
│   └── ui/                          # JavaFX UI components
│       ├── LevelListView.java       # Main window
│       ├── LevelEditorWindow.java   # Editor window
│       ├── LevelPreviewPanel.java   # libGDX preview
│       ├── ObjectPalettePanel.java  # Object selector
│       ├── ObjectConfigPanel.java   # Object properties
│       ├── BossConfigPanel.java     # Boss settings
│       └── LevelMetadataPanel.java  # Level info
└── README.md
```

### Technology Stack
- **JavaFX 21**: UI framework
- **libGDX**: Rendering engine (embedded in JavaFX via SwingNode)
- **Ashley ECS**: Entity Component System (game engine)
- **Java 17**: Language version (tools module)

### Data Persistence
- **Format**: JSON
- **Location**: `assets/levels/*.json`
- **Serialization**: libGDX Json library
- **Structure**: Level → PlacedObjects + BossConfiguration

## Requirements

- Java 17 or higher
- JavaFX 21 (automatically downloaded by Gradle)
- libGDX dependencies (provided by core module)

## Running the Tools

### Using Gradle (Recommended)

From the project root directory:

```bash
./gradlew tools:run
```

Or on Windows:

```bash
gradlew.bat tools:run
```

### From IDE (IntelliJ IDEA / Eclipse)

1. Import the project as a Gradle project
2. Locate the `ToolsApplication` class in the tools module
3. Run the main method

**Note for macOS users:** The tools require the `-XstartOnFirstThread` JVM argument, which is automatically configured in the Gradle run task. If running from an IDE, add this argument manually to your run configuration.

## Architecture

The tools module uses:
- **JavaFX**: For the main UI framework and windowing
- **libGDX (LWJGL3)**: Embedded via `Lwjgl3AWTCanvas` wrapped in JavaFX `SwingNode`
- **Core Module**: Access to game code, assets, and data structures

This architecture allows combining JavaFX's rich UI components with libGDX's powerful 2D rendering capabilities.

## Project Structure

```
tools/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/dalesmithwebdev/galaxia/tools/
│       │       ├── ToolsApplication.java    # Main JavaFX application
│       │       └── LevelEditor.java         # Level editor implementation
│       └── resources/                       # Tool-specific resources
└── build.gradle                             # Module build configuration
```

## Development

### Adding New Tools

1. Create a new class for your tool in the `com.dalesmithwebdev.galaxia.tools` package
2. Design the UI using JavaFX components
3. If you need libGDX rendering, follow the pattern in `LevelEditor.java`
4. Add a new tab in `ToolsApplication.java` to expose your tool

### Building Standalone JAR

To build a standalone executable JAR:

```bash
./gradlew tools:jar
```

The JAR will be created at: `tools/build/libs/Galaxia-tools-<version>.jar`

Run it with:

```bash
java -jar tools/build/libs/Galaxia-tools-<version>.jar
```

**macOS users:** Add the `-XstartOnFirstThread` argument:

```bash
java -XstartOnFirstThread -jar tools/build/libs/Galaxia-tools-<version>.jar
```

## Technical Notes

### JavaFX + libGDX Integration

The level editor embeds libGDX rendering using:
1. `Lwjgl3AWTCanvas` - libGDX's AWT-compatible canvas
2. `SwingNode` - JavaFX wrapper for Swing/AWT components
3. Thread coordination between JavaFX, Swing, and libGDX render threads

This approach allows:
- libGDX to render game content in real-time
- JavaFX UI controls for editing and configuration
- Seamless integration between game rendering and tool UI

### Platform Compatibility

- **Windows**: Full support
- **macOS**: Requires `-XstartOnFirstThread` JVM argument (automatically configured)
- **Linux**: Full support

## Usage Guide

### Creating a New Level
1. Launch level editor
2. Click "New Level" in main window
3. Configure level metadata (name, length, time)
4. Place objects from palette into preview area
5. Configure object properties in right panel
6. Save level

### Placing Objects
1. Select object type from left palette
2. Click in preview area to place
3. Objects snap to grid (toggleable)
4. Click existing objects to select and edit

### Editing Objects
1. Click object in preview to select
2. Configure properties in "Object" tab:
   - Position (x, y coordinates)
   - Scale (0.1 - 3.0)
   - Movement (pattern, speed, direction)
   - Enemy stats (health, fire rate, weapons)
   - Spawn timing
3. Click "Apply Changes"

### Configuring Bosses
1. Check "Has Boss" in Level tab
2. Switch to "Boss" tab
3. Configure:
   - Basic properties (health, fire rate, speed, movement)
   - Abilities (lasers, missiles, shields, EMP)
   - Phases (multiple phases with health threshold)
4. Click "Apply Changes"

### Level Validation
- Click "Validate" button to check for errors
- Checks: required fields, level boundaries, boss config consistency
- Fix any errors before saving

### Saving Levels
1. Click "Save" button
2. Validates level automatically
3. Writes JSON file to `assets/levels/`
4. Updates level list view

## Level Data Format

### Sample JSON Structure
```json
{
  "id": "level_sample",
  "name": "Sample Level 1",
  "length": 5000.0,
  "estimatedTimeSeconds": 60.0,
  "difficultyRating": 3.5,
  "hasBoss": true,
  "objects": [
    {
      "type": "ENEMY_FIGHTER",
      "x": 200.0,
      "y": 4500.0,
      "scale": 1.0,
      "movementPattern": "STRAIGHT",
      "speed": 2.0,
      "directionX": 0.0,
      "directionY": -1.0,
      "rotationSpeed": 0.0,
      "health": 10,
      "fireRate": 3000,
      "hasLasers": true,
      "hasMissiles": false,
      "hasShield": false,
      "spawnDelay": 0.0
    }
  ],
  "bossConfig": {
    "spriteRegion": "bossEnemy",
    "health": 150,
    "fireRate": 1000,
    "speed": 1.5,
    "movementPattern": "CIRCULAR",
    "hasLasers": true,
    "hasUpgradedLasers": true,
    "hasDualLasers": true,
    "hasDiagonalLasers": false,
    "hasMissiles": true,
    "hasShield": true,
    "hasEmp": false,
    "hasMultiplePhases": true,
    "phaseHealthThreshold": 50
  }
}
```

## Design Decisions

### JavaFX + libGDX Integration
- **Pattern**: libGDX Lwjgl3Application wrapped in Swing Canvas, embedded in JavaFX SwingNode
- **Why**: Provides accurate game rendering preview while maintaining JavaFX UI benefits
- **Trade-off**: Threading complexity (JavaFX thread, Swing EDT, libGDX render thread)

### Data Model Separation
- **Pattern**: Model-Service-UI architecture
- **Why**: Clean separation of concerns, easier testing, reusable business logic
- **Files**: `model/` (pure data), `service/` (logic), `ui/` (presentation)

### Grid System
- **Grid Size**: 20 pixels
- **Snap**: Optional snap-to-grid for precise alignment
- **Visual**: Toggleable grid overlay for reference

### Difficulty Calculation
- **Formula**: Weighted sum of enemy health, weapons, boss stats
- **Factors**: Enemy type (+health*0.5), boss multiplier (3x), power-ups (-2)
- **Scale**: Normalized to 0-10 range
- **Purpose**: Quick assessment for level balancing

## Limitations and Future Enhancements

### Current Limitations
1. **No Undo/Redo**: Manual object deletion only
2. **Limited Preview**: Static rendering, no animation preview
3. **No Multi-Wave Support**: Single wave per level
4. **No Path Editor**: Movement patterns are preset enums
5. **No Asset Preview**: Texture names only, no thumbnail view
6. **Fixed Viewport**: 600x800 preview size, no zoom

### Planned Enhancements
1. **Command Pattern**: Undo/redo for all operations
2. **Animation Preview**: Play level in editor with simulated player movement
3. **Wave System**: Multiple waves per level with timing
4. **Path Editor**: Visual bezier curve editor for custom movement
5. **Asset Browser**: Thumbnail view with drag-drop from atlas
6. **Zoom Controls**: Scale preview viewport (0.25x - 2.0x)
7. **Copy/Paste**: Duplicate objects within and across levels
8. **Templates**: Pre-configured enemy formations and wave patterns

## Integration with Game Runtime

### Current State
- Level editor produces JSON files
- Game must implement level loader to read JSON and spawn entities

### Integration Steps (To Be Implemented)
1. Create `LevelLoader` class in game `core` module
2. Parse JSON using libGDX Json library
3. Instantiate entities from PlacedObject definitions
4. Spawn entities based on player Y position and spawn delays
5. Handle boss spawn when level complete

### Suggested Game Code Structure
```java
public class LevelLoader {
    private Json json;
    private Level currentLevel;

    public void loadLevel(String levelId) {
        String jsonData = Gdx.files.internal("levels/" + levelId + ".json").readString();
        currentLevel = json.fromJson(Level.class, jsonData);
    }

    public void updateSpawning(float playerY, float deltaTime) {
        // Check spawn triggers based on playerY and object spawn delays
        // Instantiate entities using existing prefab classes
    }
}
```

## Troubleshooting

### Editor Won't Start
- Check Java version: `java -version` (must be 17+)
- Verify assets directory exists: `assets/ArcadeShooter.atlas`
- Check console for libGDX initialization errors

### Objects Not Rendering
- Verify texture atlas is loaded: check console for "Failed to load texture atlas"
- Ensure object texture region names match atlas regions
- Check `ObjectType.textureRegionName` values

### Save/Load Issues
- Check `assets/levels/` directory permissions
- Verify JSON structure matches model classes
- Check console for serialization errors

### macOS: "Can't use getScreenResolution() when not on the main thread"
Add `-XstartOnFirstThread` to your JVM arguments. This is automatically included in the Gradle run task.

### Performance Issues
- Reduce level object count (< 100 objects recommended for smooth editing)
- Disable grid overlay if rendering slow
- Close other editor windows

## Contributing

When adding new object types:
1. Add enum to `ObjectType` with display name and texture region
2. Add button to `ObjectPalettePanel`
3. Update `PlacedObject` defaults if needed
4. Add game prefab class for runtime spawning

When adding new properties:
1. Add field to data model (`PlacedObject`, `BossConfiguration`, `Level`)
2. Add UI controls to appropriate config panel
3. Update apply/validation logic
4. Update sample JSON file
