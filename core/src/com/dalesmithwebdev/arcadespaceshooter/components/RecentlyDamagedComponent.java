package com.dalesmithwebdev.arcadespaceshooter.components;

import com.badlogic.ashley.core.Component;

public class RecentlyDamagedComponent implements Component {
    public int timeSinceDamaged = 0;
    public int timeout = 0;

    public RecentlyDamagedComponent(int timeout) {
        this.timeout = timeout;
    }
}
