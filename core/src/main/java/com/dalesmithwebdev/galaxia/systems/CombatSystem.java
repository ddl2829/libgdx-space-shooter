package com.dalesmithwebdev.galaxia.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.constants.DamageTypeConstants;
import com.dalesmithwebdev.galaxia.constants.GameConstants;
import com.dalesmithwebdev.galaxia.constants.WeaponConstants;
import com.dalesmithwebdev.galaxia.services.GameStateService;
import com.dalesmithwebdev.galaxia.services.ServiceLocator;
import com.dalesmithwebdev.galaxia.systems.upgrades.UpgradeHandler;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;
import com.dalesmithwebdev.galaxia.utility.SoundManager;

import java.util.HashMap;
import java.util.Map;

/**
 * CombatSystem - Handles damage application, death, and combat effects
 * Single Responsibility: Combat mechanics (damage, shields, death, explosions)
 */
public class CombatSystem extends EntitySystem implements CollisionListener {

    // Upgrade handler strategy pattern
    private final Map<Class<? extends Component>, UpgradeHandler> upgradeHandlers;

    // Dependencies
    private LootSystem lootSystem;

    public CombatSystem() {
        this.upgradeHandlers = new HashMap<>();
        registerUpgradeHandlers();
    }

    /**
     * Inject LootSystem dependency
     */
    public void setLootSystem(LootSystem lootSystem) {
        this.lootSystem = lootSystem;
    }

    private void registerUpgradeHandlers() {
        upgradeHandlers.put(LaserStrengthUpgradeComponent.class,
            new com.dalesmithwebdev.galaxia.systems.upgrades.LaserStrengthUpgradeHandler());
        upgradeHandlers.put(DualLaserUpgradeComponent.class,
            new com.dalesmithwebdev.galaxia.systems.upgrades.DualLaserUpgradeHandler());
        upgradeHandlers.put(DiagonalLaserUpgradeComponent.class,
            new com.dalesmithwebdev.galaxia.systems.upgrades.DiagonalLaserUpgradeHandler());
        upgradeHandlers.put(MissileUpgradeComponent.class,
            new com.dalesmithwebdev.galaxia.systems.upgrades.MissileUpgradeHandler());
        upgradeHandlers.put(BombUpgradeComponent.class,
            new com.dalesmithwebdev.galaxia.systems.upgrades.BombUpgradeHandler());
        upgradeHandlers.put(ShieldUpgradeComponent.class,
            new com.dalesmithwebdev.galaxia.systems.upgrades.ShieldUpgradeHandler());
        upgradeHandlers.put(EmpUpgradeComponent.class,
            new com.dalesmithwebdev.galaxia.systems.upgrades.EmpUpgradeHandler());
    }

    @Override
    public void update(float gameTime) {
        GameStateService gameState = ServiceLocator.getInstance().getGameState();
        if (gameState.isPaused()) {
            return;
        }

        // Update recently damaged timers
        updateRecentlyDamagedTimers(gameTime);
    }

    /**
     * Update recently damaged component timers
     */
    private void updateRecentlyDamagedTimers(float gameTime) {
        ImmutableArray<Entity> recentlyDamaged = getEngine().getEntitiesFor(
            Family.all(RecentlyDamagedComponent.class).get()
        );

        for (Entity damaged : recentlyDamaged) {
            RecentlyDamagedComponent rdc = ComponentMap.recentlyDamagedMapper.get(damaged);
            rdc.timeSinceDamaged += gameTime;
            if (rdc.timeSinceDamaged >= rdc.timeout) {
                damaged.remove(RecentlyDamagedComponent.class);
            }
        }
    }

    @Override
    public void onCollision(Entity damageDealer, Entity damageTaker) {
        DealsDamageComponent ddc = ComponentMap.dealsDamageMapper.get(damageDealer);
        TakesDamageComponent tdc = ComponentMap.takesDamageMapper.get(damageTaker);
        PositionComponent dd_pc = ComponentMap.positionMapper.get(damageDealer);
        PositionComponent td_pc = ComponentMap.positionMapper.get(damageTaker);

        // Apply damage (unless shielded)
        if (!ComponentMap.shieldedMapper.has(damageTaker)) {
            tdc.health -= ddc.strength;

            // Play hitHurt sound if player takes damage
            if (ComponentMap.playerMapper.has(damageTaker)) {
                SoundManager.playHitHurt();
            }
        }

        // Track recently damaged
        updateRecentlyDamagedStatus(damageTaker);

        // Spawn explosion effects
        spawnExplosionEffect(damageDealer, dd_pc.position);

        // Remove damage dealer (unless it's player or explosion)
        if (!ComponentMap.playerMapper.has(damageDealer) &&
            !ComponentMap.explosionMapper.has(damageDealer)) {
            getEngine().removeEntity(damageDealer);
        }

        // Handle death
        if (tdc.health <= 0) {
            handleDeath(damageDealer, damageTaker, td_pc.position);
        }
    }

