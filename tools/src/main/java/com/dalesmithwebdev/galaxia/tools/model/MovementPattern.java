package com.dalesmithwebdev.galaxia.tools.model;

/**
 * Movement patterns for enemies and meteors
 */
public enum MovementPattern {
    STRAIGHT("Straight Down", 0, -1),
    STRAIGHT_UP("Straight Up", 0, 1),
    DIAGONAL_LEFT("Diagonal Left", -0.5f, -1),
    DIAGONAL_RIGHT("Diagonal Right", 0.5f, -1),
    ZIGZAG("Zigzag", 0, -1),
    CIRCULAR("Circular", 0, 0),
    STATIONARY("Stationary", 0, 0);

    private final String displayName;
    private final float defaultDirectionX;
    private final float defaultDirectionY;

    MovementPattern(String displayName, float defaultDirectionX, float defaultDirectionY) {
        this.displayName = displayName;
        this.defaultDirectionX = defaultDirectionX;
        this.defaultDirectionY = defaultDirectionY;
    }

    public String getDisplayName() {
        return displayName;
    }

    public float getDefaultDirectionX() {
        return defaultDirectionX;
    }

    public float getDefaultDirectionY() {
        return defaultDirectionY;
    }
}
