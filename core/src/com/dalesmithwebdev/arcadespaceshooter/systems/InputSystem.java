package com.dalesmithwebdev.arcadespaceshooter.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.*;
import com.dalesmithwebdev.arcadespaceshooter.prefabs.Player;
import com.dalesmithwebdev.arcadespaceshooter.screens.GameOverScreen;
import com.dalesmithwebdev.arcadespaceshooter.utility.ComponentMap;

public class InputSystem extends EntitySystem {
    public void update(float gameTime)
    {
        Entity player;
        ImmutableArray<Entity> playerEntities = this.getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());
        if (playerEntities.size() == 0)
        {
            player = new Player();
            this.getEngine().addEntity(player);
        }
        else {
            player = playerEntities.get(0);
        }

        HasLasersComponent hasLasersComponent = ComponentMap.hasLasersComponentComponentMapper.get(player);

        if(!ComponentMap.renderComponentComponentMapper.has(player))
        {
            ArcadeSpaceShooter.kills = 0;

            PlayerComponent ppc = ComponentMap.playerComponentComponentMapper.get(player);
            if(ppc.lives > 0) {
                hasLasersComponent.typeMask = HasLasersComponent.SINGLE;

                //Remove shield upgrade on respawn
                //player.RemoveComponent(typeof(HasShieldComponent));

                TakesDamageComponent ptdc = ComponentMap.takesDamageComponentComponentMapper.get(player);
                ptdc.health = ptdc.maxHealth;

                RenderComponent prc = new RenderComponent(ArcadeSpaceShooter.shipTextures.toArray(new TextureRegion[ArcadeSpaceShooter.shipTextures.size()]), RenderComponent.PLANE_MAIN);
                player.add(prc);
                if (!ComponentMap.positionComponentComponentMapper.has(player)) {
                    player.add(new PositionComponent(new Vector2(
                            (ArcadeSpaceShooter.screenRect.width / 2),
                            prc.height + 20
                    )));
                } else {
                    PositionComponent playerPositionComp = ComponentMap.positionComponentComponentMapper.get(player);
                    playerPositionComp.position = new Vector2(
                            (ArcadeSpaceShooter.screenRect.width / 2),
                            prc.height + 20
                    );
                }
            }
        }

        PlayerComponent pc = ComponentMap.playerComponentComponentMapper.get(player);
        SpeedComponent sc = ComponentMap.speedComponentComponentMapper.get(player);
        RenderComponent rc = ComponentMap.renderComponentComponentMapper.get(player);

