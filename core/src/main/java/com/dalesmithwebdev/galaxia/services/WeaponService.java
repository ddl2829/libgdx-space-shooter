package com.dalesmithwebdev.galaxia.services;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.constants.DamageTypeConstants;
import com.dalesmithwebdev.galaxia.constants.WeaponConstants;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;
import com.dalesmithwebdev.galaxia.utility.SoundManager;

/**
 * WeaponService - Handles weapon creation for player and potentially enemies
 * Single Responsibility: Creating and spawning weapon entities
 */
public class WeaponService {
    private final Engine engine;

    public WeaponService(Engine engine) {
        this.engine = engine;
    }

    /**
     * Fire player lasers based on current upgrade configuration
     */
    public void fireLasers(Entity player) {
        PositionComponent posc = ComponentMap.positionMapper.get(player);
        HasLasersComponent hasLasersComponent = ComponentMap.hasLasersMapper.get(player);

        if (hasLasersComponent.timeSinceLastShot <= hasLasersComponent.shotInterval) {
            return; // Cooldown not ready
        }

        hasLasersComponent.timeSinceLastShot = 0;

        // Play laser shoot sound
        SoundManager.playLaserShoot();

        // Determine laser appearance and damage based on upgrade level
        TextureRegion laser = ArcadeSpaceShooter.textures.findRegion("laserRed");
        TextureRegion explosion = ArcadeSpaceShooter.textures.findRegion("laserRedShot");
        int laserDamage = 1;

        if ((hasLasersComponent.typeMask & HasLasersComponent.UPGRADED) > 0) {
            laser = ArcadeSpaceShooter.textures.findRegion("laserGreen");
            laserDamage = 2;
            explosion = ArcadeSpaceShooter.textures.findRegion("laserGreenShot");
        }
        if ((hasLasersComponent.typeMask & HasLasersComponent.UPGRADED_AGAIN) > 0) {
            laser = ArcadeSpaceShooter.textures.findRegion("laserBlue12");
            laserDamage = 3;
            explosion = ArcadeSpaceShooter.textures.findRegion("laserBlue08");
        }

        // Fire lasers based on upgrade configuration
        if ((hasLasersComponent.typeMask & HasLasersComponent.SINGLE) > 0) {
            createLaser(
                new Vector2(posc.position.x, posc.position.y + laser.getRegionHeight() / 2.0f + WeaponConstants.LASER_OFFSET_Y),
                new Vector2(0, WeaponConstants.LASER_SPEED_Y),
                laser,
                explosion,
                laserDamage
            );
        }

        if ((hasLasersComponent.typeMask & HasLasersComponent.DUAL) > 0) {
            // Left laser
            createLaser(
                new Vector2(posc.position.x - WeaponConstants.LASER_OFFSET_DUAL, posc.position.y + laser.getRegionHeight() / 2.0f + WeaponConstants.LASER_OFFSET_Y),
                new Vector2(0, WeaponConstants.LASER_SPEED_Y),
                laser,
                explosion,
                laserDamage
            );
            // Right laser
            createLaser(
                new Vector2(posc.position.x + WeaponConstants.LASER_OFFSET_DUAL, posc.position.y + laser.getRegionHeight() / 2.0f + WeaponConstants.LASER_OFFSET_Y),
                new Vector2(0, WeaponConstants.LASER_SPEED_Y),
                laser,
                explosion,
                laserDamage
            );
        }

        if ((hasLasersComponent.typeMask & HasLasersComponent.DIAGONAL) > 0) {
            // Left diagonal laser
            createLaser(
                new Vector2(posc.position.x - WeaponConstants.LASER_OFFSET_DUAL, posc.position.y + laser.getRegionHeight() / 2.0f + WeaponConstants.LASER_OFFSET_Y),
                new Vector2(-WeaponConstants.LASER_DIAGONAL_SPEED_X, WeaponConstants.LASER_SPEED_Y),
                laser,
                explosion,
                laserDamage
            );
            // Right diagonal laser
            createLaser(
                new Vector2(posc.position.x + WeaponConstants.LASER_OFFSET_DUAL, posc.position.y + laser.getRegionHeight() / 2.0f + WeaponConstants.LASER_OFFSET_Y),
                new Vector2(WeaponConstants.LASER_DIAGONAL_SPEED_X, WeaponConstants.LASER_SPEED_Y),
                laser,
                explosion,
                laserDamage
            );
        }
    }

