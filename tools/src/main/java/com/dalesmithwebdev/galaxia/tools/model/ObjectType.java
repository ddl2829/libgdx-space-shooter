package com.dalesmithwebdev.galaxia.tools.model;

/**
 * Enum defining all placeable object types in the level editor
 * Texture sizes are based on actual game sprite dimensions from ArcadeShooter.atlas
 */
public enum ObjectType {
    // Meteors (sizes vary, using average dimensions from atlas)
    METEOR_LARGE("Large Meteor", "meteorBig", 136, 111),
    METEOR_SMALL("Small Meteor", "meteorGrey_small1", 28, 28),

    // Enemies
    ENEMY_FIGHTER("Fighter", "enemyShip", 98, 50),
    ENEMY_UFO("UFO", "ufoRed", 91, 91),
    ENEMY_BOSS("Boss", "bossEnemy", 196, 100),

    // Power-ups (all powerups are 34x33 from atlas)
    POWERUP_LASER_STRENGTH("Laser Strength", "powerupRed_bolt", 34, 33),
    POWERUP_DUAL_LASER("Dual Laser", "powerupYellow_bolt", 34, 33),
    POWERUP_DIAGONAL_LASER("Diagonal Laser", "powerupGreen_bolt", 34, 33),
    POWERUP_MISSILE("Missile", "powerupBlue_bolt", 34, 33),
    POWERUP_BOMB("Bomb", "pill_yellow", 22, 21),
    POWERUP_EMP("EMP", "pill_blue", 22, 21),
    POWERUP_SHIELD("Shield", "powerupBlue_shield", 34, 33);

    private final String displayName;
    private final String textureRegionName;
    private final int width;
    private final int height;

    ObjectType(String displayName, String textureRegionName, int width, int height) {
        this.displayName = displayName;
        this.textureRegionName = textureRegionName;
        this.width = width;
        this.height = height;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTextureRegionName() {
        return textureRegionName;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isMeteor() {
        return name().startsWith("METEOR_");
    }

    public boolean isEnemy() {
        return name().startsWith("ENEMY_");
    }

    public boolean isPowerup() {
        return name().startsWith("POWERUP_");
    }

    public boolean isBoss() {
        return this == ENEMY_BOSS;
    }
}
