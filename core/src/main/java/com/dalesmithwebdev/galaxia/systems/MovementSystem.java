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
            PositionComponent pc = ComponentMap.positionComponentComponentMapper.get(moveable);
            SpeedComponent sc = ComponentMap.speedComponentComponentMapper.get(moveable);
            Vector2 move = sc.motion.cpy();
            if(Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
                if(ComponentMap.meteorComponentComponentMapper.has(moveable) || ComponentMap.enemyComponentComponentMapper.has(moveable) || ComponentMap.backgroundObjectComponentComponentMapper.has(moveable)) {
                    move.y = move.y * 3;
                }
            }
            pc.position = pc.position.add(move);

            if (ComponentMap.playerComponentComponentMapper.has(moveable) && ComponentMap.renderComponentComponentMapper.has(moveable))
            {
                RenderComponent renderComp = ComponentMap.renderComponentComponentMapper.get(moveable);
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

            if(ComponentMap.missileComponentComponentMapper.has(moveable)) {
                MissileComponent missile = ComponentMap.missileComponentComponentMapper.get(moveable);
                missile.timelived += deltaTime;
                if(missile.timelived >= 150 && !missile.speedBoosted) {
                    missile.speedBoosted = true;
                    sc.motion.y = 15;
                    sc.motion.x = 0;
                }
            }

            if (ComponentMap.laserComponentComponentMapper.has(moveable) || ComponentMap.missileComponentComponentMapper.has(moveable) || ComponentMap.bombComponentComponentMapper.has(moveable))
            {
                //Despawn lasers, missiles, and bombs shortly after they leave the screen
                if(pc.position.y < -10 || pc.position.y > ArcadeSpaceShooter.screenRect.height + 5 || pc.position.x < 0 || pc.position.x > ArcadeSpaceShooter.screenRect.width)
                {
                    this.getEngine().removeEntity(moveable);
                }
            }

            //Despawn anything going off the bottom of the screen
            if(pc.position.y < -10 && !ComponentMap.backgroundObjectComponentComponentMapper.has(moveable))
            {
                this.getEngine().removeEntity(moveable);
            }
        }
    }
}
