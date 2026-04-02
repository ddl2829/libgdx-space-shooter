package com.dalesmithwebdev.galaxia.tools.service;

import com.dalesmithwebdev.galaxia.tools.model.BossConfiguration;
import com.dalesmithwebdev.galaxia.tools.model.EventType;
import com.dalesmithwebdev.galaxia.tools.model.Level;
import com.dalesmithwebdev.galaxia.tools.model.ObjectType;
import com.dalesmithwebdev.galaxia.tools.model.PlacedObject;
import com.dalesmithwebdev.galaxia.tools.model.TimedEvent;
import com.dalesmithwebdev.galaxia.tools.model.TriggerType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void createNewLevelProducesDefaults() {
        LevelService service = new LevelService(tempDir.resolve("levels"));

        Level level = service.createNewLevel();

        assertTrue(level.getId() != null && level.getId().startsWith("level_"), "expected generated id");
        assertEquals("New Level", level.getName());
        assertEquals(5000f, level.getLength(), 0.0001f);
        assertEquals(60f, level.getEstimatedTimeSeconds(), 0.0001f);
    }

    @Test
    void duplicateLevelCreatesDeepCopy() {
        LevelService service = new LevelService(tempDir.resolve("levels"));
        Level original = createSampleLevel();

        Level duplicate = service.duplicateLevel(original);

        assertNotSame(original, duplicate);
        assertNotSame(original.getObjects(), duplicate.getObjects());
        assertNotSame(original.getTimedEvents(), duplicate.getTimedEvents());
        assertEquals(original.getObjects().size(), duplicate.getObjects().size());
        assertEquals(original.getTimedEvents().size(), duplicate.getTimedEvents().size());
        assertTrue(duplicate.getId().startsWith(original.getId() + "_copy_"), "expected copied id");
        assertEquals(original.getName() + " (Copy)", duplicate.getName());
    }

    @Test
    void saveAndLoadRoundTripPreservesData() throws IOException {
        LevelService service = new LevelService(tempDir.resolve("levels"));
        Level original = createSampleLevel();

        service.saveLevel(original);
        Level loaded = service.loadLevel(original.getId() + ".json");

        assertNotNull(loaded);
        assertEquals(original.getId(), loaded.getId());
        assertEquals(original.getName(), loaded.getName());
        assertEquals(original.getLength(), loaded.getLength(), 0.0001f);
        assertEquals(original.getObjects().size(), loaded.getObjects().size());
        assertEquals(original.getTimedEvents().size(), loaded.getTimedEvents().size());
        assertEquals(original.getDifficultyRating(), loaded.getDifficultyRating(), 0.0001f);
    }

    @Test
    void loadAllLevelsReadsEveryJsonFile() throws IOException {
        LevelService service = new LevelService(tempDir.resolve("levels"));

        Level first = createSampleLevel();
        first.setId("level_alpha");
        service.saveLevel(first);

        Level second = createSampleLevel();
        second.setId("level_beta");
        second.setName("Second");
        service.saveLevel(second);

        List<Level> levels = service.loadAllLevels();
        assertEquals(2, levels.size());
    }

    @Test
    void validateLevelReportsStructuralErrors() {
        LevelService service = new LevelService(tempDir.resolve("levels"));
        Level level = createSampleLevel();
        level.setId(" ");
        level.setName("");
        level.setLength(0);
        level.setHasBoss(true);
        level.setBossConfig(null);
        level.getObjects().add(new PlacedObject(ObjectType.ENEMY_FIGHTER, 10, 99999));

        List<String> errors = service.validateLevel(level);

        assertContains(errors, "Level ID is required");
        assertContains(errors, "Level name is required");
        assertContains(errors, "Level length must be positive");
        assertContains(errors, "Boss configuration is required when hasBoss is true");
        assertContainsPrefix(errors, "Object 1 (Fighter) is placed beyond level length");
    }

    @Test
    void validateLevelReportsTimedEventErrors() {
        LevelService service = new LevelService(tempDir.resolve("levels"));
        Level level = createSampleLevel();
        level.setEstimatedTimeSeconds(10);

        TimedEvent lateNotification = new TimedEvent(20, TriggerType.TIME_BASED, EventType.NOTIFICATION);
        level.addTimedEvent(lateNotification);

        TimedEvent invalidWave = new TimedEvent(-1, TriggerType.POSITION_BASED, EventType.ENEMY_WAVE);
        invalidWave.setData("enemyType", "");
        invalidWave.setData("count", 0);
        level.addTimedEvent(invalidWave);

        List<String> errors = service.validateLevel(level);

        assertContainsPrefix(errors, "Event 0: Trigger time (20.0s) exceeds level duration significantly");
        assertContains(errors, "Event 0: Notification message is required");
        assertContains(errors, "Event 1: Trigger time cannot be negative");
        assertContains(errors, "Event 1: Enemy wave type is required");
        assertContains(errors, "Event 1: Enemy wave count must be positive");
    }

    @Test
    void defaultConstructorFindsProjectLevelsDirectory() {
        LevelService service = new LevelService();
        assertNotNull(service);
    }

    private static Level createSampleLevel() {
        Level level = new Level("level_sample", "Sample Level");
        level.setLength(2000);
        level.setEstimatedTimeSeconds(90);
        level.setHasBoss(true);
        level.setBossConfig(new BossConfiguration());
        level.getObjects().add(new PlacedObject(ObjectType.METEOR_SMALL, 25, 50));

        TimedEvent notification = new TimedEvent(15, TriggerType.TIME_BASED, EventType.NOTIFICATION);
        notification.setData("message", "Incoming enemy wave");
        level.addTimedEvent(notification);

        level.calculateDifficulty();
        return level;
    }

    private static void assertContains(List<String> values, String expected) {
        assertTrue(values.contains(expected), "Expected list to contain: " + expected + " but was: " + values);
    }

    private static void assertContainsPrefix(List<String> values, String expectedPrefix) {
        for (String value : values) {
            if (value.startsWith(expectedPrefix)) {
                return;
            }
        }
        throw new AssertionError("Expected list to contain prefix: " + expectedPrefix + " but was: " + values);
    }
}
