package com.dalesmithwebdev.arcadespaceshooter.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.dalesmithwebdev.arcadespaceshooter.components.ExplosionComponent;
import com.dalesmithwebdev.arcadespaceshooter.utility.ComponentMap;

public class ExplosionSystem extends EntitySystem {
    public void update(float gametime)
    {
        ImmutableArray<Entity> explosions = this.getEngine().getEntitiesFor(Family.all(ExplosionComponent.class).get());
        for(Entity explosion : explosions)
        {
            //ExplosionComponent ec = (ExplosionComponent)explosion.components[typeof(ExplosionComponent)];
            ExplosionComponent ec = ComponentMap.explosionComponentComponentMapper.get(explosion);
            ec.elapsedTime += gametime;
            if (ec.elapsedTime > ec.maxLife)
            {
                this.getEngine().removeEntity(explosion);
            }
        }
    }
}
