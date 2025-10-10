# Timed Events User Guide

Quick reference for level designers using the Galaxia timed events system.

---

## What Are Timed Events?

Timed events allow you to create dynamic, narrative-driven gameplay by triggering actions at specific times or positions during a level:
- Display messages to guide players
- Spawn power-ups at strategic moments
- Trigger enemy waves for pacing
- Change environment/music for atmosphere

---

## Event Types

### 📢 Notification
**Purpose**: Display text messages to the player

**Fields**:
- **Message**: The text to show (supports multi-line)
- **Duration**: How long to display (seconds)
- **Style**: Visual styling
  - `INFO` - Normal message (blue)
  - `WARNING` - Caution message (yellow)
  - `URGENT` - Critical message (red)

**Example Use Cases**:
- Tutorial hints: "Use missiles against shields!"
- Story beats: "Incoming transmission from HQ..."
- Warnings: "Boss battle ahead!"
- Encouragement: "Great job! Keep it up!"

---

### 📦 Item Spawn
**Purpose**: Spawn power-ups at specific coordinates

**Fields**:
- **Object Type**: Choose from available power-ups
- **X Position**: Horizontal spawn location (0-600)
- **Y Position**: Vertical spawn location (relative to trigger)

**Available Power-Ups**:
- Laser Strength
- Dual Laser
- Diagonal Laser
- Missile
- Bomb
- EMP
- Shield

**Example Use Cases**:
- Reward after difficult section
- Strategic placement before boss fight
- Hidden bonus in side areas
- Emergency health pickup

---

### ⚔️ Enemy Wave
**Purpose**: Spawn groups of enemies in formation

**Fields**:
- **Enemy Type**: Fighter, UFO, or Boss
- **Count**: Number of enemies (1-20)
- **Formation Pattern**:
  - `LINE` - Horizontal line formation
  - `V_SHAPE` - V-shaped formation
  - `SCATTERED` - Random spread
  - `CIRCLE` - Circular formation

**Example Use Cases**:
- Dramatic wave encounters
- Difficulty spikes
- Gauntlet sections
- Coordinated attacks

---

### 🌍 Environmental Change
**Purpose**: Modify background, music, or lighting

**Fields**:
- **Change Type**: BACKGROUND, MUSIC, or LIGHTING
- **Value**: Asset identifier or setting name

**Example Use Cases**:
- Music transitions for boss fights
- Background changes for story beats
- Lighting effects for atmosphere
- Dynamic environment shifts

---

## Trigger Types

### ⏰ Time-Based
Events fire at a specific time **from level start**.

**When to Use**:
- Consistent pacing across playthroughs
- Timed challenges
- Story beats at fixed intervals
- Tutorial messages early in level

**Example**: "Show warning at 30 seconds"

**Tips**:
- Level duration shown in Level tab
- Events beyond 1.5x level duration trigger warning
- Consider player skill variation in timing

---

### 📍 Position-Based
Events fire when player reaches a specific **Y position**.

**When to Use**:
- Content-based triggers (reach area → spawn)
- Guaranteed encounter placement
- Area-specific power-ups
- Position-dependent story beats

**Example**: "Spawn enemies at Y=2500"

**Tips**:
- Level length shown in Level tab
- Events beyond level length trigger error
- Use for guaranteed content delivery

---

## Creating Events

### Quick Start
1. Open level in editor
2. Click **Events** tab (right panel)
3. Click **Add Event**
4. Configure trigger:
   - Enter time (seconds) or position (Y coordinate)
   - Select TIME_BASED or POSITION_BASED
5. Select event type
6. Fill in event data
7. Click **Save**

### Event Table Columns
- **Trigger**: When event fires (e.g., "15.0s" or "Y=2500")
- **Type**: Event category with icon
- **Description**: Auto-generated summary

---

## Visual Preview

Events appear as colored markers in the level preview:

```
┌─────────────────────────┐
│ 🔵 15.0s ─────────      │  Blue = Notification
│                         │
│ 🟢 Y=2500 ────────      │  Green = Item Spawn
│                         │
│ 🔴 30.0s ──────────     │  Red = Enemy Wave
│                         │
│ 🟣 45.0s ──────────     │  Purple = Environmental
└─────────────────────────┘
```

**Marker Features**:
- Icon shows event type
- Trigger time/position displayed
- Dashed line connects to viewport center
- Summary text shows event details
- Scroll preview to see all events

---

## Editing Events

1. **Click event row** in table to select
2. Editor form populates with current data
3. **Modify fields** as needed
4. Click **Update** to save changes
5. Changes reflect immediately in preview

---

## Organizing Events

### Sorting
Click **Sort by Time** to organize events chronologically.
- Events auto-sort when added
- Manual sort available after bulk editing

### Best Practices
- **Space out notifications**: Minimum 3-5 seconds between messages
- **Test pacing**: Play through level to verify timing
- **Group related events**: Use similar trigger times for coordinated actions
- **Validate before saving**: Check for errors in validation dialog

---

## Common Patterns

### Opening Sequence
```
0s    - NOTIFICATION: "Level start message"
3s    - ENVIRONMENTAL: "Set starting music"
5s    - NOTIFICATION: "Tutorial hint"
```

### Boss Introduction
```
45s   - NOTIFICATION: "Warning: Boss approaching!"
47s   - ENVIRONMENTAL: "Change to boss music"
50s   - ENEMY_WAVE: Spawn boss enemy
```

