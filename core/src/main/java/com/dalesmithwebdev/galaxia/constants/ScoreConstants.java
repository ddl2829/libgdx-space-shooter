package com.dalesmithwebdev.galaxia.constants;

/**
 * Scoring system constants for kill credits and score calculations.
 * Score formula: (credit * multiplier) * BASE_SCORE_PER_CREDIT
 * where multiplier = 1 + log(timeStayedAlive)
 */
public final class ScoreConstants {
    private ScoreConstants() {} // Prevent instantiation

    // Kill Credits
    /** Kill credit value for destroying small meteors */
    public static final int METEOR_SMALL_CREDIT = 1;

    /** Kill credit value for destroying large meteors */
    public static final int METEOR_LARGE_CREDIT = 2;

    /** Kill credit value for destroying enemy fighters */
    public static final int ENEMY_CREDIT = 1;

    /** Kill credit value for destroying boss enemies */
    public static final int BOSS_KILL_CREDIT = 10;

    // Score Multipliers
    /** Base score multiplier applied to credit values */
    public static final int BASE_SCORE_PER_CREDIT = 100;
}
