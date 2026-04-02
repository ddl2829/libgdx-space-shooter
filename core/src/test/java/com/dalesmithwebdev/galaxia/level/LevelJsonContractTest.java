package com.dalesmithwebdev.galaxia.level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelJsonContractTest {
    private static final Gson GSON = new GsonBuilder().create();

    @Test
    void authoredLevelsMatchCurrentRuntimeContract() throws Exception {
        assertRuntimeDtoShape();

        Path levelsDir = resolveLevelsDirectory();
        List<Path> levelFiles = listLevelFiles(levelsDir);

        assertFalse(levelFiles.isEmpty(), "Expected at least one authored level in " + levelsDir);

        boolean sawEditorOnlyFields = false;

        for (Path levelFile : levelFiles) {
            String json = new String(Files.readAllBytes(levelFile), StandardCharsets.UTF_8);
            JsonObject raw = GSON.fromJson(json, JsonObject.class);
            LevelData level = GSON.fromJson(json, LevelData.class);

            assertNotNull(raw, "Failed to parse raw JSON: " + levelFile);
            assertNotNull(level, "Failed to parse level DTO: " + levelFile);

            String fileId = stripExtension(levelFile.getFileName().toString());
            assertEquals(fileId, level.getId(), "Level id should match filename for " + levelFile);
            assertNotBlank(level.getName(), "Level name is required for " + levelFile);
            assertTrue(level.getLength() > 0, "Level length must be positive for " + levelFile);
            assertTrue(!Float.isNaN(level.getDifficultyRating()), "Difficulty must be a real number for " + levelFile);
            assertTrue(level.getDifficultyRating() >= 0f && level.getDifficultyRating() <= 10f,
                "Difficulty should stay on the current 0-10 scale for " + levelFile);

            List<LevelObject> objects = level.getObjects();
            assertNotNull(objects, "Level objects list must not be null for " + levelFile);
            assertFalse(objects.isEmpty(), "Expected at least one object in " + levelFile);

            JsonArray rawObjects = raw.getAsJsonArray("objects");
            assertNotNull(rawObjects, "Raw JSON must contain an objects array for " + levelFile);
            assertEquals(rawObjects.size(), objects.size(), "Parsed object count must match raw JSON for " + levelFile);

            for (int i = 0; i < objects.size(); i++) {
                LevelObject object = objects.get(i);
                JsonObject rawObject = rawObjects.get(i).getAsJsonObject();

                assertNotBlank(object.getType(), "Object type is required for " + levelFile + " index " + i);
                assertTrue(Float.isFinite(object.getX()), "Object x must be finite for " + levelFile + " index " + i);
                assertTrue(Float.isFinite(object.getY()), "Object y must be finite for " + levelFile + " index " + i);
                assertTrue(object.getHealth() > 0, "Object health must be positive for " + levelFile + " index " + i);

                if (rawObject.has("scale") || rawObject.has("spawnDelay")) {
                    sawEditorOnlyFields = true;
                }
            }
        }

        assertTrue(sawEditorOnlyFields,
            "Expected at least one authored level object to contain editor-only fields such as scale or spawnDelay");
    }

    private static void assertRuntimeDtoShape() {
        assertHasField(LevelData.class, "id");
        assertHasField(LevelData.class, "name");
        assertHasField(LevelData.class, "length");
        assertHasField(LevelData.class, "difficultyRating");
        assertHasField(LevelData.class, "objects");

        assertHasField(LevelObject.class, "type");
        assertHasField(LevelObject.class, "x");
        assertHasField(LevelObject.class, "y");
        assertHasField(LevelObject.class, "health");
        assertHasField(LevelObject.class, "hasLasers");

        assertNoField(LevelObject.class, "scale");
        assertNoField(LevelObject.class, "spawnDelay");
    }

    private static void assertHasField(Class<?> type, String fieldName) {
        Field field = org.junit.jupiter.api.Assertions.assertDoesNotThrow(
            () -> type.getDeclaredField(fieldName),
            type.getSimpleName() + " is missing required field: " + fieldName
        );
        assertNotNull(field);
    }

    private static void assertNoField(Class<?> type, String fieldName) {
        assertThrows(NoSuchFieldException.class, () -> type.getDeclaredField(fieldName),
            type.getSimpleName() + " should not expose field: " + fieldName);
    }

    private static Path resolveLevelsDirectory() {
        List<Path> candidates = new ArrayList<Path>();
        Path current = Paths.get("").toAbsolutePath().normalize();

        for (int i = 0; i < 4 && current != null; i++) {
            candidates.add(current.resolve("assets/levels"));
            current = current.getParent();
        }

        for (Path candidate : candidates) {
            if (Files.isDirectory(candidate)) {
                return candidate;
            }
        }

        throw new AssertionError("Could not locate assets/levels from " + Paths.get("").toAbsolutePath().normalize());
    }

    private static List<Path> listLevelFiles(Path levelsDir) throws IOException {
        List<Path> files = new ArrayList<Path>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(levelsDir, "*.json")) {
            for (Path file : stream) {
                files.add(file);
            }
        }
        return files;
    }

    private static String stripExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0) {
            return filename;
        }
        return filename.substring(0, dotIndex);
    }

    private static void assertNotBlank(String value, String message) {
        assertTrue(value != null && !value.trim().isEmpty(), message);
    }
}
