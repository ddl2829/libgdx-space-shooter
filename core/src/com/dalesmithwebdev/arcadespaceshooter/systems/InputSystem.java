package com.dalesmithwebdev.arcadespaceshooter.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.*;
import com.dalesmithwebdev.arcadespaceshooter.screens.GameOverScreen;
import com.dalesmithwebdev.arcadespaceshooter.utility.ComponentMap;

public class InputSystem extends EntitySystem {
    //KeyboardState keyboardState;

    public void update(float gameTime)
    {
        //keyboardState = Keyboard.GetState();

        Entity player;
        ImmutableArray<Entity> playerEntities = ArcadeSpaceShooter.engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
        if (playerEntities.size() == 0)
        {
            player = new Entity();
            PlayerComponent ppc = new PlayerComponent();
            player.add(ppc);
            player.add(new HasShieldComponent());
            player.add(new SpeedComponent(new Vector2(0, 0)));
            player.add(new TakesDamageComponent(50, DamageSystem.ENEMY ^ DamageSystem.ENEMY_LASER ^ DamageSystem.METEOR));
            ArcadeSpaceShooter.engine.addEntity(player);

            ppc.lives = ppc.maxLives;
        }
        else {
            player = playerEntities.get(0);
        }

        if(!ComponentMap.renderComponentComponentMapper.has(player))
        {
            ArcadeSpaceShooter.kills = 0;

            PlayerComponent ppc = ComponentMap.playerComponentComponentMapper.get(player);//(PlayerComponent)player.components[typeof(PlayerComponent)];

            ppc.timeSinceRespawn = 0;
            ppc.laserLevel = 0;

            //Remove shield upgrade on respawn
            //player.RemoveComponent(typeof(HasShieldComponent));

            TakesDamageComponent ptdc = ComponentMap.takesDamageComponentComponentMapper.get(player);//(TakesDamageComponent)player.components[typeof(TakesDamageComponent)];
            ptdc.health = ptdc.maxHealth;

            RenderComponent prc = new RenderComponent(ArcadeSpaceShooter.shipTextures.toArray(new Texture[ArcadeSpaceShooter.shipTextures.size()]));
            player.add(prc);
            if(!ComponentMap.positionComponentComponentMapper.has(player))
            {
                player.add(new PositionComponent(new Vector2(
                        (ArcadeSpaceShooter.screenRect.width / 2) - (prc.CurrentTexture().getWidth() / 2.0f),
                        //(ArcadeSpaceShooter.screenRect.height / 3) * 2 + (prc.CurrentTexture().getHeight() / 2.0f)
                        prc.CurrentTexture().getHeight() + 20
                )));
            }
            else
            {
                PositionComponent playerPositionComp = ComponentMap.positionComponentComponentMapper.get(player);//(PositionComponent)player.components[typeof(PositionComponent)];
                playerPositionComp.position = new Vector2(
                        (ArcadeSpaceShooter.screenRect.width / 2) - (prc.CurrentTexture().getWidth() / 2.0f),
                        //(ArcadeSpaceShooter.screenRect.height / 3) * 2 + (prc.CurrentTexture().getHeight() / 2.0f)
                        prc.CurrentTexture().getHeight() + 20
                );
            }
        }

        PlayerComponent pc = ComponentMap.playerComponentComponentMapper.get(player);//(PlayerComponent)player.components[typeof(PlayerComponent)];
        SpeedComponent sc = ComponentMap.speedComponentComponentMapper.get(player);//(SpeedComponent)player.components[typeof(SpeedComponent)];
        RenderComponent rc = ComponentMap.renderComponentComponentMapper.get(player);//(RenderComponent)player.components[typeof(RenderComponent)];

        if(pc.lives == 0)
        {
            ArcadeSpaceShooter.PopScreen();
            ArcadeSpaceShooter.PushScreen(new GameOverScreen());
            return;
        }

        sc.motion = new Vector2(0, 0);
        pc.lastFireTime += gameTime;

        if(ComponentMap.hasShieldComponentComponentMapper.has(player))
        {
            HasShieldComponent hasShieldComp = ComponentMap.hasShieldComponentComponentMapper.get(player);//(HasShieldComponent)player.components[typeof(HasShieldComponent)];

            if(!ComponentMap.shieldedComponentComponentMapper.has(player) && hasShieldComp.shieldPower < hasShieldComp.maxShieldPower)
            {
                hasShieldComp.shieldPower += hasShieldComp.shieldRegenRate * gameTime;//.ElapsedGameTime.Milliseconds;
            }

            if (hasShieldComp.shieldPower >= hasShieldComp.maxShieldPower)
            {
                hasShieldComp.shieldPower = hasShieldComp.maxShieldPower;
                hasShieldComp.shieldCooldown = false;
            }
            if(ComponentMap.shieldedComponentComponentMapper.has(player))
            {
                hasShieldComp.shieldPower -= hasShieldComp.shieldDepleteRate * gameTime;//.ElapsedGameTime.Milliseconds;

                if (hasShieldComp.shieldPower <= 0)
                {
                    player.remove(ShieldedComponent.class);
                    hasShieldComp.shieldCooldown = true;
                    hasShieldComp.shieldPower = 0;
                }
            }

            if (!hasShieldComp.shieldCooldown)
            {
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && hasShieldComp.shieldPower >= 0)
                {
                    if(!ComponentMap.shieldedComponentComponentMapper.has(player))
                    {
                        player.add(new ShieldedComponent());
                    }
                }
                else
                {
                    player.remove(ShieldedComponent.class);
                }
            }
        }

