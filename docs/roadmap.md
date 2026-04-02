# Roadmap: Galaxia

## MVP / Must Ship

### 1. Stabilize The Level Data Pipeline

- Make editor save/load paths project-relative and portable
- Define the supported JSON schema between editor output and runtime input
- Verify the runtime can safely consume authored levels already present in `assets/levels/`
- Decide whether timed events and boss metadata are runtime-supported or editor-only

### 2. Improve Baseline Runtime Reliability

- Add smoke coverage around game startup, level loading, and screen transitions
- Audit remaining high-risk service/refactor seams around level progression and global state
- Remove or document obsolete files and stale architecture notes that confuse future work

### 3. Establish A Minimum Verification Layer

- Add a first wave of automated tests around pure Java/service/serialization code
- Document manual smoke flows for gameplay and editor workflows
- Make verification steps part of normal iteration docs instead of one-off notes

## Post-MVP / V1

- Shared schema or translation layer between editor models and runtime DTOs
- Runtime support for timed events created in the editor
- Better editor ergonomics: drag interactions, richer preview fidelity, undo/redo
- Campaign progression improvements and explicit level sequencing tools
- Cleaner separation between debug/test screens and player-facing runtime flow

## Stretch Goals

- In-editor simulation against actual runtime spawn logic
- CI validation of level JSON and schema compatibility
- Better packaging and release automation for desktop builds and tools
- Broader content systems such as branching campaigns or scripted events

## Known Risks With Mitigations

### Schema Drift Between Tools And Runtime

- Risk: editor output evolves faster than runtime support
- Mitigation: document one supported contract, add serialization tests, and version any intentional schema changes

### Portability Problems In Tooling

- Risk: hard-coded local paths break editor usage on other machines
- Mitigation: move to project-relative paths or configurable storage immediately

### Manual-Only Verification

- Risk: gameplay/editor regressions are easy to miss
- Mitigation: start with service/data tests and a required smoke checklist

### Documentation Drift

- Risk: implementation follows outdated notes in `claudedocs/`
- Mitigation: keep `docs/` current and explicitly mark legacy docs as reference-only
