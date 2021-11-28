package com.dalesmithwebdev.arcadespaceshooter.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.*;
import com.dalesmithwebdev.arcadespaceshooter.prefabs.*;
import com.dalesmithwebdev.arcadespaceshooter.screens.GameScreen;
import com.dalesmithwebdev.arcadespaceshooter.utility.ComponentMap;
import com.dalesmithwebdev.arcadespaceshooter.utility.Rand;

public class DamageSystem extends EntitySystem {
    public static int PLAYER = 1;
    public static int METEOR = 2;
    public static int ENEMY = 4;
    public static int LASER = 8;
    public static int ENEMY_LASER = 16;

    public void update(float gameTime)
    {
        ImmutableArray<Entity> thingsThatDoDamage = this.getEngine().getEntitiesFor(Family.all(DealsDamageComponent.class, PositionComponent.class, RenderComponent.class).get());
        ImmutableArray<Entity> thingsThatTakeDamage = this.getEngine().getEntitiesFor(Family.all(TakesDamageComponent.class, PositionComponent.class, RenderComponent.class).get());

        for (Entity damageDealer : thingsThatDoDamage)
        {
            DealsDamageComponent ddc = ComponentMap.dealsDamageComponentComponentMapper.get(damageDealer);
            PositionComponent dd_pc = ComponentMap.positionComponentComponentMapper.get(damageDealer);
            RenderComponent dd_rc = ComponentMap.renderComponentComponentMapper.get(damageDealer);

            Rectangle damageDealerRect = new Rectangle((int)dd_pc.position.x - (dd_rc.CurrentTexture().getWidth() / 2.0f), (int)dd_pc.position.y - (dd_rc.CurrentTexture().getHeight() / 2.0f), dd_rc.CurrentTexture().getWidth(), dd_rc.CurrentTexture().getHeight());

            for (Entity damageTaker : thingsThatTakeDamage)
            {
                TakesDamageComponent tdc = ComponentMap.takesDamageComponentComponentMapper.get(damageTaker);

                //Bitwise AND the collision masks, a value > 0 means we have objects that can be compared
                if((tdc.takesDamageFromMask & ddc.damageTypeMask) == 0)
                {
                    continue;
                }

                PositionComponent td_pc = ComponentMap.positionComponentComponentMapper.get(damageTaker);
                if(!ComponentMap.renderComponentComponentMapper.has(damageTaker))
                {
                    continue;
                }
                RenderComponent td_rc = ComponentMap.renderComponentComponentMapper.get(damageTaker);
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
                        LaserComponent dd_lc = ComponentMap.laserComponentComponentMapper.get(damageDealer);

                        //Spawn an explosion at the location of collision
                        Entity explosion = new Entity();
                        explosion.add(new RenderComponent(dd_lc.explosionTexture));
                        explosion.add(new PositionComponent(dd_pc.position));
                        explosion.add(new ExplosionComponent());
                        this.getEngine().addEntity(explosion);
                    }

                    if(!ComponentMap.playerComponentComponentMapper.has(damageDealer)) {
                        this.getEngine().removeEntity(damageDealer);
                    }

                    if (tdc.health <= 0)
                    {
                        if(ComponentMap.laserUpgradeComponentComponentMapper.has(damageTaker)) {
                            ImmutableArray<Entity> players = getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());
                            Entity player = players.first();
                            HasLasersComponent lasersComponent = ComponentMap.hasLasersComponentComponentMapper.get(player);
                            if((lasersComponent.typeMask & HasLasersComponent.UPGRADED) > 0) {
                                // already level 1
                                lasersComponent.typeMask = lasersComponent.typeMask ^ HasLasersComponent.UPGRADED;
                                lasersComponent.typeMask = lasersComponent.typeMask ^ HasLasersComponent.UPGRADED_AGAIN;
                                lasersComponent.shotInterval = 80;
                                Entity e = new Entity();
                                e.add(new NotificationComponent("Laser Upgraded!", 3000, true));
                                this.getEngine().addEntity(e);
                            } else if((lasersComponent.typeMask & HasLasersComponent.UPGRADED_AGAIN) > 0) {
                                // already level 2, give bonus points
                                ArcadeSpaceShooter.playerScore += 1000;

                                Entity e = new Entity();
                                e.add(new PositionComponent(td_pc.position));
                                e.add(new NotificationComponent("+" + 1000, 200, false));
                                this.getEngine().addEntity(e);
                            } else {
                                // level 1
                                lasersComponent.typeMask = lasersComponent.typeMask ^ HasLasersComponent.UPGRADED;
                                lasersComponent.shotInterval = 100;
                                Entity e = new Entity();
                                e.add(new NotificationComponent("Laser Upgraded!", 3000, true));
                                this.getEngine().addEntity(e);
                            }
                        }
                        if(ComponentMap.dualLaserUpgradeComponentComponentMapper.has(damageTaker)) {
                            ImmutableArray<Entity> players = getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());
                            Entity player = players.first();
                            HasLasersComponent lasersComponent = ComponentMap.hasLasersComponentComponentMapper.get(player);
                            if((lasersComponent.typeMask & HasLasersComponent.SINGLE) > 0) {
                                lasersComponent.typeMask = lasersComponent.typeMask ^ HasLasersComponent.SINGLE;
                                lasersComponent.typeMask = lasersComponent.typeMask ^ HasLasersComponent.DUAL;
                            } else {
                                ArcadeSpaceShooter.playerScore += 1000;

                                Entity e = new Entity();
                                e.add(new PositionComponent(td_pc.position));
                                e.add(new NotificationComponent("+" + 1000, 200, false));
                                this.getEngine().addEntity(e);
                            }
                        }
                        if(ComponentMap.diagonalLaserUpgradeComponentComponentMapper.has(damageTaker)) {
                            ImmutableArray<Entity> players = getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());
                            Entity player = players.first();
                            HasLasersComponent lasersComponent = ComponentMap.hasLasersComponentComponentMapper.get(player);
                            if((lasersComponent.typeMask & HasLasersComponent.DIAGONAL) > 0) {
                                ArcadeSpaceShooter.playerScore += 1000;

                                Entity e = new Entity();
                                e.add(new PositionComponent(td_pc.position));
                                e.add(new NotificationComponent("+" + 1000, 200, false));
                                this.getEngine().addEntity(e);
                            } else {
                                lasersComponent.typeMask = lasersComponent.typeMask ^ HasLasersComponent.DIAGONAL;
                            }
                        }
                        if(ComponentMap.bombUpgradeComponentComponentMapper.has(damageTaker)) {
                            ImmutableArray<Entity> players = getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());
                            Entity player = players.first();
                            if(ComponentMap.hasBombsComponentComponentMapper.has(player)) {
                                ArcadeSpaceShooter.playerScore += 1000;

                                Entity e = new Entity();
                                e.add(new PositionComponent(td_pc.position));
                                e.add(new NotificationComponent("+" + 1000, 200, false));
                                this.getEngine().addEntity(e);
                            } else {
                                player.add(new HasBombsComponent());
                                Timer.schedule(new Timer.Task() {
                                    @Override
                                    public void run() {
                                        Entity e = new Entity();
                                        e.add(new NotificationComponent("Press Z to launch a bomb", 3000, true));
                                        getEngine().addEntity(e);
                                    }
                                }, 0);
                            }
                        }
                        if(ComponentMap.shieldUpgradeComponentComponentMapper.has(damageTaker)) {
                            ImmutableArray<Entity> players = getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());
                            Entity player = players.first();
                            if(ComponentMap.hasShieldComponentComponentMapper.has(player)) {
                                ArcadeSpaceShooter.playerScore += 1000;

                                Entity e = new Entity();
                                e.add(new PositionComponent(td_pc.position));
                                e.add(new NotificationComponent("+" + 1000, 200, false));
                                this.getEngine().addEntity(e);
                            } else {
                                player.add(new HasShieldComponent());
                                Timer.schedule(new Timer.Task() {
                                    @Override
                                    public void run() {
                                        Entity e = new Entity();
                                        e.add(new NotificationComponent("Hold left shift to shield", 3000, true));
                                        getEngine().addEntity(e);
                                    }
                                }, 0);
                            }
                        }
                        if(ComponentMap.missileUpgradeComponentComponentMapper.has(damageTaker)) {
                            ImmutableArray<Entity> players = getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());
                            Entity player = players.first();
                            if(ComponentMap.hasMissilesComponentComponentMapper.has(player)) {
                                ArcadeSpaceShooter.playerScore += 1000;

                                Entity e = new Entity();
                                e.add(new PositionComponent(td_pc.position));
                                e.add(new NotificationComponent("+" + 1000, 200, false));
                                this.getEngine().addEntity(e);
                            } else {
                                player.add(new HasMissilesComponent());
                                Timer.schedule(new Timer.Task() {
                                    @Override
                                    public void run() {
                                        Entity e = new Entity();
                                        e.add(new NotificationComponent("Press X to fire missiles", 3000, true));
                                        getEngine().addEntity(e);
                                    }
                                }, 0);
                            }
                        }
                        if(ComponentMap.empUpgradeComponentComponentMapper.has(damageTaker)) {
                            ImmutableArray<Entity> players = getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());
                            Entity player = players.first();
                            if(ComponentMap.hasEmpComponentComponentMapper.has(player)) {
                                ArcadeSpaceShooter.playerScore += 1000;

                                Entity e = new Entity();
                                e.add(new PositionComponent(td_pc.position));
                                e.add(new NotificationComponent("+" + 1000, 200, false));
                                this.getEngine().addEntity(e);
                            } else {
                                player.add(new HasEmpComponent());
                                Timer.schedule(new Timer.Task() {
                                    @Override
                                    public void run() {
                                        Entity e = new Entity();
                                        e.add(new NotificationComponent("Press C to blast EMP", 3000, true));
                                        getEngine().addEntity(e);
                                    }
                                }, 0);
                            }
                        }


                        if(ComponentMap.playerComponentComponentMapper.has(damageTaker))
                        {
                            PlayerComponent playerComp = ComponentMap.playerComponentComponentMapper.get(damageTaker);
                            playerComp.lives -= 1;
                            damageTaker.remove(RenderComponent.class);
                        } else
                        {
                            this.getEngine().removeEntity(damageTaker);
                        }

                        if(ComponentMap.laserComponentComponentMapper.has(damageDealer))
                        {
                            int credit = 1;
                            if(ComponentMap.meteorComponentComponentMapper.has(damageTaker))
                            {
                                MeteorComponent meteorComponent = ComponentMap.meteorComponentComponentMapper.get(damageTaker);
                                if(meteorComponent.isBig)
                                {
                                    credit = 2;
                                }
                            }

                            if(ComponentMap.bossEnemyComponentComponentMapper.has(damageTaker)) {
                                ArcadeSpaceShooter.kills += 10;
                            } else {
                                ArcadeSpaceShooter.kills += 1;
                            }
                            double multiplier = 1 + Math.log(GameScreen.timeStayedAlive);
                            double score = (credit * multiplier) * 100;
                            ArcadeSpaceShooter.playerScore += score;

                            Entity e = new Entity();
                            e.add(new PositionComponent(td_pc.position));
                            e.add(new NotificationComponent("+" + Math.round(score), 200, false));
                            this.getEngine().addEntity(e);

                            // random chance for a upgrade drop
                            if(Rand.nextInt(100) < 5) {
                                ImmutableArray<Entity> players = getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());
                                Entity player = players.first();

                                float nextUpgrade = Rand.nextFloat() * 100;
                                if(nextUpgrade < 30) {
                                    HasLasersComponent lasersComponent = ComponentMap.hasLasersComponentComponentMapper.get(player);
                                    if ((lasersComponent.typeMask & HasLasersComponent.UPGRADED) > 0) {
                                        //spawn second upgrade
                                        Entity upgradeOne = new LaserStrengthUpgrade(2, td_pc.position.x, td_pc.position.y);
                                        this.getEngine().addEntity(upgradeOne);
                                    } else {
                                        // spawn first upgrade
                                        Entity upgradeOne = new LaserStrengthUpgrade(1, td_pc.position.x, td_pc.position.y);
                                        this.getEngine().addEntity(upgradeOne);
                                    }
                                } else if(nextUpgrade >= 30 && nextUpgrade < 40) {
                                    // dual lasers
                                    Entity upgradeOne = new DualLaserUpgrade(td_pc.position.x, td_pc.position.y);
                                    this.getEngine().addEntity(upgradeOne);
                                } else if(nextUpgrade >= 40 && nextUpgrade < 43) {
                                    // diagonal lasers
                                    Entity upgradeOne = new DiagonalLaserUpgrade(td_pc.position.x, td_pc.position.y);
                                    this.getEngine().addEntity(upgradeOne);
                                } else if(nextUpgrade >= 50 && nextUpgrade < 70) {
                                    // shields
                                    Entity upgradeOne = new ShieldUpgrade(td_pc.position.x, td_pc.position.y);
                                    this.getEngine().addEntity(upgradeOne);
                                } else if(nextUpgrade >= 70 && nextUpgrade < 80) {
                                    // bombs
                                    Entity upgradeOne = new BombUpgrade(td_pc.position.x, td_pc.position.y);
                                    this.getEngine().addEntity(upgradeOne);
                                } else if(nextUpgrade >= 80 && nextUpgrade < 85) {
                                    // missiles
                                    Entity upgradeOne = new MissileUpgrade(td_pc.position.x, td_pc.position.y);
                                    this.getEngine().addEntity(upgradeOne);
                                } else if(nextUpgrade >= 90 && nextUpgrade < 91) {
                                    // emp
                                    Entity upgradeOne = new EmpUpgrade(td_pc.position.x, td_pc.position.y);
                                    this.getEngine().addEntity(upgradeOne);
                                }
                            }
                        }

                        if(ComponentMap.meteorComponentComponentMapper.has(damageTaker))
                        {
                            MeteorComponent meteorComponent = ComponentMap.meteorComponentComponentMapper.get(damageTaker);
                            //Spawn small meteors when big ones break
                            if (meteorComponent.isBig)
                            {
                                int randAmt = this.randomInRange(2, 6);
                                for (int i = 0; i < randAmt; i++)
                                {
                                    Entity newMeteor = new Entity();
                                    newMeteor.add(new MeteorComponent(false));
                                    newMeteor.add(new TakesDamageComponent(2, LASER));
                                    newMeteor.add(new DealsDamageComponent(5, METEOR));
                                    newMeteor.add(new RenderComponent(ArcadeSpaceShooter.meteorSmall));
                                    newMeteor.add(new SpeedComponent(this.randomInRange(-3, 3), this.randomInRange(2, 5) * -1));
                                    newMeteor.add(new PositionComponent(new Vector2(td_pc.position.x, td_pc.position.y)));
                                    this.getEngine().addEntity(newMeteor);
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
