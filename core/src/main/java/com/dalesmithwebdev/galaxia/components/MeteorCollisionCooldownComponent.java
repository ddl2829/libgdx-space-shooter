package com.dalesmithwebdev.galaxia.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracks collision cooldowns for meteors to prevent repeated collision processing
 */
public class MeteorCollisionCooldownComponent implements Component {
    // Map of entity IDs to cooldown timers (in milliseconds)
    public Map<Integer, Float> cooldowns = new HashMap<>();

    // Cooldown duration before meteors can collide again
    public static final float COOLDOWN_DURATION_MS = 500f;

    /**
     * Check if collision with another meteor is on cooldown
     */
    public boolean isOnCooldown(Entity other) {
        Float cooldown = cooldowns.get(other.hashCode());
        return cooldown != null && cooldown > 0;
    }

    /**
     * Set cooldown for collision with another meteor
     */
    public void setCooldown(Entity other) {
        cooldowns.put(other.hashCode(), COOLDOWN_DURATION_MS);
    }

    /**
     * Update all cooldown timers
     */
    public void updateCooldowns(float deltaTime) {
        cooldowns.replaceAll((id, cooldown) -> Math.max(0, cooldown - deltaTime));
    }
}
