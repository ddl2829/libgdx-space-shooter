package com.dalesmithwebdev.galaxia.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.dalesmithwebdev.galaxia.constants.DamageTypeConstants;
import com.dalesmithwebdev.galaxia.constants.GameConstants;
import com.dalesmithwebdev.galaxia.constants.WeaponConstants;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.prefabs.Player;
import com.dalesmithwebdev.galaxia.screens.GameOverScreen;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;
import com.dalesmithwebdev.galaxia.utility.SoundManager;

public class InputSystem extends EntitySystem {
    private float respawnTimer = 0;
    private boolean waitingToRespawn = false;
    private static final float RESPAWN_DELAY = 2000; // 2 seconds in milliseconds

    public void update(float gameTime)
    {
        if(ArcadeSpaceShooter.paused) {
            return;
        }

        // Handle respawn timer
        if(waitingToRespawn) {
            respawnTimer += gameTime;
            if(respawnTimer < RESPAWN_DELAY) {
                return; // Still waiting to respawn
            }
            // Timer complete, continue to respawn code
        }

        Entity player;
        ImmutableArray<Entity> playerEntities = this.getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());
        if (playerEntities.size() == 0)
        {
            // Don't create a new player if game over is scheduled
            if(ArcadeSpaceShooter.gameOverScheduled) {
                return;
            }
            player = new Player();
            this.getEngine().addEntity(player);
        }
        else {
            player = playerEntities.get(0);
        }

        HasLasersComponent hasLasersComponent = ComponentMap.hasLasersMapper.get(player);

