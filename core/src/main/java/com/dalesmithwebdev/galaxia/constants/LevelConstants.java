package com.dalesmithwebdev.galaxia.constants;

/**
 * Level generation and progression constants.
 * These values control level length, spawn rates, and difficulty scaling.
 */
public final class LevelConstants {
    private LevelConstants() {} // Prevent instantiation

    // Level Generation
    /** Base length added to all procedurally generated levels */
    public static final int BASE_LEVEL_LENGTH = 5000;

    /** Additional length added per level number (linear scaling) */
    public static final int LEVEL_LENGTH_INCREMENT = 2000;

    /** Initial spawn delay for first level (milliseconds) */
    public static final int INITIAL_SPAWN_DELAY = 1000;

    // Spawn Rates (base + scaling factor per level)
    // Formula: rate = BASE + (SCALE * levelNumber)

    /** Base spawn rate for small meteors */
    public static final double SMALL_METEOR_BASE_RATE = 2.0;

    /** Spawn rate increase per level for small meteors */
    public static final double SMALL_METEOR_SCALE_RATE = 0.05;

    /** Base spawn rate for large meteors */
    public static final double LARGE_METEOR_BASE_RATE = 1.0;

    /** Spawn rate increase per level for large meteors */
    public static final double LARGE_METEOR_SCALE_RATE = 0.05;

    /** Base spawn rate for enemy fighters */
    public static final double ENEMY_BASE_RATE = 0.1;

    /** Spawn rate increase per level for enemy fighters */
    public static final double ENEMY_SCALE_RATE = 0.05;

    // Level Progression
    /** Minimum time level must run before completion check (milliseconds) */
    public static final float MIN_LEVEL_TIME_MS = 3000f;

    // Coordinate Scaling
    /** Horizontal buffer/padding from screen edges for entity spawning */
    public static final float HORIZONTAL_BUFFER = 40f;

    // Movement Speeds (pixels per second, assuming 60 FPS baseline)
    /** Fixed downward speed for entities in JSON-based levels (pixels/second) */
    public static final float ENTITY_FIXED_DOWNWARD_SPEED = -180f; // Was -3 px/frame * 60 FPS

    /** Horizontal movement speed for enemy fighters (pixels/second) */
    public static final double ENEMY_HORIZONTAL_SPEED = 48.0; // Was 0.8 px/frame * 60 FPS
}
