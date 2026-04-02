# Research: Galaxia

## Existing Projects & Tools

| Name | Description | Relevance | URL |
|------|-------------|-----------|-----|
| libGDX | Java game framework used for rendering, input, audio, assets, and application lifecycle | Core runtime foundation already used by this project | https://libgdx.com/ |
| Ashley ECS | Entity Component System library for organizing gameplay objects and systems | Core gameplay architecture in `core` | https://github.com/libgdx/ashley |
| LWJGL3 Backend | Desktop backend used by libGDX for launching the game on desktop | Powers the `lwjgl3` module | https://libgdx.com/wiki/app/starter-classes-and-configuration |
| OpenJFX | JavaFX UI toolkit used by the level editor | Powers the `tools` module UI | https://openjfx.io/ |
| gdx-liftoff | Template/scaffolding generator used to bootstrap the original project | Explains the repo's Gradle/module starting point | https://github.com/libgdx/gdx-liftoff |
| gdx-vfx | Post-processing toolkit for libGDX | Used for shader/effects pipeline | https://github.com/crashinvaders/gdx-vfx |
| jbump | Collision library inspired by bump.lua | Supports collision behavior in the runtime | https://github.com/implicit-invocation/jbump |
| Construo | Packaging plugin used in the desktop launcher module | Relevant for release/distribution work | https://github.com/fourlastor/construo |

## Recommended Libraries / Frameworks

| Package | Purpose | Notes |
|---------|---------|-------|
| libGDX 1.12.x | Runtime game framework | Already adopted and should remain the primary runtime base |
| Ashley 1.7.x | ECS data/logic organization | Appropriate for this project's entity-heavy gameplay |
| JavaFX 21 | Editor UI | Reasonable choice for internal tooling; portability needs cleanup |
| Gson / libGDX Json | Serialization | Current repo uses both; level schema alignment should be made explicit |
| Gradle multi-project build | Build orchestration | Current structure is workable and should be preserved |

## Patterns & Architecture

- ECS remains the right fit for runtime gameplay because enemies, projectiles, pickups, and effects are component-driven and processed by multiple systems.
- The service-layer refactor visible in `core/.../services` is moving the project away from oversized systems and toward more testable orchestration.
- The repository currently uses two level representations:
  - Runtime DTOs in `core/.../level`
  - Editor models in `tools/.../model`
- The most important architectural question is whether those two representations should converge into one shared schema or remain separate with a documented translation boundary.
- The editor is best treated as an internal production tool, not a generalized external product. That favors pragmatic reliability improvements over large UX expansion.

## Gaps In Existing Solutions

- The top-level README was still close to a starter template and did not describe the actual game/editor architecture.
- Planning lived in `claudedocs/` with multiple point-in-time reports, but there was no current `docs/` stack for future iteration work.
- There is no meaningful automated test suite, which makes refactors and data-contract changes riskier.
- The editor currently saves levels through a machine-specific absolute path, which blocks portability and clean onboarding.
- Runtime and editor level models appear to be only partially aligned, especially around boss and timed-event support.
- Existing docs contain some drift from the implementation, including older assumptions about the editor architecture.

## Feature Ideation

### Power User Features

- Level schema validator shared by runtime and editor
- In-editor simulation mode that previews spawns/timed events against runtime rules
- Deterministic seed controls for procedural fallback levels
- Campaign metadata and progression sequencing editor
- Difficulty heatmap or pacing visualization over level length

### Integrations Worth Considering

- Shared schema module for level data contracts
- JSON import/export validation in CI
- Lightweight screenshot or replay capture for regression checks
- Desktop release packaging through existing `construo` setup

### UX / DX Improvements

- Replace hard-coded local paths with project-relative configuration
- Add smoke-test checklists and build/run commands to canonical docs
- Keep roadmap and plan docs trimmed so future iterations start from current work
- Add a small set of serialization/service tests before larger gameplay changes

### Risks & Hard Problems

- Diverging level schemas between editor and runtime can silently corrupt content workflows
- Manual-only verification makes regressions likely during refactors
- Static globals in runtime bootstrap still make isolated testing difficult
- Legacy docs can cause implementation to target outdated assumptions

### Suggested Stretch Goals

- Shared level-schema package used by both runtime and tools
- Runtime support for authored timed events and richer boss scripting
- Automated content validation as part of `./gradlew build`
- Separate debug/dev tooling mode for shader and systems diagnostics

## Sources

- [libGDX](https://libgdx.com/)
- [Ashley ECS](https://github.com/libgdx/ashley)
- [OpenJFX](https://openjfx.io/)
- [gdx-liftoff](https://github.com/libgdx/gdx-liftoff)
- [gdx-vfx](https://github.com/crashinvaders/gdx-vfx)
- [jbump](https://github.com/implicit-invocation/jbump)
- [Construo](https://github.com/fourlastor/construo)
