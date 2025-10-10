package com.dalesmithwebdev.galaxia.systems.upgrades;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

/**
 * Strategy interface for handling player upgrade pickups.
 * Implements the Strategy pattern to eliminate duplicate upgrade logic.
 */
public interface UpgradeHandler {
    /**
     * Apply upgrade to player
     * @param player The player entity
     * @param position Position where upgrade was collected (for notifications)
     * @param engine Engine instance for creating notification entities
     * @return true if upgrade was applied, false if player already has it (bonus points granted)
     */
    boolean apply(Entity player, Vector2 position, Engine engine);

    /**
     * @return Display name for upgrade notification
     */
    String getUpgradeName();
}
