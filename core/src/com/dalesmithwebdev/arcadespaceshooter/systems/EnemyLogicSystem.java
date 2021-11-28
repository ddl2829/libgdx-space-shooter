package com.dalesmithwebdev.arcadespaceshooter.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.*;
import com.dalesmithwebdev.arcadespaceshooter.utility.ComponentMap;

public class EnemyLogicSystem extends EntitySystem {
    public void update(float gameTime)
    {
        ImmutableArray<Entity> players = this.getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());
        ImmutableArray<Entity> enemies = this.getEngine().getEntitiesFor(Family.all(EnemyComponent.class, PositionComponent.class, SpeedComponent.class).get());
        for(Entity enemy : enemies)
        {
            EnemyComponent ec = ComponentMap.enemyComponentComponentMapper.get(enemy);//(EnemyComponent)enemy.components[typeof(EnemyComponent)];
            PositionComponent pc = ComponentMap.positionComponentComponentMapper.get(enemy);//(PositionComponent)enemy.components[typeof(PositionComponent)];
            SpeedComponent sc = ComponentMap.speedComponentComponentMapper.get(enemy);//(SpeedComponent)enemy.components[typeof(SpeedComponent)];


            if (pc.position.y >= ArcadeSpaceShooter.screenRect.height)
            {
                //dont do anything if the enemy isnt on the screen
                sc.motion.x = 0;
                sc.motion.y = -1;
                continue;
            }

            if(ComponentMap.bossEnemyComponentComponentMapper.has(enemy))
            {
                if(pc.position.y >= ArcadeSpaceShooter.screenRect.height - (ArcadeSpaceShooter.screenRect.height / 4))
                {
                    sc.motion.y = -1;
                } else
                {
                    sc.motion.y = 0;
                }
            }
            else
            {
                sc.motion.y = -1;
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

            ec.timeSinceLastShot += gameTime;
            if (ec.timeSinceLastShot >= ec.shotInterval)
            {
                ec.timeSinceLastShot = 0;

                // is this a boss firing this laser?
                if(ComponentMap.bossEnemyComponentComponentMapper.has(enemy)) {
                    // check the laser level
                    BossEnemyComponent bossEnemyComponent = ComponentMap.bossEnemyComponentComponentMapper.get(enemy);
                    if(bossEnemyComponent.laserStrength == 1) {
                        Entity newLaser = new Entity();
                        newLaser.add(new RenderComponent(ArcadeSpaceShooter.laserRed));
                        newLaser.add(new LaserComponent(ArcadeSpaceShooter.explosionTexture));
                        newLaser.add(new SpeedComponent(new Vector2(0, -20)));
                        newLaser.add(new PositionComponent(new Vector2(pc.position.x - ArcadeSpaceShooter.laserGreen.getWidth() / 2.0f, pc.position.y - 30)));
                        newLaser.add(new DealsDamageComponent(10, DamageSystem.ENEMY_LASER));
                        this.getEngine().addEntity(newLaser);
                    } else if(bossEnemyComponent.laserStrength == 2) {
                        Entity newLaser = new Entity();
                        newLaser.add(new RenderComponent(ArcadeSpaceShooter.laserGreen));
                        newLaser.add(new LaserComponent(ArcadeSpaceShooter.explosionTextureGreen));
                        newLaser.add(new SpeedComponent(new Vector2(0, -20)));
                        newLaser.add(new PositionComponent(new Vector2(pc.position.x - ArcadeSpaceShooter.laserGreen.getWidth() / 2.0f, pc.position.y - 30)));
                        newLaser.add(new DealsDamageComponent(20, DamageSystem.ENEMY_LASER));
                        this.getEngine().addEntity(newLaser);
                    } else if(bossEnemyComponent.laserStrength == 3) {
                        Entity newLaser = new Entity();
                        newLaser.add(new RenderComponent(ArcadeSpaceShooter.laserGreen));
                        newLaser.add(new LaserComponent(ArcadeSpaceShooter.explosionTextureGreen));
                        newLaser.add(new SpeedComponent(new Vector2(0, -20)));
                        newLaser.add(new PositionComponent(new Vector2(pc.position.x - ArcadeSpaceShooter.laserGreen.getWidth() / 2.0f - 10, pc.position.y - 30)));
                        newLaser.add(new DealsDamageComponent(20, DamageSystem.ENEMY_LASER));
                        this.getEngine().addEntity(newLaser);

                        Entity newLaser2 = new Entity();
                        newLaser2.add(new RenderComponent(ArcadeSpaceShooter.laserGreen));
                        newLaser2.add(new LaserComponent(ArcadeSpaceShooter.explosionTextureGreen));
                        newLaser2.add(new SpeedComponent(new Vector2(0, -20)));
                        newLaser2.add(new PositionComponent(new Vector2(pc.position.x - ArcadeSpaceShooter.laserGreen.getWidth() / 2.0f + 10, pc.position.y - 30)));
                        newLaser2.add(new DealsDamageComponent(20, DamageSystem.ENEMY_LASER));
                        this.getEngine().addEntity(newLaser2);
                    } else if (bossEnemyComponent.laserStrength == 4) {
                        Entity newLaser = new Entity();
                        newLaser.add(new RenderComponent(ArcadeSpaceShooter.laserGreen));
                        newLaser.add(new LaserComponent(ArcadeSpaceShooter.explosionTextureGreen));
                        newLaser.add(new SpeedComponent(new Vector2(0, -20)));
                        newLaser.add(new PositionComponent(new Vector2(pc.position.x - ArcadeSpaceShooter.laserGreen.getWidth() / 2.0f - 10, pc.position.y - 30)));
                        newLaser.add(new DealsDamageComponent(20, DamageSystem.ENEMY_LASER));
                        this.getEngine().addEntity(newLaser);

                        Entity newLaser2 = new Entity();
                        newLaser2.add(new RenderComponent(ArcadeSpaceShooter.laserGreen));
                        newLaser2.add(new LaserComponent(ArcadeSpaceShooter.explosionTextureGreen));
                        newLaser2.add(new SpeedComponent(new Vector2(0, -20)));
                        newLaser2.add(new PositionComponent(new Vector2(pc.position.x - ArcadeSpaceShooter.laserGreen.getWidth() / 2.0f + 10, pc.position.y - 30)));
                        newLaser2.add(new DealsDamageComponent(20, DamageSystem.ENEMY_LASER));
                        this.getEngine().addEntity(newLaser2);

                        Entity newLaser3 = new Entity();
                        newLaser3.add(new RenderComponent(ArcadeSpaceShooter.laserGreen));
                        newLaser3.add(new LaserComponent(ArcadeSpaceShooter.explosionTextureGreen));
                        newLaser3.add(new SpeedComponent(new Vector2(10, -20)));
                        newLaser3.add(new PositionComponent(new Vector2(pc.position.x - ArcadeSpaceShooter.laserGreen.getWidth() / 2.0f - 10, pc.position.y - 30)));
                        newLaser3.add(new DealsDamageComponent(20, DamageSystem.ENEMY_LASER));
                        this.getEngine().addEntity(newLaser3);

                        Entity newLaser4 = new Entity();
                        newLaser4.add(new RenderComponent(ArcadeSpaceShooter.laserGreen));
                        newLaser4.add(new LaserComponent(ArcadeSpaceShooter.explosionTextureGreen));
                        newLaser4.add(new SpeedComponent(new Vector2(-10, -20)));
                        newLaser4.add(new PositionComponent(new Vector2(pc.position.x - ArcadeSpaceShooter.laserGreen.getWidth() / 2.0f + 10, pc.position.y - 30)));
                        newLaser4.add(new DealsDamageComponent(20, DamageSystem.ENEMY_LASER));
                        this.getEngine().addEntity(newLaser4);
                    }
                } else {
                    Entity newLaser = new Entity();
                    newLaser.add(new RenderComponent(ArcadeSpaceShooter.laserGreen));
                    newLaser.add(new LaserComponent(ArcadeSpaceShooter.explosionTextureGreen));
                    newLaser.add(new SpeedComponent(new Vector2(0, -20)));
                    newLaser.add(new PositionComponent(new Vector2(pc.position.x - ArcadeSpaceShooter.laserGreen.getWidth() / 2.0f, pc.position.y - 30)));
                    newLaser.add(new DealsDamageComponent(10, DamageSystem.ENEMY_LASER));
                    this.getEngine().addEntity(newLaser);
                }
            }

            float movement = pc.position.x - playerPosition.position.x;
            if (movement > 0)
            {
                sc.motion.x = (float)-ec.movementSpeed;
            }
            else
            {
                sc.motion.x = (float)ec.movementSpeed;
            }
        }
    }
}
