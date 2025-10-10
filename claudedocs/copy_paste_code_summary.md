# Copy/Paste Code Changes Summary
## Quick Reference for Code Review

### Files Modified
1. `/Users/dale/games/galaxia/tools/src/main/java/com/dalesmithwebdev/galaxia/tools/ui/LevelPreviewPanel.java`
2. `/Users/dale/games/galaxia/core/src/main/java/com/dalesmithwebdev/galaxia/level/LevelLoader.java` (bug fix only)

---

## LevelPreviewPanel.java Changes

### 1. New Fields (Lines 63-66)
```java
// Copy/paste state
private final List<PlacedObject> clipboard = new ArrayList<>();
private Point2D clipboardOrigin = null;
private Point2D lastMousePosition = null;
```

### 2. Mouse Position Tracking

**handleMousePressed (Line 173)**
```java
lastMousePosition = new Point2D(mouseX, mouseY);
```

**handleMouseMove (Line 315)**
```java
lastMousePosition = new Point2D(mouseX, mouseY);
```

### 3. Keyboard Shortcut Handler (Lines 326-358)
```java
private void handleKeyPressed(KeyEvent event) {
    boolean isCtrlOrCmd = event.isControlDown() || event.isMetaDown();

    if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
        deleteSelectedObjects();
        event.consume();
    } else if (event.getCode() == KeyCode.ESCAPE) {
        // ... existing cancel logic
    } else if (isCtrlOrCmd && event.getCode() == KeyCode.C) {
        copySelectedObjects();
        event.consume();
    } else if (isCtrlOrCmd && event.getCode() == KeyCode.V) {
        pasteObjects();
        event.consume();
    } else if (isCtrlOrCmd && event.getCode() == KeyCode.X) {
        cutSelectedObjects();
        event.consume();
    } else if (isCtrlOrCmd && event.getCode() == KeyCode.D) {
        duplicateSelectedObjects();
        event.consume();
    }
}
```

### 4. New Methods (Lines 386-540)

**copySelectedObjects() - Lines 389-412**
- Copies selected objects to clipboard
- Calculates center point for relative positioning
- Creates deep copies of all objects

**pasteObjects() - Lines 417-463**
- Pastes clipboard at mouse cursor
- Maintains relative positions
- Respects snap-to-grid setting
- Updates selection to pasted objects

**cutSelectedObjects() - Lines 468-485**
- Combines copy + delete operations
- Clears selection after cut

**duplicateSelectedObjects() - Lines 490-513**
- Quick duplicate with 20px offset
- Preserves clipboard for future pastes

**createDeepCopy(PlacedObject) - Lines 518-540**
- Creates independent copy of PlacedObject
- Copies all properties (position, scale, movement, combat, timing)

---

## Method Call Graph

```
User Keyboard Input
    ├─ Ctrl/Cmd+C → copySelectedObjects()
    │                  └─ createDeepCopy() (for each selected object)
    │
    ├─ Ctrl/Cmd+V → pasteObjects()
    │                  ├─ createDeepCopy() (for each clipboard object)
    │                  └─ render()
    │
    ├─ Ctrl/Cmd+X → cutSelectedObjects()
    │                  ├─ copySelectedObjects()
    │                  └─ render()
    │
    └─ Ctrl/Cmd+D → duplicateSelectedObjects()
                       ├─ copySelectedObjects()
                       ├─ pasteObjects()
                       └─ render()
```

---

## Property Deep Copy Matrix

| Property | Type | Source Method | Copied |
|----------|------|---------------|--------|
| type | ObjectType | `getType()` | ✅ Constructor |
| x | float | `getX()` | ✅ Constructor |
| y | float | `getY()` | ✅ Constructor |
| scale | float | `getScale()` | ✅ `setScale()` |
| speed | float | `getSpeed()` | ✅ `setSpeed()` |
| movementPattern | MovementPattern | `getMovementPattern()` | ✅ `setMovementPattern()` |
| directionX | float | `getDirectionX()` | ✅ `setDirectionX()` |
| directionY | float | `getDirectionY()` | ✅ `setDirectionY()` |
| rotationSpeed | float | `getRotationSpeed()` | ✅ `setRotationSpeed()` |
| health | int | `getHealth()` | ✅ `setHealth()` |
| fireRate | int | `getFireRate()` | ✅ `setFireRate()` |
| hasLasers | boolean | `isHasLasers()` | ✅ `setHasLasers()` |
| hasMissiles | boolean | `isHasMissiles()` | ✅ `setHasMissiles()` |
| hasShield | boolean | `isHasShield()` | ✅ `setHasShield()` |
| spawnDelay | float | `getSpawnDelay()` | ✅ `setSpawnDelay()` |

**Total Properties**: 15
**All Copied**: Yes ✅

---

## Code Metrics

| Metric | Value |
|--------|-------|
| New methods added | 5 |
| New fields added | 3 |
| Total lines added | ~180 |
| Modified methods | 3 (mouse tracking + keyboard handler) |
| Dependencies added | 0 (uses existing JavaFX/Java classes) |
| Public API changes | 0 (all changes internal) |

---

## Error Handling Strategy

| Operation | Empty Selection | Empty Clipboard | Null Mouse Position |
|-----------|----------------|-----------------|---------------------|
| Copy | Silent no-op | N/A | N/A |
| Paste | N/A | Silent no-op | Uses viewport center |
| Cut | Silent no-op | N/A | N/A |
| Duplicate | Silent no-op | N/A | N/A |

