package com.dalesmithwebdev.galaxia.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.ExplosionComponent;
import com.dalesmithwebdev.galaxia.services.GameStateService;
import com.dalesmithwebdev.galaxia.services.ServiceLocator;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;

public class ExplosionSystem extends EntitySystem {
    public void update(float gametime)
    {
        GameStateService gameState = ServiceLocator.getInstance().getGameState();
        if(gameState.isPaused()) {
            return;
        }
        ImmutableArray<Entity> explosions = this.getEngine().getEntitiesFor(Family.all(ExplosionComponent.class).get());
        for(Entity explosion : explosions)
        {
            ExplosionComponent ec = ComponentMap.explosionMapper.get(explosion);
            ec.elapsedTime += gametime;
            if (ec.elapsedTime > ec.maxLife)
            {
                this.getEngine().removeEntity(explosion);
            }
        }
    }
}
