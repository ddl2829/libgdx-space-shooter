package com.dalesmithwebdev.galaxia.tools.model;

/**
 * Configuration for boss enemies in a level
 */
public class BossConfiguration {
    private String spriteRegion = "bossEnemy";
    private int health = 100;
    private int fireRate = 1000;
    private float speed = 1.0f;
    private MovementPattern movementPattern = MovementPattern.CIRCULAR;

    // Abilities
    private boolean hasLasers = true;
    private boolean hasUpgradedLasers = false;
    private boolean hasDualLasers = true;
    private boolean hasDiagonalLasers = false;
    private boolean hasMissiles = false;
    private boolean hasShield = false;
    private boolean hasEmp = false;

    // Phase configuration
    private boolean hasMultiplePhases = false;
    private int phaseHealthThreshold = 50; // Health % to trigger phase 2

    public BossConfiguration() {
        // Default constructor for JSON
    }

    // Getters and setters
    public String getSpriteRegion() {
        return spriteRegion;
    }

    public void setSpriteRegion(String spriteRegion) {
        this.spriteRegion = spriteRegion;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getFireRate() {
        return fireRate;
    }

    public void setFireRate(int fireRate) {
        this.fireRate = fireRate;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public MovementPattern getMovementPattern() {
        return movementPattern;
    }

    public void setMovementPattern(MovementPattern movementPattern) {
        this.movementPattern = movementPattern;
    }

    public boolean isHasLasers() {
        return hasLasers;
    }

    public void setHasLasers(boolean hasLasers) {
        this.hasLasers = hasLasers;
    }

    public boolean isHasUpgradedLasers() {
        return hasUpgradedLasers;
    }

    public void setHasUpgradedLasers(boolean hasUpgradedLasers) {
        this.hasUpgradedLasers = hasUpgradedLasers;
    }

    public boolean isHasDualLasers() {
        return hasDualLasers;
    }

    public void setHasDualLasers(boolean hasDualLasers) {
        this.hasDualLasers = hasDualLasers;
    }

    public boolean isHasDiagonalLasers() {
        return hasDiagonalLasers;
    }

    public void setHasDiagonalLasers(boolean hasDiagonalLasers) {
        this.hasDiagonalLasers = hasDiagonalLasers;
    }

    public boolean isHasMissiles() {
        return hasMissiles;
    }

    public void setHasMissiles(boolean hasMissiles) {
        this.hasMissiles = hasMissiles;
    }

    public boolean isHasShield() {
        return hasShield;
    }

    public void setHasShield(boolean hasShield) {
        this.hasShield = hasShield;
    }

    public boolean isHasEmp() {
        return hasEmp;
    }

    public void setHasEmp(boolean hasEmp) {
        this.hasEmp = hasEmp;
    }

    public boolean isHasMultiplePhases() {
        return hasMultiplePhases;
    }

    public void setHasMultiplePhases(boolean hasMultiplePhases) {
        this.hasMultiplePhases = hasMultiplePhases;
    }

    public int getPhaseHealthThreshold() {
        return phaseHealthThreshold;
    }

    public void setPhaseHealthThreshold(int phaseHealthThreshold) {
        this.phaseHealthThreshold = phaseHealthThreshold;
    }
}
