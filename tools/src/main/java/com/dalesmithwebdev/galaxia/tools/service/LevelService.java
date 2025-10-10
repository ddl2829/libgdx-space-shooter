package com.dalesmithwebdev.galaxia.tools.service;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.dalesmithwebdev.galaxia.tools.model.Level;
import com.dalesmithwebdev.galaxia.tools.model.TimedEvent;
import com.dalesmithwebdev.galaxia.tools.model.TriggerType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Service for managing level persistence and loading
 */
public class LevelService {
    private static final String LEVELS_DIR = "/Users/dale/games/galaxia/assets/levels";
    private final Json json;
    private final Path levelsPath;

    public LevelService() {
        this.json = new Json();
        this.json.setOutputType(JsonWriter.OutputType.json);
        this.levelsPath = Paths.get(LEVELS_DIR);

        // Ensure levels directory exists
        try {
            Files.createDirectories(levelsPath);
        } catch (IOException e) {
            System.err.println("Failed to create levels directory: " + e.getMessage());
        }

        System.out.println("Level service initialized. Saving levels to: " + levelsPath.toAbsolutePath());
    }

    /**
     * Load all levels from the levels directory
     */
    public List<Level> loadAllLevels() {
        List<Level> levels = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(levelsPath, 1)) {
            paths.filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".json"))
                .forEach(path -> {
                    try {
                        Level level = loadLevel(path.getFileName().toString());
                        if (level != null) {
                            levels.add(level);
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to load level: " + path + " - " + e.getMessage());
                    }
                });
        } catch (IOException e) {
            System.err.println("Failed to scan levels directory: " + e.getMessage());
        }

        return levels;
    }

    /**
     * Load a specific level by filename
     */
    public Level loadLevel(String filename) throws IOException {
        Path filePath = levelsPath.resolve(filename);
        if (!Files.exists(filePath)) {
            return null;
        }

        String jsonData = Files.readString(filePath);
        return json.fromJson(Level.class, jsonData);
    }

    /**
     * Save a level to file
     */
    public void saveLevel(Level level) throws IOException {
        // Update calculated fields
        level.calculateDifficulty();

        String filename = level.getId() + ".json";
        Path filePath = levelsPath.resolve(filename);

        String jsonData = json.prettyPrint(level);
        Files.writeString(filePath, jsonData);
    }

    /**
     * Delete a level file
     */
    public void deleteLevel(Level level) throws IOException {
        String filename = level.getId() + ".json";
        Path filePath = levelsPath.resolve(filename);
        Files.deleteIfExists(filePath);
    }

    /**
     * Create a new level with default values
     */
    public Level createNewLevel() {
        String id = "level_" + System.currentTimeMillis();
        Level level = new Level(id, "New Level");
        return level;
    }

    /**
     * Duplicate an existing level
     */
    public Level duplicateLevel(Level original) {
        String id = original.getId() + "_copy_" + System.currentTimeMillis();
        String name = original.getName() + " (Copy)";

        // Serialize and deserialize to create a deep copy
        String jsonData = json.toJson(original);
        Level duplicate = json.fromJson(Level.class, jsonData);

        duplicate.setId(id);
        duplicate.setName(name);

        return duplicate;
    }

    /**
     * Validate level data
     */
    public List<String> validateLevel(Level level) {
        List<String> errors = new ArrayList<>();

        if (level.getId() == null || level.getId().trim().isEmpty()) {
            errors.add("Level ID is required");
        }

        if (level.getName() == null || level.getName().trim().isEmpty()) {
            errors.add("Level name is required");
        }

        if (level.getLength() <= 0) {
            errors.add("Level length must be positive");
        }

        if (level.isHasBoss() && level.getBossConfig() == null) {
            errors.add("Boss configuration is required when hasBoss is true");
        }

        // Check for objects outside level bounds
        for (int i = 0; i < level.getObjects().size(); i++) {
            var obj = level.getObjects().get(i);
            if (obj.getY() > level.getLength()) {
                errors.add("Object " + i + " (" + obj.getType().getDisplayName() +
                    ") is placed beyond level length");
            }
        }

        // Validate timed events
        if (level.getTimedEvents() != null) {
            for (int i = 0; i < level.getTimedEvents().size(); i++) {
                TimedEvent event = level.getTimedEvents().get(i);

                // Check trigger time validity
                if (event.getTriggerTime() < 0) {
                    errors.add("Event " + i + ": Trigger time cannot be negative");
                }

                // Check time-based triggers against estimated time
                if (event.getTriggerType() == TriggerType.TIME_BASED &&
                    event.getTriggerTime() > level.getEstimatedTimeSeconds() * 1.5) {
                    errors.add("Event " + i + ": Trigger time (" + event.getTriggerTime() +
                        "s) exceeds level duration significantly");
                }

                // Check position-based triggers against level length
                if (event.getTriggerType() == TriggerType.POSITION_BASED &&
                    event.getTriggerTime() > level.getLength()) {
                    errors.add("Event " + i + ": Trigger position (" + event.getTriggerTime() +
                        ") exceeds level length (" + level.getLength() + ")");
                }

                // Validate event data based on type
                switch (event.getEventType()) {
                    case NOTIFICATION:
                        String message = event.getData("message", "");
                        if (message.isEmpty()) {
                            errors.add("Event " + i + ": Notification message is required");
                        }
                        break;

                    case ITEM_SPAWN:
                        String objectType = event.getData("objectType", "");
                        if (objectType.isEmpty()) {
                            errors.add("Event " + i + ": Item spawn object type is required");
                        }
                        break;

                    case ENEMY_WAVE:
                        String enemyType = event.getData("enemyType", "");
                        if (enemyType.isEmpty()) {
                            errors.add("Event " + i + ": Enemy wave type is required");
                        }
                        Integer count = event.getData("count", 0);
                        if (count <= 0) {
                            errors.add("Event " + i + ": Enemy wave count must be positive");
                        }
                        break;

                    case ENVIRONMENTAL_CHANGE:
                        String changeType = event.getData("changeType", "");
                        if (changeType.isEmpty()) {
                            errors.add("Event " + i + ": Environmental change type is required");
                        }
                        break;
                }
            }
        }

        return errors;
    }
}
