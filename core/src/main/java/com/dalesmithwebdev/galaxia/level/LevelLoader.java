package com.dalesmithwebdev.galaxia.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class for loading level data from JSON files
 */
public class LevelLoader {
    private static final Gson gson = new GsonBuilder().create();
    private static List<LevelInfo> availableLevels = null;

    /**
     * Scans the levels directory and returns metadata for all available levels
     */
    public static List<LevelInfo> getAvailableLevels() {
        if (availableLevels != null) {
            return availableLevels;
        }

        availableLevels = new ArrayList<>();
        FileHandle levelsDir = Gdx.files.internal("levels");

        if (levelsDir.exists() && levelsDir.isDirectory()) {
            for (FileHandle file : levelsDir.list(".json")) {
                try {
                    // Load just enough to get the name
                    LevelData level = loadLevel(file.nameWithoutExtension());
                    if (level != null) {
                        availableLevels.add(new LevelInfo(
                            file.nameWithoutExtension(),
                            level.getName(),
                            level.getDifficultyRating()
                        ));
                    }
                } catch (Exception e) {
                    System.err.println("Failed to load level metadata: " + file.name());
                    e.printStackTrace();
                }
            }
        }

        // Sort by difficulty
        Collections.sort(availableLevels, Comparator.comparing(LevelInfo::getDifficulty));
        return availableLevels;
    }

    /**
     * Loads a level by its ID (filename without extension)
     */
    public static LevelData loadLevel(String levelId) {
        try {
            FileHandle file = Gdx.files.internal("levels/" + levelId + ".json");
            if (!file.exists()) {
                System.err.println("Level file not found: " + levelId);
                return null;
            }

            String json = file.readString();
            return gson.fromJson(json, LevelData.class);
        } catch (Exception e) {
            System.err.println("Failed to load level: " + levelId);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Clears the cached level list (useful after new levels are added)
     */
    public static void refreshLevelList() {
        availableLevels = null;
    }

    /**
     * Simple class to hold level metadata
     */
    public static class LevelInfo {
        private final String id;
        private final String name;
        private final float difficulty;

        public LevelInfo(String id, String name, float difficulty) {
            this.id = id;
            this.name = name;
            this.difficulty = difficulty;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public float getDifficulty() { return difficulty; }
    }
}
