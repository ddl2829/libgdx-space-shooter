package com.dalesmithwebdev.arcadespaceshooter.components;

import com.badlogic.ashley.core.Component;

public class PlayerComponent implements Component {
    public int lives;
    public int maxLives = 5;

    public double timeSinceRespawn = 0;

    public PlayerComponent()
    {
        this.lives = this.maxLives;
    }
}
