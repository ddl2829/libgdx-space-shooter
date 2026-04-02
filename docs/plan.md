# Plan: Next Steps

## Goal

Define and implement the next work after the completed portability and baseline test pass.

## Current Baseline

- `LevelService` now resolves a project-relative levels directory by default and accepts an injected path for tests or alternate storage.
- JUnit 5 is wired into the Gradle test tasks for `core` and `tools`.
- There is now baseline automated coverage for:
  - editor persistence and validation behavior
  - runtime JSON contract checks against authored levels

## Next Implementation Steps

### 1. Resolve The Runtime/Editor Schema Boundary

- Decide whether runtime should continue consuming a reduced level schema or move toward the richer editor model.
- Document the supported runtime fields explicitly.
- If needed, add an export/translation boundary instead of relying on silent field ignoring.

### 2. Add Manual Smoke Verification To The Routine

- Run and document smoke checks for:
  - `./gradlew lwjgl3:run`
  - `./gradlew tools:run`
  - save/load of an authored level
  - runtime loading of an authored level from `assets/levels/`
- Record any failures or environment-specific launch issues.

### 3. Add The Next Layer Of Low-Cost Tests

- Add service-level tests for runtime logic that does not require rendering:
  - level progression decisions
  - spawn queue / virtual spawn behavior
  - other pure utility logic that affects gameplay correctness
- Keep rendering and screen-transition behavior out of unit tests unless a concrete regression justifies deeper coverage.

## Likely Files To Change

- `core/src/main/java/com/dalesmithwebdev/galaxia/level/LevelData.java`
- `core/src/main/java/com/dalesmithwebdev/galaxia/level/LevelObject.java`
- `core/src/main/java/com/dalesmithwebdev/galaxia/services/LevelLoaderService.java`
- `core/src/test/java/...`
- `tools/src/test/java/...`
- `docs/clarification.md`
- `docs/testing-gaps.md`

## Open Questions

- Should runtime support stay on the current reduced JSON contract, or should it grow to match the editor schema now?
- Should `tools:run` save directly into `assets/levels/` or into a configurable export directory?
- Is the immediate milestone runtime compatibility, editor parity, or broader gameplay reliability?
