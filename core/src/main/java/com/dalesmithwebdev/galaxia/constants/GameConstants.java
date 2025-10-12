package com.dalesmithwebdev.galaxia.constants;

/**
 * Core game constants for player attributes, combat mechanics, and system settings.
 * These values control fundamental game behavior and balance.
 */
public final class GameConstants {
    private GameConstants() {} // Prevent instantiation

    // Player
    /** Initial number of lives the player starts with */
    public static final int PLAYER_INITIAL_LIVES = 3;

    /** Movement speed multiplier applied to player input direction (pixels/second) */
    public static final float PLAYER_MOVEMENT_SPEED = 300.0f; // Was 5.0 px/frame * 60 FPS

    /** Delay in milliseconds before respawning player after death */
    public static final float PLAYER_RESPAWN_DELAY_MS = 2000f;

    // Combat
    /** Duration in milliseconds that an entity remains invulnerable after taking damage */
    public static final int RECENTLY_DAMAGED_TIMEOUT_MS = 50;

    /** Invincibility frame duration for meteors hit by player ramming (milliseconds) */
    public static final int METEOR_RAMMING_INVINCIBILITY_MS = 500;

    /** Bonus points awarded when player collects a duplicate upgrade they already have */
    public static final int BONUS_POINTS_FOR_DUPLICATE_UPGRADE = 1000;

    // Effects
    /** Duration in milliseconds that the EMP effect remains active */
    public static final float EMP_DURATION_MS = 20000f;

    // Background
    /** Maximum number of background decoration elements allowed on screen */
    public static final int MAX_BACKGROUND_ELEMENTS = 15;

    // System
    /** Interval in milliseconds between memory usage reports to console */
    public static final int MEMORY_REPORT_INTERVAL_MS = 60000;
}
