# Plan: Initial Stabilization Slice

## Goal

Fix the first stabilization batch: make the editor save path portable, add basic JSON-level tests, and define the manual smoke checks that remain after that.

## Approach

Start with the smallest changes that remove current friction:

1. Make editor level storage project-relative or configurable instead of machine-specific.
2. Add a small automated test baseline for editor validation and runtime JSON loading.
3. Keep graphical behavior in manual smoke checks for now.

## Phases

### Phase 1: Portable Level Storage

- Replace the hard-coded absolute path in `tools/.../service/LevelService.java`
- Resolve level storage relative to the project or application working directory
- Keep the default pointed at `assets/levels/` or an equivalent project-relative location

### Phase 2: Level Contract Audit

- Compare `tools/.../model/Level` and related classes against `core/.../level/LevelData` and `LevelObject`
- Confirm the current runtime JSON contract against representative files in `assets/levels/`
- Update docs to state which fields are currently relied on by the runtime and which are editor-only

### Phase 3: Verification Baseline

- Add tests for level validation and serialization in the code that does not require launching libGDX rendering
- Add a short smoke checklist for:
  - `./gradlew lwjgl3:run`
  - `./gradlew tools:run`
  - save/load of one authored level
  - runtime loading of one authored level from `assets/levels/`

## Files To Create

- `tools/src/test/...` test classes for editor persistence/validation if the module is made test-ready
- `core/src/test/...` test classes for runtime level DTO loading if practical
- Any small shared utility or config file needed to locate `assets/levels/` safely

## Files Likely To Change

- `tools/src/main/java/com/dalesmithwebdev/galaxia/tools/service/LevelService.java`
- `core/src/main/java/com/dalesmithwebdev/galaxia/level/LevelData.java`
- `core/src/main/java/com/dalesmithwebdev/galaxia/level/LevelObject.java`
- `core/src/main/java/com/dalesmithwebdev/galaxia/services/LevelLoaderService.java`
- `README.md`
- `CLAUDE.md`
- `docs/clarification.md`
- `docs/testing-gaps.md`

## Open Questions

- Should runtime support stay on the current reduced JSON contract, or should it grow to match the editor schema now?
- Should `tools:run` save directly into `assets/levels/` or into a configurable export directory?
- Is the immediate milestone portability, runtime compatibility, or editor parity?
