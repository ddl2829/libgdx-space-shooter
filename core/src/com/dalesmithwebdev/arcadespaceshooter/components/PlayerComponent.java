package com.dalesmithwebdev.arcadespaceshooter.components;

import com.badlogic.ashley.core.Component;

public class PlayerComponent implements Component {
    public int lives;
    public int maxLives = 1;

    public PlayerComponent()
    {
        this.lives = this.maxLives;
    }
}
