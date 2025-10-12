package com.dalesmithwebdev.galaxia.level;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a loaded level from JSON
 */
public class LevelData {
    private String id;
    private String name;
    private int length;
    private float difficultyRating;
    private List<LevelObject> objects = new ArrayList<>();

    public LevelData() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getLength() { return length; }
    public void setLength(int length) { this.length = length; }

    public float getDifficultyRating() { return difficultyRating; }
    public void setDifficultyRating(float difficultyRating) { this.difficultyRating = difficultyRating; }

    public List<LevelObject> getObjects() { return objects; }
    public void setObjects(List<LevelObject> objects) { this.objects = objects; }
}
