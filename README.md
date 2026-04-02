# Galaxia

Galaxia is an arcade space shooter built with libGDX and Ashley ECS. The repository contains both the playable desktop game and a JavaFX-based level editor used to create JSON level data under `assets/levels/`.

## Overview

This project has moved well beyond the default `gdx-liftoff` scaffold. The current codebase includes:

- Real-time arcade combat with enemy fighters, UFOs, meteors, bosses, and player upgrades
- A service-backed level system that can load JSON-authored content or fall back to procedural spawning
- A standalone level editor in the `tools` module with object placement, boss configuration, and timed events
- Shared game assets, shaders, audio, and UI skin resources under `assets/`

The documentation set in `docs/` is the canonical planning layer for future implementation work.

## Modules

- `core`: shared gameplay code, ECS components/systems, level loading, assets integration
- `lwjgl3`: desktop launcher, packaging, and runnable jar configuration
- `tools`: JavaFX editor for authoring level JSON consumed by the runtime
- `assets`: textures, audio, shaders, UI skin, and authored level files

## Current Priorities

- Stabilize the level-authoring pipeline between `tools` and the runtime loader
- Reduce documentation drift between legacy notes in `claudedocs/` and the current codebase
- Add repeatable verification around gameplay startup, editor save/load, and level data compatibility

See:

- `docs/roadmap.md` for the prioritized backlog
- `docs/plan.md` for the next implementation slice
- `docs/clarification.md` for open product and technical decisions
- `docs/testing-gaps.md` for current QA holes

## Getting Started

### Prerequisites

- JDK 17 for Gradle and the `tools` module
- Ability to target Java 8 bytecode for `core` and `lwjgl3` through Gradle

### Run the Game

```bash
./gradlew lwjgl3:run
```

### Run the Level Editor

```bash
./gradlew tools:run
```

### Build Everything

```bash
./gradlew build
```

### Build the Desktop Jar

```bash
./gradlew lwjgl3:jar
```

Output:

- `lwjgl3/build/libs/Galaxia-<version>.jar`

## Development Notes

- `assets/assets.txt` is generated during resource processing.
- The game runtime reads authored level content from `assets/levels/`.
- There is currently no `src/test` suite; verification is mostly manual and documented in `docs/testing-gaps.md`.
- The level editor currently includes some path/configuration assumptions that should be normalized before treating it as fully portable.

## Legacy Documentation

Historical implementation notes remain under `claudedocs/`. Keep them as reference material, but treat `docs/` and this README as the current source of truth for planning and future iterations.
