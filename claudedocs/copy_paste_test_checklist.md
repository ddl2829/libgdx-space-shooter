# Copy/Paste Testing Checklist
## Galaxia Level Editor - Manual Verification

### Pre-Testing Setup
- [ ] Build project: `./gradlew tools:build`
- [ ] Launch editor: `./gradlew tools:run`
- [ ] Open existing level or create new level
- [ ] Verify canvas has focus (click canvas area)

---

## Test Suite 1: Basic Operations

### Test 1.1: Copy Single Object ✅
- [ ] Place one meteor on canvas
- [ ] Click to select meteor (cyan highlight appears)
- [ ] Press Ctrl+C (or Cmd+C on Mac)
- [ ] Verify console: "Copied 1 object(s)"
- [ ] Move mouse to new location
- [ ] Press Ctrl+V
- [ ] Verify console: "Pasted 1 object(s) at (x, y)"
- [ ] **Expected**: Two meteors on canvas, second one selected

### Test 1.2: Copy Multiple Objects ✅
- [ ] Place 5 different objects (meteors, enemies, power-ups)
- [ ] Drag selection box to select all 5
- [ ] Verify all 5 have cyan highlight
- [ ] Press Ctrl+C
- [ ] Verify console: "Copied 5 object(s)"
- [ ] Move mouse 200px away
- [ ] Press Ctrl+V
- [ ] **Expected**: 10 total objects (5 original + 5 pasted), relative positions maintained

### Test 1.3: Cut Operation ✅
- [ ] Select 3 objects
- [ ] Press Ctrl+X
- [ ] Verify console: "Cut 3 object(s)"
- [ ] **Expected**: 3 objects removed from original location
- [ ] Move mouse to new location
- [ ] Press Ctrl+V
- [ ] **Expected**: 3 objects appear at new location

### Test 1.4: Duplicate Operation ✅
- [ ] Place one large meteor
- [ ] Select meteor
- [ ] Press Ctrl+D
- [ ] **Expected**: Second meteor appears 20px right and 20px down
- [ ] Press Ctrl+D again (with new meteor selected)
- [ ] **Expected**: Third meteor at same offset from second
- [ ] **Final**: Three meteors in diagonal pattern

---

## Test Suite 2: Multi-Paste Operations

### Test 2.1: Paste Multiple Times ✅
- [ ] Copy 2 enemy fighters
- [ ] Paste at Y position 500
- [ ] Move mouse to Y position 1000
- [ ] Paste again
- [ ] Move mouse to Y position 1500
- [ ] Paste again
- [ ] **Expected**: 6 total enemies (2 at each Y position)
- [ ] Verify console shows 3 paste operations

### Test 2.2: Clipboard Persistence ✅
- [ ] Copy 1 power-up
- [ ] Paste 10 times at different locations
- [ ] **Expected**: All 10 pastes successful
- [ ] Verify clipboard doesn't clear after paste

---

## Test Suite 3: Snap-to-Grid

### Test 3.1: Paste with Grid Enabled ✅
- [ ] Enable "Snap to Grid" toggle
- [ ] Place 5 objects at arbitrary positions
- [ ] Select all and copy
- [ ] Move mouse to non-grid position (e.g., x=127.3, y=543.8)
- [ ] Paste
- [ ] **Expected**: All pasted objects snap to grid
- [ ] Verify relative positions maintained

### Test 3.2: Paste with Grid Disabled ✅
- [ ] Disable "Snap to Grid" toggle
- [ ] Copy 3 objects
- [ ] Paste at arbitrary position
- [ ] **Expected**: Objects paste at exact mouse coordinates
- [ ] No grid snapping applied

---

## Test Suite 4: Edge Cases

### Test 4.1: Empty Selection Copy ✅
- [ ] Clear selection (press Escape)
- [ ] Press Ctrl+C
- [ ] **Expected**: No error, no console message, silent no-op

### Test 4.2: Empty Selection Cut ✅
- [ ] Clear selection
- [ ] Press Ctrl+X
- [ ] **Expected**: No error, silent no-op

### Test 4.3: Empty Selection Duplicate ✅
- [ ] Clear selection
- [ ] Press Ctrl+D
- [ ] **Expected**: No error, silent no-op

### Test 4.4: Empty Clipboard Paste ✅
- [ ] Restart editor (fresh clipboard)
- [ ] Press Ctrl+V immediately
- [ ] **Expected**: No error, silent no-op

