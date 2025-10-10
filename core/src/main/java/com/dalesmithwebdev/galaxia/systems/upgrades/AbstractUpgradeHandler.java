package com.dalesmithwebdev.galaxia.systems.upgrades;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.NotificationComponent;
import com.dalesmithwebdev.galaxia.components.PositionComponent;
import com.dalesmithwebdev.galaxia.constants.GameConstants;
import com.dalesmithwebdev.galaxia.utility.SoundManager;

/**
 * Abstract base class for upgrade handlers.
 * Provides common logic for sound, bonus points, and notifications.
 */
public abstract class AbstractUpgradeHandler implements UpgradeHandler {

    @Override
    public final boolean apply(Entity player, Vector2 position, Engine engine) {
        SoundManager.playPickup();

        if (playerHasUpgrade(player)) {
            // Give bonus points instead
            grantBonusPoints(position, engine);
            return false;
        } else {
            // Apply new upgrade
            applyUpgrade(player);
            showUpgradeNotification(engine);
            showInstructionNotification(engine);
            return true;
        }
    }

    /**
     * Check if player already has this upgrade
     */
    protected abstract boolean playerHasUpgrade(Entity player);

    /**
     * Apply the upgrade to the player
     */
    protected abstract void applyUpgrade(Entity player);

    /**
     * Get instruction text to show when upgrade is first acquired (optional)
     * @return instruction text or null for no instruction
     */
    protected String getInstructionText() {
        return null;
    }

    /**
     * Grant bonus points when player already has upgrade
     */
    protected void grantBonusPoints(Vector2 position, Engine engine) {
        ArcadeSpaceShooter.playerScore += GameConstants.BONUS_POINTS_FOR_DUPLICATE_UPGRADE;
        Entity notification = new Entity();
        notification.add(new PositionComponent(position));
        notification.add(new NotificationComponent("+" + GameConstants.BONUS_POINTS_FOR_DUPLICATE_UPGRADE, 200, false));
        engine.addEntity(notification);
    }

    /**
     * Show upgrade acquired notification
     */
    protected void showUpgradeNotification(Engine engine) {
        Entity notification = new Entity();
        notification.add(new NotificationComponent(getUpgradeName(), 3000, true));
        engine.addEntity(notification);
    }

    /**
     * Show instruction notification (if applicable)
     */
    protected void showInstructionNotification(Engine engine) {
        String instruction = getInstructionText();
        if (instruction != null) {
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    Entity e = new Entity();
                    e.add(new NotificationComponent(instruction, 3000, true));
                    engine.addEntity(e);
                }
            }, 0);
        }
    }
}
