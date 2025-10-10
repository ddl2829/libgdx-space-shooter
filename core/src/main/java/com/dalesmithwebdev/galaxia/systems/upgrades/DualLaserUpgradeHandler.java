package com.dalesmithwebdev.galaxia.systems.upgrades;

import com.badlogic.ashley.core.Entity;
import com.dalesmithwebdev.galaxia.components.HasLasersComponent;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;

/**
 * Handles dual laser upgrade
 */
public class DualLaserUpgradeHandler extends AbstractUpgradeHandler {

    @Override
    protected boolean playerHasUpgrade(Entity player) {
        HasLasersComponent lasers = ComponentMap.hasLasersMapper.get(player);
        // Player has upgrade if they DON'T have SINGLE (meaning they have DUAL already)
        return (lasers.typeMask & HasLasersComponent.SINGLE) == 0;
    }

    @Override
    protected void applyUpgrade(Entity player) {
        HasLasersComponent lasers = ComponentMap.hasLasersMapper.get(player);
        // Toggle off SINGLE, toggle on DUAL
        lasers.typeMask = lasers.typeMask ^ HasLasersComponent.SINGLE;
        lasers.typeMask = lasers.typeMask ^ HasLasersComponent.DUAL;
    }

    @Override
    public String getUpgradeName() {
        return "Dual Lasers Acquired!";
    }
}
