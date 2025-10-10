package com.dalesmithwebdev.galaxia.tools.model;

/**
 * Represents an object placed in the level editor
 */
public class PlacedObject {
    private ObjectType type;
    private float x;
    private float y;
    private float scale = 1.0f;

    // Movement properties
    private MovementPattern movementPattern = MovementPattern.STRAIGHT;
    private float speed = 2.0f;
    private float directionX = 0;
    private float directionY = -1;
    private float rotationSpeed = 0;

    // Enemy-specific properties
    private int health = 10;
    private int fireRate = 3000; // milliseconds
    private boolean hasLasers = true;
    private boolean hasMissiles = false;
    private boolean hasShield = false;

    // Timing properties
    private float spawnDelay = 0; // seconds from level start

    public PlacedObject() {
        // Default constructor for JSON
    }

    public PlacedObject(ObjectType type, float x, float y) {
        this.type = type;
        this.x = x;
        this.y = y;

        // Set defaults based on type
        if (type.isMeteor()) {
            this.health = type == ObjectType.METEOR_LARGE ? 8 : 3;
            this.hasLasers = false;
        } else if (type.isEnemy()) {
            if (type.isBoss()) {
                this.health = 100;
                this.fireRate = 1000;
            } else {
                this.health = 10;
                this.fireRate = 3000;
            }
        }
    }

    // Getters and setters
    public ObjectType getType() {
        return type;
    }

    public void setType(ObjectType type) {
        this.type = type;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public MovementPattern getMovementPattern() {
        return movementPattern;
    }

    public void setMovementPattern(MovementPattern movementPattern) {
        this.movementPattern = movementPattern;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getDirectionX() {
        return directionX;
    }

    public void setDirectionX(float directionX) {
        this.directionX = directionX;
    }

    public float getDirectionY() {
        return directionY;
    }

    public void setDirectionY(float directionY) {
        this.directionY = directionY;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
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

    public boolean isHasLasers() {
        return hasLasers;
    }

    public void setHasLasers(boolean hasLasers) {
        this.hasLasers = hasLasers;
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

    public float getSpawnDelay() {
        return spawnDelay;
    }

    public void setSpawnDelay(float spawnDelay) {
        this.spawnDelay = spawnDelay;
    }

    /**
     * Calculate difficulty contribution of this object
     */
    public float getDifficultyContribution() {
        float difficulty = 0;

        if (type.isEnemy()) {
            difficulty += health * 0.5f;
            if (hasLasers) difficulty += 5;
            if (hasMissiles) difficulty += 10;
            if (hasShield) difficulty += 8;
            if (type.isBoss()) difficulty *= 3;
        } else if (type.isMeteor()) {
            difficulty += health * 0.3f;
        } else if (type.isPowerup()) {
            difficulty -= 2; // Power-ups reduce difficulty
        }

        return difficulty;
    }
}
