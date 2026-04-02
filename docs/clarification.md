# Clarification: Galaxia

## Assumptions Currently Being Made

- The project should continue as a desktop-first libGDX game rather than expand to mobile or web in the near term.
- The JavaFX editor in `tools` is intended to be the primary authoring workflow for handcrafted levels.
- JSON files under `assets/levels/` are the long-term content source of truth for designed encounters.
- Existing `claudedocs/` files are historical implementation notes, not the active planning system.
- Short-term work should prioritize stabilizing the current game and content pipeline over adding large new feature areas.

## Decisions That Materially Affect Scope Or Architecture

### Level Authoring Pipeline

- Decide whether the runtime DTOs in `core/.../level` should converge with the richer editor models in `tools/.../model`, or whether an explicit export/import translation layer should exist.
- Decide whether timed events are now part of the supported gameplay contract or remain editor-only data.
- Decide whether level IDs remain timestamp-based or move to stable semantic names.

### Runtime Architecture

- Decide how far the service-layer refactor should go before adding more gameplay features.
- Decide whether remaining static global state in `ArcadeSpaceShooter` is acceptable for now or should be treated as active debt.

### Tooling And Portability

- Decide whether the editor must be portable across machines immediately.
- If yes, replace the hard-coded absolute levels path in `LevelService` with a project-relative strategy before taking more editor dependencies.

### Quality Bar

- Decide the minimum automated verification target for this repository:
  - Data/serialization tests only
  - Headless gameplay/service tests where practical
  - Editor service tests plus manual UI smoke checks

## Open Questions That Block Clean Implementation

### Level Data Contract

- What is the intended supported schema for level files today?
- Should boss data and timed events be consumable by the runtime now, or can they remain partially implemented authoring features?
- Is backward compatibility with the existing JSON files in `assets/levels/` required?

### Tooling Workflow

- Should `tools:run` be considered production-authoring ready, or still experimental?
- Should the editor save directly into the repo's `assets/levels/`, or export to a staging directory first?

### Release Scope

- Is the next milestone focused on shipping a more stable playable build, a more reliable editor, or both?

## Optional Questions That Can Wait

- Should the project gain native packaging beyond the existing desktop jar/distribution setup?
- Should authored levels eventually support branching campaigns, difficulty variants, or narrative scripting?
- Should shader/debug screens remain inside the main repo or move behind a dedicated debug flag/tooling path?