### Test 4.5: Paste Without Mouse Movement ✅
- [ ] Copy objects
- [ ] Don't move mouse after copy
- [ ] Press Ctrl+V
- [ ] **Expected**: Objects paste at viewport center
- [ ] Verify console shows paste position

### Test 4.6: Large Selection Delete Confirmation ✅
- [ ] Select 10+ objects
- [ ] Press Delete
- [ ] **Expected**: Confirmation dialog appears
- [ ] "Delete 10 objects? This action cannot be undone."
- [ ] Test both OK and Cancel

---

## Test Suite 5: Property Preservation

### Test 5.1: All Properties Copied ✅
- [ ] Place enemy fighter
- [ ] Modify properties:
  - [ ] Scale: 1.5
  - [ ] Speed: 5.0
  - [ ] Health: 50
  - [ ] Movement Pattern: Zigzag
  - [ ] Fire Rate: 1000
  - [ ] Enable Missiles: true
- [ ] Select and copy enemy
- [ ] Paste enemy
- [ ] Select pasted enemy
- [ ] **Expected**: All properties match original
- [ ] Verify in property panel

### Test 5.2: Independent Modification ✅
- [ ] Copy/paste one enemy
- [ ] Modify original enemy's health to 100
- [ ] **Expected**: Pasted enemy health remains unchanged
- [ ] Verify deep copy isolation

---

## Test Suite 6: Multi-Selection Integration

### Test 6.1: Drag Box Selection → Copy ✅
- [ ] Place 8 objects in area
- [ ] Drag selection box around all 8
- [ ] Verify all selected (cyan highlights)
- [ ] Press Ctrl+C
- [ ] Verify console: "Copied 8 object(s)"
- [ ] Paste elsewhere
- [ ] **Expected**: 8 objects pasted with correct spacing

### Test 6.2: Shift+Click Multi-Select → Copy ✅
- [ ] Place 5 objects scattered on canvas
- [ ] Click first object (select)
- [ ] Shift+Click second object (add to selection)
- [ ] Shift+Click third object (add to selection)
- [ ] Press Ctrl+C
- [ ] **Expected**: 3 objects copied
- [ ] Paste and verify

### Test 6.3: Selection Updates After Paste ✅
- [ ] Copy 3 objects
- [ ] Paste
- [ ] **Expected**: Newly pasted objects become selection
- [ ] Verify cyan highlights on pasted objects
- [ ] Property panel shows first pasted object

---

## Test Suite 7: Complex Workflows

### Test 7.1: Enemy Formation Pattern ✅
- [ ] Create formation: 5 fighters in V-shape
- [ ] Select all 5 and copy
- [ ] Paste at 5 different Y positions (simulate waves)
- [ ] **Expected**: 5 identical V-formations throughout level
- [ ] Verify spacing consistency

### Test 7.2: Power-Up Trail ✅
- [ ] Place shield power-up
- [ ] Duplicate 10 times (Ctrl+D × 10)
- [ ] **Expected**: 11 power-ups in diagonal trail
- [ ] Each 20px offset from previous

### Test 7.3: Level Section Reorganization ✅
- [ ] Create section with 20+ mixed objects
- [ ] Select all with drag box
- [ ] Cut (Ctrl+X)
- [ ] Scroll to new Y position (500px away)
- [ ] Paste (Ctrl+V)
- [ ] **Expected**: Entire section moved intact
- [ ] All relative positions preserved

---

## Test Suite 8: Cross-Platform Shortcuts

### Test 8.1: macOS Keyboard Shortcuts ✅
*(Only test if on macOS)*
- [ ] Cmd+C (copy)
- [ ] Cmd+V (paste)
- [ ] Cmd+X (cut)
- [ ] Cmd+D (duplicate)
- [ ] **Expected**: All shortcuts work identically to Ctrl variants

### Test 8.2: Windows/Linux Keyboard Shortcuts ✅
*(Only test if on Windows/Linux)*
- [ ] Ctrl+C (copy)
- [ ] Ctrl+V (paste)
- [ ] Ctrl+X (cut)
- [ ] Ctrl+D (duplicate)
- [ ] **Expected**: All shortcuts function correctly

---

## Test Suite 9: Stress Testing

