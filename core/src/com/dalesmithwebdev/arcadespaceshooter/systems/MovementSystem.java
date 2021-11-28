package com.dalesmithwebdev.arcadespaceshooter.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.PositionComponent;
import com.dalesmithwebdev.arcadespaceshooter.components.RenderComponent;
import com.dalesmithwebdev.arcadespaceshooter.components.SpeedComponent;
import com.dalesmithwebdev.arcadespaceshooter.utility.ComponentMap;

public class MovementSystem extends EntitySystem {
    public MovementSystem() {}

    public void update(float deltaTime) {
        ImmutableArray<Entity> entities = this.getEngine().getEntitiesFor(Family.all(PositionComponent.class, SpeedComponent.class).get());

        for (Entity moveable : entities)
        {
            PositionComponent pc = ComponentMap.positionComponentComponentMapper.get(moveable);
            SpeedComponent sc = ComponentMap.speedComponentComponentMapper.get(moveable);
            Vector2 move = sc.motion.cpy();
            if(Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
                if(ComponentMap.meteorComponentComponentMapper.has(moveable) || ComponentMap.enemyComponentComponentMapper.has(moveable)) {
                    move.y = move.y * 3;
                }
            }
            pc.position = pc.position.add(move);

            if (ComponentMap.playerComponentComponentMapper.has(moveable))
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
                if (pc.position.y > ArcadeSpaceShooter.screenRect.height - renderComp.CurrentTexture().getHeight())
                {
                    pc.position.y = ArcadeSpaceShooter.screenRect.height - renderComp.CurrentTexture().getHeight();
                }
            }

            if (ComponentMap.laserComponentComponentMapper.has(moveable))
            {
                //Despawn lasers shortly after they leave the screen
                if(pc.position.y < -ArcadeSpaceShooter.laserRed.getHeight() || pc.position.y > ArcadeSpaceShooter.screenRect.height + ArcadeSpaceShooter.laserRed.getHeight() || pc.position.x < 0 || pc.position.x > ArcadeSpaceShooter.screenRect.width)
                {
                    this.getEngine().removeEntity(moveable);
                }
            }

            //Despawn anything going off the bottom of the screen
            if(pc.position.y < -10)
            {
                this.getEngine().removeEntity(moveable);
            }
        }
    }
}
