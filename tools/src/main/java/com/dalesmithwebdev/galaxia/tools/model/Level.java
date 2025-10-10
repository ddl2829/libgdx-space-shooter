package com.dalesmithwebdev.galaxia.tools.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a complete level definition
 */
public class Level {
    private String id;
    private String name;
    private float length = 5000; // Level length in game units (pixels)
    private float estimatedTimeSeconds = 60;
    private float difficultyRating = 0;
    private boolean hasBoss = false;

    private List<PlacedObject> objects = new ArrayList<>();
    private BossConfiguration bossConfig = new BossConfiguration();
    private List<TimedEvent> timedEvents = new ArrayList<>();

    public Level() {
        // Default constructor for JSON
    }

    public Level(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Calculate difficulty rating based on placed objects
     */
    public void calculateDifficulty() {
        float totalDifficulty = 0;

        for (PlacedObject obj : objects) {
            totalDifficulty += obj.getDifficultyContribution();
        }

        // Boss adds significant difficulty
        if (hasBoss) {
            totalDifficulty += bossConfig.getHealth() * 0.8f;
            if (bossConfig.isHasMissiles()) totalDifficulty += 20;
            if (bossConfig.isHasShield()) totalDifficulty += 15;
            if (bossConfig.isHasEmp()) totalDifficulty += 25;
        }

        // Normalize to 0-10 scale
        this.difficultyRating = Math.min(10, totalDifficulty / 20);
    }

    /**
     * Get enemy count statistics
     */
    public int getEnemyCount() {
        return (int) objects.stream().filter(o -> o.getType().isEnemy()).count();
    }

    public int getFighterCount() {
        return (int) objects.stream()
            .filter(o -> o.getType() == ObjectType.ENEMY_FIGHTER)
            .count();
    }

    public int getUfoCount() {
        return (int) objects.stream()
            .filter(o -> o.getType() == ObjectType.ENEMY_UFO)
            .count();
    }

    public int getMeteorCount() {
        return (int) objects.stream().filter(o -> o.getType().isMeteor()).count();
    }

    public int getPowerupCount() {
        return (int) objects.stream().filter(o -> o.getType().isPowerup()).count();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getEstimatedTimeSeconds() {
        return estimatedTimeSeconds;
    }

    public void setEstimatedTimeSeconds(float estimatedTimeSeconds) {
        this.estimatedTimeSeconds = estimatedTimeSeconds;
    }

    public float getDifficultyRating() {
        return difficultyRating;
    }

    public void setDifficultyRating(float difficultyRating) {
        this.difficultyRating = difficultyRating;
    }

    public boolean isHasBoss() {
        return hasBoss;
    }

    public void setHasBoss(boolean hasBoss) {
        this.hasBoss = hasBoss;
    }

    public List<PlacedObject> getObjects() {
        return objects;
    }

    public void setObjects(List<PlacedObject> objects) {
        this.objects = objects;
    }

    public BossConfiguration getBossConfig() {
        return bossConfig;
    }

    public void setBossConfig(BossConfiguration bossConfig) {
        this.bossConfig = bossConfig;
    }

    public List<TimedEvent> getTimedEvents() {
        return timedEvents;
    }

    public void setTimedEvents(List<TimedEvent> timedEvents) {
        this.timedEvents = timedEvents;
    }

    /**
     * Add a timed event and sort events by trigger time
     */
    public void addTimedEvent(TimedEvent event) {
        timedEvents.add(event);
        sortTimedEvents();
    }

    /**
     * Remove a timed event
     */
    public void removeTimedEvent(TimedEvent event) {
        timedEvents.remove(event);
    }

    /**
     * Sort timed events by trigger time for easy navigation
     */
    public void sortTimedEvents() {
        timedEvents.sort((e1, e2) -> Float.compare(e1.getTriggerTime(), e2.getTriggerTime()));
    }

    /**
     * Get count of timed events by type
     */
    public int getEventCount(EventType type) {
        return (int) timedEvents.stream()
            .filter(e -> e.getEventType() == type)
            .count();
    }
}