        if(!ComponentMap.renderMapper.has(player))
        {
            PlayerComponent ppc = ComponentMap.playerMapper.get(player);

            // Check if player is out of lives
            if(ppc.lives <= 0) {
                if(ArcadeSpaceShooter.gameOverScheduled) {
                    return;
                }
                // Remove player entity immediately to prevent any respawn
                this.getEngine().removeEntity(player);

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        ArcadeSpaceShooter.instance.setScreen(new GameOverScreen());
                    }
                }, 3);
                ArcadeSpaceShooter.gameOverScheduled = true;
                return;
            }

            // Check if this is initial spawn (no PositionComponent) or respawn (has PositionComponent)
            boolean isInitialSpawn = !ComponentMap.positionMapper.has(player);

            // Player has lives remaining - start respawn timer (but not for initial spawn)
            if(!isInitialSpawn && !waitingToRespawn) {
                waitingToRespawn = true;
                respawnTimer = 0;
                return;
            }

            // Respawn timer complete or initial spawn - restore/setup player
            waitingToRespawn = false;
            respawnTimer = 0;
            ArcadeSpaceShooter.kills = 0;
            hasLasersComponent.typeMask = HasLasersComponent.SINGLE;

            TakesDamageComponent ptdc = ComponentMap.takesDamageMapper.get(player);
            ptdc.health = ptdc.maxHealth;

            RenderComponent prc = new RenderComponent(ArcadeSpaceShooter.shipTextures.toArray(new TextureRegion[ArcadeSpaceShooter.shipTextures.size()]), RenderComponent.PLANE_MAIN);
            player.add(prc);
            if (isInitialSpawn) {
                player.add(new PositionComponent(new Vector2(
                        (ArcadeSpaceShooter.screenRect.width / 2),
                        prc.height + 20
                )));
            } else {
                PositionComponent playerPositionComp = ComponentMap.positionMapper.get(player);
                playerPositionComp.position = new Vector2(
                        (ArcadeSpaceShooter.screenRect.width / 2),
                        prc.height + 20
                );
            }
            return;
        }

        SpeedComponent sc = ComponentMap.speedMapper.get(player);
        RenderComponent rc = ComponentMap.renderMapper.get(player);

        sc.motion = new Vector2(0, 0);

        if(ComponentMap.hasShieldMapper.has(player))
        {
            HasShieldComponent hasShieldComp = ComponentMap.hasShieldMapper.get(player);//(HasShieldComponent)player.components[typeof(HasShieldComponent)];

            if(!ComponentMap.shieldedMapper.has(player) && hasShieldComp.shieldPower < hasShieldComp.maxShieldPower)
            {
                hasShieldComp.shieldPower += hasShieldComp.shieldRegenRate * gameTime;//.ElapsedGameTime.Milliseconds;
            }

            if (hasShieldComp.shieldPower >= hasShieldComp.maxShieldPower)
            {
                hasShieldComp.shieldPower = hasShieldComp.maxShieldPower;
                hasShieldComp.shieldCooldown = false;
            }
            if(ComponentMap.shieldedMapper.has(player))
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
                    if(!ComponentMap.shieldedMapper.has(player))
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

        if(ComponentMap.hasMissilesMapper.has(player)) {
            HasMissilesComponent hasMissilesComponent = ComponentMap.hasMissilesMapper.get(player);
            hasMissilesComponent.timeSinceLastShot += gameTime;
            if(Gdx.input.isKeyPressed(Input.Keys.X)) {
                FireMissile(player);
            }
        }

        if(ComponentMap.hasBombsMapper.has(player)) {
            HasBombsComponent hasBombsComponent = ComponentMap.hasBombsMapper.get(player);
            hasBombsComponent.timeSinceLastShot += gameTime;
            if(Gdx.input.isKeyPressed(Input.Keys.Z)) {
                FireBomb(player);
            }
        }

        if(ComponentMap.hasEmpMapper.has(player)) {
            HasEmpComponent hasEmpComponent = ComponentMap.hasEmpMapper.get(player);
            hasEmpComponent.timeSinceLastShot += gameTime;
            if(Gdx.input.isKeyPressed(Input.Keys.C)) {
                FireEmp(player);
            }
        }

        hasLasersComponent.timeSinceLastShot += gameTime;

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE))
        {
            if(ComponentMap.hasLasersMapper.has(player)) {
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

        sc.motion.scl(GameConstants.PLAYER_MOVEMENT_SPEED);// *= 5;
    }

    private void FireMissile(Entity player) {
        HasMissilesComponent hasMissilesComponent = ComponentMap.hasMissilesMapper.get(player);
        if(hasMissilesComponent.timeSinceLastShot > hasMissilesComponent.shotInterval) {
            hasMissilesComponent.timeSinceLastShot = 0;
            PositionComponent posc = ComponentMap.positionMapper.get(player);
            Entity missile1 = new Entity();
            missile1.add(new RenderComponent(ArcadeSpaceShooter.textures.findRegion("spaceMissiles", 9), RenderComponent.PLANE_ABOVE));
            missile1.add(new PositionComponent(posc.position.x - WeaponConstants.MISSILE_OFFSET_X, posc.position.y));
            missile1.add(new SpeedComponent((int)-WeaponConstants.MISSILE_SPEED_X_FACTOR, (int)WeaponConstants.MISSILE_SPEED_Y));
            missile1.add(new DealsDamageComponent(WeaponConstants.MISSILE_DAMAGE_LEFT, DamageTypeConstants.MISSILE));
            missile1.add(new MissileComponent());
            getEngine().addEntity(missile1);

            Entity missile2 = new Entity();
            missile2.add(new RenderComponent(ArcadeSpaceShooter.textures.findRegion("spaceMissiles", 9), RenderComponent.PLANE_ABOVE));
            missile2.add(new PositionComponent(posc.position.x + WeaponConstants.MISSILE_OFFSET_X, posc.position.y));
            missile2.add(new SpeedComponent((int)WeaponConstants.MISSILE_SPEED_X_FACTOR, (int)WeaponConstants.MISSILE_SPEED_Y));
            missile2.add(new DealsDamageComponent(WeaponConstants.MISSILE_DAMAGE_RIGHT, DamageTypeConstants.MISSILE));
            missile2.add(new MissileComponent());
            getEngine().addEntity(missile2);
        }
    }

    private void FireBomb(Entity player) {
        HasBombsComponent hasBombsComponent = ComponentMap.hasBombsMapper.get(player);
        if(hasBombsComponent.timeSinceLastShot > hasBombsComponent.shotInterval) {
            hasBombsComponent.timeSinceLastShot = 0;

            PositionComponent posc = ComponentMap.positionMapper.get(player);
            Entity missile1 = new Entity();
            missile1.add(new RenderComponent(ArcadeSpaceShooter.textures.findRegion("spaceMissiles", 12), RenderComponent.PLANE_ABOVE));
            missile1.add(new PositionComponent(posc.position.x, posc.position.y + WeaponConstants.BOMB_OFFSET_Y));
            missile1.add(new SpeedComponent(0, (int)WeaponConstants.BOMB_SPEED_Y));
            missile1.add(new DealsDamageComponent(10, DamageTypeConstants.BOMB));
            missile1.add(new BombComponent());
            getEngine().addEntity(missile1);
        }
    }

    private void FireEmp(Entity player) {
        HasEmpComponent hasEmpComponent = ComponentMap.hasEmpMapper.get(player);
        if(hasEmpComponent.timeSinceLastShot > hasEmpComponent.shotInterval) {
            hasEmpComponent.timeSinceLastShot = 0;
            ArcadeSpaceShooter.empActive = true;
            System.out.println("Emp Active");
        }
    }

    private void Shoot(Entity player)
    {
        PositionComponent posc = ComponentMap.positionMapper.get(player);
        HasLasersComponent hasLasersComponent = ComponentMap.hasLasersMapper.get(player);
        //int fireTime = pc.laserLevel == 0 ? 150 : pc.laserLevel == 1 ? 100 : 80;

        if (hasLasersComponent.timeSinceLastShot > hasLasersComponent.shotInterval)
        {
            hasLasersComponent.timeSinceLastShot = 0;

            // Play laser shoot sound
            SoundManager.playLaserShoot();

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
                newLaser.add(new SpeedComponent(0, (int)WeaponConstants.LASER_SPEED_Y));
                newLaser.add(new PositionComponent(new Vector2(posc.position.x, posc.position.y + laser.getRegionHeight() / 2.0f + WeaponConstants.LASER_OFFSET_Y)));
                newLaser.add(new DealsDamageComponent(laserDamage, DamageTypeConstants.LASER));
                this.getEngine().addEntity(newLaser);
            }

            if((hasLasersComponent.typeMask & HasLasersComponent.DUAL) > 0) {
                Entity newLaser1 = new Entity();
                newLaser1.add(new RenderComponent(laser, RenderComponent.PLANE_ABOVE));
                newLaser1.add(new LaserComponent(explosion));
                newLaser1.add(new SpeedComponent(0, (int)WeaponConstants.LASER_SPEED_Y));
                newLaser1.add(new PositionComponent(new Vector2(posc.position.x - WeaponConstants.LASER_OFFSET_DUAL, posc.position.y + laser.getRegionHeight() / 2.0f + WeaponConstants.LASER_OFFSET_Y)));
                newLaser1.add(new DealsDamageComponent(laserDamage, DamageTypeConstants.LASER));
                this.getEngine().addEntity(newLaser1);

                Entity newLaser2 = new Entity();
                newLaser2.add(new RenderComponent(laser, RenderComponent.PLANE_ABOVE));
                newLaser2.add(new LaserComponent(explosion));
                newLaser2.add(new SpeedComponent(0, (int)WeaponConstants.LASER_SPEED_Y));
                newLaser2.add(new PositionComponent(new Vector2(posc.position.x + WeaponConstants.LASER_OFFSET_DUAL, posc.position.y + laser.getRegionHeight() / 2.0f + WeaponConstants.LASER_OFFSET_Y)));
                newLaser2.add(new DealsDamageComponent(laserDamage, DamageTypeConstants.LASER));
                this.getEngine().addEntity(newLaser2);
            }

            if((hasLasersComponent.typeMask & HasLasersComponent.DIAGONAL) > 0) {
                Entity newLaser1 = new Entity();
                newLaser1.add(new RenderComponent(laser, RenderComponent.PLANE_ABOVE));
                newLaser1.add(new LaserComponent(explosion));
                newLaser1.add(new SpeedComponent((int)-WeaponConstants.LASER_DIAGONAL_SPEED_X, (int)WeaponConstants.LASER_SPEED_Y));
                newLaser1.add(new PositionComponent(new Vector2(posc.position.x - WeaponConstants.LASER_OFFSET_DUAL, posc.position.y + laser.getRegionHeight() / 2.0f + WeaponConstants.LASER_OFFSET_Y)));
                newLaser1.add(new DealsDamageComponent( laserDamage, DamageTypeConstants.LASER));
                this.getEngine().addEntity(newLaser1);

                Entity newLaser2 = new Entity();
                newLaser2.add(new RenderComponent(laser, RenderComponent.PLANE_ABOVE));
                newLaser2.add(new LaserComponent(explosion));
                newLaser2.add(new SpeedComponent((int)WeaponConstants.LASER_DIAGONAL_SPEED_X, (int)WeaponConstants.LASER_SPEED_Y));
                newLaser2.add(new PositionComponent(new Vector2(posc.position.x + WeaponConstants.LASER_OFFSET_DUAL, posc.position.y + laser.getRegionHeight() / 2.0f + WeaponConstants.LASER_OFFSET_Y)));
                newLaser2.add(new DealsDamageComponent(laserDamage, DamageTypeConstants.LASER));
                this.getEngine().addEntity(newLaser2);
            }
        }
    }
}