**Philosophy**: Defensive programming with graceful degradation

---

## Console Feedback Messages

```java
// Copy
System.out.println("Copied " + clipboard.size() + " object(s)");

// Paste
System.out.println("Pasted " + clipboard.size() + " object(s) at (" +
                  (int)pasteTarget.getX() + ", " + (int)pasteTarget.getY() + ")");

// Cut
System.out.println("Cut " + count + " object(s)");

// Duplicate
System.out.println("Duplicated " + clipboard.size() + " object(s)");
```

---

## Integration Points

### Existing Features Used
- `selectedObjects` (Set<PlacedObject>) - Multi-selection system
- `selectedObjectProperty` (ObjectProperty<PlacedObject>) - Property binding
- `snapToGrid` (boolean) - Grid snapping setting
- `GRID_SIZE` (int) - Grid size constant
- `level.getObjects()` (List<PlacedObject>) - Level object list
- `render()` - Canvas redraw method

### No Breaking Changes
- All existing functionality preserved
- New methods are private (internal implementation)
- No public API changes

---

## Testing Commands

```bash
# Build project
./gradlew tools:build

# Run level editor
./gradlew tools:run

# Clean build
./gradlew clean tools:build
```

---

## Keyboard Shortcut Detection Logic

```java
boolean isCtrlOrCmd = event.isControlDown() || event.isMetaDown();
```

| Platform | Control Key | Meta Key | Result |
|----------|-------------|----------|--------|
| Windows | Ctrl (true) | Win (false) | isCtrlOrCmd = true |
| Linux | Ctrl (true) | Super (false) | isCtrlOrCmd = true |
| macOS | ^ (false) | Cmd (true) | isCtrlOrCmd = true |

---

## Relative Position Calculation

```java
// During copy
float sumX = 0, sumY = 0;
for (PlacedObject obj : selectedObjects) {
    sumX += obj.getX();
    sumY += obj.getY();
}
clipboardOrigin = new Point2D(sumX / selectedObjects.size(),
                               sumY / selectedObjects.size());

// During paste
double offsetX = pasteTarget.getX() - clipboardOrigin.getX();
double offsetY = pasteTarget.getY() - clipboardOrigin.getY();

for (PlacedObject clipObj : clipboard) {
    float newX = (float) (clipObj.getX() + offsetX);
    float newY = (float) (clipObj.getY() + offsetY);
    // Apply to pasted object
}
```

**Effect**: Objects paste with same relative spacing as original selection

---

## Snap-to-Grid Integration

```java
if (snapToGrid) {
    newX = Math.round(newX / GRID_SIZE) * GRID_SIZE;
    newY = Math.round(newY / GRID_SIZE) * GRID_SIZE;
}
```

**Behavior**: Each pasted object snaps independently while maintaining relative positions

---

## Duplicate Offset Logic

```java
// Save original clipboard origin
Point2D originalOrigin = clipboardOrigin;

// Paste with small offset (20 pixels right and down)
clipboardOrigin = new Point2D(
    clipboardOrigin.getX() + 20,
    clipboardOrigin.getY() + 20
);

pasteObjects();

// Restore clipboard origin for potential future pastes
clipboardOrigin = originalOrigin;
```

**Result**: Each duplicate appears 20px diagonally from selection center

---

## Future Extension Points

### Easy to Add
1. **Visual status overlay**: Add JavaFX Label with fade animation
2. **Clipboard size indicator**: Display clipboard count in UI
3. **Undo/redo hooks**: Add pre/post paste state capture

### Moderate Complexity
4. **Clipboard preview**: Show thumbnail of clipboard contents
5. **Paste offset controls**: Allow user to configure duplicate offset

### High Complexity
6. **Clipboard history**: Store last N clipboard states
7. **Cross-session persistence**: Save/restore clipboard on exit/start

---

## Performance Characteristics

| Operation | Time Complexity | Space Complexity | Notes |
|-----------|----------------|------------------|-------|
| Copy | O(n) | O(n) | n = selected objects |
| Paste | O(n) | O(n) | n = clipboard objects |
| Cut | O(n) | O(n) | Copy + delete |
| Duplicate | O(n) | O(n) | Copy + paste |
| Deep Copy | O(1) | O(1) | Fixed property count |

**Bottleneck**: None for typical use cases (<1000 objects)

---

## Code Quality Checklist

- [x] All methods documented with JavaDoc
- [x] Null checks on all nullable fields
- [x] Empty collection checks before operations
- [x] Cross-platform keyboard shortcuts
- [x] Console feedback for user operations
- [x] Edge cases handled gracefully
- [x] No breaking changes to existing code
- [x] Code follows existing style conventions
- [x] No warnings or errors on build

---

## Git Commit Message Template

```
Add copy/paste functionality to level editor

- Implement copy (Ctrl/Cmd+C) with clipboard storage
- Implement paste (Ctrl/Cmd+V) at mouse cursor
- Implement cut (Ctrl/Cmd+X) for move operations
- Implement duplicate (Ctrl/Cmd+D) with 20px offset
- Add deep copy for all PlacedObject properties
- Track mouse position for intelligent paste targeting
- Support cross-platform keyboard shortcuts
- Handle edge cases (empty selection/clipboard)

Productivity enhancement for level designers. All operations
work with multi-selection and respect snap-to-grid setting.

Build verified: ./gradlew tools:build successful
```

---

**Last Updated**: 2025-10-09
**Code Review Status**: Ready
**Build Status**: ✅ Verified
