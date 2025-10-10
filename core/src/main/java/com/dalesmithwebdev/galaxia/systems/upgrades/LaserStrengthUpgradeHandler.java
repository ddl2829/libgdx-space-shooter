package com.dalesmithwebdev.galaxia.systems.upgrades;

import com.badlogic.ashley.core.Entity;
import com.dalesmithwebdev.galaxia.components.HasLasersComponent;
import com.dalesmithwebdev.galaxia.constants.WeaponConstants;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;

/**
 * Handles laser strength upgrades (levels 1 and 2)
 */
public class LaserStrengthUpgradeHandler extends AbstractUpgradeHandler {

    @Override
    protected boolean playerHasUpgrade(Entity player) {
        HasLasersComponent lasers = ComponentMap.hasLasersMapper.get(player);
        // Player has upgrade if they have UPGRADED_AGAIN (level 2)
        return (lasers.typeMask & HasLasersComponent.UPGRADED_AGAIN) > 0;
    }

    @Override
    protected void applyUpgrade(Entity player) {
        HasLasersComponent lasers = ComponentMap.hasLasersMapper.get(player);

        if ((lasers.typeMask & HasLasersComponent.UPGRADED) > 0) {
            // Already level 1, upgrade to level 2
            lasers.typeMask = lasers.typeMask ^ HasLasersComponent.UPGRADED;
            lasers.typeMask = lasers.typeMask ^ HasLasersComponent.UPGRADED_AGAIN;
            lasers.shotInterval = WeaponConstants.LASER_INTERVAL_UPGRADED_AGAIN_MS;
        } else {
            // First upgrade, go to level 1
            lasers.typeMask = lasers.typeMask ^ HasLasersComponent.UPGRADED;
            lasers.shotInterval = WeaponConstants.LASER_INTERVAL_UPGRADED_MS;
        }
    }

    @Override
    public String getUpgradeName() {
        return "Laser Upgraded!";
    }
}
