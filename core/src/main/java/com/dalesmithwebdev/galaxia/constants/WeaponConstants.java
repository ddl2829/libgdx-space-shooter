package com.dalesmithwebdev.galaxia.constants;

/**
 * Weapon configuration constants for damage, firing rates, and positioning.
 * Tuning these values affects combat balance and weapon effectiveness.
 */
public final class WeaponConstants {
    private WeaponConstants() {} // Prevent instantiation

    // Laser - Damage
    /** Base laser damage at level 0 (red lasers) */
    public static final int LASER_DAMAGE_BASIC = 1;

    /** Laser damage at level 1 (green lasers, UPGRADED) */
    public static final int LASER_DAMAGE_UPGRADED = 2;

    /** Laser damage at level 2 (blue lasers, UPGRADED_AGAIN) */
    public static final int LASER_DAMAGE_UPGRADED_AGAIN = 3;

    // Laser - Fire Rates
    /** Time interval between laser shots at basic level (milliseconds) */
    public static final int LASER_INTERVAL_BASIC_MS = 150;

    /** Time interval between laser shots at level 1 (milliseconds) */
    public static final int LASER_INTERVAL_UPGRADED_MS = 100;

    /** Time interval between laser shots at level 2 (milliseconds) */
    public static final int LASER_INTERVAL_UPGRADED_AGAIN_MS = 80;

    // Laser - Speed and Positioning (pixels/second)
    /** Vertical speed of laser projectiles (pixels/second) */
    public static final float LASER_SPEED_Y = 1200f; // Was 20 px/frame * 60 FPS

    /** Horizontal offset for dual laser positioning from player center */
    public static final float LASER_OFFSET_DUAL = 10f;

    /** Horizontal speed component for diagonal laser shots (pixels/second) */
    public static final float LASER_DIAGONAL_SPEED_X = 600f; // Was 10 px/frame * 60 FPS

    /** Vertical offset for laser spawn position relative to player */
    public static final float LASER_OFFSET_Y = 40f;

    // Missile
    /** Damage dealt by left missile */
    public static final int MISSILE_DAMAGE_LEFT = 8;

    /** Damage dealt by right missile */
    public static final int MISSILE_DAMAGE_RIGHT = 5;

    /** Time interval between missile shots (milliseconds) */
    public static final int MISSILE_INTERVAL_MS = 1000;

    /** Horizontal offset for missile spawn position from player center */
    public static final float MISSILE_OFFSET_X = 60f;

    /** Horizontal speed factor for missiles (pixels/second) */
    public static final float MISSILE_SPEED_X_FACTOR = 180f; // Was 3 px/frame * 60 FPS

    /** Vertical speed of missiles (pixels/second) */
    public static final float MISSILE_SPEED_Y = 60f; // Was 1 px/frame * 60 FPS

    // Bomb
    /** Damage dealt by bomb explosions */
    public static final int BOMB_DAMAGE = 10;

    /** Time interval between bomb drops (milliseconds) */
    public static final int BOMB_INTERVAL_MS = 500;

    /** Vertical speed of bombs (pixels/second) */
    public static final float BOMB_SPEED_Y = 300f; // Was 5 px/frame * 60 FPS

    /** Vertical offset for bomb spawn position relative to player */
    public static final float BOMB_OFFSET_Y = 50f;

    /** Explosion radius for bomb area of effect damage */
    public static final int BOMB_EXPLOSION_RADIUS = 200;

    // EMP
    /** Time interval between EMP activations (milliseconds) */
    public static final int EMP_INTERVAL_MS = 10000;
}
