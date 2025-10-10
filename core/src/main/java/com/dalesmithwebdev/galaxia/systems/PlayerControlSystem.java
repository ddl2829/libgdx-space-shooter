package com.dalesmithwebdev.galaxia.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.constants.GameConstants;
import com.dalesmithwebdev.galaxia.services.GameStateService;
import com.dalesmithwebdev.galaxia.services.ServiceLocator;
import com.dalesmithwebdev.galaxia.services.WeaponService;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;

/**
 * PlayerControlSystem - Handles player input, movement, shields, and weapon firing
 * Single Responsibility: Player control logic
 */
public class PlayerControlSystem extends EntitySystem {
    private final WeaponService weaponService;

    public PlayerControlSystem(WeaponService weaponService) {
        this.weaponService = weaponService;
    }

    @Override
    public void update(float gameTime) {
        GameStateService gameState = ServiceLocator.getInstance().getGameState();
        if (gameState.isPaused()) {
            return;
        }

        ImmutableArray<Entity> players = getEngine().getEntitiesFor(
            Family.all(PlayerComponent.class, RenderComponent.class).get()
        );

        if (players.size() == 0) {
            return; // No active player
        }

        Entity player = players.first();

        // Update timers
        updateWeaponTimers(player, gameTime);

        // Handle shield
        updateShield(player, gameTime);

        // Handle movement
        handleMovement(player);

        // Handle weapons
        handleWeapons(player);
    }

    /**
     * Update weapon cooldown timers
     */
    private void updateWeaponTimers(Entity player, float gameTime) {
        // Laser timer
        if (ComponentMap.hasLasersMapper.has(player)) {
            HasLasersComponent lasers = ComponentMap.hasLasersMapper.get(player);
            lasers.timeSinceLastShot += gameTime;
        }

        // Missile timer
        if (ComponentMap.hasMissilesMapper.has(player)) {
            HasMissilesComponent missiles = ComponentMap.hasMissilesMapper.get(player);
            missiles.timeSinceLastShot += gameTime;
        }

        // Bomb timer
        if (ComponentMap.hasBombsMapper.has(player)) {
            HasBombsComponent bombs = ComponentMap.hasBombsMapper.get(player);
            bombs.timeSinceLastShot += gameTime;
        }

        // EMP timer
        if (ComponentMap.hasEmpMapper.has(player)) {
            HasEmpComponent emp = ComponentMap.hasEmpMapper.get(player);
            emp.timeSinceLastShot += gameTime;
        }
    }

    /**
     * Handle shield regeneration and activation
     */
    private void updateShield(Entity player, float gameTime) {
        if (!ComponentMap.hasShieldMapper.has(player)) {
            return; // Player doesn't have shield upgrade
        }

        HasShieldComponent hasShieldComp = ComponentMap.hasShieldMapper.get(player);

        // Regenerate shield when not active and not in cooldown
        if (!ComponentMap.shieldedMapper.has(player) && hasShieldComp.shieldPower < hasShieldComp.maxShieldPower) {
            hasShieldComp.shieldPower += hasShieldComp.shieldRegenRate * gameTime;
        }

        // Cap shield power and end cooldown
        if (hasShieldComp.shieldPower >= hasShieldComp.maxShieldPower) {
            hasShieldComp.shieldPower = hasShieldComp.maxShieldPower;
            hasShieldComp.shieldCooldown = false;
        }

        // Deplete shield when active
        if (ComponentMap.shieldedMapper.has(player)) {
            hasShieldComp.shieldPower -= hasShieldComp.shieldDepleteRate * gameTime;

            if (hasShieldComp.shieldPower <= 0) {
                player.remove(ShieldedComponent.class);
                hasShieldComp.shieldCooldown = true;
                hasShieldComp.shieldPower = 0;
            }
        }

        // Handle shield activation input
        if (!hasShieldComp.shieldCooldown) {
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && hasShieldComp.shieldPower >= 0) {
                if (!ComponentMap.shieldedMapper.has(player)) {
                    player.add(new ShieldedComponent());
                }
            } else {
                player.remove(ShieldedComponent.class);
            }
        }
    }

    /**
     * Handle player movement input
     */
    private void handleMovement(Entity player) {
        SpeedComponent sc = ComponentMap.speedMapper.get(player);
        RenderComponent rc = ComponentMap.renderMapper.get(player);

        // Reset motion
        sc.motion = new Vector2(0, 0);

        // Handle directional input
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            rc.currentTexture = 1; // Left bank sprite
            sc.motion.x = -1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            rc.currentTexture = 2; // Right bank sprite
            sc.motion.x = 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            sc.motion.y = 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            sc.motion.y = -1;
        }

        // Reset sprite to center when not banking
        if (!Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            rc.currentTexture = 0;
        }

        // Apply movement speed
        sc.motion.scl(GameConstants.PLAYER_MOVEMENT_SPEED);
    }

    /**
     * Handle weapon firing input
     */
    private void handleWeapons(Entity player) {
        // Lasers (SPACE)
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (ComponentMap.hasLasersMapper.has(player)) {
                weaponService.fireLasers(player);
            }
        }

        // Missiles (X)
        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            if (ComponentMap.hasMissilesMapper.has(player)) {
                weaponService.fireMissiles(player);
            }
        }

        // Bombs (Z)
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            if (ComponentMap.hasBombsMapper.has(player)) {
                weaponService.fireBomb(player);
            }
        }

        // EMP (C)
        if (Gdx.input.isKeyPressed(Input.Keys.C)) {
            if (ComponentMap.hasEmpMapper.has(player)) {
                weaponService.fireEmp(player);
            }
        }
    }
}