### Test 9.1: Large Selection Copy/Paste ✅
- [ ] Place 50+ objects on canvas
- [ ] Select all with drag box
- [ ] Copy
- [ ] Paste
- [ ] **Expected**: All 50+ objects pasted correctly
- [ ] No performance issues
- [ ] Console feedback correct

### Test 9.2: Rapid Duplicate Chain ✅
- [ ] Place one object
- [ ] Press Ctrl+D 20 times rapidly
- [ ] **Expected**: 21 objects in diagonal pattern
- [ ] No errors or lag

### Test 9.3: Clipboard Size Limit ✅
- [ ] Place 100+ objects
- [ ] Select all and copy
- [ ] Paste
- [ ] **Expected**: All objects copied successfully
- [ ] No memory errors

---

## Test Suite 10: Integration Testing

### Test 10.1: Copy → Move → Paste ✅
- [ ] Copy 5 objects
- [ ] Before pasting, move original objects elsewhere
- [ ] Paste
- [ ] **Expected**: Pasted objects independent of originals
- [ ] Original objects unaffected by paste

### Test 10.2: Tiled Placement + Copy ✅
- [ ] Hold Shift and drag to create tiled placement line
- [ ] Release to place objects
- [ ] Select placed objects and copy
- [ ] Paste elsewhere
- [ ] **Expected**: Copied tiled pattern pastes correctly

### Test 10.3: Scroll + Paste ✅
- [ ] Copy objects at Y=500
- [ ] Scroll viewport to Y=2000
- [ ] Move mouse to new visible area
- [ ] Paste
- [ ] **Expected**: Objects paste at mouse position
- [ ] Scroll adjustment handled correctly

---

## Test Suite 11: UI Feedback

### Test 11.1: Console Output Verification ✅
- [ ] Perform each operation once
- [ ] Verify console messages:
  - [ ] "Copied N object(s)"
  - [ ] "Pasted N object(s) at (x, y)"
  - [ ] "Cut N object(s)"
  - [ ] "Duplicated N object(s)"

### Test 11.2: Selection Visual Feedback ✅
- [ ] After paste, verify cyan highlights on pasted objects
- [ ] After cut, verify originals removed and selection cleared
- [ ] After duplicate, verify new objects selected

---

## Test Suite 12: Error Handling

### Test 12.1: Canvas Focus Requirement ✅
- [ ] Click outside canvas (e.g., property panel)
- [ ] Press Ctrl+C (or Ctrl+V)
- [ ] **Expected**: Shortcut may not work (focus on other component)
- [ ] Click canvas to regain focus
- [ ] Press Ctrl+C again
- [ ] **Expected**: Now works correctly

### Test 12.2: No Crashes on Edge Cases ✅
- [ ] Test all operations with:
  - [ ] Empty level (no objects)
  - [ ] Single object
  - [ ] Maximum objects (100+)
- [ ] **Expected**: No crashes, errors handled gracefully

---

## Post-Testing Verification

### Build Verification
- [ ] `./gradlew tools:build` succeeds
- [ ] No compilation errors
- [ ] No warnings related to copy/paste code

### Code Review
- [ ] All keyboard shortcuts documented
- [ ] Console feedback for all operations
- [ ] Edge cases handled gracefully
- [ ] Cross-platform shortcuts work

---

## Test Results Summary

| Test Suite | Tests | Passed | Failed | Notes |
|------------|-------|--------|--------|-------|
| 1. Basic Operations | 4 | ☐ | ☐ | |
| 2. Multi-Paste | 2 | ☐ | ☐ | |
| 3. Snap-to-Grid | 2 | ☐ | ☐ | |
| 4. Edge Cases | 6 | ☐ | ☐ | |
| 5. Property Preservation | 2 | ☐ | ☐ | |
| 6. Multi-Selection | 3 | ☐ | ☐ | |
| 7. Complex Workflows | 3 | ☐ | ☐ | |
| 8. Cross-Platform | 2 | ☐ | ☐ | |
| 9. Stress Testing | 3 | ☐ | ☐ | |
| 10. Integration | 3 | ☐ | ☐ | |
| 11. UI Feedback | 2 | ☐ | ☐ | |
| 12. Error Handling | 2 | ☐ | ☐ | |

**Total Tests**: 34
**Status**: Ready for Manual Testing

---

**Tester**: _______________
**Date**: _______________
**Platform**: ☐ Windows ☐ macOS ☐ Linux
**Overall Result**: ☐ PASS ☐ FAIL