    /**
     * Create a single laser entity
     */
    private void createLaser(Vector2 position, Vector2 velocity, TextureRegion texture, TextureRegion explosion, int damage) {
        Entity laser = new Entity();
        laser.add(new RenderComponent(texture, RenderComponent.PLANE_ABOVE));
        laser.add(new LaserComponent(explosion));
        laser.add(new SpeedComponent((int) velocity.x, (int) velocity.y));
        laser.add(new PositionComponent(position));
        laser.add(new DealsDamageComponent(damage, DamageTypeConstants.LASER));
        engine.addEntity(laser);
    }

    /**
     * Fire player missiles (dual missiles)
     */
    public void fireMissiles(Entity player) {
        HasMissilesComponent hasMissilesComponent = ComponentMap.hasMissilesMapper.get(player);
        if (hasMissilesComponent.timeSinceLastShot <= hasMissilesComponent.shotInterval) {
            return; // Cooldown not ready
        }

        hasMissilesComponent.timeSinceLastShot = 0;
        PositionComponent posc = ComponentMap.positionMapper.get(player);

        // Left missile
        Entity missile1 = new Entity();
        missile1.add(new RenderComponent(ArcadeSpaceShooter.textures.findRegion("spaceMissiles", 9), RenderComponent.PLANE_ABOVE));
        missile1.add(new PositionComponent(posc.position.x - WeaponConstants.MISSILE_OFFSET_X, posc.position.y));
        missile1.add(new SpeedComponent((int) -WeaponConstants.MISSILE_SPEED_X_FACTOR, (int) WeaponConstants.MISSILE_SPEED_Y));
        missile1.add(new DealsDamageComponent(WeaponConstants.MISSILE_DAMAGE_LEFT, DamageTypeConstants.MISSILE));
        missile1.add(new MissileComponent());
        engine.addEntity(missile1);

        // Right missile
        Entity missile2 = new Entity();
        missile2.add(new RenderComponent(ArcadeSpaceShooter.textures.findRegion("spaceMissiles", 9), RenderComponent.PLANE_ABOVE));
        missile2.add(new PositionComponent(posc.position.x + WeaponConstants.MISSILE_OFFSET_X, posc.position.y));
        missile2.add(new SpeedComponent((int) WeaponConstants.MISSILE_SPEED_X_FACTOR, (int) WeaponConstants.MISSILE_SPEED_Y));
        missile2.add(new DealsDamageComponent(WeaponConstants.MISSILE_DAMAGE_RIGHT, DamageTypeConstants.MISSILE));
        missile2.add(new MissileComponent());
        engine.addEntity(missile2);
    }

    /**
     * Fire player bomb
     */
    public void fireBomb(Entity player) {
        HasBombsComponent hasBombsComponent = ComponentMap.hasBombsMapper.get(player);
        if (hasBombsComponent.timeSinceLastShot <= hasBombsComponent.shotInterval) {
            return; // Cooldown not ready
        }

        hasBombsComponent.timeSinceLastShot = 0;
        PositionComponent posc = ComponentMap.positionMapper.get(player);

        Entity bomb = new Entity();
        bomb.add(new RenderComponent(ArcadeSpaceShooter.textures.findRegion("spaceMissiles", 12), RenderComponent.PLANE_ABOVE));
        bomb.add(new PositionComponent(posc.position.x, posc.position.y + WeaponConstants.BOMB_OFFSET_Y));
        bomb.add(new SpeedComponent(0, (int) WeaponConstants.BOMB_SPEED_Y));
        bomb.add(new DealsDamageComponent(WeaponConstants.BOMB_DAMAGE, DamageTypeConstants.BOMB));
        bomb.add(new BombComponent());
        engine.addEntity(bomb);
    }

    /**
     * Activate player EMP
     */
    public void fireEmp(Entity player) {
        HasEmpComponent hasEmpComponent = ComponentMap.hasEmpMapper.get(player);
        if (hasEmpComponent.timeSinceLastShot <= hasEmpComponent.shotInterval) {
            return; // Cooldown not ready
        }

        hasEmpComponent.timeSinceLastShot = 0;
        GameStateService gameState = ServiceLocator.getInstance().getGameState();
        gameState.setEmpActive(true);
        System.out.println("Emp Active");
    }
}
