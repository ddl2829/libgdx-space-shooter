package com.dalesmithwebdev.galaxia.constants;

/**
 * Damage type constants for collision mask checking
 */
public final class DamageTypeConstants {
    private DamageTypeConstants() {} // Prevent instantiation

    public static final int PLAYER = 1;
    public static final int METEOR = 2;
    public static final int ENEMY = 4;
    public static final int LASER = 8;
    public static final int ENEMY_LASER = 16;
    public static final int MISSILE = 32;
    public static final int BOMB = 64;
}
