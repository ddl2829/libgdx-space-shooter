package com.dalesmithwebdev.galaxia.systems.upgrades;

import com.badlogic.ashley.core.Entity;
import com.dalesmithwebdev.galaxia.components.HasLasersComponent;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;

/**
 * Handles diagonal laser upgrade
 */
public class DiagonalLaserUpgradeHandler extends AbstractUpgradeHandler {

    @Override
    protected boolean playerHasUpgrade(Entity player) {
        HasLasersComponent lasers = ComponentMap.hasLasersMapper.get(player);
        return (lasers.typeMask & HasLasersComponent.DIAGONAL) > 0;
    }

    @Override
    protected void applyUpgrade(Entity player) {
        HasLasersComponent lasers = ComponentMap.hasLasersMapper.get(player);
        lasers.typeMask = lasers.typeMask ^ HasLasersComponent.DIAGONAL;
    }

    @Override
    public String getUpgradeName() {
        return "Diagonal Lasers Acquired!";
    }
}
