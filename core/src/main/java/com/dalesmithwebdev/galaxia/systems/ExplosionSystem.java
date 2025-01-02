package com.dalesmithwebdev.galaxia.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.ExplosionComponent;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;

public class ExplosionSystem extends EntitySystem {
    public void update(float gametime)
    {
        if(ArcadeSpaceShooter.paused) {
            return;
        }
        ImmutableArray<Entity> explosions = this.getEngine().getEntitiesFor(Family.all(ExplosionComponent.class).get());
        for(Entity explosion : explosions)
        {
            ExplosionComponent ec = ComponentMap.explosionComponentComponentMapper.get(explosion);
            ec.elapsedTime += gametime;
            if (ec.elapsedTime > ec.maxLife)
            {
                this.getEngine().removeEntity(explosion);
            }
        }
    }
}