        boolean upgradedLasers = false;
        if (ArcadeSpaceShooter.kills > 10 && pc.laserLevel == 0)
        {
            pc.laserLevel = 1;
            upgradedLasers = true;
        }
        if (ArcadeSpaceShooter.kills > 20 && pc.laserLevel == 1)
        {
            pc.laserLevel = 2;
            upgradedLasers = true;
        }
        if (ArcadeSpaceShooter.kills > 60 && pc.laserLevel == 2)
        {
            pc.laserLevel = 3;
            upgradedLasers = true;
        }

        if (upgradedLasers)
        {
            Entity e = new Entity();
            e.add(new NotificationComponent("Lasers Improved", 2000, true));
            ArcadeSpaceShooter.engine.addEntity(e);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && !ComponentMap.shieldedComponentComponentMapper.has(player))
        {
            Shoot(player);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
        {
            rc.currentTexture = 1;
            sc.motion.x = -1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
        {
            rc.currentTexture = 2;
            sc.motion.x = 1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP))
        {
            sc.motion.y = 1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
        {
            sc.motion.y = -1;
        }
        if(!Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT) && rc != null)
        {
            rc.currentTexture = 0;
        }

        sc.motion.scl(5);// *= 5;
    }

    private void Shoot(Entity player)
    {
        PlayerComponent pc = ComponentMap.playerComponentComponentMapper.get(player);//(PlayerComponent)player.components[typeof(PlayerComponent)];
        PositionComponent posc = ComponentMap.positionComponentComponentMapper.get(player);//(PositionComponent)player.components[typeof(PositionComponent)];

        if (pc.lastFireTime > 100)
        {
            Texture laser = ArcadeSpaceShooter.laserRed;
            if (pc.laserLevel >= 1)
            {
                laser = ArcadeSpaceShooter.laserGreen;
            }

            Texture expTexToUse = pc.laserLevel == 0 ? ArcadeSpaceShooter.explosionTexture : ArcadeSpaceShooter.explosionTextureGreen;

            if (pc.laserLevel < 2)
            {
                Entity newLaser = new Entity();
                newLaser.add(new RenderComponent(laser));
                newLaser.add(new LaserComponent(expTexToUse));
                newLaser.add(new SpeedComponent(new Vector2(0, 20)));
                newLaser.add(new PositionComponent(new Vector2(posc.position.x, posc.position.y + laser.getHeight() / 2.0f + 40)));
                newLaser.add(new DealsDamageComponent(pc.laserLevel + 1, DamageSystem.LASER));
                ArcadeSpaceShooter.engine.addEntity(newLaser);
            }
            if (pc.laserLevel >= 2)
            {
                Entity newLaser1 = new Entity();
                newLaser1.add(new RenderComponent(laser));
                newLaser1.add(new LaserComponent(expTexToUse));
                newLaser1.add(new SpeedComponent(new Vector2(0, 20)));
                newLaser1.add(new PositionComponent(new Vector2(posc.position.x - 10, posc.position.y + laser.getHeight() / 2.0f + 40)));
                newLaser1.add(new DealsDamageComponent(pc.laserLevel + 1, DamageSystem.LASER));
                ArcadeSpaceShooter.engine.addEntity(newLaser1);

                Entity newLaser2 = new Entity();
                newLaser2.add(new RenderComponent(laser));
                newLaser2.add(new LaserComponent(expTexToUse));
                newLaser2.add(new SpeedComponent(new Vector2(0, 20)));
                newLaser2.add(new PositionComponent(new Vector2(posc.position.x + 10, posc.position.y + laser.getHeight() / 2.0f + 40)));
                newLaser2.add(new DealsDamageComponent(pc.laserLevel + 1, DamageSystem.LASER));
                ArcadeSpaceShooter.engine.addEntity(newLaser2);
            }
            if (pc.laserLevel >= 3)
            {
                Entity newLaser1 = new Entity();
                newLaser1.add(new RenderComponent(laser));
                newLaser1.add(new LaserComponent(expTexToUse));
                newLaser1.add(new SpeedComponent(new Vector2(-10, 20)));
                newLaser1.add(new PositionComponent(new Vector2(posc.position.x - 10, posc.position.y + laser.getHeight() / 2.0f + 40)));
                newLaser1.add(new DealsDamageComponent(pc.laserLevel + 1, DamageSystem.LASER));
                ArcadeSpaceShooter.engine.addEntity(newLaser1);

                Entity newLaser2 = new Entity();
                newLaser2.add(new RenderComponent(laser));
                newLaser2.add(new LaserComponent(expTexToUse));
                newLaser2.add(new SpeedComponent(new Vector2(10, 20)));
                newLaser2.add(new PositionComponent(new Vector2(posc.position.x + 10, posc.position.y + laser.getHeight() / 2.0f + 40)));
                newLaser2.add(new DealsDamageComponent(pc.laserLevel + 1, DamageSystem.LASER));
                ArcadeSpaceShooter.engine.addEntity(newLaser2);
            }
            pc.lastFireTime = 0;
        }
    }
}
