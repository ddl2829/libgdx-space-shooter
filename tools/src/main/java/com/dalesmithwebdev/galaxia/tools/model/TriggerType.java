package com.dalesmithwebdev.galaxia.tools.model;

/**
 * Defines how a timed event is triggered
 */
public enum TriggerType {
    /**
     * Event fires at a specific time (seconds from level start)
     */
    TIME_BASED("Time-Based", "Triggers at a specific time in seconds"),

    /**
     * Event fires when player reaches a specific Y position
     */
    POSITION_BASED("Position-Based", "Triggers when player reaches Y position");

    private final String displayName;
    private final String description;

    TriggerType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
