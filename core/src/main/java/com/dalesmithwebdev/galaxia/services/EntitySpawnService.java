package com.dalesmithwebdev.galaxia.services;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.dalesmithwebdev.galaxia.level.LevelObject;

import java.util.ArrayList;
import java.util.List;

/**
 * EntitySpawnService - Progressive entity spawning with performance management
 * Single Responsibility: Manage spawn queue and throttle entity creation
 */
public class EntitySpawnService {
    private final List<PendingSpawn> spawnQueue = new ArrayList<>();
    private int maxEntitiesPerFrame = 5; // Spawn at most this many entities per frame to avoid lag

    /**
     * Pending entity spawn - created from level data but not yet added to engine
     */
    public static class PendingSpawn {
        private final LevelObject levelObject;
        private final float spawnY;

        public PendingSpawn(LevelObject obj, float spawnY) {
            this.levelObject = obj;
            this.spawnY = spawnY;
        }

        public LevelObject getLevelObject() {
            return levelObject;
        }

        public float getSpawnY() {
            return spawnY;
        }
    }

    /**
     * Add entity to spawn queue
     */
    public void queueEntity(LevelObject levelObject, float spawnY) {
        spawnQueue.add(new PendingSpawn(levelObject, spawnY));
    }

    /**
     * Process spawn queue - spawn entities progressively to avoid single-frame lag spike.
     * Entities are created by the provided callback function.
     */
    public void processSpawnQueue(Engine engine, EntityCreationCallback callback) {
        if (spawnQueue.isEmpty()) {
            return;
        }

        // Spawn a limited number of entities per frame to avoid lag
        int spawnedThisFrame = 0;
        while (!spawnQueue.isEmpty() && spawnedThisFrame < maxEntitiesPerFrame) {
            // Take from front of queue (FIFO)
            PendingSpawn pending = spawnQueue.remove(0);
            spawnedThisFrame++;

            // Create entity via callback
            Entity entity = callback.createEntity(pending);
            if (entity != null) {
                engine.addEntity(entity);
            }
        }
    }

    /**
     * Check if spawn queue has any enemies or bosses left
     */
    public boolean isSpawnQueueEmpty() {
        if (spawnQueue.isEmpty()) {
            return true;
        }

        // Check if any remaining entities in queue are enemies or bosses
        for (PendingSpawn pending : spawnQueue) {
            String type = pending.levelObject.getType();
            if (type.equals("ENEMY_FIGHTER") || type.startsWith("BOSS_")) {
                return false; // Still have enemies waiting to spawn
            }
        }

        return true; // Only powerups/meteors left, doesn't count
    }

    /**
     * Clear the spawn queue
     */
    public void clearQueue() {
        spawnQueue.clear();
    }

    /**
     * Get queue size
     */
    public int getQueueSize() {
        return spawnQueue.size();
    }

    /**
     * Get max entities per frame
     */
    public int getMaxEntitiesPerFrame() {
        return maxEntitiesPerFrame;
    }

    /**
     * Set max entities per frame
     */
    public void setMaxEntitiesPerFrame(int maxEntitiesPerFrame) {
        this.maxEntitiesPerFrame = maxEntitiesPerFrame;
    }

    /**
     * Callback interface for entity creation
     */
    public interface EntityCreationCallback {
        Entity createEntity(PendingSpawn pending);
    }
}
