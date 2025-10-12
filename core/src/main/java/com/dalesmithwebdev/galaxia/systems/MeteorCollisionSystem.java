package com.dalesmithwebdev.galaxia.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.services.GameStateService;
import com.dalesmithwebdev.galaxia.services.ServiceLocator;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;

/**
 * MeteorCollisionSystem - Handles meteor-to-meteor collision detection, deflection physics, and size-based damage
 * Single Responsibility: Meteor-to-meteor physics and combat interactions
 */
public class MeteorCollisionSystem extends EntitySystem {

    // Coefficient of restitution for meteor collisions (0.0 = inelastic, 1.0 = perfectly elastic)
    private static final float RESTITUTION = 0.4f;

    // Minimum velocity to consider for collision response (prevents tiny oscillations)
    private static final float MIN_VELOCITY_THRESHOLD = 0.1f;

    // Minimum impact velocity for damage to occur
    private static final float MIN_DAMAGE_VELOCITY = 3.0f;

    // Damage scaling factors
    private static final int BIG_TO_SMALL_DAMAGE = 3; // Big meteor hitting small
    private static final int BIG_TO_BIG_DAMAGE = 1;   // Big meteor hitting big
    private static final int SMALL_TO_BIG_DAMAGE = 0; // Small meteor hitting big (no damage)

    @Override
    public void update(float deltaTime) {
        GameStateService gameState = ServiceLocator.getInstance().getGameState();
        if (gameState.isPaused()) {
            return;
        }

        ImmutableArray<Entity> meteors = getEngine().getEntitiesFor(
            Family.all(MeteorComponent.class, PositionComponent.class, RenderComponent.class, SpeedComponent.class, TakesDamageComponent.class).get()
        );

        // Update cooldown timers for all meteors with cooldown components
        for (Entity meteor : meteors) {
            if (ComponentMap.meteorCollisionCooldownMapper.has(meteor)) {
                MeteorCollisionCooldownComponent cooldown = ComponentMap.meteorCollisionCooldownMapper.get(meteor);
                cooldown.updateCooldowns(deltaTime);
            }
        }

        // Check each meteor against every other meteor
        for (int i = 0; i < meteors.size(); i++) {
            Entity meteor1 = meteors.get(i);
            PositionComponent pos1 = ComponentMap.positionMapper.get(meteor1);
            RenderComponent render1 = ComponentMap.renderMapper.get(meteor1);
            SpeedComponent speed1 = ComponentMap.speedMapper.get(meteor1);
            MeteorComponent meteorComp1 = ComponentMap.meteorMapper.get(meteor1);

            Rectangle rect1 = new Rectangle(
                pos1.position.x - (render1.width / 2.0f),
                pos1.position.y - (render1.height / 2.0f),
                render1.width,
                render1.height
            );

            // Check against all subsequent meteors (avoid checking same pair twice)
            for (int j = i + 1; j < meteors.size(); j++) {
                Entity meteor2 = meteors.get(j);
                PositionComponent pos2 = ComponentMap.positionMapper.get(meteor2);
                RenderComponent render2 = ComponentMap.renderMapper.get(meteor2);
                SpeedComponent speed2 = ComponentMap.speedMapper.get(meteor2);
                MeteorComponent meteorComp2 = ComponentMap.meteorMapper.get(meteor2);

                Rectangle rect2 = new Rectangle(
                    pos2.position.x - (render2.width / 2.0f),
                    pos2.position.y - (render2.height / 2.0f),
                    render2.width,
                    render2.height
                );

                // Check for collision
                if (rect1.overlaps(rect2)) {
                    // Check cooldown before processing collision
                    if (!isOnCooldown(meteor1, meteor2)) {
                        handleMeteorCollision(meteor1, meteor2, pos1, pos2, speed1, speed2,
                                            meteorComp1, meteorComp2, render1, render2);

                        // Set cooldown for both meteors
                        setCooldown(meteor1, meteor2);
                        setCooldown(meteor2, meteor1);
                    }
                }
            }
        }
    }

