# Testing Gaps

## Current State

- No established automated tests under `src/test`
- Most verification is manual
- Editor/runtime level compatibility is not protected by automated checks

## Recommended Baseline Approach

- Use standard JUnit 5 tests for pure Java logic, validation, and serialization.
- Run them through Gradle's normal `test` task.
- Keep the first wave away from rendering, shaders, `SpriteBatch`, and screen transitions.
- Use manual smoke checks for anything that still needs a real graphics backend.

## Highest-Value Missing Coverage

- Level JSON serialization/deserialization round trips
- `LevelService.validateLevel()` coverage for invalid object placement and timed-event validation
- Runtime loading of representative authored levels from `assets/levels/`
- Basic gameplay startup smoke validation for desktop launcher

## Recommended First Test Set For This Repository

### 1. Editor Data And Validation Tests

- Add `tools/src/test/java/.../LevelServiceTest`
- Cover:
  - creating a new level
  - duplicating a level
  - saving and loading a level round trip
  - validation failures for empty IDs, empty names, negative trigger times, out-of-bounds positions, and invalid enemy-wave counts
- Use temporary directories in tests instead of the real `assets/levels/` folder
- As part of this work, refactor `LevelService` so the levels directory can be injected or configured

### 2. Runtime Level Contract Tests

- Add `core/src/test/java/.../LevelDataLoadTest`
- Cover:
  - loading one or more representative JSON files from `assets/levels/`
  - asserting required fields are parsed
  - documenting which editor fields are ignored by the runtime today
- These tests should make schema drift visible as soon as editor output changes

### 3. Service-Level Runtime Tests

- Add tests for code that can run without rendering, especially:
  - level progression decisions
  - spawn queue or virtual spawn calculations
  - pure utility/constants behavior where bugs would affect gameplay
- Prefer extracting pure methods or small services over forcing rendering systems into unit tests

### 4. Later Integration Coverage

- Add a small libGDX headless harness only if code needs `Gdx.app`, `Gdx.files`, or similar globals but not real drawing
- Keep OpenGL-bound code out of this layer unless a concrete regression justifies it
- For JavaFX UI automation, wait until persistence and validation coverage exists

## Suggested Near-Term Build Changes

- Add JUnit 5 dependencies to `core/build.gradle` and `tools/build.gradle`
- Configure Gradle test tasks to use the JUnit Platform
- Keep tests separate by module:
  - `core` for runtime data/services
  - `tools` for editor persistence/validation
- Avoid coupling early tests to launcher code in `lwjgl3`

## Manual Smoke Checks To Keep Running

- Launch the game with `./gradlew lwjgl3:run`
- Launch the level editor with `./gradlew tools:run`
- Open, edit, save, and reload at least one existing level
- Confirm at least one authored level loads into gameplay without crashing
- Confirm key menus/screens still transition correctly after runtime changes

## Known Risk Areas

- Editor path handling on machines other than the original author's setup
- Drift between editor-side and runtime-side level models
- Refactors around services and globals without automated regression checks

## Practical Sequence

1. Make `LevelService` path handling injectable or project-relative.
2. Add JUnit 5 and enable Gradle `test`.
3. Land editor persistence/validation tests.
4. Land runtime JSON contract tests.
5. Reassess whether any headless libGDX tests are justified.
