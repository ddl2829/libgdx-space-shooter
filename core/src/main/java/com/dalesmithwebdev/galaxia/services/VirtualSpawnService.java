package com.dalesmithwebdev.galaxia.services;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.dalesmithwebdev.galaxia.constants.LevelConstants;
import com.dalesmithwebdev.galaxia.level.LevelObject;

import java.util.ArrayList;
import java.util.List;

/**
 * VirtualSpawnService - Just-in-time entity spawning with virtual tracking
 *
 * Instead of queueing all entities upfront, this service calculates spawn times
 * and creates entities only when they're about to become visible. This eliminates
 * level load lag and reduces active entity count dramatically.
 *
 * Key Features:
 * - Zero upfront lag (no queue building)
 * - Minimal active entities (viewport culling)
 * - O(1) spawn processing (sorted list with index tracking)
 * - Identical gameplay (spawn times match original system)
 */
public class VirtualSpawnService {
    /**
     * Virtual entity data - lightweight representation before actual entity creation
     * Separates spawn timing (when to create) from spawn position (where to place)
     */
    public static class VirtualEntity {
        private final LevelObject levelObject;
        private final float spawnX;
        private final float spawnScreenY; // Where to place entity on screen
        private final float spawnOrder; // Normalized timing value (0 to N)

        public VirtualEntity(LevelObject levelObject, float spawnX, float spawnScreenY, float spawnOrder) {
            this.levelObject = levelObject;
            this.spawnX = spawnX;
            this.spawnScreenY = spawnScreenY;
            this.spawnOrder = spawnOrder;
        }

        public LevelObject getLevelObject() {
            return levelObject;
        }

        public float getSpawnX() {
            return spawnX;
        }

        public float getSpawnScreenY() {
            return spawnScreenY;
        }

        public float getSpawnOrder() {
            return spawnOrder;
        }
    }

    /**
     * Callback interface for entity creation
     */
    public interface EntityCreationCallback {
        Entity createEntity(VirtualEntity virtualEntity);
    }

    // Virtual entity list (sorted by spawn order)
    private final List<VirtualEntity> virtualEntities = new ArrayList<>();

    // Current processing index (entities before this have been spawned)
    private int currentIndex = 0;

    // Current progress through level (normalized, starts at 0)
    private float levelProgress = 0;

    // Configuration constants
    private static final float SPAWN_BUFFER = 100f; // Units ahead to spawn entity

    /**
     * Schedule an entity for virtual spawning with normalized timing
     * @param levelObject The level object to spawn
     * @param spawnX Screen X position where entity should appear
     * @param spawnScreenY Screen Y position where entity should appear
     * @param normalizedSpawnOrder Normalized timing value (0 = start of level)
     */
    public void scheduleEntity(LevelObject levelObject, float spawnX, float spawnScreenY, float normalizedSpawnOrder) {
        virtualEntities.add(new VirtualEntity(levelObject, spawnX, spawnScreenY, normalizedSpawnOrder));
    }

    /**
     * Sort virtual entities by spawn order after all have been scheduled
     * Should be called once after all scheduleEntity() calls during level load
     */
    public void sortBySpawnOrder() {
        // Sort by normalized spawn order (ascending - 0 spawns first)
        virtualEntities.sort((a, b) -> Float.compare(a.spawnOrder, b.spawnOrder));

        // Start level progress BEFORE first spawn point to prevent batch spawning
        // This ensures entities spawn individually as progress reaches their spawn order
        levelProgress = -SPAWN_BUFFER;

        System.out.println("VirtualSpawnService: Sorted " + virtualEntities.size() + " entities");
        if (!virtualEntities.isEmpty()) {
            System.out.println("  First spawn order: " + virtualEntities.get(0).spawnOrder);
            System.out.println("  Last spawn order: " + virtualEntities.get(virtualEntities.size()-1).spawnOrder);
            System.out.println("  Starting progress at: " + levelProgress + " (to prevent batch spawning)");
        }
    }

    /**
     * Process virtual spawns - create entities as level progresses
     * This should be called every frame during level gameplay
     *
     * @param deltaTime Time elapsed since last frame (in seconds)
     */
    public void processVirtualSpawns(Engine engine, float levelRunningTime, EntityCreationCallback callback, float screenHeight, float deltaTime) {
        // Progress through the level at the same rate entities move
        // deltaTime is in milliseconds, speed is in pixels/second, so divide by 1000
        levelProgress += Math.abs(LevelConstants.ENTITY_FIXED_DOWNWARD_SPEED) * (deltaTime / 1000f);

        // Spawn all entities that are now within spawn buffer
        while (currentIndex < virtualEntities.size()) {
            VirtualEntity ve = virtualEntities.get(currentIndex);

            // Spawn when level progress reaches entity's spawn order (with buffer)
            // Since spawn order is normalized from 0, this comparison is straightforward
            if (levelProgress < ve.spawnOrder - SPAWN_BUFFER) {
                break; // Still too early for this entity
            }

            // DETAILED LOGGING: Log every single spawn
            System.out.println(String.format("SPAWN #%d: type=%-20s | spawnOrder=%-8.1f | progress=%-8.1f | screenY=%-8.1f | screenX=%-8.1f",
                currentIndex + 1,
                ve.getLevelObject().getType(),
                ve.spawnOrder,
                levelProgress,
                ve.spawnScreenY,
                ve.spawnX
            ));

            // Create and spawn the entity
            Entity entity = callback.createEntity(ve);
            if (entity != null) {
                engine.addEntity(entity);
            }

            currentIndex++;
        }
    }

    /**
     * Check if all virtual entities have been spawned
     */
    public boolean isSpawnComplete() {
        return currentIndex >= virtualEntities.size();
    }

    /**
     * Check if spawn queue has any enemies or bosses left to spawn
     */
    public boolean hasEnemiesRemaining() {
        for (int i = currentIndex; i < virtualEntities.size(); i++) {
            String type = virtualEntities.get(i).levelObject.getType();
            if (type.equals("ENEMY_FIGHTER") || type.startsWith("BOSS_")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clear all virtual entities and reset state
     */
    public void clear() {
        virtualEntities.clear();
        currentIndex = 0;
        levelProgress = -SPAWN_BUFFER; // Start before first spawn point
    }

    /**
     * Get total count of scheduled virtual entities
     */
    public int getVirtualEntityCount() {
        return virtualEntities.size();
    }

    /**
     * Get count of remaining entities to spawn
     */
    public int getRemainingCount() {
        return virtualEntities.size() - currentIndex;
    }
}
