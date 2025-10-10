package com.dalesmithwebdev.galaxia.systems.upgrades;

import com.badlogic.ashley.core.Entity;
import com.dalesmithwebdev.galaxia.components.HasMissilesComponent;
import com.dalesmithwebdev.galaxia.constants.WeaponConstants;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;

/**
 * Handles missile upgrade
 */
public class MissileUpgradeHandler extends AbstractUpgradeHandler {

    @Override
    protected boolean playerHasUpgrade(Entity player) {
        return ComponentMap.hasMissilesMapper.has(player);
    }

    @Override
    protected void applyUpgrade(Entity player) {
        player.add(new HasMissilesComponent(WeaponConstants.MISSILE_INTERVAL_MS));
    }

    @Override
    public String getUpgradeName() {
        return "Missiles Acquired!";
    }

    @Override
    protected String getInstructionText() {
        return "Press X to fire missiles";
    }
}