    /**
     * Check if two meteors are on collision cooldown
     */
    private boolean isOnCooldown(Entity meteor1, Entity meteor2) {
        if (ComponentMap.meteorCollisionCooldownMapper.has(meteor1)) {
            MeteorCollisionCooldownComponent cooldown = ComponentMap.meteorCollisionCooldownMapper.get(meteor1);
            if (cooldown.isOnCooldown(meteor2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Set collision cooldown between two meteors
     */
    private void setCooldown(Entity meteor, Entity other) {
        MeteorCollisionCooldownComponent cooldown;
        if (ComponentMap.meteorCollisionCooldownMapper.has(meteor)) {
            cooldown = ComponentMap.meteorCollisionCooldownMapper.get(meteor);
        } else {
            cooldown = new MeteorCollisionCooldownComponent();
            meteor.add(cooldown);
        }
        cooldown.setCooldown(other);
    }

    /**
     * Handle elastic collision between two meteors with size-based damage
     */
    private void handleMeteorCollision(Entity meteor1, Entity meteor2,
                                        PositionComponent pos1, PositionComponent pos2,
                                        SpeedComponent speed1, SpeedComponent speed2,
                                        MeteorComponent meteorComp1, MeteorComponent meteorComp2,
                                        RenderComponent render1, RenderComponent render2) {
        // Calculate collision normal (from meteor1 center to meteor2 center)
        Vector2 collisionNormal = pos2.position.cpy().sub(pos1.position);
        float distance = collisionNormal.len();

        // Prevent division by zero if meteors are exactly on top of each other
        if (distance < 0.01f) {
            collisionNormal.set(1, 0); // Default separation direction
            distance = 0.01f;
        } else {
            collisionNormal.nor(); // Normalize
        }

        // Calculate relative velocity
        Vector2 relativeVelocity = speed1.motion.cpy().sub(speed2.motion);

        // Calculate relative velocity in terms of the collision normal
        float velocityAlongNormal = relativeVelocity.dot(collisionNormal);

        // Do not resolve if velocities are separating
        if (velocityAlongNormal > 0) {
            return;
        }

        // Calculate impact velocity magnitude for damage calculation
        float impactSpeed = Math.abs(velocityAlongNormal);

        // Calculate impulse scalar (using restitution)
        float impulseMagnitude = -(1 + RESTITUTION) * velocityAlongNormal;
        impulseMagnitude /= 2; // Assume equal mass

        // Apply impulse to each meteor
        Vector2 impulse = collisionNormal.cpy().scl(impulseMagnitude);

        speed1.motion.add(impulse);
        speed2.motion.sub(impulse);

        // Separate meteors aggressively to prevent orbiting
        separateMeteors(pos1, pos2, collisionNormal, distance, render1, render2);

        // Apply size-based damage if impact is strong enough
        if (impactSpeed >= MIN_DAMAGE_VELOCITY) {
            applySizeBasedDamage(meteor1, meteor2, meteorComp1, meteorComp2, impactSpeed);
        }

        // Clamp very small velocities to zero to prevent jitter
        clampMinimumVelocity(speed1);
        clampMinimumVelocity(speed2);
    }

    /**
     * Apply damage based on meteor sizes and impact velocity
     */
    private void applySizeBasedDamage(Entity meteor1, Entity meteor2,
                                       MeteorComponent meteorComp1, MeteorComponent meteorComp2,
                                       float impactSpeed) {
        TakesDamageComponent damage1 = ComponentMap.takesDamageMapper.get(meteor1);
        TakesDamageComponent damage2 = ComponentMap.takesDamageMapper.get(meteor2);

        // Calculate damage based on size matchup
        int damageToMeteor1 = 0;
        int damageToMeteor2 = 0;

        if (meteorComp1.isBig && meteorComp2.isBig) {
            // Both big: both take equal damage
            damageToMeteor1 = BIG_TO_BIG_DAMAGE;
            damageToMeteor2 = BIG_TO_BIG_DAMAGE;
        } else if (meteorComp1.isBig && !meteorComp2.isBig) {
            // Meteor1 big, meteor2 small: small takes damage, big doesn't
            damageToMeteor2 = BIG_TO_SMALL_DAMAGE;
            damageToMeteor1 = SMALL_TO_BIG_DAMAGE;
        } else if (!meteorComp1.isBig && meteorComp2.isBig) {
            // Meteor1 small, meteor2 big: small takes damage, big doesn't
            damageToMeteor1 = BIG_TO_SMALL_DAMAGE;
            damageToMeteor2 = SMALL_TO_BIG_DAMAGE;
        }
        // If both small: no damage (harmless deflection)

        // Apply damage scaled by impact speed
        float speedMultiplier = Math.min(impactSpeed / 10f, 1.5f); // Cap at 1.5x
        damage1.health -= (int)(damageToMeteor1 * speedMultiplier);
        damage2.health -= (int)(damageToMeteor2 * speedMultiplier);
    }

    /**
     * Separate overlapping meteors by pushing them apart aggressively
     */
    private void separateMeteors(PositionComponent pos1, PositionComponent pos2,
                                  Vector2 collisionNormal, float distance,
                                  RenderComponent render1, RenderComponent render2) {
        // Use actual render sizes for more accurate separation
        float radius1 = Math.max(render1.width, render1.height) / 2.0f;
        float radius2 = Math.max(render2.width, render2.height) / 2.0f;
        float minDistance = radius1 + radius2;

        float penetration = minDistance - distance;

        if (penetration > 0) {
            // Add extra separation buffer to prevent orbiting (110% of penetration)
            float separationAmount = penetration * 1.1f;
            Vector2 separation = collisionNormal.cpy().scl(separationAmount / 2);
            pos1.position.sub(separation);
            pos2.position.add(separation);
        }
    }

    /**
     * Clamp very small velocities to zero to prevent jitter
     */
    private void clampMinimumVelocity(SpeedComponent speed) {
        if (Math.abs(speed.motion.x) < MIN_VELOCITY_THRESHOLD) {
            speed.motion.x = 0;
        }
        if (Math.abs(speed.motion.y) < MIN_VELOCITY_THRESHOLD) {
            speed.motion.y = 0;
        }
    }
}
