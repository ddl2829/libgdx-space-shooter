package com.dalesmithwebdev.galaxia.systems;

import com.badlogic.ashley.core.Entity;

/**
 * Listener interface for collision events between entities
 */
public interface CollisionListener {
    /**
     * Called when two entities collide
     * @param damageDealer Entity that deals damage
     * @param damageTaker Entity that takes damage
     */
    void onCollision(Entity damageDealer, Entity damageTaker);
}
