package com.dalesmithwebdev.galaxia.systems.upgrades;

import com.badlogic.ashley.core.Entity;
import com.dalesmithwebdev.galaxia.components.HasBombsComponent;
import com.dalesmithwebdev.galaxia.constants.WeaponConstants;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;

/**
 * Handles bomb upgrade
 */
public class BombUpgradeHandler extends AbstractUpgradeHandler {

    @Override
    protected boolean playerHasUpgrade(Entity player) {
        return ComponentMap.hasBombsMapper.has(player);
    }

    @Override
    protected void applyUpgrade(Entity player) {
        player.add(new HasBombsComponent(WeaponConstants.BOMB_INTERVAL_MS));
    }

    @Override
    public String getUpgradeName() {
        return "Bombs Acquired!";
    }

    @Override
    protected String getInstructionText() {
        return "Press Z to launch a bomb";
    }
}
