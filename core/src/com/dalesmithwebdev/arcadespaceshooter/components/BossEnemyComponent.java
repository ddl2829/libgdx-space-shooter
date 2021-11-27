package com.dalesmithwebdev.arcadespaceshooter.components;

import com.badlogic.ashley.core.Component;

public class BossEnemyComponent implements Component {
    public int laserStrength;

    public BossEnemyComponent(int laserStrength) {
        this.laserStrength = laserStrength;
    }
}
