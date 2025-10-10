# MEDIUM: Split InputSystem

**Priority**: MEDIUM
**Impact**: Medium (improves clarity and testability)
**Effort**: Medium (2 hours)
**SOLID Violations**: Single Responsibility Principle

## Problem Statement

`InputSystem.java:19-346` handles 7 distinct responsibilities:

1. Player respawn logic (lines 20-114)
2. Shield management (lines 122-162)
3. Missile firing (lines 164-169, 222-243)
4. Bomb firing (lines 172-178, 245-259)
5. EMP firing (lines 180-186, 261-268)
6. Laser shooting (lines 188-194, 270-344)
7. Movement input (lines 196-219)

**Complexity**: ~25 cyclomatic complexity
**Lines**: 347

## Proposed Solution

Split into 2 focused systems:

### 1. PlayerControlSystem
**Responsibility**: Handle player input (movement, weapons)

```java
package com.dalesmithwebdev.galaxia.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.services.WeaponService;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;

public class PlayerControlSystem extends EntitySystem {
    private final WeaponService weaponService;

    public PlayerControlSystem(WeaponService weaponService) {
        this.weaponService = weaponService;
    }

    @Override
    public void update(float gameTime) {
        ImmutableArray<Entity> players = getEngine().getEntitiesFor(
            Family.all(PlayerComponent.class, RenderComponent.class).get()
        );

        if (players.size() == 0) return;

        Entity player = players.first();

        updateShield(player, gameTime);
        updateWeapons(player, gameTime);
        updateMovement(player);
    }

    private void updateShield(Entity player, float gameTime) {
        // Extract shield logic from InputSystem:122-162
    }

    private void updateWeapons(Entity player, float gameTime) {
        // Handle space (lasers), X (missiles), Z (bombs), C (EMP)
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            weaponService.fireLasers(player);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.X)) {
            weaponService.fireMissiles(player);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.Z)) {
            weaponService.fireBomb(player);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.C)) {
            weaponService.fireEmp(player);
        }
    }

    private void updateMovement(Entity player) {
        // Extract movement logic from InputSystem:196-219
    }
}
```

### 2. RespawnSystem
**Responsibility**: Handle player death and respawn

```java
package com.dalesmithwebdev.galaxia.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.prefabs.Player;
import com.dalesmithwebdev.galaxia.screens.GameOverScreen;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;

public class RespawnSystem extends EntitySystem {
    private float respawnTimer = 0;
    private boolean waitingToRespawn = false;
    private static final float RESPAWN_DELAY_MS = 2000f;

    @Override
    public void update(float gameTime) {
        // Handle respawn timer
        if(waitingToRespawn) {
            respawnTimer += gameTime;
            if(respawnTimer < RESPAWN_DELAY_MS) {
                return;
            }
        }

        // Check if player needs to be created or respawned
        ImmutableArray<Entity> players = getEngine().getEntitiesFor(
            Family.all(PlayerComponent.class).get()
        );

        if (players.size() == 0) {
            handleNoPlayer();
        } else {
            Entity player = players.first();
            if(!ComponentMap.renderComponentComponentMapper.has(player)) {
                handlePlayerDeath(player);
            }
        }
    }

    private void handleNoPlayer() {
        // Extract logic from InputSystem:41-49
    }

    private void handlePlayerDeath(Entity player) {
        // Extract logic from InputSystem:56-114
    }

    private void respawnPlayer(Entity player) {
        // Reset respawn timer
        waitingToRespawn = false;
        respawnTimer = 0;

        // Restore player state
        HasLasersComponent lasers = ComponentMap.hasLasersComponentComponentMapper.get(player);
        lasers.typeMask = HasLasersComponent.SINGLE;

        TakesDamageComponent health = ComponentMap.takesDamageComponentComponentMapper.get(player);
        health.health = health.maxHealth;

        // Restore render component
        RenderComponent rc = new RenderComponent(/*...*/);
        player.add(rc);

        // Position at spawn point
        PositionComponent pc = ComponentMap.positionComponentComponentMapper.get(player);
        pc.position = getSpawnPosition();
    }

    private Vector2 getSpawnPosition() {
        // Calculate spawn position at bottom center
        return new Vector2(
            ArcadeSpaceShooter.screenRect.width / 2,
            60
        );
    }

    private void triggerGameOver() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                ArcadeSpaceShooter.instance.setScreen(new GameOverScreen());
            }
        }, 3);
        ArcadeSpaceShooter.gameOverScheduled = true;
    }
}
```

### 3. WeaponService (Extract Weapon Creation)

