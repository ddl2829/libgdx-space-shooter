package com.dalesmithwebdev.galaxia.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.MissileComponent;
import com.dalesmithwebdev.galaxia.components.PositionComponent;
import com.dalesmithwebdev.galaxia.components.RenderComponent;
import com.dalesmithwebdev.galaxia.components.SpeedComponent;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;

public class MovementSystem extends EntitySystem {
    public MovementSystem() {}

    public void update(float deltaTime) {
        if(ArcadeSpaceShooter.paused) {
            return;
        }
        ImmutableArray<Entity> entities = this.getEngine().getEntitiesFor(Family.all(PositionComponent.class, SpeedComponent.class).get());

        for (Entity moveable : entities)
        {
            PositionComponent pc = ComponentMap.positionMapper.get(moveable);
            SpeedComponent sc = ComponentMap.speedMapper.get(moveable);
            Vector2 move = sc.motion.cpy();
            if(Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
                if(ComponentMap.meteorMapper.has(moveable) || ComponentMap.enemyMapper.has(moveable) || ComponentMap.backgroundObjectMapper.has(moveable)) {
                    move.y = move.y * 3;
                }
            }
            pc.position = pc.position.add(move);

            if (ComponentMap.playerMapper.has(moveable) && ComponentMap.renderMapper.has(moveable))
            {
                RenderComponent renderComp = ComponentMap.renderMapper.get(moveable);
                if (pc.position.x < 0)
                {
                    pc.position.x = 0;
                }
                if (pc.position.y < 0)
                {
                    pc.position.y = 0;
                }
                if (pc.position.x > ArcadeSpaceShooter.screenRect.width)
                {
                    pc.position.x = ArcadeSpaceShooter.screenRect.width;
                }
                if (pc.position.y > ArcadeSpaceShooter.screenRect.height - renderComp.height)
                {
                    pc.position.y = ArcadeSpaceShooter.screenRect.height - renderComp.height;
                }
            }

            if(ComponentMap.missileMapper.has(moveable)) {
                MissileComponent missile = ComponentMap.missileMapper.get(moveable);
                missile.timelived += deltaTime;
                if(missile.timelived >= 150 && !missile.speedBoosted) {
                    missile.speedBoosted = true;
                    sc.motion.y = 15;
                    sc.motion.x = 0;
                }
            }

            if (ComponentMap.laserMapper.has(moveable) || ComponentMap.missileMapper.has(moveable) || ComponentMap.bombMapper.has(moveable))
            {
                //Despawn lasers, missiles, and bombs shortly after they leave the screen
                if(pc.position.y < -10 || pc.position.y > ArcadeSpaceShooter.screenRect.height + 5 || pc.position.x < 0 || pc.position.x > ArcadeSpaceShooter.screenRect.width)
                {
                    this.getEngine().removeEntity(moveable);
                }
            }

            //Despawn anything going off the bottom of the screen
            if(pc.position.y < -10 && !ComponentMap.backgroundObjectMapper.has(moveable))
            {
                this.getEngine().removeEntity(moveable);
            }
        }
    }
}