    /**
     * Update or add recently damaged component
     */
    private void updateRecentlyDamagedStatus(Entity entity) {
        RecentlyDamagedComponent rdc;
        if (ComponentMap.recentlyDamagedMapper.has(entity)) {
            rdc = ComponentMap.recentlyDamagedMapper.get(entity);
            rdc.timeSinceDamaged = 0;
        } else {
            rdc = new RecentlyDamagedComponent(GameConstants.RECENTLY_DAMAGED_TIMEOUT_MS);
            entity.add(rdc);
        }
    }

    /**
     * Spawn explosion effect based on weapon type
     */
    private void spawnExplosionEffect(Entity damageDealer, Vector2 position) {
        Entity explosion = null;

        // Laser explosion
        if (ComponentMap.laserMapper.has(damageDealer)) {
            LaserComponent lc = ComponentMap.laserMapper.get(damageDealer);
            explosion = new Entity();
            explosion.add(new RenderComponent(lc.explosionTexture, RenderComponent.PLANE_ABOVE));
            explosion.add(new PositionComponent(position));
            explosion.add(new ExplosionComponent());
        }
        // Missile explosion
        else if (ComponentMap.missileMapper.has(damageDealer)) {
            explosion = new Entity();
            explosion.add(new RenderComponent(
                ArcadeSpaceShooter.textures.findRegion("laserRedShot"),
                RenderComponent.PLANE_ABOVE
            ));
            explosion.add(new PositionComponent(position));
            explosion.add(new ExplosionComponent());
        }
        // Bomb explosion (area damage)
        else if (ComponentMap.bombMapper.has(damageDealer)) {
            explosion = new Entity();
            explosion.add(new RenderComponent(
                ArcadeSpaceShooter.textures.findRegion("laserRedShot"),
                RenderComponent.PLANE_ABOVE
            ));
            explosion.add(new PositionComponent(position));
            explosion.add(new ExplosionComponent(WeaponConstants.BOMB_EXPLOSION_RADIUS));
            explosion.add(new DealsDamageComponent(
                WeaponConstants.BOMB_DAMAGE,
                DamageTypeConstants.BOMB
            ));
        }

        if (explosion != null) {
            getEngine().addEntity(explosion);
        }
    }

    /**
     * Handle entity death (player vs enemy/meteor)
     */
    private void handleDeath(Entity killer, Entity killed, Vector2 position) {
        // Handle upgrade pickups
        handleUpgradePickup(killed, position);

        if (ComponentMap.playerMapper.has(killed)) {
            handlePlayerDeath(killed);
        } else {
            handleEnemyOrMeteorDeath(killer, killed, position);
        }
    }

    /**
     * Handle player death - reset upgrades, decrement lives
     */
    private void handlePlayerDeath(Entity player) {
        // Reset upgrades
        HasLasersComponent lasers = ComponentMap.hasLasersMapper.get(player);
        lasers.typeMask = HasLasersComponent.SINGLE;
        player.remove(HasBombsComponent.class);
        player.remove(HasMissilesComponent.class);
        player.remove(HasShieldComponent.class);
        player.remove(HasEmpComponent.class);

        // Decrement lives
        PlayerComponent playerComp = ComponentMap.playerMapper.get(player);
        playerComp.lives -= 1;

        // Remove render component - InputSystem will handle respawn
        player.remove(RenderComponent.class);

        // Play explosion sound
        SoundManager.playExplosion();
    }

    /**
     * Handle enemy or meteor death - remove entity, delegate to loot system
     */
    private void handleEnemyOrMeteorDeath(Entity killer, Entity killed, Vector2 position) {
        // Play explosion sound for enemies and meteors
        if (ComponentMap.enemyMapper.has(killed) ||
            ComponentMap.meteorMapper.has(killed)) {
            SoundManager.playExplosion();
        }

        // Remove entity
        getEngine().removeEntity(killed);

        // Delegate to LootSystem for scoring and drops
        if (lootSystem != null) {
            lootSystem.handleKill(killer, killed, position);
            lootSystem.handleMeteorSplit(killed, position);
        }
    }

    /**
     * Handle upgrade pickup using strategy pattern
     */
    private void handleUpgradePickup(Entity killed, Vector2 position) {
        ImmutableArray<Entity> players = getEngine().getEntitiesFor(
            Family.all(PlayerComponent.class).get()
        );
        if (players.size() == 0) return;

        Entity player = players.first();

        // Check each registered upgrade type
        for (Map.Entry<Class<? extends Component>, UpgradeHandler> entry : upgradeHandlers.entrySet()) {
            Component upgradeComponent = killed.getComponent(entry.getKey());
            if (upgradeComponent != null) {
                entry.getValue().apply(player, position, getEngine());
                return;
            }
        }
    }
}
