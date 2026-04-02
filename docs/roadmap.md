# Roadmap: Galaxia

## MVP / Must Ship

### 1. Stabilize The Level Data Pipeline

- Keep the now-portable editor save/load path stable across machines and launch contexts
- Decide whether the current reduced runtime JSON contract remains intentional
- Verify the runtime can safely consume authored levels already present in `assets/levels/`
- Decide whether timed events and boss metadata are runtime-supported or editor-only

### 2. Improve Baseline Runtime Reliability

- Add smoke coverage around game startup, level loading, and screen transitions
- Audit remaining high-risk service/refactor seams around level progression and global state
- Remove or document obsolete files and stale architecture notes that confuse future work

### 3. Establish A Minimum Verification Layer

- Extend the initial automated tests into more runtime service coverage
- Document and run manual smoke flows for gameplay and editor workflows
- Keep graphical behavior out of the first automated batch unless a concrete need appears

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

- Risk: editor path handling may still behave differently under some launch contexts or packaging modes
- Mitigation: keep path resolution project-relative/configurable and verify it in normal workflows

### Manual-Only Verification

- Risk: gameplay/editor regressions are easy to miss
- Mitigation: start with service/data tests and a required smoke checklist

### Documentation Drift

- Risk: implementation follows outdated notes in `claudedocs/`
- Mitigation: keep `docs/` current and explicitly mark legacy docs as reference-only
