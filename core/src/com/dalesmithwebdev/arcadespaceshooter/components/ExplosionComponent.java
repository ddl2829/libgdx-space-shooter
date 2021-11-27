package com.dalesmithwebdev.arcadespaceshooter.components;

import com.badlogic.ashley.core.Component;

public class ExplosionComponent implements Component {
    public int elapsedTime = 0;
    public int maxLife = 200;
}
