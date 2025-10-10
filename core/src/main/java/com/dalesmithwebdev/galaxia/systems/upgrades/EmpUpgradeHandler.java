package com.dalesmithwebdev.galaxia.systems.upgrades;

import com.badlogic.ashley.core.Entity;
import com.dalesmithwebdev.galaxia.components.HasEmpComponent;
import com.dalesmithwebdev.galaxia.constants.WeaponConstants;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;

/**
 * Handles EMP upgrade
 */
public class EmpUpgradeHandler extends AbstractUpgradeHandler {

    @Override
    protected boolean playerHasUpgrade(Entity player) {
        return ComponentMap.hasEmpMapper.has(player);
    }

    @Override
    protected void applyUpgrade(Entity player) {
        player.add(new HasEmpComponent(WeaponConstants.EMP_INTERVAL_MS));
    }

    @Override
    public String getUpgradeName() {
        return "EMP Acquired!";
    }

    @Override
    protected String getInstructionText() {
        return "Press C to blast EMP";
    }
}
