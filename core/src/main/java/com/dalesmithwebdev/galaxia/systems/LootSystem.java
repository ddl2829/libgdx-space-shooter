package com.dalesmithwebdev.galaxia.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.constants.DamageTypeConstants;
import com.dalesmithwebdev.galaxia.constants.ScoreConstants;
import com.dalesmithwebdev.galaxia.constants.UpgradeConstants;
import com.dalesmithwebdev.galaxia.prefabs.*;
import com.dalesmithwebdev.galaxia.screens.GameScreen;
import com.dalesmithwebdev.galaxia.services.GameStateService;
import com.dalesmithwebdev.galaxia.services.ServiceLocator;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;
import com.dalesmithwebdev.galaxia.utility.Rand;

/**
 * Handles scoring, upgrade drops, and special effects when enemies are killed.
 * Separated from DamageSystem to follow Single Responsibility Principle.
 */
public class LootSystem extends EntitySystem {

    /**
     * Calculate score when entity is killed by laser and spawn score notification
     */
    public void handleKill(Entity killer, Entity victim, Vector2 position) {
        // Only laser kills grant score
        if (!ComponentMap.laserMapper.has(killer)) {
            return;
        }

        GameStateService gameState = ServiceLocator.getInstance().getGameState();

        int credit = calculateCredit(victim);
        double multiplier = 1 + Math.log(GameScreen.timeStayedAlive);
        double score = (credit * multiplier) * ScoreConstants.BASE_SCORE_PER_CREDIT;
        gameState.addScore(score);

        // Update kill count
        if (ComponentMap.bossEnemyMapper.has(victim)) {
            gameState.incrementKills(ScoreConstants.BOSS_KILL_CREDIT);
        } else {
            gameState.incrementKills(1);
        }

        // Spawn score notification
        Entity notification = new Entity();
        notification.add(new PositionComponent(position));
        notification.add(new NotificationComponent("+" + Math.round(score), 200, false));
        getEngine().addEntity(notification);

        // Random chance for upgrade drop
        rollForUpgradeDrop(position);
    }

    /**
     * Calculate credit value for killed entity
     */
    private int calculateCredit(Entity victim) {
        if (ComponentMap.meteorMapper.has(victim)) {
            MeteorComponent meteorComponent = ComponentMap.meteorMapper.get(victim);
            return meteorComponent.isBig ? ScoreConstants.METEOR_LARGE_CREDIT : ScoreConstants.METEOR_SMALL_CREDIT;
        }
        return ScoreConstants.ENEMY_CREDIT;
    }

    /**
     * Roll for random upgrade drop based on player state
     */
    private void rollForUpgradeDrop(Vector2 position) {
        if (Rand.nextInt(100) >= UpgradeConstants.UPGRADE_DROP_CHANCE_PERCENT) {
            return; // No drop this time
        }

        ImmutableArray<Entity> players = getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());
        if (players.size() == 0) return;

        Entity player = players.first();
        float roll = Rand.nextFloat() * 100;

        Entity upgrade = selectUpgradeType(player, roll, position);
        if (upgrade != null) {
            getEngine().addEntity(upgrade);
        }
    }

    /**
     * Select which upgrade to drop based on probability thresholds and player state
     */
    private Entity selectUpgradeType(Entity player, float roll, Vector2 position) {
        float x = position.x;
        float y = position.y;

        if (roll < UpgradeConstants.LASER_STRENGTH_THRESHOLD_END) {
            return createLaserStrengthUpgrade(player, x, y);
        } else if (roll >= UpgradeConstants.DUAL_LASER_THRESHOLD_START &&
                   roll < UpgradeConstants.DUAL_LASER_THRESHOLD_END) {
            return new DualLaserUpgrade(x, y);
        } else if (roll >= UpgradeConstants.DIAGONAL_LASER_THRESHOLD_START &&
                   roll < UpgradeConstants.DIAGONAL_LASER_THRESHOLD_END) {
            // Diagonal lasers only drop at higher levels
            if (LevelSystem.levelNumber >= UpgradeConstants.DIAGONAL_LASER_MIN_LEVEL) {
                return new DiagonalLaserUpgrade(x, y);
            }
        } else if (roll >= UpgradeConstants.SHIELD_THRESHOLD_START &&
                   roll < UpgradeConstants.SHIELD_THRESHOLD_END) {
            return new ShieldUpgrade(x, y);
        } else if (roll >= UpgradeConstants.BOMB_THRESHOLD_START &&
                   roll < UpgradeConstants.BOMB_THRESHOLD_END) {
            return new BombUpgrade(x, y);
        } else if (roll >= UpgradeConstants.MISSILE_THRESHOLD_START &&
                   roll < UpgradeConstants.MISSILE_THRESHOLD_END) {
            return new MissileUpgrade(x, y);
        } else if (roll >= UpgradeConstants.EMP_THRESHOLD_START &&
                   roll < UpgradeConstants.EMP_THRESHOLD_END) {
            return new EmpUpgrade(x, y);
        }

        return null;
    }

    /**
     * Create appropriate laser strength upgrade based on player's current level
     */
    private Entity createLaserStrengthUpgrade(Entity player, float x, float y) {
        HasLasersComponent lasersComponent = ComponentMap.hasLasersMapper.get(player);
        if ((lasersComponent.typeMask & HasLasersComponent.UPGRADED) > 0) {
            // Player has level 1, spawn level 2 upgrade (if high enough level)
            if (LevelSystem.levelNumber >= UpgradeConstants.LASER_STRENGTH_LEVEL_2_MIN_LEVEL) {
                return new LaserStrengthUpgrade(2, x, y);
            }
        } else {
            // Player has no upgrade, spawn level 1
            return new LaserStrengthUpgrade(1, x, y);
        }
        return null;
    }

    /**
     * Handle meteor splitting when big meteors are destroyed
     */
    public void handleMeteorSplit(Entity bigMeteor, Vector2 position) {
        if (!ComponentMap.meteorMapper.has(bigMeteor)) {
            return;
        }

        MeteorComponent meteorComponent = ComponentMap.meteorMapper.get(bigMeteor);
        if (!meteorComponent.isBig) {
            return; // Only big meteors split
        }

        int count = Rand.nextInt(2, 6); // Random 2-6 small meteors
        for (int i = 0; i < count; i++) {
            Entity smallMeteor = new Entity();
            smallMeteor.add(new MeteorComponent(false));
            smallMeteor.add(new TakesDamageComponent(2, DamageTypeConstants.LASER ^ DamageTypeConstants.MISSILE ^ DamageTypeConstants.BOMB));
            smallMeteor.add(new DealsDamageComponent(5, DamageTypeConstants.METEOR));
            smallMeteor.add(new RenderComponent(
                ArcadeSpaceShooter.smallMeteors.get(Rand.nextInt(ArcadeSpaceShooter.smallMeteors.size())),
                RenderComponent.PLANE_MAIN
            ));

            // Random velocity for scattered effect (pixels per second)
            // Old frame-based: 2-6 px/frame, converted to time-based: 120-360 px/sec at 60 FPS
            int[] speeds = new int[] { 120, 120, 120, 180, 180, 180, 240, 240, 300, 360 };
            int vx = speeds[Rand.nextInt(speeds.length)] * (Rand.nextBoolean() ? -1 : 1);
            int vy = -speeds[Rand.nextInt(speeds.length)];
            smallMeteor.add(new SpeedComponent(vx, vy));
            smallMeteor.add(new PositionComponent(new Vector2(position.x, position.y)));

            getEngine().addEntity(smallMeteor);
        }
    }
}
