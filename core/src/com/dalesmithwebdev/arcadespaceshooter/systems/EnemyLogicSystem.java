package com.dalesmithwebdev.arcadespaceshooter.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.*;
import com.dalesmithwebdev.arcadespaceshooter.utility.ComponentMap;
import com.dalesmithwebdev.arcadespaceshooter.utility.Rand;

public class EnemyLogicSystem extends EntitySystem {
    public void update(float gameTime)
    {
        ImmutableArray<Entity> players = this.getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());
        ImmutableArray<Entity> enemies = this.getEngine().getEntitiesFor(Family.all(EnemyComponent.class, PositionComponent.class, SpeedComponent.class).get());
        for(Entity enemy : enemies)
        {
            PositionComponent pc = ComponentMap.positionComponentComponentMapper.get(enemy);
            SpeedComponent sc = ComponentMap.speedComponentComponentMapper.get(enemy);

            if (pc.position.y >= ArcadeSpaceShooter.screenRect.height)
            {
                //dont do anything if the enemy isnt on the screen
                sc.motion.x = 0;
                sc.motion.y = -2;
                continue;
            }

            if(ComponentMap.bossEnemyComponentComponentMapper.has(enemy))
            {
                if(pc.position.y >= ArcadeSpaceShooter.screenRect.height - (ArcadeSpaceShooter.screenRect.height / 4))
                {
                    sc.motion.y = -2;
                } else
                {
                    sc.motion.y = 0;
                }
            }
            else
            {
                sc.motion.y = -2;
            }
            if(players.size() == 0)
            {
                sc.motion.x = 0;
                continue;
            }

            PositionComponent playerPosition = ComponentMap.positionComponentComponentMapper.get(players.get(0));//(PositionComponent)players[0].components[typeof(PositionComponent)];

            if(pc.position.y < playerPosition.position.y)
            {
                sc.motion.x = 0;
                continue;
            }

            EnemyComponent ec = ComponentMap.enemyComponentComponentMapper.get(enemy);
            if(ComponentMap.hasLasersComponentComponentMapper.has(enemy)) {
                HasLasersComponent hasLasersComponent = ComponentMap.hasLasersComponentComponentMapper.get(enemy);
                hasLasersComponent.timeSinceLastShot += gameTime;
                if (hasLasersComponent.timeSinceLastShot >= hasLasersComponent.shotInterval) {
                    if(Rand.nextInt(100) < 20) {
                        hasLasersComponent.timeSinceLastShot = 0;

                        boolean upgraded = (hasLasersComponent.typeMask & HasLasersComponent.UPGRADED) > 0;
                        boolean upgraded_twice = (hasLasersComponent.typeMask & HasLasersComponent.UPGRADED_AGAIN) > 0;

                        int damageAmount = 10;
                        TextureRegion explosion = ArcadeSpaceShooter.textures.findRegion("laserRedShot");
                        TextureRegion laserGraphic = ArcadeSpaceShooter.textures.findRegion("laserRed");
                        if(upgraded) {
                            damageAmount = 20;
                            explosion = ArcadeSpaceShooter.textures.findRegion("laserGreenShot");
                            laserGraphic = ArcadeSpaceShooter.textures.findRegion("laserGreen");
                        }
                        if(upgraded_twice) {
                            damageAmount = 30;
                            explosion = ArcadeSpaceShooter.textures.findRegion("laserBlue08");
                            laserGraphic = ArcadeSpaceShooter.textures.findRegion("laserBlue12");
                        }

                        if((hasLasersComponent.typeMask & HasLasersComponent.SINGLE) > 0) {
                            Entity newLaser = new Entity();
                            newLaser.add(new RenderComponent(laserGraphic, RenderComponent.PLANE_ABOVE));
                            newLaser.add(new LaserComponent(explosion));
                            newLaser.add(new SpeedComponent(0, -20));
                            newLaser.add(new PositionComponent(new Vector2(pc.position.x - laserGraphic.getRegionWidth() / 2.0f, pc.position.y - 30)));
                            newLaser.add(new DealsDamageComponent(damageAmount, DamageSystem.ENEMY_LASER));
                            this.getEngine().addEntity(newLaser);
                        }

                        if ((hasLasersComponent.typeMask & HasLasersComponent.DUAL) > 0) {
                            Entity newLaser = new Entity();
                            newLaser.add(new RenderComponent(laserGraphic, RenderComponent.PLANE_ABOVE));
                            newLaser.add(new LaserComponent(explosion));
                            newLaser.add(new SpeedComponent(0, -20));
                            newLaser.add(new PositionComponent(new Vector2(pc.position.x - laserGraphic.getRegionWidth() / 2.0f - 10, pc.position.y - 30)));
                            newLaser.add(new DealsDamageComponent(damageAmount, DamageSystem.ENEMY_LASER));
                            this.getEngine().addEntity(newLaser);

                            Entity newLaser2 = new Entity();
                            newLaser2.add(new RenderComponent(laserGraphic, RenderComponent.PLANE_ABOVE));
                            newLaser2.add(new LaserComponent(explosion));
                            newLaser2.add(new SpeedComponent(0, -20));
                            newLaser2.add(new PositionComponent(new Vector2(pc.position.x - laserGraphic.getRegionWidth() / 2.0f + 10, pc.position.y - 30)));
                            newLaser2.add(new DealsDamageComponent(damageAmount, DamageSystem.ENEMY_LASER));
                            this.getEngine().addEntity(newLaser2);
                        }

                        if ((hasLasersComponent.typeMask & HasLasersComponent.DIAGONAL) > 0) {
                            Entity newLaser3 = new Entity();
                            newLaser3.add(new RenderComponent(laserGraphic, RenderComponent.PLANE_ABOVE));
                            newLaser3.add(new LaserComponent(explosion));
                            newLaser3.add(new SpeedComponent(10, -20));
                            newLaser3.add(new PositionComponent(new Vector2(pc.position.x - laserGraphic.getRegionWidth() / 2.0f - 10, pc.position.y - 30)));
                            newLaser3.add(new DealsDamageComponent(damageAmount, DamageSystem.ENEMY_LASER));
                            this.getEngine().addEntity(newLaser3);

                            Entity newLaser4 = new Entity();
                            newLaser4.add(new RenderComponent(laserGraphic, RenderComponent.PLANE_ABOVE));
                            newLaser4.add(new LaserComponent(explosion));;
                            newLaser4.add(new SpeedComponent(-10, -20));
                            newLaser4.add(new PositionComponent(new Vector2(pc.position.x - laserGraphic.getRegionWidth() / 2.0f + 10, pc.position.y - 30)));
                            newLaser4.add(new DealsDamageComponent(damageAmount, DamageSystem.ENEMY_LASER));
                            this.getEngine().addEntity(newLaser4);
                        }
                    }
                }
            }

            float movement = pc.position.x - playerPosition.position.x;
            if (movement > 0)
            {
                sc.motion.x = (float)-sc.movementSpeed;
            }
            else
            {
                sc.motion.x = (float)sc.movementSpeed;
            }
        }
    }
}
