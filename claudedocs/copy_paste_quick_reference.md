# Copy/Paste Quick Reference Guide
## Galaxia Level Editor

### Keyboard Shortcuts

| Action | Windows/Linux | macOS | Description |
|--------|---------------|-------|-------------|
| **Copy** | `Ctrl+C` | `Cmd+C` | Copy selected objects to clipboard |
| **Paste** | `Ctrl+V` | `Cmd+V` | Paste clipboard at mouse cursor |
| **Cut** | `Ctrl+X` | `Cmd+X` | Cut (copy + delete) selected objects |
| **Duplicate** | `Ctrl+D` | `Cmd+D` | Duplicate with 20px offset |
| **Delete** | `Delete` or `Backspace` | `Delete` or `Backspace` | Delete selected objects |
| **Clear Selection** | `Esc` | `Esc` | Deselect all objects |

---

### Common Workflows

#### Creating Enemy Formations
```
1. Place 5 enemy fighters in tight formation
2. Select all 5 (drag box)
3. Press Ctrl+C (copy)
4. Move mouse down 200 pixels
5. Press Ctrl+V (paste)
6. Repeat step 4-5 for each wave
→ Result: Consistent formations throughout level
```

#### Quick Power-Up Distribution
```
1. Place shield power-up at strategic location
2. Select the power-up
3. Press Ctrl+D (duplicate) 5 times
4. Move each duplicate to desired location
→ Result: 6 evenly distributed power-ups
```

#### Reorganizing Level Sections
```
1. Select entire group (drag box around 20+ objects)
2. Press Ctrl+X (cut)
3. Scroll to new location
4. Press Ctrl+V (paste)
→ Result: Entire section moved cleanly
```

---

### Tips & Tricks

#### Multi-Selection Before Copy
- **Drag box**: Click and drag to select multiple objects
- **Shift+Click**: Add individual objects to selection
- **Copy all**: Select → Ctrl+C → paste anywhere

#### Paste Multiple Times
- Clipboard persists after paste
- You can paste the same objects 10+ times
- Each paste creates independent copies

#### Snap-to-Grid Paste
- Enable "Snap to Grid" toggle before pasting
- All pasted objects snap while maintaining relative positions
- Useful for aligned formations

#### Quick Duplicate Chains
- Select object → Ctrl+D → Ctrl+D → Ctrl+D
- Creates diagonal pattern (20px offset each time)
- Great for creating trails or patterns

---

### Console Feedback

Operations show confirmation in console:
```
Copied 5 object(s)
Pasted 5 object(s) at (300, 1500)
Cut 3 object(s)
Duplicated 2 object(s)
```

---

### Edge Cases

| Situation | Behavior |
|-----------|----------|
| Copy with no selection | Silent no-op (no error) |
| Paste with empty clipboard | Silent no-op (no error) |
| Paste without moving mouse | Uses viewport center as target |
| Paste beyond level bounds | Allowed (scroll to find) |
| Large selection delete (>5) | Confirmation dialog appears |

---

### Keyboard Shortcut Conflicts

✅ **No conflicts**: Canvas must have focus (click canvas first)
✅ **Text fields**: Copy/paste in text fields still works normally
✅ **Cross-platform**: Shortcuts work on Windows, Linux, and macOS

---

### Performance Notes

- **Clipboard size**: No practical limit (tested with 100+ objects)
- **Paste speed**: Instant for typical use cases (<100 objects)
- **Memory**: Clipboard persists until next copy/cut
- **Session**: Clipboard cleared when editor closes

---

### FAQ

**Q: Can I paste objects from one level to another?**
A: Not yet - clipboard is per-session and doesn't persist across level switches.

**Q: Does undo work after paste?**
A: Not yet - undo/redo system integration is planned for future release.

**Q: Can I copy objects to external clipboard?**
A: No - clipboard is internal to the level editor application.

**Q: Why doesn't paste work?**
A: Ensure canvas has focus (click canvas first) and clipboard has objects (copy first).

**Q: Can I paste rotated objects?**
A: Yes - all object properties are copied, including rotation speed and movement patterns.

---

### Known Limitations

- Clipboard doesn't persist between sessions
- No undo/redo integration yet
- No visual clipboard preview
- Console feedback only (no UI notifications)

---

**Last Updated**: 2025-10-09
**Version**: 1.0
