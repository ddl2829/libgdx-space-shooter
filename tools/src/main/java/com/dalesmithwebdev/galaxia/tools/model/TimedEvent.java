package com.dalesmithwebdev.galaxia.tools.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a timed event that occurs during level gameplay
 * Events can be triggered by time or player position
 */
public class TimedEvent {
    private float triggerTime;
    private TriggerType triggerType = TriggerType.TIME_BASED;
    private EventType eventType = EventType.NOTIFICATION;
    private Map<String, Object> eventData = new HashMap<>();

    public TimedEvent() {
        // Default constructor for JSON serialization
    }

    public TimedEvent(float triggerTime, TriggerType triggerType, EventType eventType) {
        this.triggerTime = triggerTime;
        this.triggerType = triggerType;
        this.eventType = eventType;
    }

    /**
     * Get a summary description of this event for display
     */
    public String getSummary() {
        switch (eventType) {
            case NOTIFICATION:
                String message = (String) eventData.getOrDefault("message", "");
                return "Show: " + (message.length() > 30 ? message.substring(0, 27) + "..." : message);

            case ITEM_SPAWN:
                String objectType = (String) eventData.getOrDefault("objectType", "UNKNOWN");
                Number x = (Number) eventData.getOrDefault("x", 0);
                Number y = (Number) eventData.getOrDefault("y", 0);
                return String.format("Spawn %s at (%.0f, %.0f)", objectType, x.floatValue(), y.floatValue());

            case ENEMY_WAVE:
                String enemyType = (String) eventData.getOrDefault("enemyType", "UNKNOWN");
                Number count = (Number) eventData.getOrDefault("count", 1);
                String pattern = (String) eventData.getOrDefault("pattern", "LINE");
                return String.format("%d x %s [%s]", count.intValue(), enemyType, pattern);

            case ENVIRONMENTAL_CHANGE:
                String changeType = (String) eventData.getOrDefault("changeType", "UNKNOWN");
                return "Change: " + changeType;

            default:
                return eventType.getDisplayName();
        }
    }

    /**
     * Get a formatted string for the trigger time/position
     */
    public String getTriggerDisplay() {
        if (triggerType == TriggerType.TIME_BASED) {
            return String.format("%.1fs", triggerTime);
        } else {
            return String.format("Y=%.0f", triggerTime);
        }
    }

    // Getters and setters
    public float getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(float triggerTime) {
        this.triggerTime = triggerTime;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Map<String, Object> getEventData() {
        return eventData;
    }

    public void setEventData(Map<String, Object> eventData) {
        this.eventData = eventData;
    }

    /**
     * Helper method to get event data with type safety
     */
    @SuppressWarnings("unchecked")
    public <T> T getData(String key, T defaultValue) {
        Object value = eventData.get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return (T) value;
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }

    /**
     * Helper method to set event data
     */
    public void setData(String key, Object value) {
        eventData.put(key, value);
    }
}
