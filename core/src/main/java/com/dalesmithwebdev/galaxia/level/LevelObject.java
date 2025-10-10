package com.dalesmithwebdev.galaxia.level;

/**
 * Represents a single object placed in a level
 */
public class LevelObject {
    private String type;
    private float x;
    private float y;
    private int health = 10;
    private boolean hasLasers = true;

    public LevelObject() {}

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public boolean isHasLasers() { return hasLasers; }
    public void setHasLasers(boolean hasLasers) { this.hasLasers = hasLasers; }
}
