# Testing Gaps

## Current State

- Baseline automated tests now exist under `core/src/test` and `tools/src/test`
- Most verification is manual
- Editor/runtime level compatibility has baseline JSON contract checks, but broader runtime behavior is still not covered

## Recommended Baseline Approach

- Use standard JUnit 5 tests for pure Java logic, validation, and serialization.
- Run them through Gradle's normal `test` task.
- Keep the first wave away from rendering, shaders, `SpriteBatch`, and screen transitions.
- Use manual smoke checks for anything that still needs a real graphics backend.

## Highest-Value Missing Coverage

- Basic gameplay startup smoke validation for desktop launcher
- Editor and runtime manual smoke execution in a real Java environment
- Service-level runtime tests for progression and spawn behavior
- Clear documentation of which editor fields are intentionally ignored by the runtime

## Recommended First Test Set For This Repository

### 1. Editor Data And Validation Tests

- Completed:
  - `LevelService` now supports injected paths and project-relative default resolution
  - `tools/src/test/java/.../LevelServiceTest` covers create/duplicate/save-load and validation edge cases

### 2. Runtime Level Contract Tests

- Completed:
  - `core/src/test/java/.../LevelJsonContractTest` validates representative authored levels against the current runtime DTO contract
- Remaining:
  - document the runtime/editor schema boundary more explicitly in prose
  - decide whether ignored editor-only fields should remain ignored or be translated

### 3. Service-Level Runtime Tests

- Next:
  - add tests for code that can run without rendering, especially:
  - level progression decisions
  - spawn queue or virtual spawn calculations
  - pure utility/constants behavior where bugs would affect gameplay
- Prefer extracting pure methods or small services over forcing rendering systems into unit tests

### 4. Later Integration Coverage

- Add a small libGDX headless harness only if code needs `Gdx.app`, `Gdx.files`, or similar globals but not real drawing
- Keep OpenGL-bound code out of this layer unless a concrete regression justifies it
- For JavaFX UI automation, wait until persistence and validation coverage exists

## Suggested Near-Term Build Changes

- Completed:
  - JUnit 5 dependencies are configured in `core/build.gradle` and `tools/build.gradle`
  - Gradle test tasks are configured to use the JUnit Platform
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

1. Run the new tests in a real Java environment and fix any build/runtime issues.
2. Execute the documented manual smoke checks for game and editor flows.
3. Decide the runtime/editor schema boundary explicitly.
4. Add service-level runtime tests for non-rendering gameplay logic.
5. Reassess whether any headless libGDX tests are justified.
