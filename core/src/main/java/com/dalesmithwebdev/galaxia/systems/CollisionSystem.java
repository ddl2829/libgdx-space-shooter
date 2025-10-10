package com.dalesmithwebdev.galaxia.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.services.GameStateService;
import com.dalesmithwebdev.galaxia.services.ServiceLocator;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;

import java.util.ArrayList;
import java.util.List;

/**
 * CollisionSystem - Detects collisions between entities and notifies listeners
 * Single Responsibility: Collision detection only
 */
public class CollisionSystem extends EntitySystem {
    private final List<CollisionListener> listeners = new ArrayList<>();

    public void addListener(CollisionListener listener) {
        listeners.add(listener);
    }

    @Override
    public void update(float gameTime) {
        GameStateService gameState = ServiceLocator.getInstance().getGameState();
        if (gameState.isPaused()) {
            return;
        }

        ImmutableArray<Entity> damageDealers = getEngine().getEntitiesFor(
            Family.all(DealsDamageComponent.class, PositionComponent.class, RenderComponent.class).get()
        );
        ImmutableArray<Entity> damageTakers = getEngine().getEntitiesFor(
            Family.all(TakesDamageComponent.class, PositionComponent.class, RenderComponent.class).get()
        );

        for (Entity damageDealer : damageDealers) {
            DealsDamageComponent ddc = ComponentMap.dealsDamageMapper.get(damageDealer);
            PositionComponent dd_pc = ComponentMap.positionMapper.get(damageDealer);
            RenderComponent dd_rc = ComponentMap.renderMapper.get(damageDealer);

            // Calculate damage dealer bounding box
            Rectangle damageDealerRect = calculateBoundingBox(damageDealer, dd_pc, dd_rc);
            if (damageDealerRect == null) {
                continue;
            }

            for (Entity damageTaker : damageTakers) {
                TakesDamageComponent tdc = ComponentMap.takesDamageMapper.get(damageTaker);

                // Check if collision is valid based on damage masks
                if ((tdc.takesDamageFromMask & ddc.damageTypeMask) == 0) {
                    continue;
                }

                PositionComponent td_pc = ComponentMap.positionMapper.get(damageTaker);
                if (!ComponentMap.renderMapper.has(damageTaker)) {
                    continue;
                }

                RenderComponent td_rc = ComponentMap.renderMapper.get(damageTaker);
                Rectangle damageTakerRect = new Rectangle(
                    (int) td_pc.position.x - (td_rc.width / 2.0f),
                    (int) td_pc.position.y - (td_rc.height / 2.0f),
                    td_rc.width,
                    td_rc.height
                );

                // Check for overlap and notify listeners
                if (damageDealerRect.overlaps(damageTakerRect)) {
                    notifyCollision(damageDealer, damageTaker);
                    break; // One collision per damage dealer per frame
                }
            }
        }
    }

    /**
     * Calculate bounding box for entity (handles explosions differently)
     */
    private Rectangle calculateBoundingBox(Entity entity, PositionComponent pos, RenderComponent render) {
        if (ComponentMap.explosionMapper.has(entity)) {
            ExplosionComponent ec = ComponentMap.explosionMapper.get(entity);
            return new Rectangle(
                (int) pos.position.x - ec.radius,
                (int) pos.position.y - ec.radius,
                ec.radius * 2,
                ec.radius * 2
            );
        } else {
            try {
                return new Rectangle(
                    (int) pos.position.x - (render.width / 2.0f),
                    (int) pos.position.y - (render.height / 2.0f),
                    render.width,
                    render.height
                );
            } catch (NullPointerException npe) {
                npe.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Notify all registered listeners of collision
     */
    private void notifyCollision(Entity damageDealer, Entity damageTaker) {
        for (CollisionListener listener : listeners) {
            listener.onCollision(damageDealer, damageTaker);
        }
    }
}