### Mid-Level Power-Up
```
Y=2000 - NOTIFICATION: "Power-up ahead!"
Y=2200 - ITEM_SPAWN: Spawn shield at (300, 2200)
```

### Difficulty Spike
```
30s   - NOTIFICATION: "Incoming enemy squadron!"
32s   - ENEMY_WAVE: 5x Fighter (V_SHAPE)
35s   - ENEMY_WAVE: 3x UFO (LINE)
```

---

## Validation Errors

### Common Issues

**"Trigger time cannot be negative"**
- Fix: Use positive values only (0 or greater)

**"Trigger time exceeds level duration significantly"**
- Warning: Event may not trigger if level ends early
- Fix: Reduce trigger time or extend level length

**"Trigger position exceeds level length"**
- Error: Event will never trigger
- Fix: Reduce Y position or increase level length

**"Notification message is required"**
- Fix: Enter message text in text area

**"Enemy wave count must be positive"**
- Fix: Enter count ≥ 1

---

## Tips & Tricks

### Timing Strategy
- **Early game (0-15s)**: Tutorials, introductions
- **Mid game (15-45s)**: Main content, waves, power-ups
- **Late game (45s+)**: Boss fights, climactic moments

### Position Strategy
- **Y = 0-1000**: Introduction area
- **Y = 1000-3000**: Main gameplay zone
- **Y = 3000-4500**: Challenge escalation
- **Y = 4500+**: Boss arena / finale

### Message Writing
- **Keep it brief**: Max 2 lines for quick reading
- **Use urgency levels**: INFO for tips, WARNING for threats, URGENT for danger
- **Test readability**: Play through to verify message clarity

### Power-Up Placement
- **After challenges**: Reward players for difficult sections
- **Strategic positions**: Place where players need them most
- **Visible locations**: Center or predictable paths
- **Fair timing**: Give reaction time after notification

### Enemy Waves
- **Gradual escalation**: Start simple, increase complexity
- **Formation variety**: Mix formations for visual interest
- **Breathing room**: Allow recovery time between waves
- **Combine with notifications**: Warn before major waves

---

## Keyboard Shortcuts

Currently no keyboard shortcuts implemented.

**Suggested for future**:
- `Ctrl+N`: New event
- `Delete`: Delete selected event
- `Ctrl+D`: Duplicate event
- `Ctrl+S`: Save level

---

## Troubleshooting

### Event doesn't appear in preview
- Check trigger time/position is within level bounds
- Scroll preview to event location
- Verify event was saved (check table)

### Event fires at wrong time
- Verify trigger type (TIME_BASED vs POSITION_BASED)
- Check trigger value matches intended time/position
- Time-based events depend on level scroll speed

### Validation fails on save
- Read error messages carefully
- Fix all listed issues
- Common: negative times, missing data, out-of-bounds positions

### Preview marker hard to see
- Check event is in visible area
- Markers only show when scrolled into view
- Color-coded by type for easy identification

---

## Advanced Techniques

### Layered Notifications
Stack multiple messages with brief timing:
```
10.0s - "Wave incoming!"
10.5s - "Prepare for combat!"
11.0s - "Here they come!"
```

### Position-Based Story Beats
Use position triggers for exploration-based narrative:
```
Y=1000 - "You've entered the asteroid field..."
Y=2000 - "Distress signal detected!"
Y=3000 - "Approaching enemy base..."
```

### Dynamic Difficulty
Vary enemy waves with patterns:
```
20s - 3x Fighter (LINE)
25s - 5x Fighter (V_SHAPE)
30s - 8x Fighter (SCATTERED)
```

### Environmental Storytelling
Use environmental changes to enhance narrative:
```
0s    - ENVIRONMENTAL: "peaceful_music"
Y=2500 - ENVIRONMENTAL: "ominous_music"
Y=4000 - ENVIRONMENTAL: "combat_music"
```

---

## Example: Complete Level Flow

```
[START]
0.0s    - NOTIFICATION: "Welcome to Sector 7. Good luck, pilot!"
3.0s    - ENVIRONMENTAL: "exploration_music"

[EARLY GAME]
Y=500   - ITEM_SPAWN: Shield powerup
10.0s   - NOTIFICATION: "Asteroids ahead! Watch your shields!"
Y=1000  - ENEMY_WAVE: 3x Fighter (LINE)

[MID GAME]
15.0s   - NOTIFICATION: "Enemy patrols detected!"
18.0s   - ENEMY_WAVE: 5x Fighter (V_SHAPE)
Y=2000  - ITEM_SPAWN: Dual laser powerup
25.0s   - NOTIFICATION: "UFO squadron incoming!"
Y=2500  - ENEMY_WAVE: 4x UFO (SCATTERED)

[LATE GAME]
35.0s   - NOTIFICATION: "Approaching command ship!"
Y=3500  - ITEM_SPAWN: Bomb powerup
40.0s   - ENVIRONMENTAL: "boss_music"
42.0s   - NOTIFICATION: "CRITICAL: Boss detected! All weapons hot!"
Y=4000  - ENEMY_WAVE: 1x Boss

[FINALE]
Y=4800  - NOTIFICATION: "Almost there! Finish strong!"
```

---

## Support & Feedback

For questions or feature requests:
1. Check validation messages for specific errors
2. Review this guide for best practices
3. Test in-game to verify event behavior
4. Report bugs or suggestions to development team

---

**Version**: 1.0
**Last Updated**: 2025-10-09
