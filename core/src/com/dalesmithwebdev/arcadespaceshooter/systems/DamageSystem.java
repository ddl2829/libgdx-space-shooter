package com.dalesmithwebdev.arcadespaceshooter.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.*;
import com.dalesmithwebdev.arcadespaceshooter.screens.GameScreen;
import com.dalesmithwebdev.arcadespaceshooter.utility.ComponentMap;

public class DamageSystem extends EntitySystem {
    public static int PLAYER = 1;
    public static int METEOR = 2;
    public static int ENEMY = 4;
    public static int LASER = 8;
    public static int ENEMY_LASER = 16;

    public void update(float gameTime)
    {
        ImmutableArray<Entity> thingsThatDoDamage = ArcadeSpaceShooter.engine.getEntitiesFor(Family.all(DealsDamageComponent.class, PositionComponent.class, RenderComponent.class).get());
        ImmutableArray<Entity> thingsThatTakeDamage = ArcadeSpaceShooter.engine.getEntitiesFor(Family.all(TakesDamageComponent.class, PositionComponent.class, RenderComponent.class).get());

        for (Entity damageDealer : thingsThatDoDamage)
        {
            DealsDamageComponent ddc = ComponentMap.dealsDamageComponentComponentMapper.get(damageDealer);//(DealsDamageComponent)damageDealer.components[typeof(DealsDamageComponent)];
            PositionComponent dd_pc = ComponentMap.positionComponentComponentMapper.get(damageDealer);//(PositionComponent)damageDealer.components[typeof(PositionComponent)];
            RenderComponent dd_rc = ComponentMap.renderComponentComponentMapper.get(damageDealer);//(RenderComponent)damageDealer.components[typeof(RenderComponent)];

            Rectangle damageDealerRect = new Rectangle((int)dd_pc.position.x - (dd_rc.CurrentTexture().getWidth() / 2.0f), (int)dd_pc.position.y - (dd_rc.CurrentTexture().getHeight() / 2.0f), dd_rc.CurrentTexture().getWidth(), dd_rc.CurrentTexture().getHeight());

            for (Entity damageTaker : thingsThatTakeDamage)
            {
                TakesDamageComponent tdc = ComponentMap.takesDamageComponentComponentMapper.get(damageTaker);//(TakesDamageComponent)damageTaker.components[typeof(TakesDamageComponent)];

                //Bitwise AND the collision masks, a value > 0 means we have objects that can be compared
                if((tdc.takesDamageFromMask & ddc.damageTypeMask) == 0)
                {
                    continue;
                }

                PositionComponent td_pc = ComponentMap.positionComponentComponentMapper.get(damageTaker);//(PositionComponent)damageTaker.components[typeof(PositionComponent)];
                if(!ComponentMap.renderComponentComponentMapper.has(damageTaker))
                {
                    continue;
                }
                RenderComponent td_rc = ComponentMap.renderComponentComponentMapper.get(damageTaker);//(RenderComponent)damageTaker.components[typeof(RenderComponent)];
                Rectangle damageTakerRect = new Rectangle((int)td_pc.position.x - (td_rc.CurrentTexture().getWidth() / 2.0f), (int)td_pc.position.y - (td_rc.CurrentTexture().getHeight() / 2.0f), td_rc.CurrentTexture().getWidth(), td_rc.CurrentTexture().getHeight());
                if(damageDealerRect.overlaps(damageTakerRect))
                {
                    if(!ComponentMap.shieldedComponentComponentMapper.has(damageTaker))
                    {
                        tdc.health -= ddc.strength;
                    }

                    //Check if the damage dealer was a laser
                    if(ComponentMap.laserComponentComponentMapper.has(damageDealer))
                    {
                        LaserComponent dd_lc = ComponentMap.laserComponentComponentMapper.get(damageDealer);//(LaserComponent)damageDealer.components[typeof(LaserComponent)];

                        //Spawn an explosion at the location of collision
                        Entity explosion = new Entity();
                        explosion.add(new RenderComponent(dd_lc.explosionTexture));
                        explosion.add(new PositionComponent(dd_pc.position));
                        explosion.add(new ExplosionComponent());
                        ArcadeSpaceShooter.engine.addEntity(explosion);
                    }

                    ArcadeSpaceShooter.engine.removeEntity(damageDealer);

                    if (tdc.health <= 0)
                    {
                        if(ComponentMap.playerComponentComponentMapper.has(damageTaker))
                        {
                            PlayerComponent playerComp = ComponentMap.playerComponentComponentMapper.get(damageTaker);//(PlayerComponent)damageTaker.components[typeof(PlayerComponent)];
                            playerComp.lives -= 1;
                            damageTaker.remove(RenderComponent.class);
                        } else
                        {
                            ArcadeSpaceShooter.engine.removeEntity(damageTaker);
                        }

                        if(ComponentMap.laserComponentComponentMapper.has(damageDealer))
                        {
                            int credit = 1;
                            if(ComponentMap.meteorComponentComponentMapper.has(damageTaker))
                            {
                                MeteorComponent meteorComponent = ComponentMap.meteorComponentComponentMapper.get(damageTaker);//(MeteorComponent)damageTaker.components[typeof(MeteorComponent)];
                                if(meteorComponent.isBig)
                                {
                                    credit = 2;
                                }
                            }

                            if(ComponentMap.bossEnemyComponentComponentMapper.has(damageTaker)) {
                                BossEnemyComponent bossEnemyComponent = ComponentMap.bossEnemyComponentComponentMapper.get(damageTaker);
                                ArcadeSpaceShooter.kills += 5 * bossEnemyComponent.laserStrength;
                            } else {
                                ArcadeSpaceShooter.kills += 1;
                            }
                            double multiplier = 1 + Math.log(GameScreen.timeStayedAlive);
                            double score = (credit * multiplier) * 100;
                            ArcadeSpaceShooter.playerScore += score;

                            Entity e = new Entity();
                            e.add(new PositionComponent(td_pc.position));
                            e.add(new NotificationComponent("+" + Math.round(score), 200, false));
                            //Game1.instance.world.AddEntity(e);
                            ArcadeSpaceShooter.engine.addEntity(e);
                        }

                        //if (damageTaker.HasComponent(typeof(MeteorComponent)))
                        if(ComponentMap.meteorComponentComponentMapper.has(damageTaker))
                        {
                            //MeteorComponent meteorComponent = (MeteorComponent)damageTaker.components[typeof(MeteorComponent)];
                            MeteorComponent meteorComponent = ComponentMap.meteorComponentComponentMapper.get(damageTaker);
                            //Spawn small meteors when big ones break
                            if (meteorComponent.isBig)
                            {
                                int randAmt = this.randomInRange(2, 6);
                                for (int i = 0; i < randAmt; i++)
                                {
                                    Entity newMeteor = new Entity();
                                    newMeteor.add(new MeteorComponent(false));
                                    newMeteor.add(new TakesDamageComponent(5, LASER));
                                    newMeteor.add(new DealsDamageComponent(5, METEOR));
                                    newMeteor.add(new RenderComponent(ArcadeSpaceShooter.meteorSmall));
                                    newMeteor.add(new SpeedComponent(new Vector2(this.randomInRange(-3, 3), this.randomInRange(2, 5) * -1)));
                                    newMeteor.add(new PositionComponent(new Vector2(td_pc.position.x, td_pc.position.y)));
                                    ArcadeSpaceShooter.engine.addEntity(newMeteor);
                                }
                            }
                        }
                    }

                    break;
                }
            }
        }

    }

    private int randomInRange(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
