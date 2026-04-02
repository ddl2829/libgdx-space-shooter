# CLAUDE.md

## Project Summary

Galaxia is a libGDX arcade shooter with Ashley ECS gameplay code in `core`, a desktop launcher in `lwjgl3`, and a JavaFX level editor in `tools`. The runtime can load JSON levels from `assets/levels/` or fall back to procedural generation when authored data is unavailable.

## Read This First

Before choosing work, review:

- `docs/roadmap.md`
- `docs/plan.md`
- `docs/clarification.md`
- `docs/testing-gaps.md`

Use `claudedocs/` only as historical reference when the newer docs do not answer a question.

## Build And Run

```bash
./gradlew lwjgl3:run
./gradlew tools:run
./gradlew build
./gradlew test
```

Notes:

- There is no established `src/test` suite yet, so `test` may do little or nothing.
- `tools` targets Java 17 with JavaFX 21.
- `core` and `lwjgl3` compile to Java 8 compatibility.

## Architecture

### Runtime

- `core/src/main/java/com/dalesmithwebdev/galaxia/ArcadeSpaceShooter.java`: bootstraps assets, services, systems, and first screen
- `components/`: ECS data components
- `systems/`: gameplay processors such as movement, collision, combat, rendering, player control, and level progression
- `services/`: service-layer extractions from earlier refactors, including state, spawning, progression, and weapon logic
- `prefabs/`: entity construction helpers for player, enemies, meteors, bosses, and pickups
- `level/`: JSON level DTOs and loader utilities used by the runtime

### Tools

- `tools/.../model/`: editor-side level data model
- `tools/.../service/LevelService.java`: persistence and validation for editor-authored levels
- `tools/.../ui/`: level list, editor, preview, config panels, and timed events UI

## Known Constraints

- The editor persistence layer currently uses a hard-coded absolute levels path. The runtime expects `assets/levels/`.
- Documentation predating the current state of the repo is stored in `claudedocs/` and may not match the code exactly.
- The repository has almost no automated test coverage; manual smoke testing remains part of normal development.

## Working Rules

- Favor targeted fixes that keep runtime/editor level formats aligned.
- When touching level data structures, inspect both `core/.../level` and `tools/.../model` plus `LevelService`.
- Update `docs/` when priorities, assumptions, or verification steps change.
- Do not remove legacy docs unless the replacement exists in `docs/` and the migration is explicit.

## Out Of Scope For Routine Iterations

- Full engine rewrites
- Platform expansion beyond the current desktop-focused setup
- Large art/audio overhauls unrelated to the current implementation task
