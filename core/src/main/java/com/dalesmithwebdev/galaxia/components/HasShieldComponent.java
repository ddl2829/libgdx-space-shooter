package com.dalesmithwebdev.galaxia.components;

import com.badlogic.ashley.core.Component;

public class HasShieldComponent implements Component {
    public boolean shieldCooldown = false;
    public double shieldPower = 3000;
    public double maxShieldPower = 3000;
    public double shieldRegenRate = 0.3f;
    public double shieldDepleteRate = 1.0f;
}
