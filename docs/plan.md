# Plan: Initial Stabilization Slice

## Goal

Make the game/editor content workflow trustworthy enough that future iterations can add gameplay or tooling features without guessing about the level schema, save path, or validation process.

## Approach

Start with the highest-leverage foundation work:

1. Normalize how the editor locates and writes level files.
2. Document and enforce the current runtime/editor level data contract.
3. Add minimal automated coverage around serialization and validation where code can be tested without a full graphical runtime.

This slice should favor small, verifiable changes over large refactors.

## Phases

### Phase 1: Portable Level Storage

- Replace the hard-coded absolute path in `tools/.../service/LevelService.java`
- Resolve level storage relative to the project or application working directory
- Document the chosen convention in repo docs

### Phase 2: Level Contract Audit

- Compare `tools/.../model/Level` and related classes against `core/.../level/LevelData` and `LevelObject`
- Decide whether unsupported fields are ignored, translated, or promoted into runtime support
- Update docs to state the actual supported contract

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

- Should runtime support be expanded to consume the full editor schema now, or should the editor export a reduced runtime-safe schema?
- What working directory assumptions are acceptable for both `tools:run` and packaged/distributed usage?
- Is the immediate milestone editor reliability, gameplay stability, or parity between both?