```java
package com.dalesmithwebdev.galaxia.services;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.constants.WeaponConstants;
import com.dalesmithwebdev.galaxia.systems.DamageSystem;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;
import com.dalesmithwebdev.galaxia.utility.SoundManager;

public class WeaponService {
    private final Engine engine;
    private final AssetService assets;

    public WeaponService(Engine engine, AssetService assets) {
        this.engine = engine;
        this.assets = assets;
    }

    public void fireLasers(Entity player) {
        HasLasersComponent lasers = ComponentMap.hasLasersComponentComponentMapper.get(player);

        if (lasers.timeSinceLastShot < lasers.shotInterval) {
            return;
        }

        lasers.timeSinceLastShot = 0;
        SoundManager.playLaserShoot();

        PositionComponent pos = ComponentMap.positionComponentComponentMapper.get(player);

        // Determine laser type and damage
        TextureRegion laserTexture = getLaserTexture(lasers);
        TextureRegion explosionTexture = getExplosionTexture(lasers);
        int damage = getLaserDamage(lasers);

        // Fire based on laser configuration
        if((lasers.typeMask & HasLasersComponent.SINGLE) > 0) {
            createLaser(pos.position, new Vector2(0, WeaponConstants.LASER_SPEED_Y),
                       laserTexture, explosionTexture, damage);
        }

        if((lasers.typeMask & HasLasersComponent.DUAL) > 0) {
            createLaser(
                new Vector2(pos.position.x - WeaponConstants.LASER_OFFSET_DUAL, pos.position.y),
                new Vector2(0, WeaponConstants.LASER_SPEED_Y),
                laserTexture, explosionTexture, damage
            );
            createLaser(
                new Vector2(pos.position.x + WeaponConstants.LASER_OFFSET_DUAL, pos.position.y),
                new Vector2(0, WeaponConstants.LASER_SPEED_Y),
                laserTexture, explosionTexture, damage
            );
        }

        if((lasers.typeMask & HasLasersComponent.DIAGONAL) > 0) {
            createLaser(
                new Vector2(pos.position.x - WeaponConstants.LASER_OFFSET_DUAL, pos.position.y),
                new Vector2(-WeaponConstants.LASER_DIAGONAL_SPEED_X, WeaponConstants.LASER_SPEED_Y),
                laserTexture, explosionTexture, damage
            );
            createLaser(
                new Vector2(pos.position.x + WeaponConstants.LASER_OFFSET_DUAL, pos.position.y),
                new Vector2(WeaponConstants.LASER_DIAGONAL_SPEED_X, WeaponConstants.LASER_SPEED_Y),
                laserTexture, explosionTexture, damage
            );
        }
    }

    private void createLaser(Vector2 position, Vector2 velocity,
                            TextureRegion texture, TextureRegion explosion, int damage) {
        Entity laser = new Entity();
        laser.add(new RenderComponent(texture, RenderComponent.PLANE_ABOVE));
        laser.add(new PositionComponent(position));
        laser.add(new SpeedComponent(velocity.x, velocity.y));
        laser.add(new LaserComponent(explosion));
        laser.add(new DealsDamageComponent(damage, DamageSystem.LASER));
        engine.addEntity(laser);
    }

    private TextureRegion getLaserTexture(HasLasersComponent lasers) {
        // Extract logic from InputSystem:283-297
    }

    private TextureRegion getExplosionTexture(HasLasersComponent lasers) {
        // Extract logic from InputSystem:284-296
    }

    private int getLaserDamage(HasLasersComponent lasers) {
        // Extract logic from InputSystem:285-297
    }

    public void fireMissiles(Entity player) {
        // Extract from InputSystem:222-243
    }

    public void fireBomb(Entity player) {
        // Extract from InputSystem:245-259
    }

    public void fireEmp(Entity player) {
        // Extract from InputSystem:261-268
    }
}
```

## Refactoring Steps

1. Create `WeaponService.java`
2. Extract weapon creation methods (Shoot, FireMissile, FireBomb, FireEmp)
3. Create `PlayerControlSystem.java`
4. Extract input handling and weapon triggering
5. Create `RespawnSystem.java`
6. Extract respawn logic
7. Update `ArcadeSpaceShooter` to register new systems
8. Delete `InputSystem.java`
9. Test all player controls and respawn

## Files to Create

- `core/src/main/java/com/dalesmithwebdev/galaxia/services/WeaponService.java`
- `core/src/main/java/com/dalesmithwebdev/galaxia/systems/PlayerControlSystem.java`
- `core/src/main/java/com/dalesmithwebdev/galaxia/systems/RespawnSystem.java`

## Files to Modify

- `core/src/main/java/com/dalesmithwebdev/galaxia/ArcadeSpaceShooter.java` - Register new systems
- `core/src/main/java/com/dalesmithwebdev/galaxia/systems/InputSystem.java` - DELETE

## Benefits

- **Clarity**: Each system has clear, focused purpose
- **Testability**: Can test respawn logic without input handling
- **Reusability**: WeaponService can be used by AI enemies
- **Maintainability**: Easier to modify weapon behavior
- **SRP**: Each class has one reason to change

## Metrics

- **InputSystem**: 347 lines → 0 lines (deleted)
- **PlayerControlSystem**: ~150 lines (input + movement + shields)
- **RespawnSystem**: ~100 lines (death + respawn logic)
- **WeaponService**: ~200 lines (all weapon creation)
- **Net Change**: +450 lines but far better organized
