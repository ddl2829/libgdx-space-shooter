package com.dalesmithwebdev.galaxia.systems.upgrades;

import com.badlogic.ashley.core.Entity;
import com.dalesmithwebdev.galaxia.components.HasShieldComponent;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;

/**
 * Handles shield upgrade
 */
public class ShieldUpgradeHandler extends AbstractUpgradeHandler {

    @Override
    protected boolean playerHasUpgrade(Entity player) {
        return ComponentMap.hasShieldMapper.has(player);
    }

    @Override
    protected void applyUpgrade(Entity player) {
        player.add(new HasShieldComponent());
    }

    @Override
    public String getUpgradeName() {
        return "Shield Acquired!";
    }

    @Override
    protected String getInstructionText() {
        return "Hold left shift to shield";
    }
}