        if(pc.lives == 0)
        {
            rc.visible = false;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    ArcadeSpaceShooter.PopScreen();
                    ArcadeSpaceShooter.PushScreen(new GameOverScreen());
                }
            }, 3);

            return;
        }

        sc.motion = new Vector2(0, 0);

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

        if(ComponentMap.hasMissilesComponentComponentMapper.has(player)) {
            HasMissilesComponent hasMissilesComponent = ComponentMap.hasMissilesComponentComponentMapper.get(player);
            hasMissilesComponent.timeSinceLastShot += gameTime;
            if(Gdx.input.isKeyPressed(Input.Keys.X)) {
                FireMissile(player);
            }
        }

        if(ComponentMap.hasBombsComponentComponentMapper.has(player)) {
            HasBombsComponent hasBombsComponent = ComponentMap.hasBombsComponentComponentMapper.get(player);
            hasBombsComponent.timeSinceLastShot += gameTime;
            if(Gdx.input.isKeyPressed(Input.Keys.Z)) {
                FireBomb(player);
            }
        }

        if(ComponentMap.hasEmpComponentComponentMapper.has(player)) {
            HasEmpComponent hasEmpComponent = ComponentMap.hasEmpComponentComponentMapper.get(player);
            hasEmpComponent.timeSinceLastShot += gameTime;
            if(Gdx.input.isKeyPressed(Input.Keys.C)) {
                FireEmp(player);
            }
        }

        hasLasersComponent.timeSinceLastShot += gameTime;

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE))
        {
            if(ComponentMap.hasLasersComponentComponentMapper.has(player)) {
                Shoot(player);
            }
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

    private void FireMissile(Entity player) {
        HasMissilesComponent hasMissilesComponent = ComponentMap.hasMissilesComponentComponentMapper.get(player);
        if(hasMissilesComponent.timeSinceLastShot > hasMissilesComponent.shotInterval) {
            hasMissilesComponent.timeSinceLastShot = 0;
            PositionComponent posc = ComponentMap.positionComponentComponentMapper.get(player);
            Entity missile1 = new Entity();
            missile1.add(new RenderComponent(ArcadeSpaceShooter.textures.findRegion("spaceMissiles", 9), RenderComponent.PLANE_ABOVE));
            missile1.add(new PositionComponent(posc.position.x - 60, posc.position.y));
            missile1.add(new SpeedComponent(-3, 1));
            missile1.add(new DealsDamageComponent(8, DamageSystem.MISSILE));
            missile1.add(new MissileComponent());
            getEngine().addEntity(missile1);

            Entity missile2 = new Entity();
            missile2.add(new RenderComponent(ArcadeSpaceShooter.textures.findRegion("spaceMissiles", 9), RenderComponent.PLANE_ABOVE));
            missile2.add(new PositionComponent(posc.position.x + 60, posc.position.y));
            missile2.add(new SpeedComponent(3, 1));
            missile2.add(new DealsDamageComponent(5, DamageSystem.MISSILE));
            missile2.add(new MissileComponent());
            getEngine().addEntity(missile2);
        }
    }

    private void FireBomb(Entity player) {
        HasBombsComponent hasBombsComponent = ComponentMap.hasBombsComponentComponentMapper.get(player);
        if(hasBombsComponent.timeSinceLastShot > hasBombsComponent.shotInterval) {
            hasBombsComponent.timeSinceLastShot = 0;

            PositionComponent posc = ComponentMap.positionComponentComponentMapper.get(player);
            Entity missile1 = new Entity();
            missile1.add(new RenderComponent(ArcadeSpaceShooter.textures.findRegion("spaceMissiles", 12), RenderComponent.PLANE_ABOVE));
            missile1.add(new PositionComponent(posc.position.x, posc.position.y + 50));
            missile1.add(new SpeedComponent(0, 5));
            missile1.add(new DealsDamageComponent(10, DamageSystem.BOMB));
            missile1.add(new BombComponent());
            getEngine().addEntity(missile1);
        }
    }

    private void FireEmp(Entity player) {
        HasEmpComponent hasEmpComponent = ComponentMap.hasEmpComponentComponentMapper.get(player);
        if(hasEmpComponent.timeSinceLastShot > hasEmpComponent.shotInterval) {
            hasEmpComponent.timeSinceLastShot = 0;
            ArcadeSpaceShooter.empActive = true;
            System.out.println("Emp Active");
        }
    }

    private void Shoot(Entity player)
    {
        PositionComponent posc = ComponentMap.positionComponentComponentMapper.get(player);
        HasLasersComponent hasLasersComponent = ComponentMap.hasLasersComponentComponentMapper.get(player);
        //int fireTime = pc.laserLevel == 0 ? 150 : pc.laserLevel == 1 ? 100 : 80;

        if (hasLasersComponent.timeSinceLastShot > hasLasersComponent.shotInterval)
        {
            hasLasersComponent.timeSinceLastShot = 0;

            TextureRegion laser = ArcadeSpaceShooter.textures.findRegion("laserRed");
            TextureRegion explosion = ArcadeSpaceShooter.textures.findRegion("laserRedShot");
            int laserDamage = 1;
            if ((hasLasersComponent.typeMask & HasLasersComponent.UPGRADED) > 0)
            {
                laser = ArcadeSpaceShooter.textures.findRegion("laserGreen");
                laserDamage = 2;
                explosion = ArcadeSpaceShooter.textures.findRegion("laserGreenShot");
            }
            if ((hasLasersComponent.typeMask & HasLasersComponent.UPGRADED_AGAIN) > 0)
            {
                laser = ArcadeSpaceShooter.textures.findRegion("laserBlue12");
                laserDamage = 3;
                explosion = ArcadeSpaceShooter.textures.findRegion("laserBlue08");
            }

            if((hasLasersComponent.typeMask & HasLasersComponent.SINGLE) > 0) {
                Entity newLaser = new Entity();
                newLaser.add(new RenderComponent(laser, RenderComponent.PLANE_ABOVE));
                newLaser.add(new LaserComponent(explosion));
                newLaser.add(new SpeedComponent(0, 20));
                newLaser.add(new PositionComponent(new Vector2(posc.position.x, posc.position.y + laser.getRegionHeight() / 2.0f + 40)));
                newLaser.add(new DealsDamageComponent(laserDamage, DamageSystem.LASER));
                this.getEngine().addEntity(newLaser);
            }

            if((hasLasersComponent.typeMask & HasLasersComponent.DUAL) > 0) {
                Entity newLaser1 = new Entity();
                newLaser1.add(new RenderComponent(laser, RenderComponent.PLANE_ABOVE));
                newLaser1.add(new LaserComponent(explosion));
                newLaser1.add(new SpeedComponent(0, 20));
                newLaser1.add(new PositionComponent(new Vector2(posc.position.x - 10, posc.position.y + laser.getRegionHeight() / 2.0f + 40)));
                newLaser1.add(new DealsDamageComponent(laserDamage, DamageSystem.LASER));
                this.getEngine().addEntity(newLaser1);

                Entity newLaser2 = new Entity();
                newLaser2.add(new RenderComponent(laser, RenderComponent.PLANE_ABOVE));
                newLaser2.add(new LaserComponent(explosion));
                newLaser2.add(new SpeedComponent(0, 20));
                newLaser2.add(new PositionComponent(new Vector2(posc.position.x + 10, posc.position.y + laser.getRegionHeight() / 2.0f + 40)));
                newLaser2.add(new DealsDamageComponent(laserDamage, DamageSystem.LASER));
                this.getEngine().addEntity(newLaser2);
            }

            if((hasLasersComponent.typeMask & HasLasersComponent.DIAGONAL) > 0) {
                Entity newLaser1 = new Entity();
                newLaser1.add(new RenderComponent(laser, RenderComponent.PLANE_ABOVE));
                newLaser1.add(new LaserComponent(explosion));
                newLaser1.add(new SpeedComponent(-10, 20));
                newLaser1.add(new PositionComponent(new Vector2(posc.position.x - 10, posc.position.y + laser.getRegionHeight() / 2.0f + 40)));
                newLaser1.add(new DealsDamageComponent( laserDamage, DamageSystem.LASER));
                this.getEngine().addEntity(newLaser1);

                Entity newLaser2 = new Entity();
                newLaser2.add(new RenderComponent(laser, RenderComponent.PLANE_ABOVE));
                newLaser2.add(new LaserComponent(explosion));
                newLaser2.add(new SpeedComponent(10, 20));
                newLaser2.add(new PositionComponent(new Vector2(posc.position.x + 10, posc.position.y + laser.getRegionHeight() / 2.0f + 40)));
                newLaser2.add(new DealsDamageComponent(laserDamage, DamageSystem.LASER));
                this.getEngine().addEntity(newLaser2);
            }
        }
    }
}
