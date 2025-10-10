package com.dalesmithwebdev.galaxia.tools.model;

/**
 * Types of events that can be triggered during gameplay
 */
public enum EventType {
    /**
     * Display a notification message to the player
     */
    NOTIFICATION("Notification", "Display text message to player", "📢"),

    /**
     * Spawn an item/power-up at specific coordinates
     */
    ITEM_SPAWN("Item Spawn", "Spawn a power-up at coordinates", "📦"),

    /**
     * Trigger an enemy wave spawn
     */
    ENEMY_WAVE("Enemy Wave", "Spawn group of enemies", "⚔️"),

    /**
     * Environmental change (background, music, etc.)
     */
    ENVIRONMENTAL_CHANGE("Environment", "Change background or music", "🌍");

    private final String displayName;
    private final String description;
    private final String icon;

    EventType(String displayName, String description, String icon) {
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }
}
