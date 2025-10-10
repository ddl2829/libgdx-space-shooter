package com.dalesmithwebdev.galaxia.constants;

/**
 * Upgrade drop probability and level requirement constants.
 * These values control the frequency and distribution of powerup drops.
 */
public final class UpgradeConstants {
    private UpgradeConstants() {} // Prevent instantiation

    // Drop Chances
    /** Percentage chance (0-100) that an enemy will drop an upgrade on death */
    public static final int UPGRADE_DROP_CHANCE_PERCENT = 15;

    // Upgrade Type Probabilities (out of 100)
    // These define the weight for each upgrade type in the random selection

    /** Weight for laser strength upgrade (0-30 range = 30% of drops) */
    public static final int LASER_STRENGTH_WEIGHT = 30;

    /** Weight for dual laser upgrade (30-40 range = 10% of drops) */
    public static final int DUAL_LASER_WEIGHT = 10;

    /** Weight for diagonal laser upgrade (40-43 range = 3% of drops) */
    public static final int DIAGONAL_LASER_WEIGHT = 3;

    /** Weight for shield upgrade (50-70 range = 20% of drops) */
    public static final int SHIELD_WEIGHT = 20;

    /** Weight for bomb upgrade (70-80 range = 10% of drops) */
    public static final int BOMB_WEIGHT = 10;

    /** Weight for missile upgrade (80-85 range = 5% of drops) */
    public static final int MISSILE_WEIGHT = 5;

    /** Weight for EMP upgrade (90-91 range = 1% of drops) */
    public static final int EMP_WEIGHT = 1;

    // Cumulative Probability Thresholds
    // Used for range checks: if(random >= START && random < END)

    /** End of laser strength upgrade range (0 to this value) */
    public static final int LASER_STRENGTH_THRESHOLD_END = LASER_STRENGTH_WEIGHT; // 30

    /** Start of dual laser upgrade range */
    public static final int DUAL_LASER_THRESHOLD_START = LASER_STRENGTH_THRESHOLD_END; // 30

    /** End of dual laser upgrade range */
    public static final int DUAL_LASER_THRESHOLD_END = DUAL_LASER_THRESHOLD_START + DUAL_LASER_WEIGHT; // 40

    /** Start of diagonal laser upgrade range */
    public static final int DIAGONAL_LASER_THRESHOLD_START = DUAL_LASER_THRESHOLD_END; // 40

    /** End of diagonal laser upgrade range */
    public static final int DIAGONAL_LASER_THRESHOLD_END = DIAGONAL_LASER_THRESHOLD_START + DIAGONAL_LASER_WEIGHT; // 43

    /** Start of shield upgrade range (gap from 43-50 is intentional dead zone) */
    public static final int SHIELD_THRESHOLD_START = 50;

    /** End of shield upgrade range */
    public static final int SHIELD_THRESHOLD_END = SHIELD_THRESHOLD_START + SHIELD_WEIGHT; // 70

    /** Start of bomb upgrade range */
    public static final int BOMB_THRESHOLD_START = SHIELD_THRESHOLD_END; // 70

    /** End of bomb upgrade range */
    public static final int BOMB_THRESHOLD_END = BOMB_THRESHOLD_START + BOMB_WEIGHT; // 80

    /** Start of missile upgrade range */
    public static final int MISSILE_THRESHOLD_START = BOMB_THRESHOLD_END; // 80

    /** End of missile upgrade range */
    public static final int MISSILE_THRESHOLD_END = MISSILE_THRESHOLD_START + MISSILE_WEIGHT; // 85

    /** Start of EMP upgrade range (gap from 85-90 is intentional dead zone) */
    public static final int EMP_THRESHOLD_START = 90;

    /** End of EMP upgrade range */
    public static final int EMP_THRESHOLD_END = EMP_THRESHOLD_START + EMP_WEIGHT; // 91

    // Level Requirements
    /** Minimum level required to drop diagonal laser upgrades */
    public static final int DIAGONAL_LASER_MIN_LEVEL = 3;

    /** Minimum level required to drop laser strength level 2 upgrades */
    public static final int LASER_STRENGTH_LEVEL_2_MIN_LEVEL = 3;
}
